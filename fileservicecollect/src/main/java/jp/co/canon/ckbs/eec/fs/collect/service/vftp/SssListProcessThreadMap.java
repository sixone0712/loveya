package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import java.util.HashMap;
import java.util.Map;

public class SssListProcessThreadMap {
    Map<String, SssListProcessThread> threadMap = new HashMap<>();

    public void put(String requestNo, SssListProcessThread thread){
        synchronized (threadMap){
            threadMap.put(requestNo, thread);
        }
    }

    public SssListProcessThread get(String requestNo){
        synchronized (threadMap){
            return threadMap.get(requestNo);
        }
    }

    public void remove(String requestNo){
        synchronized (threadMap){
            threadMap.remove(requestNo);
        }
    }
}
