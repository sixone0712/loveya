package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.SubRequest;
import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetSubRequest extends SubRequest {
    private String serverName;
    private GetFileItemList fileList;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public GetSubRequest(String serverName, GetFileItemList fileList) {
        this.serverName = serverName;
        this.fileList = fileList;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void notifyDownload(FileItem item){
        propertyChangeSupport.firePropertyChange("downloaded", null, item);
    }

    private void processGetRequest(FTP ftp, GetFileItem item){
        String filename = item.getName();
        String destDir = item.getDestDir();
        
        InputStream is = null;
        FileOutputStream os = null;
        long total_file_bytes = 0;
        
        try {
            ftp.cd(item.getPath());
        } catch (SocketTimeoutException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (FTPException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            is = ftp.openFileStream(filename);
            File destDirFile = new File(new File(destDir), serverName);
            if (!destDirFile.exists()){
                destDirFile.mkdirs();
            }
            os = new FileOutputStream(new File(destDirFile, filename));
        
            byte[] b = new byte[4096];
            int readed_bytes = 0;
            do {
                readed_bytes = is.read(b);
                if(readed_bytes > 0){
                    os.write(b, 0, readed_bytes);
                    total_file_bytes += readed_bytes;
                }
            } while (readed_bytes > 0);
            
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (os != null){
            try {
                os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (is != null){
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        fileList.progressToCompleted(item);

        FileItem fileItem = new FileItem();
        fileItem.setFilename(item.getName());
        fileItem.setPath(item.getPath());
        fileItem.setServer(serverName);
        fileItem.setFilesize(total_file_bytes);

        notifyDownload(fileItem);
    }

    @Override
    public void processRequest(FTP ftp) {
        GetFileItem item = null;

        item = fileList.readyToProgress();
        while (item != null){
            processGetRequest(ftp, item);
            item = fileList.readyToProgress();
        }
    }
}