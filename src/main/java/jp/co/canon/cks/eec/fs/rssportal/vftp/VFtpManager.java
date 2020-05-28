package jp.co.canon.cks.eec.fs.rssportal.vftp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.ServerInfo;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetFileParam;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetRequest;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetRequestRepository;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list.ListRequest;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list.ListRequestRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VFtpManager {
    @Autowired
    private ServerInfoRepository serverInfoRepository;
    @Autowired
    private ListRequestRepository listRequestRepository;
    @Autowired
    private GetRequestRepository getRequestRepository;

    Map<String, ListRequest> listRequestMap = new HashMap<String, ListRequest>();
    Map<String, GetRequest> getRequestMap = new HashMap<String, GetRequest>();

    private PropertyChangeListener listRequestPropertyChangeListener = new PropertyChangeListener(){
    
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("completed")){
                Object o = evt.getSource();
                ListRequest srcreq = (ListRequest)o;

                synchronized(VFtpManager.this){
                    listRequestRepository.save(srcreq);
                    listRequestMap.remove(srcreq.getRequestNo());
                }
                return;
            }
            if (evt.getPropertyName().equals("statusChanged")){
                Object o = evt.getSource();
                ListRequest srcreq = (ListRequest)o;
                synchronized(VFtpManager.this){
                    listRequestRepository.save(srcreq);
                }
                return;
            }
        }
    };

    private PropertyChangeListener getRequestPropertyChangeListener = new PropertyChangeListener(){
    
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("completed")){
                Object o = evt.getSource();
                GetRequest srcreq = (GetRequest)o;

                synchronized(VFtpManager.this){
                    getRequestRepository.save(srcreq);
                    getRequestMap.remove(srcreq.getRequestNo());
                }
                return;
            }
            if (evt.getPropertyName().equals("statusChanged")){
                Object o = evt.getSource();
                GetRequest srcreq = (GetRequest)o;
                synchronized(VFtpManager.this){
                    getRequestRepository.save(srcreq);
                }
                return;
            }
        }
    };

    private ServerInfo[] getServerInfoByName(String name){
        if (name == null){
            return serverInfoRepository.getAllServerInfos();
        }
        ServerInfo serverInfo = serverInfoRepository.getServerInfoByName(name);
        if (serverInfo != null){
            return new ServerInfo[]{serverInfo};
        }
        return new ServerInfo[]{};
    }

    public String requestFileList(String server, String path, String directory){
        ServerInfo[] serverInfos = getServerInfoByName(server);
        if (serverInfos.length == 0){
            return null;
        }

        ListRequest req = new ListRequest();
        req.setPath(path);
        req.setDirectory(directory);
        for(ServerInfo serverInfo : serverInfos){
            req.add(serverInfo);
        }
        listRequestRepository.save(req);
        listRequestMap.put(req.getRequestNo(), req);
        req.addPropertyChangeListener(listRequestPropertyChangeListener);        
        req.execute();
        return req.getRequestNo();
    }

    public FileListStatus requestFileListStatus(String requestNo){
        ListRequest req = null;
        synchronized(this){
            req = listRequestMap.get(requestNo);
            if (req == null){
                req = listRequestRepository.getRequestById(requestNo);
            }
        }
        if (req != null){
            return req.convertToFileListStatus();
        }

        return null;
    }

	public String requestDownload(GetFileParam[] params, String compressFilename) {
        GetRequest req = new GetRequest();
        getRequestRepository.save(req);
        req.setCompressFilename(compressFilename);
        for (GetFileParam param : params){
            ServerInfo[] serverInfos = getServerInfoByName(param.getServer());
            for(ServerInfo serverInfo : serverInfos){
                req.add(serverInfo, param.getPath(), param.getFilename());
            }
        }
        getRequestRepository.save(req);
        getRequestMap.put(req.getRequestNo(), req);
        req.addPropertyChangeListener(getRequestPropertyChangeListener);
        req.execute();

		return req.getRequestNo();
    }
    
    public FileDownloadStatus requestDownloadStatus(String requestNo){
        GetRequest req = null;
        log.debug("requestDownloadStatus " + requestNo);
        synchronized(this){
            req = getRequestMap.get(requestNo);
            if (req == null){
                log.debug("find from map failed");
                req = getRequestRepository.getRequestById(requestNo);
            }
        }
        if (req != null){            
            return req.convertToFileDownloadStatus();
        }
        log.debug("request No : " + requestNo);
        return null;
    }

    public void deleteRequestDownload(String requestNo){
        GetRequest req = getRequestMap.get(requestNo);
        if (req != null){
            req.stop();
            synchronized(this){
                getRequestMap.remove(requestNo);    
                getRequestRepository.delete(requestNo);
            }
        }
    }

    public void deleteRequestFileList(String requestNo){
        ListRequest req = listRequestMap.get(requestNo);
        if (req != null){
            req.stop();
            synchronized(this){
                listRequestMap.remove(requestNo);
                listRequestRepository.delete(requestNo);
            }
        }
    }
}