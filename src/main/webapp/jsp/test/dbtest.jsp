<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!doctype html>

<script src="//code.jquery.com/jquery-3.2.1.min.js"></script>

<style type="text/css">

    body {
        font-size: 11pt;
    }

    div {
        border: none;
        position: relative;
        padding: 8px;
        margin: 12px;
        width: fit-content;
    }

</style>

<script>
    function addUserPermission() {
        console.log("request add-userperm");

        let perm = $('#new-user-perm').val();
        if(perm=='') {
            console.log("empty permission");
            return;
        }

        $.ajax({
            url: '/dbtest/userperm/add?permname=' + perm,
            type: 'get',
            async: true,
            success: resp=>console.log(resp),
            error: resp=>console.log(resp)
        });
    }

    function getUserPermissions() {
        console.log("request get-userperms");
        $.ajax({
            url: '/dbtest/userperm/get',
            type: 'get',
            async: true,
            success: resp=>console.log(resp),
            error: resp=>console.log(resp)
        });
    }

    function addUser() {
        console.log("request to add user");
        let username = $('#new-user').val();
        let password = $('#user-password').val();

        if(username=='' || password=='') {
            console.log('param error');
            return;
        }

        let url = '/dbtest/user/add?username='+username+'&password='+password;
        console.log('url='+url);
        $.ajax({
            url: url,
            type: 'get',
            async: true,
            success: resp=>console.log(resp),
            error: resp=>console.log(resp)
            });
    }

    function viewUser() {
        console.log("request to view user");
        let url = '/dbtest/user/list';
        $.ajax({
            url: url,
            type: 'get',
            async: true,
            success: resp=>console.log(resp),
            error: resp=>console.log(resp)
        });
    }

</script>

<h1>데이터베이스 테스트 페이지 (디버깅 모드)</h1>

<div>
    <form>
        <h2>유저 권한 관련</h2>
        <input type="text" id="new-user-perm" />
        <input type="button" value="Add" onclick="addUserPermission();"/>
        <input type="button" value="View" onclick="getUserPermissions();"/> <br>
    </form>
</div>

<div>
    <form>
        <h2>유저 관련</h2>
        username : <input type="text" id="new-user" /> <br>
        password : <input type="password" id="user-password" /> <br>
        <input type="button" value="Add" onclick="addUser();" />
        <input type="button" value="View" onclick="viewUser();" />
    </form>
</div>