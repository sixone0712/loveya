package jp.co.canon.cks.eec.fs.rssportal.vo;


import java.sql.Timestamp;

public class CollectPlanVo {

    private int id;
    private String tool;
    private String logType;
    private Timestamp created;
    private String description;
    private int collectionType;
    private Timestamp lastCollect;
    private long interval;
    private Timestamp start;
    private Timestamp end;
    private int owner;
    private Timestamp nextAction;
    private boolean expired;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("collect-plan [id=").append(id);
        sb.append(" expired=").append(expired);
        sb.append(" interval=").append(interval);
        sb.append(" lastCollect=").append(lastCollect==null?"":lastCollect.toString());
        sb.append(" nextAction=").append(nextAction.toString());
        sb.append("]");
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(int collectionType) {
        this.collectionType = collectionType;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getLastCollect() {
        return lastCollect;
    }

    public void setLastCollect(Timestamp lastCollect) {
        this.lastCollect = lastCollect;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Timestamp getNextAction() {
        return nextAction;
    }

    public void setNextAction(Timestamp nextAction) {
        this.nextAction = nextAction;
    }
}
