package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CompletedSssListRequestQueue {
    BlockingQueue<VFtpSssListRequest> completedRequestQueue = new LinkedBlockingDeque<>();

    public VFtpSssListRequest get(){
        synchronized (completedRequestQueue){
            return completedRequestQueue.peek();
        }
    }

    public void pop(){
        synchronized (completedRequestQueue){
            completedRequestQueue.remove();
        }
    }

    public void add(VFtpSssListRequest request){
        synchronized (completedRequestQueue){
            completedRequestQueue.add(request);
        }
    }
}
