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


export const postJson2 = async (postId, postData) => {

    let res = await axios.post(postId, postData, {
        headers: {
            'Content-Type': 'application/json',
        }});

    console.log("postJson2");
    let data = await res.data;
    console.log("postJson2Final", data);
    return data;
};