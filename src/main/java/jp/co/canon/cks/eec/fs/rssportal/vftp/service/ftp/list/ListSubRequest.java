package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.SubRequest;
import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;
import lombok.Getter;

public class ListSubRequest extends SubRequest {
    private @Getter String serverName;
    private @Getter String path;
    private @Getter String directory;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public ListSubRequest(String serverName, String path, String directory){
        this.serverName = serverName;
        this.path = path;
        this.directory = directory;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void notifySuccess(List<FileItem> result){
        propertyChangeSupport.firePropertyChange("listresult", null, result);
    }

    private static String[] getToken(String lsLine){
        ArrayList<String> r = new ArrayList<>();
		int pos = 0;
		for (int i=0; i < lsLine.length() && r.size() < 8; i++) {
			if (lsLine.charAt(i) == ' ') {
				if (pos != i) {
					r.add(lsLine.substring(pos, i));
				}
				pos = i+1;
			}
		}
		
		if (pos < lsLine.length()) {
			String name = lsLine.substring(pos).trim();
			if (lsLine.startsWith("l")) {	// 属性がlのため
				int spos = name.indexOf("->");
				if (spos > 0) {
					name = name.substring(0, spos).trim();
				}
			}
			r.add(name);
		}
		return (String[])r.toArray(new String[0]);
    }

    private FileItem createFileItemFromLsLine(String lsLine){
        if (lsLine.startsWith("d")){
            return null;
        }
        String[] data = getToken(lsLine);
        if (data.length < 5){
            return null;
        }

        FileItem item = new FileItem();

        int index = data.length - 1;
        item.setServer(serverName);
        item.setPath(path + "/" + directory);
        item.setFilename(data[index]);

        item.setFilesize(Long.parseLong(data[index-4]));
        return item;
    }

    public void processRequest(FTP ftp) {
        try {
            ftp.cd(path);
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (directory != null){
            try {
                ftp.cd(directory);
            } catch (SocketTimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        List<String> result = null;
        
        try {
            result = ftp.ls("");
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (result != null){
            ArrayList<FileItem> list = new ArrayList<>();
            for (String line : result){
                FileItem item = createFileItemFromLsLine(line);
                list.add(item);
            }
            notifySuccess(list);
        }
    }
}