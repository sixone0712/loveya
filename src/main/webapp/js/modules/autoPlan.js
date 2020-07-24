import {createAction, handleActions} from 'redux-actions';
import {Map} from 'immutable';
import {applyPenders} from 'redux-pender';
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
const AUTO_PLAN_SET_EDIT_PLAN_LIST = "autoPlan/AUTO_PLAN_SET_EDIT_PLAN_LIST";

export const autoPlanInit = createAction(AUTO_PLAN_INIT);
export const autoPlanSetPlanId = createAction(AUTO_PLAN_SET_PLAN_ID);
export const autoPlanSetCollectStart = createAction(AUTO_PLAN_SET_COLLECT_START);
export const autoPlanSetFrom = createAction(AUTO_PLAN_SET_FROM);
export const autoPlanSetTo = createAction(AUTO_PLAN_SET_TO);
export const autoPlanSetCollectType = createAction(AUTO_PLAN_SET_COLLECT_TYPE);
export const autoPlanSetInterval = createAction(AUTO_PLAN_SET_INTERVAL);
export const autoPlanSetIntervalUnit = createAction(AUTO_PLAN_SET_INTERVAL_UNIT);
export const autoPlanSetDescription = createAction(AUTO_PLAN_SET_DESCRIPTION);
export const autoPlanSetEditPlanList = createAction(AUTO_PLAN_SET_EDIT_PLAN_LIST);

const initialState = Map({
    autoPlan: Map({
        planId: "",
        collectStart: moment().startOf('day'),
        from: moment().startOf('day'),
        to : moment().endOf('day'),
        collectType: Define.AUTO_MODE_CONTINUOUS,
        interval: 1,
        intervalUnit: Define.AUTO_UNIT_MINUTE,
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
    [AUTO_PLAN_SET_EDIT_PLAN_LIST] : (state, action) => {
        const { planId, collectStart, from, to, collectType, interval, description } = action.payload;
        const intervalInt = Number(interval);
        let convInterval = ""
        let intervalUnit = "";

        const minutes = (intervalInt / (1000 * 60)).toFixed(0);
        const hours = (intervalInt / (1000 * 60 * 60)).toFixed(0);
        const days = (intervalInt / (1000 * 60 * 60 * 24)).toFixed(0);

        if(days > 0) {
            intervalUnit = Define.AUTO_UNIT_DAY;
            convInterval = String(days);
        } else if( hours > 0) {
            intervalUnit = Define.AUTO_UNIT_HOUR;
            convInterval = String(hours);
        } else {
            intervalUnit = Define.AUTO_UNIT_MINUTE;
            convInterval = String(minutes);
        }

        return state.setIn(["autoPlan", "planId"], planId)
                    .setIn(["autoPlan", "planId"], planId)
                    .setIn(["autoPlan", "collectStart"], moment(collectStart))
                    .setIn(["autoPlan", "from"], moment(from))
                    .setIn(["autoPlan", "to"], moment(to))
                    .setIn(["autoPlan", "collectType"], collectType)
                    .setIn(["autoPlan", "interval"], convInterval)
                    .setIn(["autoPlan", "intervalUnit"], intervalUnit)
                    .setIn(["autoPlan", "description"], description);

    },

}, initialState);

export default applyPenders(reducer, [
    { }
])




