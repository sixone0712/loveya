package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import java.util.HashMap;
import java.util.Map;

public class StringToOtherTypeMap<T>{
    Map<String, T> map = new HashMap<>();

    public void remove(String key){
        synchronized (map){
            map.remove(key);
        }
    }

    public T get(String key){
        synchronized (map){
            return map.get(key);
        }
    }

    public void put(String key, T value){
        synchronized (map){
            map.put(key, value);
        }
    }

    public T[] getValues(){
        synchronized (map){
            return (T[]) map.values().toArray();
        }
    }
}
