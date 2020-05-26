package jp.co.canon.cks.eec.fs.rssportal.vftp.controller;

import org.springframework.web.bind.annotation.PostMapping;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.FileListStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.ServerInfoRepository;
import jp.co.canon.cks.eec.fs.rssportal.vftp.VFtpManager;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker.CompatChecker;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker.SSSChecker;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.ServerInfo;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetFileParamList;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

@Controller
public class VFtpController {
    @Autowired
    ServerInfoRepository serverInfoRepository;
    @Autowired
    VFtpManager manager;

    @PostMapping("/vftp/compat/getrequest")
    @ResponseBody
    public ResponseEntity<?> postCompatGetRequest(@RequestBody CompatGetRequestParam param) {
        String filename = param.getFilename();
        CompatChecker compatChecker = CompatChecker.fromFilename(filename);
        if (compatChecker == null){
            return ResponseEntity.badRequest().body("requested parameter is not valid");
        }

        String server = null;

        String deviceName = compatChecker.getDeviceName();
        if (deviceName != null){
            server = serverInfoRepository.getServerNameByDevice(deviceName);
            if (server == null){
                return ResponseEntity.badRequest().body("requested parameter is not valid");
            }
        }

        ServerInfo[] serverInfos = null;
        if (server != null){
            ServerInfo serverInfo = serverInfoRepository.getServerInfoByName(server);
            serverInfos = new ServerInfo[]{serverInfo};
        }
        if (server == null){
            serverInfos = serverInfoRepository.getAllServerInfos();
        }

        GetFileParamList paramList = new GetFileParamList();
        for (ServerInfo info : serverInfos){
            paramList.add(info.getName(), "/VROOT/COMPAT/Optional", filename);
        }

        String requestNo = manager.requestDownload(paramList.toArray(), filename+".zip");
        if (requestNo == null){
            return ResponseEntity.badRequest().body("requested parameter is not valid");
        }
        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);
        
