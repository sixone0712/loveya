package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AutoCollector extends Thread {

    private final CollectPlanService service;
    private List<CollectPlanVo> plans;
    private CollectPlanVo nextPlan;
    private boolean planLocked = true;
    private boolean planUpdated = true;

    @Autowired
    private AutoCollector(CollectPlanService service) {
        if(service==null) {
            throw new BeanInitializationException("service injection failed");
        }
        this.service = service;
        service.addNotifier(notifyUpdate);
        service.scheduleAllPlans();

        this.start();
    }

    @Override
    public void run() {
        log.info("AutoCollector start");

        try {
            while(true) {
                CollectPlanVo plan = getNext();
                if(plan==null) {
                    sleep(5000);
                    continue;
                }

                Date cur = new Date(System.currentTimeMillis());
                if(plan.getNextAction().before(cur)) {
                    collect(plan);
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("sleep exception occurs");
            e.printStackTrace();
        }
    }

    private boolean collect(CollectPlanVo plan) throws InterruptedException {

        log.info("collect: "+plan.toString());
        sleep(3000); // testing

        service.updateLastCollect(plan);
        service.schedulePlan(plan);
        return false;
    }

    private CollectPlanVo getNext() {
        if(planUpdated) {
            nextPlan = service.getNextPlan();
            if(nextPlan!=null) {
                log.info("nextPlan=" + nextPlan.getDescription() + " nextaction=" + nextPlan.getNextAction().toString());
            }
            if(nextPlan!=null) {
                planUpdated = false;
            }
        }
        return nextPlan;
    }

    private Runnable notifyUpdate = ()->{
        System.out.println("notifyUpdate()");
        planUpdated = true;
    };


    private final Log log = LogFactory.getLog(getClass());
}
