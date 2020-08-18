package jp.co.canon.cks.eec.fs.rssportal.vo;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter @Getter
public class CollectPlanVo implements Comparable<CollectPlanVo> {

    private int id;
    private String planType;
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
    private String command;
    private String directory;

    private String status;
    private String detail;
    private String collectTypeStr;

    public CollectPlanVo createCollectPlanResponse() {
        CollectPlanVo plan = new CollectPlanVo();
        plan.id = this.getId();
        plan.planType = this.getPlanType();
        plan.planName = this.getPlanName();
        plan.fab = this.getFab();
        plan.tool = this.getTool();
        plan.logType = this.getLogType();
        plan.logTypeStr = this.getLogTypeStr();
        plan.created = this.getCreated();
        plan.description = this.getDescription();
        plan.collectionType = this.getCollectionType();
        plan.lastCollect = this.getLastCollect();
        plan.interval = this.getInterval();
        plan.collectStart = this.getCollectStart();
        plan.start = this.getStart();
        plan.end = this.getEnd();
        plan.owner = this.getOwner();
        plan.nextAction = this.getNextAction();
        plan.lastPoint = this.getLastPoint();
        plan.stop = this.isStop();
        plan.lastStatus = this.getLastStatus();
        plan.planStatus = this.getPlanStatus();
        plan.command = this.getCommand();
        plan.directory = this.getDirectory();
        plan.status = this.getLastStatus().equals("halted")||this.getLastStatus().equals("completed")||this.isStop()?"stop":"running";
        plan.detail = this.getLastStatus();
        plan.collectTypeStr = this.getCollectTypeStr();
        return plan;
    }

    @Override
    public int compareTo(CollectPlanVo o) {
        return this.id-o.getId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("collect-plan [id=").append(id);
        sb.append(" status=").append(status);
        sb.append(" next=").append(nextAction==null?"":nextAction.toString());
        sb.append("]");
        return sb.toString();
    }
   
}
