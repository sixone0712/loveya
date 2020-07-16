package jp.co.canon.cks.eec.fs.rssportal.vo;


import java.sql.Timestamp;

public class CollectPlanVo {

    private int id;
    private String planName;
    private String fab;
    private String tool;
    private String logType;
    private String logTypeStr;
    private Timestamp created;
    private String description;
    private int collectionType;
    private Timestamp lastCollect;
    private long interval;
    private Timestamp collectStart;
    private Timestamp start;
    private Timestamp end;
    private int owner;
    private Timestamp nextAction;
    private Timestamp lastPoint;
    private boolean stop;
    private String lastStatus;
    private PlanStatus planStatus;

    private String status;
    private String detail;
    private String collectTypeStr;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("collect-plan [id=").append(id);
        sb.append(" stop=").append(stop);
        sb.append(" interval=").append(interval);
        sb.append(" lastCollect=").append(lastCollect==null?"":lastCollect.toString());
        sb.append(" nextAction=").append(nextAction==null?"":nextAction.toString());
        sb.append("]");
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getFab() {
        return fab;
    }

    public void setFab(String fab) {
        this.fab = fab;
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

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
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

    public Timestamp getCollectStart() {
        return collectStart;
    }

    public void setCollectStart(Timestamp collectStart) {
        this.collectStart = collectStart;
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

    public Timestamp getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(Timestamp lastPoint) {
        this.lastPoint = lastPoint;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public PlanStatus getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(PlanStatus planStatus) {
        this.planStatus = planStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCollectTypeStr() {
        return collectTypeStr;
    }

    public void setCollectTypeStr(String collectTypeStr) {
        this.collectTypeStr = collectTypeStr;
    }

    public String getLogTypeStr() {
        return logTypeStr;
    }

    public void setLogTypeStr(String logTypeStr) {
        this.logTypeStr = logTypeStr;
    }
}
