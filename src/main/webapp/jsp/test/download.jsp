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
    function requestDl() {

        /*
            {
        "fileId": 0,
        "fileStatus": "",
        "logId": "001",
        "fileName": "20200319141829",
        "fileSize": 22,
        "fileDate": "1584595109000",
        "filePath": "20200319141829",
        "file": true,
        "structId": "VSS",
        "targetName": "VSSVM87A5",
        "logName": "001 RUNNING STATUS",
        "sizeKB": 1
    },
         */

        let data = {list: [
            {
                machine:'machine-1',
                category:'category-1',
                file:'file-1',
                filesize:'3204',
                date: '2020-14-12'
            },{
                machine:'machine-2',
                category:'category-2',
                file:'file-2',
                filesize:'32444',
                date: '2020-14-12'
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
                    console.log(resp);

                    let total = resp.totalFiles;
                    let download = resp.downloadFiles;
                    $('#progress').text(download);
                    $('#total').text(total);

                    if(total!=0 && total==download) {
                        $('#link').text('TBD');
                    } else {
                        setTimeout(timer, 500);
                    }
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
                    setTimeout(timer, 500);
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
        <h2>Step1. Request download</h2>
        <h3>dl/request</h3>
        <input type="button" value="go" onclick="requestDl();">
    </div>

    <div>
        <h2>Step2. Polling download status</h2>
        <h3>dl/status</h3>
        dlId : <input id="id" name="downloadId" value="">
        Progress : <span id="progress">?</span> / <span id="total">?</span>
    </div>

    <div>
        <h2>Step3. Download</h2>
        <h3>dl/download</h3>
        <span id="link"></span>
    </div>
</form>







