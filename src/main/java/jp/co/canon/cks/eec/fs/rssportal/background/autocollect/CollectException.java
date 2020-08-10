package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import lombok.Getter;

public class CollectException extends Exception {

    private final CollectPlanVo plan;

    @Getter
    private final boolean error;

    public CollectException(CollectPlanVo plan, boolean error) {
        this.plan = plan;
        this.error = error;

    }
    public CollectException(CollectPlanVo plan) {
        this.plan = plan;
        this.error = true;
    }

    public CollectException(CollectPlanVo plan, String message) {
        super(message);
        this.plan = plan;
        this.error = true;
    }

    @Override
    public String getMessage() {
        return String.format("#%d:%s(%s) %s", plan.getId(), plan.getPlanName(), plan.getPlanType(),
                error?super.getMessage():"normal exit");
    }


}
