import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';
import * as Define from '../define';
import moment from "moment";

const AUTO_PLAN_INIT = "autoPlan/AUTO_PLAN_INIT";
const AUTO_PLAN_SET_PLAN_ID = "autoPlan/AUTO_PLAN_SET_PLAN_ID";
const AUTO_PLAN_SET_COLLECT_START = "autoPlan/AUTO_PLAN_SET_COLLECT_START";
const AUTO_PLAN_SET_FROM = "autoPlan/AUTO_PLAN_SET_FROM";
const AUTO_PLAN_SET_TO = "autoPlan/AUTO_PLAN_SET_TO";
const AUTO_PLAN_SET_COLLECT_TYPE = "autoPlan/AUTO_PLAN_SET_COLLECT_TYPE";
const AUTO_PLAN_SET_INTERVAL = "autoPlan/AUTO_PLAN_SET_INTERVAL";
const AUTO_PLAN_SET_INTERVAL_UNIT = "autoPlan/AUTO_PLAN_SET_INTERVAL_UNIT";
const AUTO_PLAN_SET_DESCRIPTION = "autoPlan/AUTO_PLAN_SET_DESCRIPTION";

export const autoPlanInit = createAction(AUTO_PLAN_INIT);
export const autoPlanSetPlanId = createAction(AUTO_PLAN_SET_PLAN_ID);
export const autoPlanSetCollectStart = createAction(AUTO_PLAN_SET_COLLECT_START);
export const autoPlanSetFrom = createAction(AUTO_PLAN_SET_FROM);
export const autoPlanSetTo = createAction(AUTO_PLAN_SET_TO);
export const autoPlanSetCollectType = createAction(AUTO_PLAN_SET_COLLECT_TYPE);
export const autoPlanSetInterval = createAction(AUTO_PLAN_SET_INTERVAL);
export const autoPlanSetIntervalUnit = createAction(AUTO_PLAN_SET_INTERVAL_UNIT);
export const autoPlanSetDescription = createAction(AUTO_PLAN_SET_DESCRIPTION);

const initialState = Map({
    autoPlan: Map({
        planId: "",
        collectStart: moment().utc().startOf('day'),
        from: moment().utc().startOf('day'),
        to : moment().utc().endOf('day'),
        collectType: "continue",
        interval: "",
        intervalUnit: "minute",
        description: ""
    })
});

const reducer =  handleActions({
    [AUTO_PLAN_INIT]: (state, action) => {
        return initialState;
    },
    [AUTO_PLAN_SET_PLAN_ID] : (state, action) => {
        const planId = action.payload;
        return state.setIn(["autoPlan", "planId"], planId);
    },
    [AUTO_PLAN_SET_COLLECT_START] : (state, action) => {
        const collectStart = action.payload;
        return state.setIn(["autoPlan", "collectStart"], collectStart);
    },
    [AUTO_PLAN_SET_FROM] : (state, action) => {
        const from = action.payload;
        return state.setIn(["autoPlan", "from"], from);
    },
    [AUTO_PLAN_SET_TO] : (state, action) => {
        const to = action.payload;
        return state.setIn(["autoPlan", "to"], to);
    },
    [AUTO_PLAN_SET_COLLECT_TYPE] : (state, action) => {
        const collectType = action.payload;
        return state.setIn(["autoPlan", "collectType"], collectType);
    },
    [AUTO_PLAN_SET_INTERVAL] : (state, action) => {
        const interval = action.payload;
        return state.setIn(["autoPlan", "interval"], interval);
    },
    [AUTO_PLAN_SET_INTERVAL_UNIT] : (state, action) => {
        const intervalUnit = action.payload;
        return state.setIn(["autoPlan", "intervalUnit"], intervalUnit);
    },
    [AUTO_PLAN_SET_DESCRIPTION] : (state, action) => {
        const description = action.payload;
        return state.setIn(["autoPlan", "description"], description);
    },

}, initialState);

export default applyPenders(reducer, [
    { }
])




