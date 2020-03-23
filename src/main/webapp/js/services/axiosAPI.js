import axios from 'axios';
 
export function get(getId) {
    return axios.get('/api/' + getId);
}

export function post(postId, postData) {
    return axios.post('/api/' + postId, postData);
}