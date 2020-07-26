package jp.co.canon.ckbs.eec.fs.collect.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.ftp.FtpFileService;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class FtpCommandController {
    @Autowired
    FtpFileService service;

    @GetMapping(value="/ftp/files")
    ResponseEntity<?> getFtpFileList(
            @RequestParam(name="machine", required = false) String machine,
            @RequestParam(name="category", required = false) String category,
            @RequestParam(name="from", required = false) String from,
            @RequestParam(name="to", required = false) String to,
            @RequestParam(name="keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name="path", required = false, defaultValue = "") String path
    ){
        LogFileList logFileList = service.getLogFileList(machine, category, from, to, keyword, path);
        if (logFileList.getErrorCode() != null){
            String errCode = logFileList.getErrorCode();
            if (errCode.startsWith("400")){
                return ResponseEntity.badRequest().body(logFileList);
            }
            if (errCode.startsWith("500")){
                return ResponseEntity.status(500).body(logFileList);
            }
        }
        return ResponseEntity.ok(logFileList);
    }

    @PostMapping(value="/ftp/download/{machine}")
    ResponseEntity<FtpDownloadRequestResponse> createFtpDownloadRequest(
            @PathVariable String machine,
            @RequestBody CreateFtpDownloadRequestParam param
    ){
        try {
            FtpDownloadRequestResponse res;
            FtpDownloadRequest request;
            request = service.addDownloadRequest(machine, param.getCategory(), param.getFileList(), param.isArchive());
            res = FtpDownloadRequestResponse.fromRequest(request);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            FtpDownloadRequestResponse res = new FtpDownloadRequestResponse();
            res.setErrorCode("400 Bad Request");
            res.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value="/ftp/download/{machine}/{requestNo}")
    ResponseEntity<FtpDownloadRequestListResponse> getFtpDownloadRequestList(
            @PathVariable String machine,
            @PathVariable String requestNo,
            @RequestParam(name="status", required = false) String status
    ){
        FtpDownloadRequest[] requestList = service.getFtpDownloadRequest(machine, requestNo);

        FtpDownloadRequestListResponse res = new FtpDownloadRequestListResponse();
        res.setRequestList(requestList);

        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/ftp/download/{machine}")
    ResponseEntity<FtpDownloadRequestListResponse> getFtpDownloadRequestList(
            @PathVariable String machine,
            @RequestParam(name="status", required = false) String status
    ){
        return getFtpDownloadRequestList(machine, null, status);
    }

    @GetMapping(value="/ftp/download")
    ResponseEntity<FtpDownloadRequestListResponse> getFtpDownloadRequestList(
            @RequestParam(name="status", required = false) String status
    ){
        return getFtpDownloadRequestList(null, null, status);
    }

    @DeleteMapping(value="/ftp/download/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteFtpDownloadRequest(
            @PathVariable String machine,
            @PathVariable String requestNo
    ){
        service.cancelDownloadRequest(machine, requestNo);
        return ResponseEntity.ok().body("");
    }
}
