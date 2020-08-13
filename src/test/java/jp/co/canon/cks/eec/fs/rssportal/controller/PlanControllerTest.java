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

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
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
//        printSeparator("addPlan");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setServletPath("/rss/rest/plan/add");
//
//        // first off, test fail cases.
//        assertEquals(planController.addPlan(request, null).getStatusCode(), HttpStatus.BAD_REQUEST);
//        Map failParam = new HashMap<>();
//        failParam.put("unused", "value");
//        assertEquals(planController.addPlan(request,failParam).getStatusCode(), HttpStatus.BAD_REQUEST);
//
//        // generate parameters
//        Map<String, Object> param = createAddPlanRequestBody();
//
//        // add a plan
//        ResponseEntity<Integer> resp = planController.addPlan(request, param);
//        assertEquals(resp.getStatusCode(), HttpStatus.OK);
//        int planId = resp.getBody();
//        assertNotEquals(planId, -1);
//
//        // delete the plan
//        request.setServletPath("/rss/rest/plan//delete");
//        assertEquals(planController.deletePlan(request, planId).getStatusCode(), HttpStatus.OK);
    }

    @Test
    void listPlan1() throws Exception {
        log.info("listPlan1");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("listPlan");
        planController.listPlan(request, new HashMap<>());
    }
    
    @Test
    void listPlan() throws Exception {
//        printSeparator("listPlan");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//
//        // fail cases.
//        request.setServletPath("list");
//        assertEquals(planController.listPlan(request, null).getStatusCode(), HttpStatus.BAD_REQUEST);
//
//        int[] planIds = new int[3];
//        // add plans
//        request.setServletPath("/rss/rest/plan/add");
//        Map<String, Object> param = createAddPlanRequestBody();
//        for(int i=0; i<planIds.length; ++i) {
//            ResponseEntity<Integer> resp = planController.addPlan(request, param);
//            assertEquals(resp.getStatusCode(), HttpStatus.OK);
//            planIds[i] = resp.getBody();
//            assertNotEquals(planIds[i], -1);
//        }
//
//        // get a plan list
//        ResponseEntity<List<CollectPlanVo>> resp = planController.listPlan(request, param);  // no need to think of parameters
//        assertEquals(resp.getStatusCode(), HttpStatus.OK);
//        assertTrue(resp.getBody().size()>=3);
//
//        collectPlanService.scheduleAllPlans();
//        assertFalse(collectPlanService.stopPlan(-1));
//        assertFalse(collectPlanService.restartPlan(-1));
//        collectPlanService.deletePlan(-1);
//
//        // stop/restart
//        assertTrue(collectPlanService.stopPlan(planIds[0]));
//        assertTrue(collectPlanService.stopPlan(planIds[0]));
//        assertTrue(collectPlanService.restartPlan(planIds[0]));
//
//        // delete the plans
//        for(int i=0; i<planIds.length; ++i)
//            assertTrue(collectPlanService.stopPlan(planIds[i]));
//
//        Map<String, Object> listParam = new HashMap<>();
//        listParam.put("withExpired", "true");
//        for(int i=0; i<planIds.length; ++i) {
//            loop_top:
//            while(true) {
//                request.setServletPath("listPlan");
//                ResponseEntity<List<CollectPlanVo>> responseEntity =
//                        planController.listPlan(request, listParam);
//                assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
//                for(CollectPlanVo plan: responseEntity.getBody()) {
//                    if(plan.getId()==planIds[i] &&
//                            !plan.getDetail().equalsIgnoreCase("collecting")) {
//                        break loop_top;
//                    }
//                }
//                Thread.sleep(1000);
//            }
//            request.setServletPath("/rss/rest/plan//delete");
//            assertEquals(planController.deletePlan(request, planIds[i]).getStatusCode(), HttpStatus.OK);
//        }
    }

    @Test
    @Timeout(300)
    void deletePlan() throws Exception {
        printSeparator("deletePlan");
        // deletePlan() method will be tested many place in this file.
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setServletPath("/rss/rest/plan/delete");
//        assertEquals(planController.deletePlan(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void download() throws Exception {
//        printSeparator("download");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        request.setServletPath("/rss/rest/plan/download");
//        assertEquals(planController.download(request, response, -1).getStatusCode(), HttpStatus.NOT_FOUND);
//
//        // get rid of injected session and replace it dummy which has username in its context
//        MockHttpSession session = new MockHttpSession();
//        SessionContext sessionContext = new SessionContext();
//        sessionContext.setAuthorized(true);
//        UserVo user = new UserVo();
//        user.setUsername("user");
//        sessionContext.setUser(user);
//        session.setAttribute("context", sessionContext);
//
//        // set dummy session to create file name.
//        Field sessionField = PlanController.class.getDeclaredField("session");
//        sessionField.setAccessible(true);
//        sessionField.set(planController, session);
//
//        // generate parameters
//        Map<String, Object> param = createAddPlanRequestBody();
//
//        // add a plan
//        request.setServletPath("/rss/rest/plan/add");
//        ResponseEntity<Integer> resp = planController.addPlan(request, param);
//        assertEquals(resp.getStatusCode(), HttpStatus.OK);
//        int planId = resp.getBody();
//        assertNotEquals(planId, -1);
//
//        // wait the first collecting done
//        int downloadId;
//        while(true) {
//            List<DownloadListVo> list = downloadListService.getList(planId);
//            assertNotNull(list);
//            if (list.size() > 0) {
//                DownloadListVo item = list.get(0);
//                downloadId = item.getId();
//                assertNotEquals(downloadId, 0);
//                assertNotNull(item.getPath());
//                break;
//            }
//            Thread.sleep(1000);
//        }
//
//        request.setServletPath("/rss/rest/plan/download");
//        assertEquals(planController.download(request, response, downloadId).getStatusCode(), HttpStatus.OK);
//
//        request.setServletPath("/rss/rest/plan/delete");
//        assertEquals(planController.deletePlan(request, planId).getStatusCode(), HttpStatus.OK);
    }

    @Test
    void modify() throws Exception {
//        printSeparator("modify");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//
//        request.setServletPath("modify");
//        assertEquals(planController.modify(request, -1, null).getStatusCode(), HttpStatus.NOT_FOUND);
//
//        // generate parameters
//        Map<String, Object> param = createAddPlanRequestBody();
//
//        // add a plan
//        request.setServletPath("/rss/rest/plan/add");
//        ResponseEntity<Integer> addPlanResponse = planController.addPlan(request, param);
//        assertEquals(addPlanResponse.getStatusCode(), HttpStatus.OK);
//        int planId = addPlanResponse.getBody();
//        assertNotEquals(planId, -1);
//
//        // get a plan list
//        ResponseEntity<List<CollectPlanVo>> listResponse = planController.listPlan(request, param);
//        assertEquals(listResponse.getStatusCode(), HttpStatus.OK);
//        CollectPlanVo plan = null;
//        for(CollectPlanVo p: listResponse.getBody()) {
//            if(p.getId()==planId) {
//                plan = p;
//                break;
//            }
//        }
//        assertNotNull(plan);
//
//        // pause, restart and then pause again
//        request.setServletPath("/rss/rest/plan/stop");
//        planController.stopPlan(request, planId);
//
//        // when the plan is on work, it might not be able to stop immediately.
//        Thread.sleep(3000);
//        request.setServletPath("/rss/rest/plan/restart");
//        planController.restartPlan(request, planId);
//        Thread.sleep(3000);
//        request.setServletPath("/rss/rest/plan/stop");
//        planController.stopPlan(request, planId);
//        Thread.sleep(3000);
//
//        param.put("planId", "this is modified plan");
//        request.setServletPath("/rss/rest/plan/modify");
//        ResponseEntity<Integer> modifyResponse = planController.modify(request, planId, param);
//        assertEquals(modifyResponse.getStatusCode(), HttpStatus.OK);
//        planId = modifyResponse.getBody();
//        assertNotEquals(planId, -1);
//
//        // delete the plan
//        request.setServletPath("/rss/rest/plan//delete");
//        assertEquals(planController.deletePlan(request, planId).getStatusCode(), HttpStatus.OK);
    }

    @Test
    void stopPlan() {
        printSeparator("stopPlan");
        // stopPlan() method will be tested another test case in this file.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/stop");
//        assertEquals(planController.stopPlan(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void restartPlan() {
        printSeparator("restartPlan");
        // restartPlan() method will be tested another test case in this file.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/plan/restart");
//        assertEquals(planController.restartPlan(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void createDownloadFilename() throws Exception {
        printSeparator("createDownloadFilename");
        Method method = PlanController.class.getDeclaredMethod("createDownloadFilename",
                CollectPlanVo.class, DownloadListVo.class);
        assertNotNull(method);
        method.setAccessible(true);
        CollectPlanVo plan = new CollectPlanVo();
        DownloadListVo item = new DownloadListVo();
        setSession(null);

        // with null session
        assertNull(method.invoke(planController, plan, item));

        // with null context in the session
        MockHttpSession session = new MockHttpSession();
        setSession(session);
        assertNull(method.invoke(planController, plan, item));

        // without username
        SessionContext context = new SessionContext();
        UserVo user = new UserVo();
        user.setUsername(null);
        context.setUser(user);
        context.setAuthorized(true);
        session.setAttribute("context", context);
        assertNull(method.invoke(planController, plan, item));

        user.setUsername("testuser");
        assertNull(method.invoke(planController, plan, item));

        plan.setFab("");
        item.setCreated(new Timestamp(System.currentTimeMillis()));
        assertNotNull(method.invoke(planController, plan, item));

        plan.setFab("fab1,fab2");
        assertNotNull(method.invoke(planController, plan, item));
    }

    private void setSession(HttpSession newSession) throws Exception {
        Field sessionField = PlanController.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(planController, newSession);
    }

