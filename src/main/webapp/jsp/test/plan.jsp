<%@ page language="java" contentType="text/html; charset=utf-8"%>
<script src="//code.jquery.com/jquery-3.2.1.min.js"></script>

<script>
    function requestAddPlan() {
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
</script>

정기 수집 요청 <br>

<input type="button" value="add" onclick="requestAddPlan();" />

<br>
레퍼런스 코드는 이 페이지의 js를 참고하여 주시기 바랍니다.