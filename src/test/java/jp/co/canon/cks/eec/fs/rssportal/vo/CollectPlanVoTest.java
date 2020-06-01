package jp.co.canon.cks.eec.fs.rssportal.vo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectPlanVoTest {
    @Test
    public void test_getset(){
        CollectPlanVo planVo = new CollectPlanVo();

        planVo.setId(101);
        Assertions.assertEquals(101, planVo.getId());

        Assertions.assertNull(planVo.getCreated());

        planVo.setLastStatus("status11");
        Assertions.assertEquals("status11", planVo.getLastStatus());

        planVo.setPlanStatus(PlanStatus.halted);
        Assertions.assertEquals(PlanStatus.halted, planVo.getPlanStatus());

        planVo.setStatus("abcstatus");
        Assertions.assertEquals("abcstatus", planVo.getStatus());

        planVo.setDetail("detailstatus");
        Assertions.assertEquals("detailstatus", planVo.getDetail());

        planVo.setCollectTypeStr("collectAll");
        Assertions.assertEquals("collectAll", planVo.getCollectTypeStr());
    }
}