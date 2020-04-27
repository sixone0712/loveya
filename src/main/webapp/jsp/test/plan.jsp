<%@ page language="java" contentType="text/html; charset=utf-8"%>
<script src="//code.jquery.com/jquery-3.2.1.min.js"></script>

<script>
    function requestAddPlan() {
        let data = {
            tools: [
                "EQVM87", "EQVM88"
            ],
            logTypes: [
                "001", "002"
            ],
            collectStart: "2020-04-25 12:34:45",
            from: "2020-04-25 12:34:45",
            to: "2020-04-28 12:34:45",
            collectType: "cycle",
            interval: "120000",
            description: "test1"
        };

        $.ajax({
            url:'/plan/add',
            type:"post",
            data: JSON.stringify(data),
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
            },
            error: () => console.log('Error')
        });
    }

    function requestAddPlanWithoutParam() {
        $.ajax({
            url:'/plan/add',
            type:"post",
            //data: JSON.stringify(data),
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
            },
            error: resp => {
                console.log('Error');
                console.log(resp);
            }
        });
    }

    function requestRestAddPlan() {
        let data = {
            tools: [
                "eeee", "aaa", "41424"
            ],
            logTypes: [
                "type1", "type2"
            ],
            from: "2020-04-23 12:34:45",
            to: "2020-04-24 12:34:45",
            collectType: "cycle",
            interval: "3600000",
            description: "des------"
        };

        $.ajax({
            url:'/r/plan/add',
            type:"post",
            data: 'tools:[\"a\", \"b\", \"c\"]',
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
            },
            error: () => console.log('Error')
        });

    }

    function requestListPlan() {

        $.ajax({
            url:'/plan/list?withExpired=yes',
            type:"get",
            // data: JSON.stringify(data),
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
            },
            error: () => console.log('Error')
        });
    }

    function requestDelete() {
        let id = $('#deleteId').val();
        console.log('deleteId='+id);
        if(id==undefined || id=='') {
            console.log('invalid id');
            return;
        }
        $.ajax({
            url:'/plan/delete?id='+id,
            type:"get",
            // data: JSON.stringify(data),
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
            },
            error: () => console.log('Error')
        });
    }

    function requestDownload() {
        let id = $('#downloadId').val();
        console.log('downloadId='+id);
        if(id==undefined || id=='') {
            console.log('invalid id');
            return;
        }
        $.ajax({
            url:'/plan/download?id='+id,
            type:"get",
            // data: JSON.stringify(data),
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
            },
            error: () => console.log('Error')
        });
    }
</script>

정기 수집 요청 <br>

<input type="button" value="add" onclick="requestAddPlan();" />
<input type="button" value="Error" onclick="requestAddPlanWithoutParam();" />

<br>
레퍼런스 코드는 이 페이지의 js를 참고하여 주시기 바랍니다.
<hr>

등록된 플랜 확인<br>

<input type="button" value="list" onclick="requestListPlan();" />
<br><hr>

플랜 삭제<br>
<input type="text" id="deleteId" />
<input type="button" value="delete" onclick="requestDelete();" />

<br><hr>

다운로드<br>
<input type="text" id="downloadId" />
<input type="button" value="download" onclick="requestDownload();" />

<hr>
