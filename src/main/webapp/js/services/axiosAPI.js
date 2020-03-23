import axios from 'axios';
 
export function get(getId) {
    return axios.get('/api/' + getId);
}

export function post(postId, postData) {
    return axios.post(postId, postData);
}

export function postDownload(postId, postData) {
    return axios.post(postId, postData, {
        headers: {
            'Content-Type': 'application/json',
        }});
}