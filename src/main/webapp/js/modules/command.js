import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import services from '../services';
import {applyPenders} from 'redux-pender';

const COMMAND_INIT = "cmd/COMMAND_INIT";
const COMMAND_LOAD_LIST = "cmd/COMMAND_GET_LIST";
const COMMAND_CHECK_ONLY_ONE_LIST = "cmd/COMMAND_CHECK_ONLY_ONE_LIST";
const COMMAND_CHECK_LIST = "cmd/COMMAND_CHECK_LIST";
const COMMAND_CHECK_ALL_LIST = "cmd/COMMAND_CHECK_ALL_LIST";

export const commandInit = createAction(COMMAND_INIT);
export const commandLoadList = createAction(COMMAND_LOAD_LIST, services.axiosAPI.requestGet);
export const commandCheckOnlyOneList = createAction(COMMAND_CHECK_ONLY_ONE_LIST);
export const commandCheckList = createAction(COMMAND_CHECK_LIST);
export const commandCheckAllList = createAction(COMMAND_CHECK_ALL_LIST);

const initialState = Map({
    command: Map({
        error: "",
        checkedCnt: 0,
        totalCnt: 0,
        lists: List([])
        /*
        lists: [
            {
                index: 0
                id: 1
                cmd_name: "TEST_1"
                cmd_type: "vftp_sss"
                checked: false
            }
        ]
        */
    }),
});

const reducer = handleActions({
    [COMMAND_INIT]: (state, action) => {
        console.log("COMMAND_INIT");
        return initialState;
    },
    [COMMAND_CHECK_ONLY_ONE_LIST]: (state, action) => {
        const index = +action.payload;      // to number
        const commandList = state.getIn(["command", "lists"]);
        const allFalseList = commandList.map(list => list.set("checked", false));
        const newCommandList = allFalseList.update(index, list => list.set("checked", true))

        return state.setIn(["command", "lists"], newCommandList)
                    .setIn(["command", "checkedCnt"], 1);
    },
    [COMMAND_CHECK_LIST]: (state, action) => {
        const index = +action.payload;
        const command = state.get("command");
        const lists = command.get("lists");
        let checkedCnt = command.get("checkedCnt");
        const checked = lists.getIn([index, "checked"]);
        const newLists = lists.update(index, list => list.set("checked", !checked))

        if (checked) checkedCnt--;
        else checkedCnt++;

        return state.setIn(["command", "lists"], newLists)
                    .setIn(["command", "checkedCnt"], checkedCnt);
    },
    [COMMAND_CHECK_ALL_LIST]: (state, action) => {
        const check = action.payload;
        const lists = state.getIn(["command", "lists"]);
        const newLists = lists.map(list => list.set("checked", check));
        let checkedCnt = 0;

        if (check) checkedCnt = newLists.size;

        return state.setIn(["command", "lists"], newLists)
                    .setIn(["command", "checkedCnt"], checkedCnt);
    },
}, initialState);

export default applyPenders(reducer, [
    {
        type: COMMAND_LOAD_LIST,
        onSuccess: (state, action) => {
            const { lists } = action.payload.data;
            const commandList = lists.map((item, idx) => ({
                index: idx,
                id: item.id,
                cmd_name: item.cmd_name,
                cmd_type: item.cmd_type,
                checked: false,
            }));
            const totalCnt = commandList.length;

            return state.setIn(["command", "lists"], fromJS(commandList))
              .setIn(["command", "totalCnt"], totalCnt)
              .setIn(["command", "error"], "");
        },
        onFailure: (state, action) => {
            const { error: { reason } } = action.payload.response.data;
            return state.setIn(["command", "error"], reason);
        }
    },
])