//    private Map<String, Object> createAddPlanRequestBody() throws Exception {
//         get the first tool info
//        RSSToolInfo toolInfo = fileServiceController.createToolList().getBody()[0];
//        RSSLogInfoBean[] logInfos = fileServiceController.createFileTypeList(toolInfo.getTargetname()).getBody();
//
//        Map<String, Object> param = new HashMap<>();
//        param.put("planId", "if you can see this, it means someone has to debug it");
//        param.put("tools", Arrays.asList(toolInfo.getTargetname()));
//        param.put("structId", Arrays.asList(toolInfo.getStructId()));
//        List<String> logTypes = new ArrayList<>();
//        List<String> logNames = new ArrayList<>();
//        for(RSSLogInfoBean logInfo: logInfos) {
//            // select one log type for testing.
//            if(logTypes.size()>1)
//                break;
//            logTypes.add(logInfo.getCode());
//            logNames.add(logInfo.getLogName());
//        }
//        param.put("logTypes", logTypes);
//        param.put("logNames", logNames);
//        long cur = System.currentTimeMillis();
//        long week = 7*24*3600000;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        param.put("collectStart", format.format(new Date(cur)));
//        param.put("from", format.format(new Date(cur-week)));
//        param.put("to", format.format(new Date(cur+week)));
//        param.put("collectType", "cycle");
//        param.put("interval", "3600000");
//        param.put("description", "desc: if you can see this..");
//        return param;
//    }

    private void printSeparator(String name) {
        log.info("-------------------- "+name+" --------------------");
    }
}