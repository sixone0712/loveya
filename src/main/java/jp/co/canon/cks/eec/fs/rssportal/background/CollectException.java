package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;

public class CollectException extends Exception {

    private final CollectPlanVo plan;

    public CollectException(CollectPlanVo plan) {
        this.plan = plan;
    }

    public CollectException(CollectPlanVo plan, String message) {
        super(message);
        this.plan = plan;
    }

    @Override
    public String getMessage() {
        return String.format("#%d:%s(%s) %s", plan.getId(), plan.getPlanName(), plan.getPlanType(),
                super.getMessage());
    }
}
