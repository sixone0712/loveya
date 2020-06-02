package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSLogInfoBean;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSToolInfo;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlanControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PlanController planController;
    private final FileServiceController fileServiceController;
    private final DownloadListService downloadListService;
    private final CollectPlanService collectPlanService;

    @Autowired
    public PlanControllerTest(PlanController planController,
                              FileServiceController fileServiceController,
                              DownloadListService downloadListService,
                              CollectPlanService collectPlanService) {
        this.planController = planController;
        this.fileServiceController = fileServiceController;
        this.downloadListService = downloadListService;
        this.collectPlanService = collectPlanService;
    }

    @Test
    void addPlan() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/add");

        // generate parameters
        Map<String, Object> param = createAddPlanRequestBody();

        // add a plan
        ResponseEntity<Integer> resp = planController.addPlan(request, param);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        int planId = resp.getBody();
        assertNotEquals(planId, -1);

        // delete the plan
        request.setServletPath("/rss/rest/plan//delete");
        assertEquals(planController.deletePlan(request, planId).getStatusCode(), HttpStatus.OK);
    }

    @Test
    void listPlan() throws Exception {
        int[] planIds = new int[3];
        // add plans
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/add");
        Map<String, Object> param = createAddPlanRequestBody();
        for(int i=0; i<planIds.length; ++i) {
            ResponseEntity<Integer> resp = planController.addPlan(request, param);
            assertEquals(resp.getStatusCode(), HttpStatus.OK);
            planIds[i] = resp.getBody();
            assertNotEquals(planIds[i], -1);
        }

        // get a plan list
        ResponseEntity<List<CollectPlanVo>> resp = planController.listPlan(request, param);  // no need to think of parameters
        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        assertTrue(resp.getBody().size()>=3);

        collectPlanService.scheduleAllPlans();
        assertFalse(collectPlanService.stopPlan(-1));
        assertFalse(collectPlanService.restartPlan(-1));
        collectPlanService.deletePlan(-1);

        // delete the plans
        request.setServletPath("/rss/rest/plan//delete");
        for(int i=0; i<planIds.length; ++i) {
            assertTrue(collectPlanService.stopPlan(planIds[i]));
            assertTrue(collectPlanService.stopPlan(planIds[i]));
            assertTrue(collectPlanService.restartPlan(planIds[i]));
            assertEquals(planController.deletePlan(request, planIds[i]).getStatusCode(), HttpStatus.OK);
        }
    }

    @Test
    @Timeout(300)
    void deletePlan() throws Exception {
        // deletePlan() method will be tested many place in this file.
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setServletPath("/rss/rest/plan/delete");
        assertEquals(planController.deletePlan(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void download() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath("/rss/rest/plan/download");
        assertEquals(planController.download(request, response, -1).getStatusCode(), HttpStatus.NOT_FOUND);

        // get rid of injected session and replace it dummy which has username in its context
        MockHttpSession session = new MockHttpSession();
        SessionContext sessionContext = new SessionContext();
        sessionContext.setAuthorized(true);
        UserVo user = new UserVo();
        user.setUsername("user");
        sessionContext.setUser(user);
        session.setAttribute("context", sessionContext);

        // set dummy session to create file name.
        Field sessionField = PlanController.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(planController, session);

        // generate parameters
        Map<String, Object> param = createAddPlanRequestBody();

        // add a plan
        request.setServletPath("/rss/rest/plan/add");
        ResponseEntity<Integer> resp = planController.addPlan(request, param);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        int planId = resp.getBody();
        assertNotEquals(planId, -1);

        // wait the first collecting done
        int downloadId;
        while(true) {
            List<DownloadListVo> list = downloadListService.getList(planId);
            assertNotNull(list);
            if (list.size() > 0) {
                DownloadListVo item = list.get(0);
                downloadId = item.getId();
                assertNotEquals(downloadId, 0);
                assertNotNull(item.getPath());
                break;
            }
            Thread.sleep(1000);
        }

        request.setServletPath("/rss/rest/plan/download");
        assertEquals(planController.download(request, response, downloadId).getStatusCode(), HttpStatus.OK);

        request.setServletPath("/rss/rest/plan/delete");
        assertEquals(planController.deletePlan(request, planId).getStatusCode(), HttpStatus.OK);
    }

    @Test
    void modify() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/add");

        // generate parameters
        Map<String, Object> param = createAddPlanRequestBody();

        // add a plan
        ResponseEntity<Integer> addPlanResponse = planController.addPlan(request, param);
        assertEquals(addPlanResponse.getStatusCode(), HttpStatus.OK);
        int planId = addPlanResponse.getBody();
        assertNotEquals(planId, -1);

        // get a plan list
        ResponseEntity<List<CollectPlanVo>> listResponse = planController.listPlan(request, param);
        assertEquals(listResponse.getStatusCode(), HttpStatus.OK);
        CollectPlanVo plan = null;
        for(CollectPlanVo p: listResponse.getBody()) {
            if(p.getId()==planId) {
                plan = p;
                break;
            }
        }
        assertNotNull(plan);

        // pause, restart and then pause again
        request.setServletPath("/rss/rest/plan/stop");
        planController.stopPlan(request, planId);

        // when the plan is on work, it might not be able to stop immediately.
        Thread.sleep(3000);
        request.setServletPath("/rss/rest/plan/restart");
        planController.restartPlan(request, planId);
        Thread.sleep(3000);
        request.setServletPath("/rss/rest/plan/stop");
        planController.stopPlan(request, planId);
        Thread.sleep(3000);

        param.put("planId", "this is modified plan");
        request.setServletPath("/rss/rest/plan/modify");
        ResponseEntity<Integer> modifyResponse = planController.modify(request, planId, param);
        assertEquals(modifyResponse.getStatusCode(), HttpStatus.OK);
        planId = modifyResponse.getBody();
        assertNotEquals(planId, -1);

        // delete the plan
        request.setServletPath("/rss/rest/plan//delete");
        assertEquals(planController.deletePlan(request, planId).getStatusCode(), HttpStatus.OK);
    }

    @Test
    void stopPlan() {
        // stopPlan() method will be tested another test case in this file.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/stop");
        assertEquals(planController.stopPlan(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void restartPlan() {
        // restartPlan() method will be tested another test case in this file.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/restart");
        assertEquals(planController.restartPlan(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> createAddPlanRequestBody() throws Exception {
        // get the first tool info
        RSSToolInfo toolInfo = fileServiceController.createToolList()[0];
        RSSLogInfoBean[] logInfos = fileServiceController.createFileTypeList(toolInfo.getTargetname());

        Map<String, Object> param = new HashMap<>();
        param.put("planId", "if you can see this, it means someone has to debug it");
        param.put("tools", Arrays.asList(toolInfo.getTargetname()));
        param.put("structId", Arrays.asList(toolInfo.getStructId()));
        List<String> logTypes = new ArrayList<>();
        List<String> logNames = new ArrayList<>();
        for(RSSLogInfoBean logInfo: logInfos) {
            // select one log type for testing.
            if(logTypes.size()>1)
                break;
            logTypes.add(logInfo.getCode());
            logNames.add(logInfo.getLogName());
        }
        param.put("logTypes", logTypes);
        param.put("logNames", logNames);
        long cur = System.currentTimeMillis();
        long week = 7*24*3600000;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("collectStart", format.format(new Date(cur)));
        param.put("from", format.format(new Date(cur-week)));
        param.put("to", format.format(new Date(cur+week)));
        param.put("collectType", "cycle");
        param.put("interval", "3600000");
        param.put("description", "desc: if you can see this..");
        return param;
    }
}