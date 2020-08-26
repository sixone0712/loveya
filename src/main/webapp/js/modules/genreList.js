import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import {applyPenders} from 'redux-pender';
import services from '../services';

const GENRE_INIT_ALL_LIST = "genreList/GENRE_INIT_ALL_LIST";
const GENRE_INIT_SERVER_ERROR = "genreList/GENRE_INIT_SERVER_ERROR";
const GENRE_LOAD_DB_LIST = "genreList/GENRE_LOAD_DB_LIST";
const GENRE_GET_DB_LIST = "genreList/GENRE_GET_DB_LIST";
const GENRE_SET_DB_LIST = "genreList/GENRE_SET_DB_LIST";

export const genreInitAllList = createAction(GENRE_INIT_ALL_LIST);
export const genreInitServerError = createAction(GENRE_INIT_SERVER_ERROR);
export const genreLoadDbList = createAction(GENRE_LOAD_DB_LIST, services.axiosAPI.requestGet);
export const genreGetDbList = createAction(GENRE_GET_DB_LIST, services.axiosAPI.requestGet);
export const genreSetDbList = createAction(GENRE_SET_DB_LIST, services.axiosAPI.requestPost);

export const initialState = Map({
    genreList: Map({
        isServerErr: false,
        totalCnt: -1,
        curIdx: "",
        needUpdate : false,
        update: "",
        result: 1,
        list: List([
            Map({
                id: 0,
                name: "",
                category: List[Map({})],
                created: "",
                modified: "",
                validity: false
            })
        ]),
    })
});

const reducer =  handleActions({
    [GENRE_INIT_ALL_LIST]: (state, action) => {
        return initialState;
    },
    [GENRE_INIT_SERVER_ERROR] : (state, action) => {
        return state.setIn(["genreList","isServerErr"], false);
    }
}, initialState);

export default applyPenders(reducer, [
    {
        type: GENRE_LOAD_DB_LIST,
        onSuccess: (state, action) => {
            console.log("[genreList/GENRE_LOAD_DB_LIST]");

            const {data, result, update} = action.payload.data;

            if (result !== 0) {
                console.warn("[GENRE_LOAD_DB_LIST] error ", result);
                return state.setIn(["genreList", "result"], result);
            }

            const newGenreList = data.map(list => {
                const data = list.category.replace(/(\s*)/g, "");
                const category = data.split(",");
                return {
                    id: list.id,
                    name: list.name,
                    category: category,
                    created: list.created,
                    modified: list.modified,
                    validity: list.validity
                }
            });

            return state
                .setIn(["genreList", "list"], fromJS(newGenreList))
                .setIn(["genreList", "totalCnt"], newGenreList.length)
                .setIn(["genreList", "result"], result)
                .setIn(["genreList", "update"], update)
                .setIn(["genreList", "needUpdate"], false);
        }
    },
    {
        type: GENRE_GET_DB_LIST,
        onPending: (state, action) => {
            return state.setIn(["genreList","isServerErr"], false)
        },
        onFailure: (state, action) => {
            return state.setIn(["genreList","isServerErr"], true)
        },
        onSuccess: (state, action) => {
            console.log("[genreList/GENRE_GET_DB_LIST]");

            const {data, result, update} = action.payload.data;

            if (result !== 0) {
                console.warn("[GENRE_GET_DB_LIST] error ", result);
                return state.setIn(["genreList", "result"], result);
            }

            const newGenreList = data.map(list => {
                const data = list.category.replace(/(\s*)/g, "");
                const category = data.split(",");
                return {
                    id: list.id,
                    name: list.name,
                    category: category,
                    created: list.created,
                    modified: list.modified,
                    validity: list.validity
                }
            });

            return state
                .setIn(["genreList", "list"], fromJS(newGenreList))
                .setIn(["genreList", "totalCnt"], newGenreList.length)
                .setIn(["genreList", "result"], result)
                .setIn(["genreList", "update"], update)
                .setIn(["genreList", "needUpdate"], false);
        }
    },
    {
        type: GENRE_SET_DB_LIST,
        onPending: (state, action) => {
            return state.setIn(["genreList","isServerErr"], false)
        },
        onFailure: (state, action) => {
            return state.setIn(["genreList","isServerErr"], true)
        },
        onSuccess: (state, action) => {
            console.log("[genreList/GENRE_SET_DB_LIST] onSuccess");

            const { data, result, update } = action.payload.data;
            let needUpdate = false;

            if(result !== 0) {
                console.warn("[GENRE_SET_DB_LIST] result ", result);

                console.log("update", update);
                console.log("state.getIn(\"genreList\", \"update\") ", state.getIn(["genreList", "update"]) );

                if(state.getIn(["genreList", "update"]) !== update) {
                    needUpdate = true;
                    console.warn("[GENRE_SET_DB_LIST] needUpdate ", needUpdate);
                }
                return state.setIn(["genreList", "result"], result)
                            .setIn(["genreList", "needUpdate"], needUpdate);
            }

            const newGenreList = data.map(list => {
                const category = list.category.split(",");
                return {
                    id: list.id,
                    name: list.name,
                    category: category,
                    created: list.created,
                    modified: list.modified,
                    validity: list.validity
                }
            });

            //console.log("newGenreList", newGenreList);

            return state
                .setIn(["genreList", "list"], fromJS(newGenreList))
                .setIn(["genreList", "totalCnt"], newGenreList.length)
                .setIn(["genreList", "update"], update)
                .setIn(["genreList", "needUpdate"], needUpdate)
                .setIn(["genreList","isServerErr"], false);
        }
    },
])




