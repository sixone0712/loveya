package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;

import java.util.HashMap;
import java.util.Map;

public class SssListRequestMap {
    Map<String, VFtpSssListRequest> requestMap = new HashMap<>();

    public void remove(String requestNo){
        synchronized (requestMap){
            requestMap.remove(requestNo);
        }
    }

    public VFtpSssListRequest get(String requestNo){
        synchronized (requestMap){
            return requestMap.get(requestNo);
        }
    }

    public void put(String requestNo, VFtpSssListRequest request){
        synchronized (requestMap){
            requestMap.put(requestNo, request);
        }
    }
}
