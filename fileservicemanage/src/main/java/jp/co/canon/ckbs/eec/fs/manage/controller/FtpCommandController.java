package jp.co.canon.ckbs.eec.fs.manage.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.configuration.Category;
import jp.co.canon.ckbs.eec.fs.manage.service.CategoryList;
import jp.co.canon.ckbs.eec.fs.manage.service.FileServiceManageException;
import jp.co.canon.ckbs.eec.fs.manage.service.FtpFileService;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FtpCommandController {
    @Autowired
    FtpFileService fileService;

    @GetMapping(value="/machines")
    ResponseEntity<MachineList> getMachineList(){
        Machine[] machines = fileService.getMachineList();
        MachineList machineList = new MachineList();
        machineList.setMachines(machines);
        return ResponseEntity.ok(machineList);
    }

    @GetMapping(value="/ftp/categories")
    ResponseEntity<CategoryList> getMachineCategories(@RequestParam(required = false) String machine){
        CategoryList categoryList = new CategoryList();

        Category[] categories = fileService.getCategories(machine);

        categoryList.setCategories(categories);
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping(value="/ftp/files")
    ResponseEntity<?> getFileList(@RequestParam(name="machine", required = false) String machine,
                                  @RequestParam(name="category", required = false) String category,
                                  @RequestParam(name="from", required = false) String from,
                                  @RequestParam(name="to", required = false) String to,
                                  @RequestParam(name="keyword", required = false, defaultValue = "") String keyword,
                                  @RequestParam(name="path", required = false, defaultValue = "") String path){
        LogFileList logFileList = null;
        try {
            logFileList = fileService.getFtpFileList(machine, category, from, to, keyword, path);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(logFileList);
    }

    @PostMapping(value="/ftp/download/{machine}")
    ResponseEntity<?> createFtpDownloadRequest(@PathVariable String machine,
                                               @RequestBody CreateFtpDownloadRequestParam param){

        FtpDownloadRequestResponse res = null;
        try {
            res = fileService.createFtpDownloadRequest(machine, param.getCategory(), param.isArchive(), param.getFileList());
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/ftp/download/{machine}/{requestNo}")
    ResponseEntity<FtpDownloadRequestListResponse> getFtpDownloadRequestList(@PathVariable String machine,
                                                @PathVariable String requestNo){
        FtpDownloadRequestListResponse res =
                null;
        try {
            res = fileService.getFtpDownloadRequestList(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(value="/ftp/download/{machine}")
    ResponseEntity<FtpDownloadRequestListResponse> getFtpDownloadRequestList(@PathVariable String machine){
        return getFtpDownloadRequestList(machine, null);
    }

    @GetMapping(value="/ftp/download")
    ResponseEntity<FtpDownloadRequestListResponse> getFtpDownloadRequestList(){
        return getFtpDownloadRequestList(null, null);
    }

    @DeleteMapping(value="/ftp/download/{machine}/{requestNo}")
    ResponseEntity<?> cancelAndDeleteFtpDownloadRequest(@PathVariable String machine, @PathVariable String requestNo){
        // TODO : implement this.
        try {
            fileService.cancelAndDeleteRequest(machine, requestNo);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("");
    }
}
