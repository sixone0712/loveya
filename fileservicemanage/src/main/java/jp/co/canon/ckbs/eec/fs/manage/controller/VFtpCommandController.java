package jp.co.canon.ckbs.eec.fs.manage.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.*;
import jp.co.canon.ckbs.eec.fs.manage.service.FileServiceManageException;
import jp.co.canon.ckbs.eec.fs.manage.service.VFtpFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VFtpCommandController {
    @Autowired
    VFtpFileService fileService;

    @PostMapping(value="/vftp/sss/list/{machine}")
    ResponseEntity<VFtpListRequestResponse> createSssListRequest(@PathVariable String machine, @RequestBody CreateVFtpListRequestParam param){
        VFtpListRequestResponse res = null;
        try {
            res = fileService.createVFtpSssListRequest(machine, param.getDirectory());
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/vftp/sss/list/{machine}/{requestNo}")
    ResponseEntity<VFtpListRequestResponse> getSssListRequest(@PathVariable String machine, @PathVariable String requestNo){
        VFtpListRequestResponse res = null;
        try {
            res = fileService.getVFtpSssListRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value="/vftp/sss/list/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteSssListRequest(@PathVariable String machine, @PathVariable String requestNo){
        try {
            fileService.cancelAndDeleteVFtpSssListRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("cancelAndDeleteListRequest");
    }

    @PostMapping(value="/vftp/sss/download/{machine}")
    ResponseEntity<VFtpSssDownloadRequestResponse> createSssDownloadRequest(@PathVariable String machine, @RequestBody CreateVFtpSssDownloadRequestParam param){
        VFtpSssDownloadRequestResponse res = null;
        try {
            res = fileService.createVFtpSssDownloadRequest(machine, param.getDirectory(), param.getFileList(), param.isArchive());
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/vftp/sss/download/{machine}/{requestNo}")
    ResponseEntity<VFtpSssDownloadRequestResponse> getSssDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        VFtpSssDownloadRequestResponse res = null;
        try {
            res = fileService.getVFtpSssDownloadRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            res = new VFtpSssDownloadRequestResponse();
            res.setErrorCode(e.getCode());
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value="/vftp/sss/download/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteSssDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        try {
            fileService.cancelAndDeleteVFtpSssDownloadRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("cancelAndDeleteSssDownloadRequest");
    }

    @PostMapping(value="/vftp/compat/download/{machine}")
    ResponseEntity<VFtpCompatDownloadRequestResponse> createCompatDownloadRequest(@PathVariable String machine, @RequestBody CreateVFtpCompatDownloadRequestParam param){
        VFtpCompatDownloadRequestResponse res = null;
        try {
            res = fileService.createVFtpCompatDownloadRequest(machine, param.getFilename(), param.isArchive());
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/vftp/compat/download/{machine}/{requestNo}")
    ResponseEntity<VFtpCompatDownloadRequestResponse> getCompatDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        VFtpCompatDownloadRequestResponse res = null;
        try {
            res = fileService.getVFtpCompatDownloadRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value="/vftp/compat/download/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteCompatDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        try {
            fileService.cancelAndDeleteVFtpCompatDownloadRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("cancelAndDeleteCompatDownloadRequest");
    }
}
