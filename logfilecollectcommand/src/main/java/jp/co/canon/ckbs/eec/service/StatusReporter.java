package jp.co.canon.ckbs.eec.service;

public class StatusReporter {
    public void println(String msg){
        synchronized (this){
            System.out.println(msg);
        }
    }

    public void reportStatus(String msg){
        this.println("STATUS:" + msg);
    }

    public void reportError(String msg){
        this.println("ERR:" + msg);
    }
}
