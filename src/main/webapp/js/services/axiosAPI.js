import axios from 'axios';
 
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