import axios from 'axios';
import * as Define from "../define";

// Add a request interceptor
axios.interceptors.request.use(
    config => {
        const token = sessionStorage.getItem("accessToken");
        if(token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }
        config.headers['Content-Type'] = 'application/json';
        console.log("[axios.interceptors.request]config.headers", config.headers);
        return config;
    },
    error => {
        return Promise.reject(error);
    });

//Add a response interceptor
axios.interceptors.response.use(
    (response) => {
        console.log("[axios.interceptors.response.use] response", response);
        return response
    },
    function (error) {
        console.log("[axios.interceptors.response.use] error", error);
        const originalRequest = error.config;
        if (error.response.status === Define.UNAUTHORIZED && originalRequest.url === '/rss/api/auths/token') {
            console.log("[axios.interceptors.response.use] case 1");
            //router.push('/login');
            window.sessionStorage.clear();
            window.location.replace('/rss');
            return Promise.reject(error);
        }
        if (error.response.status === Define.UNAUTHORIZED && !originalRequest._retry) {
            console.log("[axios.interceptors.response.use] case 2");
            originalRequest._retry = true;
            const refreshToken = sessionStorage.getItem("refreshToken");
            return axios.post('/rss/api/auths/token', { "refreshToken": refreshToken })
                .then(res => {
                    //if (res.status === 201) {
                    if (res.status === Define.OK) {
                        sessionStorage.setItem("accessToken", res.data.accessToken);
                        axios.defaults.headers.common['Authorization'] = 'Bearer ' + sessionStorage.getItem("accessToken");
                        return axios(originalRequest);
                    }
                })
        }
        return Promise.reject(error);
    }
);

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
