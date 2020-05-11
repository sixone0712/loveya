import { combineReducers } from 'redux';

import viewList from './viewList';
import genreList from './genreList';
import searchList from './searchList';
import login from './login';
import autoPlan from './autoPlan';
import { penderReducer } from 'redux-pender';

export default combineReducers({
    viewList,
    genreList,
    searchList,
    login,
    autoPlan,
    pender: penderReducer
});