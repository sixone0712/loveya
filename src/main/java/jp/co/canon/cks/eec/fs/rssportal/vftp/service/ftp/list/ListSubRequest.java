package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FtpWorker;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.SubRequest;
import jp.co.canon.cks.eec.util.ftp.FTP;
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

    public void processRequest(FtpWorker worker, FTP ftp) throws Exception {
        List<String> result = null;
        try {
            ftp.cd(path);

            if (directory != null){
                ftp.cd(directory);
            }
            
            result = ftp.ls("");
        } catch (Exception e) {
            throw e;
        }
        ArrayList<FileItem> list = new ArrayList<>();
        for (String line : result){
            FileItem item = createFileItemFromLsLine(line);
            list.add(item);
        }
        notifySuccess(list);
    }
}