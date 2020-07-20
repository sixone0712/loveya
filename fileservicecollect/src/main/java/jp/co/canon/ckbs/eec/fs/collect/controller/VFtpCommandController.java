package jp.co.canon.ckbs.eec.fs.collect.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.*;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.FileServiceCollectException;
import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VFtpCommandController {
    @Autowired
    VFtpFileService fileService;

    @PostMapping(value="/vftp/sss/list/{machine}")
    ResponseEntity<VFtpSssListRequestResponse> createSssListRequest(@PathVariable String machine, @RequestBody CreateVFtpListRequestParam param){
        VFtpSssListRequestResponse res = new VFtpSssListRequestResponse();
        try {
            VFtpSssListRequest request = fileService.addSssListRequest(machine, param.getDirectory());
            res.fromRequest(request);
            return ResponseEntity.ok(res);
        } catch(FileServiceCollectException e){
            res.setErrorCode(e.getCode());
            res.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(value="/vftp/sss/list/{machine}/{requestNo}")
    ResponseEntity<VFtpSssListRequestResponse> getSssListRequest(@PathVariable String machine, @PathVariable String requestNo){
        VFtpSssListRequestResponse res = new VFtpSssListRequestResponse();
        VFtpSssListRequest request = fileService.getSssListRequest(machine, requestNo);
        if (request != null){
            res.fromRequest(request);
        } else {
            res.setErrorCode("404");
            res.setErrorMessage("Request Not Found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value="/vftp/sss/list/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteSssListRequest(@PathVariable String machine, @PathVariable String requestNo){
        fileService.cancelAndDeleteSssListRequest(machine, requestNo);
        return ResponseEntity.ok("cancelAndDeleteListRequest");
    }

    @PostMapping(value="/vftp/sss/download/{machine}")
    ResponseEntity<VFtpSssDownloadRequestResponse> createSssDownloadRequest(@PathVariable String machine, @RequestBody CreateVFtpSssDownloadRequestParam param){
        VFtpSssDownloadRequestResponse res = new VFtpSssDownloadRequestResponse();
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/vftp/sss/download/{machine}/{requestNo}")
    ResponseEntity<VFtpSssDownloadRequestResponse> getSssDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        VFtpSssDownloadRequestResponse res = new VFtpSssDownloadRequestResponse();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value="/vftp/sss/download/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteSssDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        return ResponseEntity.ok("cancelAndDeleteSssDownloadRequest");
    }

    @PostMapping(value="/vftp/compat/download/{machine}")
    ResponseEntity<VFtpCompatDownloadRequestResponse> createCompatDownloadRequest(@PathVariable String machine, @RequestBody CreateVFtpCompatDownloadRequestParam param){
        VFtpCompatDownloadRequestResponse res = new VFtpCompatDownloadRequestResponse();
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/vftp/compat/download/{machine}/{requestNo}")
    ResponseEntity<VFtpCompatDownloadRequestResponse> getCompatDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        VFtpCompatDownloadRequestResponse res = new VFtpCompatDownloadRequestResponse();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value="/vftp/compat/download/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteCompatDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        return ResponseEntity.ok("cancelAndDeleteCompatDownloadRequest");
    }
}
