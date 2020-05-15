package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.SocketTimeoutException;

import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;

enum FtpWorkerState {
    NONE, 
    CONNECTED, 
    LOGIN_COMPLETED, 
    PROCESSING,
    CONNECT_FAILED,
    LOGIN_FAILED,
    PROCESS_COMPLETED,
}

public class FtpWorker extends Thread {
    private SubRequestList list;
    private ServerInfo serverInfo;
    private FtpWorkerState state = FtpWorkerState.NONE;
    private boolean stop = false;

    private FTP ftp;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public FtpWorker(SubRequestList list, ServerInfo serverInfo) {
        this.list = list;
        this.serverInfo = serverInfo;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void setWorkerState(FtpWorkerState state) {
        this.state = state;
    }

    public FtpWorkerState getWorkerState() {
        return this.state;
    }

    public void stopWorker(){
        this.stop = true;
    }

    private void connect() {
        ftp = new FTP(serverInfo.getHost(), serverInfo.getPort());

        try {
            ftp.connect();
            setWorkerState(FtpWorkerState.CONNECTED);
        } catch (FTPException e) {
            e.printStackTrace();
            this.setWorkerState(FtpWorkerState.CONNECT_FAILED);
            propertyChangeSupport.firePropertyChange("error", null, "connection failed");
            ftp = null;
            return;
        }

        try {
            ftp.login(serverInfo.getUsername(), serverInfo.getPassword());
            setWorkerState(FtpWorkerState.LOGIN_COMPLETED);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            this.setWorkerState(FtpWorkerState.LOGIN_FAILED);
            propertyChangeSupport.firePropertyChange("error", null, "login failed(timeout)");
            ftp.close();
            ftp = null;
            return;
        } catch (FTPException e) {
            e.printStackTrace();
            this.setWorkerState(FtpWorkerState.LOGIN_FAILED);
            propertyChangeSupport.firePropertyChange("error", null, "login failed(...)");            
            ftp.close();
            ftp = null;
            return;
        }
    }

    private void disconnect(){
        if (ftp != null){
            ftp.close();
            ftp = null;
        }
    }

    private void processRequests(){
        SubRequest req = list.readyToProgress();
        while(req != null && this.stop == false){
            
            req.processRequest(ftp);
            list.progressToCompleted(req);

            req = list.readyToProgress();
        }
    }

    @Override
    public void run(){
        propertyChangeSupport.firePropertyChange("workerStart", null, null);
        connect();
        if (getWorkerState() == FtpWorkerState.LOGIN_COMPLETED)
        {
            setWorkerState(FtpWorkerState.PROCESSING);

            processRequests();

            setWorkerState(FtpWorkerState.PROCESS_COMPLETED);
        }
        disconnect();
        propertyChangeSupport.firePropertyChange("workerEnd", null, null);
    }
}