        EntityModel<FileDownloadStatus> entityModel = new EntityModel<>(sts);
        Link link = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder.methodOn(VFtpController.class).getCompatGetRequest(requestNo)).withSelfRel();

        return ResponseEntity.created(link.toUri()).body(entityModel);
    }

    @GetMapping("/vftp/compat/getrequest/{requestNo}")
    @ResponseBody
    public ResponseEntity<?> getCompatGetRequest(@PathVariable String requestNo) {
        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);
        if (sts == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<FileDownloadStatus> entityModel = new EntityModel<>(sts);

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/vftp/compat/getrequest/{requestNo}/download")
    @ResponseBody
    public ResponseEntity<?> getCompatGetRequestDownload(@PathVariable String requestNo){
        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);
        if (sts == null){
            return ResponseEntity.notFound().build();
        }
        if (sts.getStatus() != FileDownloadStatus.Status.COMPLETED){
            return ResponseEntity.notFound().build();
        }

        File destFile = sts.createDownloadFile();
        if (destFile == null){
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource isr;
            isr = new InputStreamResource(new FileInputStream(destFile));
            return ResponseEntity.ok()
                        .contentLength(destFile.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename="+sts.getDownloadFileName())
                        .body(isr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/vftp/compat/getrequest/{requestNo}")
    @ResponseBody
    public ResponseEntity<?> deleteCompatGetRequest(@PathVariable String requestNo) {
        manager.deleteRequestDownload(requestNo);
        return ResponseEntity.ok("ok");
    }

    @PostMapping(value = "/vftp/sss/listrequest")
    @ResponseBody
    public ResponseEntity<?> postSssListRequest(@RequestBody SssListRequestParam param) {
        String directory = param.getDirectory();
        // Parameter Validation Checker
        SSSChecker checker = SSSChecker.fromDirectoryName(directory);
        if (checker == null){
            return ResponseEntity.badRequest().body("requested directory name is not valid");
        }

        String deviceName = checker.getDeviceName();
        String server = null;
        if (deviceName != null) {
            server = serverInfoRepository.getServerNameByDevice(deviceName);
            if (server == null) {
                return ResponseEntity.badRequest().body("requested directory name is not valid");
            }
        }
        // Call Request
        String requestNo = manager.requestFileList(server, "/VROOT/SSS/Optional", directory);
        if (requestNo == null) {
            return ResponseEntity.badRequest().body("request failed...");
        }

        // Make Response
        FileListStatus sts = manager.requestFileListStatus(requestNo);
        EntityModel<FileListStatus> entityModel = new EntityModel<>(sts);

        Link link = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(VFtpController.class).getSssListRequest(requestNo)).withSelfRel();
        return ResponseEntity.created(link.toUri()).body(entityModel);
    }

    @GetMapping(value = "/vftp/sss/listrequest/{requestNo}")
    @ResponseBody
    public ResponseEntity<?> getSssListRequest(@PathVariable String requestNo) {
        FileListStatus sts = manager.requestFileListStatus(requestNo);
        if (sts == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<FileListStatus> entityModel = new EntityModel<>(sts);

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping(value = "/vftp/sss/listrequest/{requestNo}")
    @ResponseBody
    public ResponseEntity<?> deleteSssListRequest(@PathVariable String requestNo) {
        manager.deleteRequestFileList(requestNo);
        return ResponseEntity.ok("ok");
    }

    private String getSssCompressFilenameFromPath(String path) {
        final String prefix = "/VROOT/SSS/Optional";
        if (path.startsWith(prefix)) {
            String filename = path.substring(prefix.length());
            return filename + ".zip";
        }
        return null;
    }

    @PostMapping(value = "/vftp/sss/getrequest")
    @ResponseBody
    public ResponseEntity<?> postSssGetRequest(@RequestBody SssGetRequestParam param) {
        /* Parameter Check */
        if (param == null) {
            return ResponseEntity.badRequest().body("requested parameter is not valid");
        }
        if (param.getList().size() == 0) {
            return ResponseEntity.badRequest().body("requested parameter is not valid");
        }
        for (SssGetItem item : param.getList()) {
            if (item.getServer() == null) {
                return ResponseEntity.badRequest().body("requested parameter is not valid");
            }
            if (item.getPath() == null) {
                return ResponseEntity.badRequest().body("requested parameter is not valid");
            }
            if (item.getFilename() == null) {
                return ResponseEntity.badRequest().body("requested parameter is not valid");
            }
            if (serverInfoRepository.getServerInfoByName(item.getServer()) == null) {
                return ResponseEntity.badRequest().body("requested parameter is not valid(unknown server)");
            }
        }
        String compressFilename = getSssCompressFilenameFromPath(param.getList().get(0).getPath());
        if (compressFilename == null) {
            return ResponseEntity.badRequest().body("requested parameter is not valid");
        }
        String requestNo = manager.requestDownload(param.toGetFileParamList().toArray(), compressFilename);
        if (requestNo == null) {
            return ResponseEntity.badRequest().body("cannot start download");
        }

        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);

        EntityModel<FileDownloadStatus> entityModel = new EntityModel<>(sts);
        Link link = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(VFtpController.class).getSssGetRequest(requestNo)).withSelfRel();
        return ResponseEntity.created(link.toUri()).body(entityModel);
    }

    @GetMapping(value = "/vftp/sss/getrequest/{requestNo}")
    @ResponseBody
    public ResponseEntity<?> getSssGetRequest(@PathVariable String requestNo) {
        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);
        if (sts == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<FileDownloadStatus> entityModel = new EntityModel<>(sts);

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping(value = "/vftp/sss/getrequest/{requestNo}/download")
    @ResponseBody
    public ResponseEntity<?> getSssGetRequestDownload(@PathVariable String requestNo) {
        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);
        if (sts == null) {
            return ResponseEntity.notFound().build();
        }
        if (sts.getStatus() != FileDownloadStatus.Status.COMPLETED) {
            return ResponseEntity.notFound().build();
        }

        File destFile = sts.createDownloadFile();
        if (destFile == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource isr;
            isr = new InputStreamResource(new FileInputStream(destFile));
            return ResponseEntity.ok()
                        .contentLength(destFile.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename="+sts.getDownloadFileName())
                        .body(isr);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/vftp/sss/getrequest/{requestNo}")
    @ResponseBody
    public ResponseEntity<?> deleteSssGetRequest(@PathVariable String requestNo){
        manager.deleteRequestDownload(requestNo);
        return ResponseEntity.ok("ok");
    }

    @Getter
    @Setter
    public static class CompatGetRequestParam {
        private String filename;
    }

    @Getter
    @Setter
    public static class SssListRequestParam {
        private String directory;
    }

    @Getter
    @Setter
    public static class SssGetItem {
        private String server;
        private String path;
        private String filename;
    }

    @Getter
    @Setter
    public static class SssGetRequestParam {
        private List<SssGetItem> list;

        public GetFileParamList toGetFileParamList(){
            GetFileParamList rlist = new GetFileParamList();
            for(SssGetItem item : list){
                rlist.add(item.getServer(), item.getPath(), item.getFilename());
            }
            return rlist;
        }
    }

}