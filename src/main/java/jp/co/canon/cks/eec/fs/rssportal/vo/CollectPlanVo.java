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

    @Override
    public int compareTo(CollectPlanVo o) {
        return this.id-o.getId();
    }

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
   
}
