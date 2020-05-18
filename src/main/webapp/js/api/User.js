import * as Define from "../define";
import md5 from 'md5-hash'


export const createUser = (props, uinfo) => {
    const { userActions } = props;

    return userActions.createUser(`/user/create?name=${uinfo.name}&pwd=${md5(uinfo.pwd)}&auth=${uinfo.authValue}`);
};

export const deleteUser = (props, id) => {
    const { userActions } = props;

    return userActions.deleteUser(`/user/delete?id=${id}`);
};

export const getDBUserList = (props) => {
    const { userActions } = props;
    return userActions.loadUserList(`/user/loadUserList`);
};

export const getUserList = (props) => {
    const { UserList } = props;
    return UserList.toJS().list;
};


export const getUserInfoErrorCode = (props) => {
    const { userInfo } = props;
    return userInfo.toJS().result;
};

export const getUserAuth = (props, id) => {
    const  {list}  = props.UserList.toJS();
    if(id >0)
    {
        const find = list.find(item => item.id == id);
        return find.auth;
    }
    return '';
};
