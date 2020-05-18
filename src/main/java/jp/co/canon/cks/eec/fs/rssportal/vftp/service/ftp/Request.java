package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Request implements PropertyChangeListener {
    private Map<String, SubRequestList> readyQueueMap = new HashMap<>();
    private ServerInfoMap serverInfoMap = new ServerInfoMap();
    private ArrayList<FtpWorker> workerList = new ArrayList<>();
    private ArrayList<String> errorList = new ArrayList<>();

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private SubRequestList getListForServer(String name) {
        SubRequestList list = readyQueueMap.get(name);
        if (list == null) {
            list = new SubRequestList();
            readyQueueMap.put(name, list);
        }
        return list;
    }

    public String[] getErrorList(){
        return errorList.toArray(new String[0]);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addSubRequest(ServerInfo serverInfo, SubRequest req) {
        String serverName = serverInfo.getName();
        serverInfoMap.add(serverName, serverInfo);
        SubRequestList list = getListForServer(serverName);
        list.addReady(req);
    }

    public void execute(boolean waitToComplete) {
        for (Entry<String, SubRequestList> x : readyQueueMap.entrySet()) {
            SubRequestList list = x.getValue();
            String serverName = x.getKey();
            ServerInfo serverInfo = serverInfoMap.getServerInfo(serverName);

            FtpWorker ftpWorker = new FtpWorker(list, serverInfo);
            ftpWorker.addPropertyChangeListener(this);
            workerList.add(ftpWorker);
            ftpWorker.start();
        }
        if (waitToComplete) {
            for (FtpWorker worker : workerList) {
                try {
                    worker.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void execute() {
        this.execute(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {        
        if (evt.getPropertyName().equals("workerStart")){
            log.debug("Worker Start : " + evt.getSource());
            return;
        }
        if (evt.getPropertyName().equals("workerEnd")){
            log.debug("Worker End : " + evt.getSource());
            synchronized(this){
                workerList.remove(evt.getSource());
                if (workerList.size() == 0){
                    propertyChangeSupport.firePropertyChange("completed", null, null);
                }
            }
            return;
        }
        if (evt.getPropertyName().equals("error")){
            String errorMessage = (String)evt.getNewValue();
            synchronized(this){
                errorList.add(errorMessage);
                for(FtpWorker worker : workerList){
                    worker.stopWorker();
                }
            }
            return;
        }

    }
}