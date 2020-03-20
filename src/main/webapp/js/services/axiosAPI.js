import axios from 'axios';
 
export function get(getId) {
    return axios.get('http://localhost:8080/api/' + getId);
}

export function post(postId, postData) {
    return axios.post('http://localhost:8080/api/' + postId, postData);
}