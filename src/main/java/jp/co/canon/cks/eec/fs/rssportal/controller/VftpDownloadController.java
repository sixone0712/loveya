package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.background.*;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.model.vftp.VFtpFileInfoExtends;
import jp.co.canon.cks.eec.fs.rssportal.model.vftp.VFtpSssListRequestResponseExtends;
import jp.co.canon.cks.eec.fs.rssportal.service.JwtService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/rss/api/vftp")
public class VftpDownloadController extends DownloadControllerCommon {
    private final Log log = LogFactory.getLog(getClass());

    @Value("${rssportal.file-collect-service.retry}")
    private int fileServiceRetryCount;

    @Value("${rssportal.file-collect-service.retry-interval}")
    private int fileServiceRetryInterval;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    @Autowired
    public VftpDownloadController(JwtService jwtService, FileDownloader fileDownloader,
                                  FileServiceManageConnectorFactory connectorFactory) {
        super(jwtService, fileDownloader, connectorFactory);
    }

    // Request VFTP Comapt Downlaod
    @PostMapping("/compat/download")
    @ResponseBody
    public ResponseEntity<?> vftpCompatDownloadRequest(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));
        RSSError error = new RSSError();
        Map<String, Object> resBody = new HashMap<>();

        List<String> fabs = param.containsKey("fabNames")?(List)param.get("fabNames"):null;
        List<String> machines = param.containsKey("machineNames")?(List)param.get("machineNames"):null;
        String command = param.containsKey("command") ? (String) param.get("command") : null;

        if(fabs==null || machines==null || fabs.size()==0 || fabs.size()!=machines.size() || command==null) {
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        List<DownloadRequestForm> list = new ArrayList<>();
        for(int i=0; i<machines.size(); ++i) {
            VFtpCompatDownloadRequestForm form = new VFtpCompatDownloadRequestForm(fabs.get(i), machines.get(i), command, true);
            list.add(form);
        }

        String downloadId = fileDownloader.addRequest(CollectType.vftp_compat, list);
        log.info("vftp-compat downloadId="+downloadId);
        resBody.put("downloadId", downloadId);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Cancel VFTP Comapt Download
    @DeleteMapping({"/compat/download/{downloadId}", "/sss/download/{downloadId}"})
    @ResponseBody
    public ResponseEntity<?> vftpCompatDownloadCancel(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if (downloadId == null) {
            log.warn("downloadId is null");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        if(!fileDownloader.cancelRequest(downloadId)) {
            log.warn("downloadId is invalid");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // Request VFTP Compat Download status
    @GetMapping({"/compat/download/{downloadId}", "/sss/download/{downloadId}"})
    @ResponseBody
    public ResponseEntity<?> vftpCompatDownloadStatus(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(downloadId == null) {
            log.warn("downloadId is null");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        if(fileDownloader.isValidId(downloadId)==false) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new DownloadStatusResponseBody(fileDownloader, downloadId));
    }

    // Download VFP Compat file
    @RequestMapping({"/compat/storage/{downloadId}", "/sss/storage/{downloadId}"})
    public ResponseEntity<?> vftpCompatDownloadFile(
            @PathVariable("downloadId") String downloadId,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(downloadId == null) {
            log.error("invalid param");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        if(fileDownloader.isValidId(downloadId)==false) {
            log.error("invalid dlId");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
        if(fileDownloader.getStatus(downloadId).equals("done")==false) {
            log.error("in-progress");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        String dlPath = fileDownloader.getDownloadInfo(downloadId);
        log.info("download path="+dlPath);

        try {
            InputStream is = new FileInputStream(new File(dlPath));
            InputStreamResource isr = new InputStreamResource(is);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(Files.size(Paths.get(dlPath)));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            response.setHeader("Content-Disposition", "attachment; filename="+createZipFilename(downloadId));
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(isr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        error.setReason(RSSErrorReason.NOT_FOUND);
        resBody.put("error", error.getRSSError());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
    }

    private ArrayList<VFtpSssListRequestResponseExtends> searchVftpSSSRequest(ArrayList<String> fabNames,
                                                                              ArrayList<String> machineNames,
                                                                              String command) throws Exception {
        ArrayList<VFtpSssListRequestResponseExtends> requestList = new ArrayList<VFtpSssListRequestResponseExtends>();
        int doneCnt = 0;
        int retry = 0;
        for(int i=0; i< machineNames.size(); i++) {
            retry = 0;
            while (retry < fileServiceRetryCount) {
                try {
                    VFtpSssListRequestResponse responseInfo =
                            (VFtpSssListRequestResponse)connectorFactory.getConnector(fileServiceAddress).createVFtpSssListRequest(
                                    machineNames.get(i),
                                    command);
                    VFtpSssListRequestResponseExtends newResponse = new VFtpSssListRequestResponseExtends();
                    newResponse.setFabName(fabNames.get(i));
                    newResponse.setErrorCode(responseInfo.getErrorCode());
                    newResponse.setErrorMessage(responseInfo.getErrorMessage());
                    newResponse.setRequest(responseInfo.getRequest());
                    requestList.add(newResponse);
                    doneCnt++;
                    break;
                } catch (Exception e) {
                    retry++;
                    log.error("searchVftpSSSRequest" + " : request failed(retry: " + retry + ")");
                    log.error("searchVftpSSSRequest" + " : " + e);
                    Thread.sleep(fileServiceRetryInterval);
                }
            }
        }
        if(doneCnt == 0) {
            throw new Exception("searchVftpSSSRequest : An error occurred for every request.");
        }
        return requestList;
    }

    private ArrayList<VFtpFileInfoExtends> searchVftpSSSResponse(ArrayList<VFtpSssListRequestResponseExtends> requestList) throws Exception {
        ArrayList<VFtpFileInfoExtends> responseList = new ArrayList<VFtpFileInfoExtends>();
        int listCnt = requestList.size();
        int doneCnt = 0;
        int errCnt = 0;
        ArrayList<VFtpSssListRequestResponseExtends> newRequestList = new ArrayList<VFtpSssListRequestResponseExtends>();
        newRequestList.addAll(requestList);
        while (doneCnt < listCnt) {
            for (Iterator<VFtpSssListRequestResponseExtends> iter = newRequestList.iterator(); iter.hasNext(); ) {
                int retry = 0;
                VFtpSssListRequestResponseExtends req = iter.next();
                VFtpSssListRequestResponse res = null;
                while (retry < fileServiceRetryCount) {
                    try {
                         res = connectorFactory.getConnector(fileServiceAddress).getVFtpSssListRequest(
                                req.getRequest().getMachine(), req.getRequest().getRequestNo());
                         break;
                    } catch (Exception e) {
                        retry++;
                        log.error("searchVftpSSSResponse" + " : request failed(retry: " + retry + ")");
                        log.error("searchVftpSSSResponse" + " : " + e);
                        Thread.sleep(fileServiceRetryInterval);
                    }
                }
                if(res == null) {
                    iter.remove();
                    doneCnt++;
                    errCnt++;
                } else {
                    VFtpSssListRequest.Status status = res.getRequest().getStatus();
                    if (status == VFtpSssListRequest.Status.ERROR) {
                        iter.remove();
                        doneCnt++;
                        errCnt++;
                    } else if (status == VFtpSssListRequest.Status.CANCEL) {
                        iter.remove();
                        doneCnt++;
                    } else if (status == VFtpSssListRequest.Status.EXECUTED) {
                        for (VFtpFileInfo file : res.getRequest().getFileList()) {
                            VFtpFileInfoExtends convFile = new VFtpFileInfoExtends();
                            convFile.setFabName(req.getFabName());
                            convFile.setMachineName(res.getRequest().getMachine());
                            convFile.setCommand(res.getRequest().getDirectory());
                            convFile.setFileName(file.getFileName());
                            convFile.setFileType(file.getFileType());
                            convFile.setFileSize(file.getFileSize());
                            responseList.add(convFile);
                        }
                        iter.remove();
                        doneCnt++;
                    }
                    if (doneCnt < listCnt) {
                        Thread.yield();
                    }
                }
            }
        }
        if (listCnt == errCnt) {
            throw new Exception("searchVftpSSSResponse : An error occurred for every request.");
        }
        return responseList;
    }

    // Search VFTP SSS File
    @PostMapping("/sss")
    @ResponseBody
    public ResponseEntity<?> searchVftpSSSFileList(HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception {
        String requestUrl = String.format("[Post] %s", request.getServletPath());
        log.info(requestUrl);
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(param == null) {
            log.info(requestUrl + " : no param");
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        ArrayList<String> fabNames = param.containsKey("fabNames") ? (ArrayList<String>) param.get("fabNames") : null;
        ArrayList<String> machineNames = param.containsKey("machineNames") ? (ArrayList<String>) param.get("machineNames") : null;
        String command = param.containsKey("command") ? (String) param.get("command") : null;

        if(fabNames == null || machineNames == null || command == null) {
            log.error(requestUrl + " : parameter is not matched");
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        if(fabNames.size() != machineNames.size()) {
            log.error(requestUrl + " : fabNames.size() and machineNames.size() are not the same.");
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        try {
            ArrayList<VFtpSssListRequestResponseExtends> requestList = searchVftpSSSRequest(fabNames, machineNames, command);
            ArrayList<VFtpFileInfoExtends> responseList = searchVftpSSSResponse(requestList);
            resBody.put("lists", responseList);
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (Exception e) {
            log.error(requestUrl + " : " + e);
            error.setReason(RSSErrorReason.INTERNAL_ERROR);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Request VFTP SSS Downlaod
    @PostMapping("/sss/download")
    @ResponseBody
    public ResponseEntity<?> vftpSSSDownloadRequest(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        List<Map<String, Object>> requestList = (List<Map<String, Object>>) param.get("lists");
        List<DownloadRequestForm> list = new ArrayList<>();

        loop_top:
        for(Map item: requestList) {
            String fabName = (String) item.get("fabName");
            String machineName = (String) item.get("machineName");
            String directory = (String) item.get("command");
            String fileName = (String) item.get("fileName");
            long fileSize = ((Integer)item.get("fileSize")).longValue();

            for(DownloadRequestForm _form: list) {
                VFtpSssDownloadRequestForm form = (VFtpSssDownloadRequestForm)_form;
                if(form.getMachine().equals(machineName) && form.getFab().equals(fabName) && form.getDirectory().equals(directory)) {
                    form.addFile(fileName, fileSize);
                    continue loop_top;
                }
            }
            VFtpSssDownloadRequestForm form = new VFtpSssDownloadRequestForm(fabName, machineName, directory);
            form.addFile(fileName, fileSize);
            list.add(form);
        }

        String downloadId = fileDownloader.addRequest(CollectType.vftp_sss, list);
        log.info("vftp-sss downloadId="+downloadId);
        resBody.put("downloadId", downloadId);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    /*
    // Cancel VFTP SSS Download
    @DeleteMapping("/sss/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> vftpSSSDownloadCancel(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(!fileDownloader.cancelRequest(downloadId)) {
            log.warn("downloadId is invalid");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // Request VFTP SSS Download status
    @GetMapping("/sss/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> vftpSSSDownloadStatus(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Download VFP SSS file
    @RequestMapping("/sss/storage/{downloadId}")
    public ResponseEntity<?> vftpSSSDownloadFile(
            @PathVariable("downloadId") String downloadId,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }
     */
}
