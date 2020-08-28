package jp.co.canon.ckbs.eec.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileInfoQueue {
    BlockingQueue<FileInfo> fileQueue = new LinkedBlockingQueue<>();

    public void push(FileInfo info){
        synchronized (this) {
            fileQueue.add(info);
        }
    }

    public FileInfo poll(){
        synchronized (this) {
            return fileQueue.poll();
        }
    }

    public int size(){
        synchronized (this){
            return fileQueue.size();
        }
    }
}
