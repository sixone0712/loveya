import axios from 'axios';
import * as Define from "../define";

export const checkSession = (response) => {
    if(response !== undefined) {
        console.log("[axiosAPI][checkSession]response", response);
        //console.log("[axiosAPI][checkSession]response.data", response.data);
        //console.log("[axiosAPI][checkSession]response.headers", response.headers)
        const { userauth } = response.headers;
        console.log("[axiosAPI][checkSession]userauth", userauth);
        if (userauth == null || userauth == "false") {
            console.log("[axiosAPI][checkSession]logout then go to login page");
            axios.get(Define.REST_API_URL + "/user/logout")
              .then(
                (res) => {
                    window.sessionStorage.clear();
                    window.location.replace('/rss');
                })
              .catch(error => {
                  window.sessionStorage.clear();
                  window.location.replace('/rss');
              });
        }
    } else {
        console.log("[axiosAPI][checkSession] response is undefined");
    }
}

export const getCancelToken = axios.CancelToken;
export let getCancel;
export const postCancelToken = axios.CancelToken;
export let postCancel;
export const putCancelToken = axios.CancelToken;
export let putCancel;
export const patchCancelToken = axios.CancelToken;
export let patchCancel;
export const deleteCancelToken = axios.CancelToken;
export let deleteCancel;

export const requestGet = (url) => {
    return axios.get(url, {
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
        },
        // References
        // https://yamoo9.github.io/axios/guide/cancellation.html
        cancelToken: new getCancelToken(function executor(c) {
            // The executor function takes the cancel function as a parameter.
            getCancel = c;
        })
    });
}

export const requestPost = (url, data) => {
    return axios.post(url, data, {
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
}

export const requestPut = (url, data) => {
    return axios.put(url, data, {
        headers: {
            'Content-Type': 'application/json',
        },
        // References
        // https://yamoo9.github.io/axios/guide/cancellation.html
        cancelToken: new putCancelToken(function executor(c) {
            // The executor function takes the cancel function as a parameter.
            putCancel = c;
        })
    });
}

export const requestPatch = (url, data) => {
    return axios.patch(url, data, {
        headers: {
            'Content-Type': 'application/json',
        },
        // References
        // https://yamoo9.github.io/axios/guide/cancellation.html
        cancelToken: new patchCancelToken(function executor(c) {
            // The executor function takes the cancel function as a parameter.
            patchCancel = c;
        })
    });
}

export const requestDelete = async (url) => {
    return await axios.delete(url, {
        headers: {
            'Content-Type': 'application/json',
        },
        // References
        // https://yamoo9.github.io/axios/guide/cancellation.html
        cancelToken: new deleteCancelToken(function executor(c) {
            // The executor function takes the cancel function as a parameter.
            deleteCancel = c;
        })
    });
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
