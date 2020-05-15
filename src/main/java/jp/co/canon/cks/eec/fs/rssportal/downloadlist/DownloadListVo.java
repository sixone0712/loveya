package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadListVo {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int id;
    private Timestamp created;
    private String status;
    private int planId;
    private String path;
    private String title;

    public DownloadListVo() {}

    public DownloadListVo(Timestamp created, String status, int planId, String path) {
        this.created = created;
        this.status = status;
        this.planId = planId;
        this.path = path;
        this.title = createTitle();
    }

    private String createTitle() {
        if(created==null)
            return null;
        return dateFormat.format(created.getTime());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
