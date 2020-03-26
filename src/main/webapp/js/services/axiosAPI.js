import axios from 'axios';
import moment from "moment";
import * as Define from "../define";
 
export const get = (getId) => {
    return axios.get(getId, {
        headers: {
            // Internet Explorer requests caching

            // 캐시 헤더가 설정되지 않은 경우 Internet Explorer 11 (및 이전 버전)은 기본적으로 모든 리소스를 캐시하여
            // 연속적으로 get 요청 시, 서버에 get요청을 보내지 않고 캐시 된 데이터를 응답하는 문제가 있다.

            // 해당 설정은 IE11에서 적용되지 않았다.
            //'Cache-Control': 'no-cache',

            // 클라이언트 측의 모든 API 요청에 헤더를 추가
            'Pragma': 'no-cache'

            // 참조 사이트
            // https://cherniavskii.com/internet-explorer-requests-caching/
            // https://kdevkr.github.io/archives/2018/understanding-cache-control/
        }});
};

export function postByObject(url, postData) {
    return axios.post(url, postData);
}

export function postByJson(url, postData) {
    return axios.post(url, postData, {
        headers: {
            'Content-Type': 'application/json',
        }});
}

export const downloadFile = async (dlId) => {
    const method = 'GET';
    const url = "/dl/download?dlId=" + dlId;
    const result = await axios.request({
        url,
        method,
        responseType: 'blob',   //important
    })
        .then( res  => {
            // IE에서는 Blob데이터를 직접 처리하지 못하므로 msSaveOrOpenBlob를 사용해야한다.
            if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                const contentDisposition = res.headers['content-disposition']; // 파일 이름
                let fileName = 'unknown';
                if (contentDisposition) {
                    const [fileNameMatch] = contentDisposition.split(';').filter(str => str.includes('filename'));
                    if (fileNameMatch)
                        [, fileName] = fileNameMatch.split('=');
                }
                window.navigator.msSaveOrOpenBlob(new Blob([res.data]), fileName);
            } else {
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
                link.setAttribute('download', fileName);
                link.style.cssText = 'display:none';
                document.body.appendChild(link);
                link.click();
                link.remove();
                window.URL.revokeObjectURL(url);
            }

            return Define.RSS_SUCCESS;
        })
        .catch(error => {
           return Define.RSS_FAIL
        });

    return result;
};
