package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollectRunner extends Thread {

    private Log log = LogFactory.getLog(getClass());
    private final CollectPlanVo plan;

    private CollectRunner(CollectPlanVo plan) {
        this.plan = plan;
    }

    @Override
    public void run() {
        
    }
}
