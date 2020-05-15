package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SubRequestList {
    private Queue<SubRequest> ready = new LinkedList<>();
    private Queue<SubRequest> inprogress = new LinkedList<>();
    private ArrayList<SubRequest> completed = new ArrayList<>();

    public synchronized void addReady(SubRequest req){
        ready.add(req);
    }

    public synchronized SubRequest readyToProgress(){
        SubRequest req = ready.poll();
        if (req != null){
            inprogress.add(req);
        }
        return req;
    }

    public synchronized void progressToCompleted(SubRequest req){
        if (inprogress.contains(req)){
            inprogress.remove(req);
            completed.add(req);    
        }
    }
}