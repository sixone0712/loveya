package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import java.util.HashMap;
import java.util.Map;

public class StringToOtherTypeMap<T>{
    Map<String, T> requestMap = new HashMap<>();

    public void remove(String requestNo){
        synchronized (requestMap){
            requestMap.remove(requestNo);
        }
    }

    public T get(String requestNo){
        synchronized (requestMap){
            return requestMap.get(requestNo);
        }
    }

    public void put(String requestNo, T request){
        synchronized (requestMap){
            requestMap.put(requestNo, request);
        }
    }

    public T[] getValues(){
        synchronized (requestMap){
            return (T[]) requestMap.values().toArray();
        }
    }
}
