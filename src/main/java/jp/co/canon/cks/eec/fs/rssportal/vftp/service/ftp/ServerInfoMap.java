package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import java.util.HashMap;
import java.util.Map;

public class ServerInfoMap {
    private Map<String, ServerInfo> serverInfoMap = new HashMap<>();

    public ServerInfoMap(){
    }

    public boolean exists(String name){
        return serverInfoMap.containsKey(name);
    }

    public void add(String name, ServerInfo info){
        if (!exists(name)){
            serverInfoMap.put(name, info);
        }
    }

    public ServerInfo getServerInfo(String name){
        return serverInfoMap.get(name);
    }
}