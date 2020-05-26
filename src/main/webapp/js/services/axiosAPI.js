import axios from 'axios';
import * as Define from "../define";

export const checkSession = (data) => {
    console.log("[axiosAPI][checkSession]");
    console.log("[axiosAPI][checkSession]data.headers", data.headers)
    const { userauth } = data.headers;
    console.log("[axiosAPI][checkSession]userauth", userauth);
    if(userauth == null || userauth == "false") {
        console.log("[axiosAPI][checkSession]replace");
        window.sessionStorage.clear();
        window.location.replace('/rss');
        //window.location.reload(true);
        return null;
    }

    return data;
}

export const isLogin = async (getId) => {
    const res =  await axios.get(getId, {
        headers: {
            'Pragma': 'no-cache'
        }});

    return res;
}

export const get = async (getId) => {
    const res =  await axios.get(getId, {
        headers: {
            // Internet Explorer requests caching
            // If the cache header is not set, Internet Explorer 11 (and earlier) caches all resources by default,
            // and in case of successive get requests, the cached data is responded without sending a get request to the server.

            // This setting was not applied in IE11.
            //'Cache-Control': 'no-cache',

            // Add headers to all API requests on the client side
            'Pragma': 'no-cache'

            // References
            // https://cherniavskii.com/internet-explorer-requests-caching/
            // https://kdevkr.github.io/archives/2018/understanding-cache-control/
        }});

    return checkSession(res);
};

export const postCancelToken = axios.CancelToken;
export let postCancel;
export const post = async (url, postData) => {
    const res =  await axios.post(url, postData, {
        headers: {
            'Content-Type': 'application/json',
        },
        // References
        // https://yamoo9.github.io/axios/guide/cancellation.html
        cancelToken: new postCancelToken(function executor(c) {
            // The executor function takes the cancel function as a parameter.
            postCancel = c;
        })
    });
    return checkSession(res);
}

export const downloadFile = async (url) => {
    const method = 'GET';
    let fileName = 'unknown';
    let state = {
        result:  Define.RSS_FAIL,
        fileName: "unknown"
    };
    const result = await axios.request({
        url,
        method,
        responseType: 'blob',   //important
    })
        .then( res  => {
            // Since IE cannot directly process the blob data, msSaveOrOpenBlob should be used.
            if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                const contentDisposition = res.headers['content-disposition'];  // file name

                if (contentDisposition) {
                    const [fileNameMatch] = contentDisposition.split(';').filter(str => str.includes('filename'));
                    if (fileNameMatch)
                        [, fileName] = fileNameMatch.split('=');
                }
                window.navigator.msSaveOrOpenBlob(new Blob([res.data]), fileName);
            } else {
                const url = window.URL.createObjectURL(new Blob([res.data]));
                const link = document.createElement('a');
                const contentDisposition = res.headers['content-disposition'];  // file name

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
            state.result = Define.RSS_SUCCESS;
            state.fileName =  fileName;
           return state;
        })
        .catch(error => {
            const errResp = error.response;
            state.result =  Define.COMMON_FAIL_SERVER_ERROR;
            if(typeof errResp == "undefined") {
                return state;
            }
            console.error("[axioAPI][downloadFile]errResp", error.response);
            console.error("[axioAPI][downloadFile]errResp.status", error.response.status);
            if(errResp.status === 404) {
                state.result =  Define.COMMON_FAIL_NOT_FOUND;
            }
            return state;
        });

    return result;
};
