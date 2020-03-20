import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';

const GENRE_LOAD_LIST = "genreList/GENRE_LOAD_LIST";
const GENRE_ADD_LIST = "genreList/GENRE_ADD_LIST";
const GENRE_DELETE_LIST = "genreList/GENRE_DELETE_LIST";
const GENRE_EDIT_LIST = "genreList/GENRE_EDIT_LIST";

export const genreLoadList = createAction(GENRE_LOAD_LIST, services.axiosAPI.get); 	// { genreLists }
export const genreAddList = createAction(GENRE_ADD_LIST); 	// { mode, logInfoList}}
export const genreDeleteList = createAction(GENRE_DELETE_LIST); 	// { mode, logInfoList}}
export const genreEditList = createAction(GENRE_EDIT_LIST); 	// { mode, logInfoList}}


const initialState = Map({
    genreCnt: -1,
	genreLists: List([
		Map({
            dispName: "",
            keyName: "",
            machine: List[Map({})],
            fileCat: List[Map({})],
        })
	]),
});


export default handleActions({
    ...pender(
    {
        type: GENRE_LOAD_LIST, // type 이 주어지면, 이 type 에 접미사를 붙인 액션핸들러들이 담긴 객체를 생성합니다.

        // 요청중 / 실패 했을 때 추가적으로 해야 할 작업이 있다면 이렇게 onPending 과 onFailure 를 추가해주면됩니다.
        // onPending: (state, action) => state,
        // onFailure: (state, action) => state

        onSuccess: (state, action) => { // 성공했을때 해야 할 작업이 따로 없으면 이 함수 또한 생략해도 됩니다.
            console.log("[genreList/GENRE_LOAD_LIST]");
            const genreLists = fromJS(action.payload.data);
            const genreCnt = genreLists.size;

            console.log("[genreList/GENRE_LOAD_LIST] genreLists", genreLists.toJS());
            console.log("[genreList/GENRE_LOAD_LIST] genreCnt", genreCnt);


            return state
                .set("genreCnt", genreCnt)
                .set("genreLists", genreLists);
        },
        // 함수가 생략됐을때 기본 값으론 (state, action) => state 가 설정됩니다 (state 를 그대로 반환한다는 것이죠)
    }),

    [GENRE_ADD_LIST]: (state, action) => {
        
        const genreLists = state.get("genreLists");
        const genreCnt = state.get("genreCnt");
        console.log("[genreList/GENRE_ADD_LIST] genreLists", genreLists.toJS());
        console.log("[genreList/GENRE_ADD_LIST] genreCnt", genreCnt);
        console.log("[genreList/GENRE_ADD_LIST] genreLists.size", genreLists.size);
        
        const newGenreItem = {
            dispName : action.payload.dispName,
            keyName : action.payload.keyName,
            machine: action.payload.machine,
            fileCat: action.payload.fileCat,
        };

        console.log("[genreList/GENRE_ADD_LIST] action.payload.dispName", action.payload.dispName);
        console.log("[genreList/GENRE_ADD_LIST] action.payload.keyName", action.payload.keyName);
        console.log("[genreList/GENRE_ADD_LIST] action.payload.machine", action.payload.machine);
        console.log("[genreList/GENRE_ADD_LIST] action.payload.fileCat", action.payload.fileCat);

        if(genreCnt === -1) {

            return state
                    .set("genreCnt", genreLists.size)
                    .set("genreLists", genreLists.update(0, (list) => fromJS(newGenreItem)));


        } else {
           
            return state
                    .set("genreCnt", genreLists.size + 1)
                    .set("genreLists", genreLists.push(fromJS(newGenreItem)));
        }
    },


    [GENRE_DELETE_LIST]: (state, action) => {
        const genreLists = state.get("genreLists");
        const genreCnt = state.get("genreCnt");
        const deleteName = action.payload.keyName;

        console.log("[genreList/GENRE_DELETE_LIST] action.payload.keyName", action.payload.keyName);
        console.log("genreLists", genreLists.toJS());


        const deleteIdx = genreLists.findIndex(idx => idx.get('keyName') === action.payload.keyName);
        
        console.log("[genreList/GENRE_DELETE_LIST] deleteIdx", deleteIdx);

        return state
                    .set("genreCnt", genreLists.size - 1)
                    .set("genreLists", genreLists.delete(deleteIdx));

    },

    [GENRE_EDIT_LIST]: (state, action) => {
        const genreLists = state.get("genreLists");
        const { dispName, machine, fileCat } = action.payload;

        const editIdx = genreLists.findIndex(idx => idx.get('keyName') === action.payload.keyName);
        const editItem = {
            dispName : dispName,
            keyName : dispName,
            machine: machine,
            fileCat: fileCat,
        };

        return state
                    .set("genreCnt", genreLists.size)
                    .set("genreLists", genreLists.update(editIdx, (list) => fromJS(editItem)));

    }
}, initialState);

