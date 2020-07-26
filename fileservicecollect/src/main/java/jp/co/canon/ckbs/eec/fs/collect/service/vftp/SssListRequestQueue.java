package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SssListRequestQueue {
    BlockingQueue<VFtpSssListRequest> requestQueue = new LinkedBlockingDeque<>();

    public VFtpSssListRequest get(){
        synchronized (requestQueue){
            return requestQueue.peek();
        }
    }

    public void pop(){
        synchronized (requestQueue){
            requestQueue.remove();
        }
    }

    public void add(VFtpSssListRequest request){
        synchronized (requestQueue){
            requestQueue.add(request);
        }
    }
}
