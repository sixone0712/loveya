package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import jp.co.canon.cks.eec.fs.rssportal.model.ftp.RSSFtpSearchRequest;
import jp.co.canon.cks.eec.fs.rssportal.model.ftp.RSSFtpSearchResponse;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.rmi.RemoteException;
import java.text.ParseException;
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

    @Autowired
    public FileDownloaderController(HttpSession session, FileDownloader fileDownloader) {
        this.session = session;
        this.fileDownloader = fileDownloader;
    }
    private boolean createFileList(List<RSSFtpSearchResponse> list, RSSFtpSearchRequest request) {
        if(list==null || request==null) return false;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        try {
            from.setTime(format.parse(request.getStartDate()));
            to.setTime(format.parse(request.getEndDate()));
        } catch (ParseException e) {
            log.error("[createFileList] failed to parse datetime ("+request.getMachineName()+":"+request.getCategoryCode());
            return false;
        }

        int fileServiceRetryCount = fileDownloader.getFileServiceRetryCount();
        int fileServiceRetryInterval = fileDownloader.getFileServiceRetryInterval();

        FileInfoModel[] fileInfo;
        int retry = 0;
        while(retry<fileServiceRetryCount) {
            try {
                fileInfo = fileDownloader.getServiceManage().createFileList(
                        request.getMachineName(),
                        request.getCategoryCode(),
                        from,
                        to,
                        request.getKeyword(),
                        request.getDir());

                for(FileInfoModel file: fileInfo) {
                    if(file.getName().endsWith(".") || file.getName().endsWith("..") || file.getSize()==0)
                        continue;
                    if(file.getType().equals("D")) {
                        // Search for files in a directory only when the directory date falls within the search date range
                        // FTP Folder Date is UTC
                        /*
                        long dirTimestamp = Long.parseLong(format.format(file.getTimestamp().getTime()));
                        log.info("file.getTimestamp().getTime():" + file.getTimestamp().getTime());
                        log.info("file.getTimestamp():" + file.getTimestamp());
                        log.info("dirTimestamp: " + dirTimestamp);
                        log.info("file.getName(): " + file.getName());
                        long searchFrom = Long.parseLong(request.getStartDate());
                        long searchTo = Long.parseLong(request.getEndDate());
                        if(dirTimestamp > searchTo || dirTimestamp < searchFrom) continue;
                         */

                        RSSFtpSearchRequest child = request.getClone();
                        child.setDir(file.getName());
                        if(!createFileList(list, child)) {
                            log.warn(String.format("[createFileList]connection error (%s %s %s)",
                                    request.getMachineName(), request.getCategoryCode(), request.getDir()));
                        }
                    } else {
                        RSSFtpSearchResponse info = new RSSFtpSearchResponse();
                        info.setFile(true);
                        //info.setFileId(0);
                        info.setCategoryCode(request.getCategoryCode());
                        info.setFileName(file.getName());
                        String[] paths = file.getName().split("/");
                        if(paths.length>1) {
                            int lastIndex = file.getName().lastIndexOf("/");
                            info.setFilePath(file.getName().substring(0, lastIndex));
                        } else {
                            info.setFilePath(".");
                        }
                        info.setFileSize(file.getSize());
                        info.setFileDate(format.format(file.getTimestamp().getTime()));
                        //info.setFileStatus("");
                        info.setFabName(request.getFabName());
                        info.setMachineName(request.getMachineName());
                        info.setCategoryName(request.getCategoryName());
                        list.add(info);
                    }
                }
                return true;
            } catch (RemoteException e) {
                log.error("[createFileList]request failed(retry: " + (++retry) + ")");
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
        List<RSSFtpSearchResponse> responselists = new ArrayList<>();
        ArrayList<String> fabNames = requestList.containsKey("fabNames") ? (ArrayList<String>) requestList.get("fabNames") : null;
        ArrayList<String> machineNames = requestList.containsKey("machineNames") ? (ArrayList<String>) requestList.get("machineNames") : null;
        ArrayList<String> categoryCodes = requestList.containsKey("categoryCodes") ? (ArrayList<String>) requestList.get("categoryCodes") : null;
        ArrayList<String> categoryNames = requestList.containsKey("categoryNames") ? (ArrayList<String>) requestList.get("categoryNames") : null;
        String startDate = requestList.containsKey("startDate") ? (String) requestList.get("startDate") : null;
        String endDate = requestList.containsKey("endDate") ? (String) requestList.get("endDate") : null;

        if(fabNames == null || machineNames == null || categoryCodes == null || startDate == null || endDate == null) {
            log.error("[searchFTPFileList] parameter error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if((fabNames.size() != machineNames.size()) || (categoryCodes.size() != categoryNames.size())) {
            log.error("[searchFTPFileList] parameter is not matched");
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

        Map<String, Object> res = new HashMap<>();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> searchFTPFileListWithThreadPool(HttpServletRequest request, @RequestBody Map<String, Object> requestList) throws Exception {
        log.info(String.format("[Post] %s", request.getServletPath()));
        List<RSSFtpSearchResponse> responselists = new ArrayList<>();
        ArrayList<String> fabNames = requestList.containsKey("fabNames") ? (ArrayList<String>) requestList.get("fabNames") : null;
        ArrayList<String> machineNames = requestList.containsKey("machineNames") ? (ArrayList<String>) requestList.get("machineNames") : null;
        ArrayList<String> categoryCodes = requestList.containsKey("categoryCodes") ? (ArrayList<String>) requestList.get("categoryCodes") : null;
        ArrayList<String> categoryNames = requestList.containsKey("categoryNames") ? (ArrayList<String>) requestList.get("categoryNames") : null;
        String startDate = requestList.containsKey("startDate") ? (String) requestList.get("startDate") : null;
        String endDate = requestList.containsKey("endDate") ? (String) requestList.get("endDate") : null;

        if(fabNames == null || machineNames == null || categoryCodes == null || startDate == null || endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if((fabNames.size() != machineNames.size()) || (categoryCodes.size() != categoryNames.size())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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

        Map<String, Object> res = new HashMap<>();
        res.put("lists", responselists);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping(value="/download")
    @ResponseBody
    public ResponseEntity<?> ftpDownloadRequest(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> res = new HashMap<>();
        if(param.size() == 0 || param.containsKey("lists") == false) {
            log.warn("no target to download");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        param.forEach((key, value)->log.info("key="+key+"\nvalue="+value));

        List<DownloadForm> requestList = new ArrayList<>();
        Map<String, Map<String, DownloadForm>> map = new HashMap<>();
        List<Map<String, Object>> downloadList = (List<Map<String, Object>>) param.get("lists");

        for(Map item: downloadList) {
            String fabName = (String) item.get("fabName");
            String machineName = (String) item.get("machineName");
            String categoryCode = (String) item.get("categoryCode");
            String categoryName = (String) item.get("categoryName");
            String fileName = (String) item.get("fileName");
            String fileSize = Integer.toString((Integer) item.get("fileSize"));
            String fileDate = (String) item.get("fileDate");
            boolean file = (boolean) item.get("file"); // if an item doesn't contains 'file', it occurs NullPointException.

            if(fabName!=null && machineName!=null && categoryCode!=null && categoryName!=null && fileName!=null && fileSize!=null && fileDate!=null) {
                if(file) {
                    addDownloadItem(map, fabName, machineName, categoryCode, categoryName, fileName, fileSize, fileDate);
                } else {
                    fileDownloader.createDownloadFileList(requestList, fabName, machineName, categoryCode, categoryName, null, null, fileName);
                }
            } else {
                log.error("parameter failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        map.forEach((m, submap)->submap.forEach((c, dlForm)->requestList.add(dlForm)));
        log.warn("requestList size="+requestList.size());
        String downloadId = fileDownloader.addRequest(requestList);
        log.info("downloadId: " + downloadId);
        res.put("downloadId", downloadId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> ftpDownloadCancel(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        if (downloadId == null) {
            log.warn("downloadId is null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (!fileDownloader.cancelRequest(downloadId)) {
            log.warn("downloadId is invalid");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<DownloadStatusResponseBody> ftpDownloadStatus(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        if(downloadId == null) {
            log.warn("downloadId is null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        log.trace("ftpDownloadStatus(downloadId="+downloadId+")");

        if(fileDownloader.isValidId(downloadId)==false) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new DownloadStatusResponseBody(fileDownloader, downloadId));
    }

    @RequestMapping("/storage/{downloadId}")
    public ResponseEntity<InputStreamResource> ftpDownloadFile(
            @PathVariable("downloadId") String downloadId,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info(String.format("[Get] %s", request.getServletPath()));

        if(downloadId == null) {
            log.error("invalid param");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if(fileDownloader.isValidId(downloadId)==false) {
            log.error("invalid dlId");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if(fileDownloader.getStatus(downloadId).equals("done")==false) {
            log.error("in-progress");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
            //return new ResponseEntity(isr, headers, HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(isr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    private String createZipFilename(String downloadId) {
        // format: username_fabname{_fabname2}_YYYYMMDD_hhmmss.zip

        if(session==null) {
            log.error("null session");
            return null;
        }

        SessionContext context = (SessionContext)session.getAttribute("context");
        if(context==null || context.isAuthorized()==false) {
            log.error("unauthorized download request");
            return null;
        }
        String username = context.getUser().getUsername();
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

        DownloadForm form;

        // We have to consider sub-directories now.
        String[] paths = file.split("/");
        if(paths.length!=1) {
            // This file places at the sub-directory.
            // In this case, a key composes with 'logType/sub-directory-name' pattern.
            logType = logType+"/"+paths[0];
        }

        if(map.containsKey(tool)) {
            Map<String, DownloadForm> submap = (Map<String, DownloadForm>) map.get(tool);
            if(submap.containsKey(logType)) {
                form = submap.get(logType);
            } else {
                form = new DownloadForm("FS_P#M", fab, tool, logType, logTypeStr);
                submap.put(logType, form);
            }
        } else {
            form = new DownloadForm("FS_P#M", fab, tool, logType, logTypeStr);
            Map<String, DownloadForm> submap = new HashMap<>();
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
