package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import java.util.LinkedList;
import java.util.Queue;

public class GetFileItemList {
    private Queue<GetFileItem> ready = new LinkedList<>();
    private Queue<GetFileItem> inprogress = new LinkedList<>();
    private Queue<GetFileItem> completed = new LinkedList<>();

    public synchronized void addReady(GetFileItem item){
        ready.add(item);
    }

    public synchronized GetFileItem readyToProgress(){
        GetFileItem item = ready.poll();
        if (item != null){
            inprogress.add(item);
        }
        return item;
    }

    public synchronized void progressToCompleted(GetFileItem item){
        if (inprogress.contains(item)){
            inprogress.remove(item);
            completed.add(item);    
        }
    }
}