package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.ckbs.eec.fs.collect.service.FileInfo;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.background.CollectType;
import jp.co.canon.cks.eec.fs.rssportal.background.DownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.background.FtpDownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.model.ftp.RSSFtpSearchRequest;
import jp.co.canon.cks.eec.fs.rssportal.model.ftp.RSSFtpSearchResponse;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/rss/api/ftp")
public class FileDownloaderController {

    private final HttpSession session;
    private final FileDownloader fileDownloader;
    private final JwtService jwtService;
    private final FileServiceManageConnectorFactory connectorFactory;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    @Autowired
    public FileDownloaderController(HttpSession session, FileDownloader fileDownloader, JwtService jwtService, FileServiceManageConnectorFactory connectorFactory) {
        this.session = session;
        this.fileDownloader = fileDownloader;
        this.jwtService = jwtService;
        this.connectorFactory = connectorFactory;
    }

    private boolean createFileList(List<RSSFtpSearchResponse> list, RSSFtpSearchRequest request) {
        if(list==null || request==null) return false;

        int fileServiceRetryCount = fileDownloader.getFileServiceRetryCount();
        int fileServiceRetryInterval = fileDownloader.getFileServiceRetryInterval();

        LogFileList fileInfo = null;
        int retry = 0;
        while(retry<fileServiceRetryCount) {
            try {
                fileInfo = connectorFactory.getConnector(fileServiceAddress).getFtpFileList(request.getMachineName(),
                        request.getCategoryCode(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getKeyword(),
                        request.getDir());

                for(FileInfo file: fileInfo.getList()) {
                    if(file.getType().equals("D")) {
                        // Search for files in a directory only when the directory date falls within the search date range
                        // FTP Folder Date is UTC
                        /*
                        long dirTimestamp = Long.parseLong(file.getTimestamp());
                        log.info("dirTimestamp: " + dirTimestamp);
                        log.info("file.getFilename(): " + file.getFilename());
                        long searchFrom = Long.parseLong(request.getStartDate());
                        long searchTo = Long.parseLong(request.getEndDate());
                        if(dirTimestamp > searchTo || dirTimestamp < searchFrom) continue;
                        */
                        if(!fileDownloader.isBetween(file.getTimestamp(), request.getStartDate(), request.getEndDate())) {
                            continue;
                        }
                        RSSFtpSearchRequest child = request.getClone();
                        child.setDir(file.getFilename());
                        if(!createFileList(list, child)) {
                            log.warn(String.format("[createFileList]connection error (%s %s %s)",
                                    request.getMachineName(), request.getCategoryCode(), request.getDir()));
                        }
                    } else {
                        if(file.getFilename().endsWith(".") || file.getFilename().endsWith("..") || file.getSize()==0)
                            continue;

                        RSSFtpSearchResponse info = new RSSFtpSearchResponse();
                        info.setCategoryCode(request.getCategoryCode());
                        info.setFileName(file.getFilename());
                        String[] paths = file.getFilename().split("/");
                        if(paths.length>1) {
                            int lastIndex = file.getFilename().lastIndexOf("/");
                            info.setFilePath(file.getFilename().substring(0, lastIndex));
                        } else {
                            info.setFilePath(".");
                        }
                        info.setFileSize(file.getSize());
                        info.setFileDate(file.getTimestamp());
                        info.setFileType(file.getType());
                        info.setFabName(request.getFabName());
                        info.setMachineName(request.getMachineName());
                        info.setCategoryName(request.getCategoryName());
                        list.add(info);
                    }
                }
                return true;
            } catch (Exception e) {
                log.error("[createFileList]request failed(retry: " + (++retry) + ")");
                log.error("[createFileList]request failed: " + e);
                try {
                    Thread.sleep(fileServiceRetryInterval);
                } catch (InterruptedException interruptedException) {
                    log.error("[createFileList]failed to sleep");
                }
            }
        }
        return false;
    }

    //@PostMapping
    //@ResponseBody
    public ResponseEntity<?> searchFTPFileList(HttpServletRequest request, @RequestBody Map<String, Object> requestList) throws Exception {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        List<RSSFtpSearchResponse> responselists = new ArrayList<>();

        ArrayList<String> fabNames = requestList.containsKey("fabNames") ? (ArrayList<String>) requestList.get("fabNames") : null;
        ArrayList<String> machineNames = requestList.containsKey("machineNames") ? (ArrayList<String>) requestList.get("machineNames") : null;
        ArrayList<String> categoryCodes = requestList.containsKey("categoryCodes") ? (ArrayList<String>) requestList.get("categoryCodes") : null;
        ArrayList<String> categoryNames = requestList.containsKey("categoryNames") ? (ArrayList<String>) requestList.get("categoryNames") : null;
        String startDate = requestList.containsKey("startDate") ? (String) requestList.get("startDate") : null;
        String endDate = requestList.containsKey("endDate") ? (String) requestList.get("endDate") : null;

        if(fabNames == null || machineNames == null || categoryCodes == null || startDate == null || endDate == null) {
            log.error("[searchFTPFileList] parameter error");
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        if((fabNames.size() != machineNames.size()) || (categoryCodes.size() != categoryNames.size())) {
            log.error("[searchFTPFileList] parameter is not matched");
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        /*
        log.info("fabNames: " + fabNames);
        log.info("machineNames: " + machineNames);
        log.info("categoryCodes: " + categoryCodes);
        log.info("categoryNames: " + categoryNames);
        log.info("startDate: " + startDate);
        log.info("endDate: " + endDate);
        */

        for(int i = 0; i < machineNames.size(); i++) {
            for(int j = 0; j < categoryCodes.size(); j++) {
                RSSFtpSearchRequest serachReqeust = new RSSFtpSearchRequest();
                serachReqeust.setFabName(fabNames.get(i));
                serachReqeust.setMachineName(machineNames.get(i));
                serachReqeust.setCategoryCode(categoryCodes.get(j));
                serachReqeust.setCategoryName(categoryNames.get(j));
                serachReqeust.setStartDate(startDate);
                serachReqeust.setEndDate(endDate);

                if(!createFileList(responselists, serachReqeust)) {
                    log.warn("[createFileList]failed to connect "+serachReqeust.getMachineName());
                }
            }
        }

        resBody.put("lists", responselists);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> searchFTPFileListWithThreadPool(HttpServletRequest request, @RequestBody Map<String, Object> requestList) throws Exception {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        List<RSSFtpSearchResponse> responselists = new ArrayList<>();

        ArrayList<String> fabNames = requestList.containsKey("fabNames") ? (ArrayList<String>) requestList.get("fabNames") : null;
        ArrayList<String> machineNames = requestList.containsKey("machineNames") ? (ArrayList<String>) requestList.get("machineNames") : null;
        ArrayList<String> categoryCodes = requestList.containsKey("categoryCodes") ? (ArrayList<String>) requestList.get("categoryCodes") : null;
        ArrayList<String> categoryNames = requestList.containsKey("categoryNames") ? (ArrayList<String>) requestList.get("categoryNames") : null;
        String startDate = requestList.containsKey("startDate") ? (String) requestList.get("startDate") : null;
        String endDate = requestList.containsKey("endDate") ? (String) requestList.get("endDate") : null;

        if(fabNames == null || machineNames == null || categoryCodes == null || startDate == null || endDate == null) {
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        if((fabNames.size() != machineNames.size()) || (categoryCodes.size() != categoryNames.size())) {
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        /*
        log.info("fabNames: " + fabNames);
        log.info("machineNames: " + machineNames);
        log.info("categoryCodes: " + categoryCodes);
        log.info("startDate: " + startDate);
        log.info("endDate: " + endDate);
        */

        // Create ThreadPool with 10 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        // Futrure object to hold the result when threads are executed asynchronously
        ArrayList<Future<ArrayList<RSSFtpSearchResponse>>> futures = new ArrayList<Future<ArrayList<RSSFtpSearchResponse>>>();

        for(int i = 0; i < machineNames.size(); i++) {
            for(int j = 0; j < categoryCodes.size(); j++) {
                RSSFtpSearchRequest serachReqeust = new RSSFtpSearchRequest();
                serachReqeust.setFabName(fabNames.get(i));
                serachReqeust.setMachineName(machineNames.get(i));
                serachReqeust.setCategoryCode(categoryCodes.get(j));
                serachReqeust.setCategoryName(categoryNames.get(j));
                serachReqeust.setStartDate(startDate);
                serachReqeust.setEndDate(endDate);

                // Futrure object to hold the result when threads are executed asynchronously
                Callable<ArrayList<RSSFtpSearchResponse>> callable = new Callable<ArrayList<RSSFtpSearchResponse>>() {
                    @Override
                    public ArrayList<RSSFtpSearchResponse> call() throws Exception {
                        ArrayList<RSSFtpSearchResponse> result = new ArrayList<>();
                        if(!createFileList(result, serachReqeust)) {
                            log.warn("[createFileList]failed to connect "+serachReqeust.getMachineName());
                        }
                        return result;
                    }
                };
                futures.add(threadPool.submit(callable));
            }
        }
        threadPool.shutdown();

        for (Future<ArrayList<RSSFtpSearchResponse>> future : futures) {
            responselists.addAll(future.get());
        }

        resBody.put("lists", responselists);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @PostMapping(value="/download")
    @ResponseBody
    public ResponseEntity<?> ftpDownloadRequest(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(param.size() == 0 || param.containsKey("lists") == false) {
            log.warn("no target to download");
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        param.forEach((key, value)->log.info("key="+key+"\nvalue="+value));

        List<DownloadRequestForm> requestList = new ArrayList<>();
        Map<String, Map<String, FtpDownloadRequestForm>> map = new HashMap<>();
        List<Map<String, Object>> downloadList = (List<Map<String, Object>>) param.get("lists");

        for(Map item: downloadList) {
            String fabName = (String) item.get("fabName");
            String machineName = (String) item.get("machineName");
            String categoryCode = (String) item.get("categoryCode");
            String categoryName = (String) item.get("categoryName");
            String fileName = (String) item.get("fileName");
            String fileSize = Integer.toString((Integer) item.get("fileSize"));
            String fileDate = (String) item.get("fileDate");
            boolean file = true; // (boolean) item.get("file"); // if an item doesn't contains 'file', it occurs NullPointException.

            if(fabName!=null && machineName!=null && categoryCode!=null && categoryName!=null && fileName!=null && fileSize!=null && fileDate!=null) {
                if(file) {
                    addDownloadItem(map, fabName, machineName, categoryCode, categoryName, fileName, fileSize, fileDate);
                } else {
                    try {
                        fileDownloader.createFtpDownloadFileList(requestList, fabName, machineName, categoryCode, categoryName, null, null, fileName);
                    } catch (InterruptedException e) {
                        log.error("stopped creating download list");
                        return null;
                    }
                }
            } else {
                log.error("parameter failed");
                error.setReason(RSSErrorReason.INVALID_PARAMETER);
                resBody.put("error", error.getRSSError());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
            }
        }

        map.forEach((m, submap)->submap.forEach((c, dlForm)->requestList.add(dlForm)));
        //log.info("requestList size="+requestList.size());
        String downloadId = fileDownloader.addRequest(CollectType.ftp, requestList);
        //log.info("downloadId: " + downloadId);
        resBody.put("downloadId", downloadId);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @DeleteMapping("/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> ftpDownloadCancel(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if (downloadId == null) {
            log.warn("downloadId is null");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        if (!fileDownloader.cancelRequest(downloadId)) {
            log.warn("downloadId is invalid");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> ftpDownloadStatus(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(downloadId == null) {
            log.warn("downloadId is null");
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        log.trace("ftpDownloadStatus(downloadId="+downloadId+")");

        if(fileDownloader.isValidId(downloadId)==false) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new DownloadStatusResponseBody(fileDownloader, downloadId));
    }

    @RequestMapping("/storage/{downloadId}")
    public ResponseEntity<?> ftpDownloadFile(
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

    private String createZipFilename(String downloadId) {
        // format: username_fabname{_fabname2}_YYYYMMDD_hhmmss.zip

        String username = jwtService.getCurAccTokenUserName();

        if(username==null) {
            log.error("no username");
            return null;
        }

        List<String> fabs = fileDownloader.getFabs(downloadId);
        if(fabs==null || fabs.size()==0) {
            log.error("no fab info");
            return null;
        }
        String fab = fabs.get(0);
        if(fabs.size()>1) {
            for(int i=1; i<fabs.size(); ++i)
                fab += "_"+fabs.get(i);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String cur = dateFormat.format(new Date(System.currentTimeMillis()));

        String fileName = String.format("%s_%s_%s.zip", username, fab, cur);
        log.info("filename = "+fileName);
        return fileName;
    }

    private void addDownloadItem(final Map map, String fab, String tool, String logType, String logTypeStr,
                                 String file, String size, String date) {

        FtpDownloadRequestForm form;

        // We have to consider sub-directories now.
        String[] paths = file.split("/");
        if(paths.length!=1) {
            // This file places at the sub-directory.
            // In this case, a key composes with 'logType/sub-directory-name' pattern.
            logType = logType+"/"+paths[0];
        }

        if(map.containsKey(tool)) {
            Map<String, FtpDownloadRequestForm> submap = (Map<String, FtpDownloadRequestForm>) map.get(tool);
            if(submap.containsKey(logType)) {
                form = submap.get(logType);
            } else {
                form = new FtpDownloadRequestForm(fab, tool, logType, logTypeStr);
                submap.put(logType, form);
            }
        } else {
            form = new FtpDownloadRequestForm(fab, tool, logType, logTypeStr);
            Map<String, FtpDownloadRequestForm> submap = new HashMap<>();
            submap.put(logType, form);
            map.put(tool, submap);
        }

        if(form==null) {
            log.error("fatal: addDownloadItem could not find form");
            return;
        }
        form.addFile(file, Long.parseLong(size), date);
    }

    private final Log log = LogFactory.getLog(getClass());
}
