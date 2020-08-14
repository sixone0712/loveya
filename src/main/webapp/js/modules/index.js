import { combineReducers } from 'redux';

import viewList from './viewList';
import genreList from './genreList';
import searchList from './searchList';
import login from './login';
import user from './User';
import dlHistory from './dlHistory';
import autoPlan from './autoPlan';
import command from './command';
import vftpCompat from './vftpCompat';
import vftpSss from "./vftpSss";
import { penderReducer } from 'redux-pender';

export default combineReducers({
    viewList,
    genreList,
    searchList,
    login,
    user,
    autoPlan,
    dlHistory,
    vftpCompat,
    vftpSss,
    command,
    pender: penderReducer
});