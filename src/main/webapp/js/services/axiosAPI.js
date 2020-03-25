import axios from 'axios';
import moment from "moment";
import * as Define from "../define";
 
export function get(getId) {
    return axios.get(getId);
}

export function post(postId, postData) {
    return axios.post(postId, postData);
}

export function postJson(postId, postData) {
    return axios.post(postId, postData, {
        headers: {
            'Content-Type': 'application/json',
        }});
}

export const downloadFile = async (dlId) => {
    const method = 'GET';
    const url = "dl/download?dlId=" + dlId;
    const result = await axios.request({
        url,
        method,
        responseType: 'blob',   //important
    })
        .then( res  => {
            //.then(({ data }) => {
            /*
            const downloadUrl = window.URL.createObjectURL(new Blob([data]));
            console.log("new Blob([data])", new Blob([data]));
            const link = document.createElement('a');
            const  fileName = moment().format("YYYYMMDDHHmmss").toString() + ".zip";
            link.href = downloadUrl;
            //link.setAttribute('download', fileName);    //any other extension
            document.body.appendChild(link);
            //link.click();
            //link.remove();
            window.URL.revokeObjectURL(downloadUrl);
            return Define.RSS_SUCCESS;
             */
            const url = window.URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement('a');
            const contentDisposition = res.headers['content-disposition']; // 파일 이름
            let fileName = 'unknown';
            if (contentDisposition) {
                const [fileNameMatch] = contentDisposition.split(';').filter(str => str.includes('filename'));
                if (fileNameMatch)
                    [, fileName] = fileNameMatch.split('=');
            }
            link.href = url;
            link.setAttribute('download', `${fileName}`);
            link.style.cssText = 'display:none';
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
            return Define.RSS_SUCCESS;

        })
        .catch(error => {
           return Define.RSS_FAIL
        });

    return result;
};
