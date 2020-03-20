<script src="//code.jquery.com/jquery-3.2.1.min.js"></script>

<style type="text/css">

body {
    font-size: 11pt;
}

</style>

<script>
    function requestDl() {

        let data = {list: [
            {
                machine:'machine-1',
                category:'category-1',
                file:'file-1',
                filesize:'3204'
            },{
                machine:'machine-2',
                category:'category-2',
                file:'file-2',
                filesize:'32444'
            }
        ]};

        var finish = false;
        var timer = function() {
            let dlId = $('#id').attr('value');
            console.log('inner dlId='+dlId);
            $.ajax({
                url:'/dl/status?dlId='+dlId,
                type:'get',
                async:true,
                success: resp=>{
                    console.log('status=');
                    console.log(resp);
                },
                error: ()=>{
                    console.log('failed to check status');
                }
            });
        };

        $.ajax({
            url:'/dl/request',
            type:"post",
            data: JSON.stringify(data),
            contentType: 'application/json',
            async:true,
            success: resp => {
                console.log('Success');
                console.log(resp);
                $('#id').attr('value', resp);
                if(finish==false) {
                    setTimeout(timer, 1000);
                }
            },
            error: () => console.log('Error')
        });

    }
</script>

<h1>Hello! Let's check download feature.. </h1>
<br>

<form>
    <div>
        <h2>request download</h2>
        <input type="button" value="go" onclick="requestDl();">
    </div>
    <hr>
    <div>
        dlId : <input id="id" name="downloadId" value="">
    </div>
    <hr>
    <div>
        Progress : <span id="progress">?</span> / <span id="total">?</span>
    </div>
</form>







