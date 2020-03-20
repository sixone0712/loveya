import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS } from 'immutable';
import { pender } from 'redux-pender';
import services from '../services';

const VIEW_LOAD_TOOLINFO_LIST= 'viewList/VIEW_LOAD_TOOLINFO_LIST';
const VIEW_LOAD_LOGTYPE_LIST= 'viewList/VIEW_LOAD_LOGTYPE_LIST';
const VIEW_CHECK_TOOL_LIST = 'viewList/VIEW_CHECK_TOOL_LIST';
const VIEW_CHECK_ALL_TOOL_LIST = 'viewList/VIEW_CHECK_ALL_TOOL_LIST';
const VIEW_CHECK_LOGTYPE_LIST= 'viewList/VIEW_CHECK_LOGTYPE_LIST';
const VIEW_CHECK_ALL_LOGTYPE_LIST= 'viewList/VIEW_CHECK_ALL_LOGTYPE_LIST';
const VIEW_APPLY_GENRE_LIST= 'viewList/VIEW_APPLY_GENRE_LIST';

export const viewLoadToolInfoList = createAction(VIEW_LOAD_TOOLINFO_LIST, services.axiosAPI.get);
export const viewLoadLogTypeList = createAction(VIEW_LOAD_LOGTYPE_LIST, services.axiosAPI.get);
export const viewCheckToolList = createAction(VIEW_CHECK_TOOL_LIST); 	// index
export const viewCheckAllToolList = createAction(VIEW_CHECK_ALL_TOOL_LIST);		// check
export const viewCheckLogTypeList = createAction(VIEW_CHECK_LOGTYPE_LIST); 	// index
export const viewCheckAllLogTypeList = createAction(VIEW_CHECK_ALL_LOGTYPE_LIST);		// check
export const viewApplyGenreList = createAction(VIEW_APPLY_GENRE_LIST); 	// { index, genrelist }

const initialState = Map({
	gotReady: false,	// flag for loaded data

	equipmentList: List([
		Map({
			keyIndex: 0,
			structId: ""
		})
	]),

	toolInfoList: List([
		Map({
			keyIndex: 0,
			structId: "",
			collectServerId: 0,
			collectHostName: null,
			targetname: "",
			targettype: "",
			checked: false
		})
	]),

	logInfoList: List([
		Map({
			keyIndex: 0,
			logType: 0,
			logCode: "",
			logName: "",
			fileListForwarding: null,
			checked: false
		})
	])
});

export default handleActions({
	...pender(
		{
			type: VIEW_LOAD_TOOLINFO_LIST, // type 이 주어지면, 이 type 에 접미사를 붙인 액션핸들러들이 담긴 객체를 생성합니다.

			// 요청중 / 실패 했을 때 추가적으로 해야 할 작업이 있다면 이렇게 onPending 과 onFailure 를 추가해주면됩니다.
			// onPending: (state, action) => state,
			// onFailure: (state, action) => state

			onSuccess: (state, action) => { // 성공했을때 해야 할 작업이 따로 없으면 이 함수 또한 생략해도 됩니다.
				console.log("handleActions[VIEW_LOAD_TOOLINFO_LIST]");
				const lists = action.payload.data;

				const equipLists = lists.map((list => list.structId));

				const filterdList = equipLists.filter( (item, idx, equipLists) => {
					return equipLists.indexOf( item ) === idx ;
				});

				const newEquipLists = filterdList.map((item, idx) => {
					return {
						keyIndex: idx,
						equipmentId: item
					}
				});

				const newLists = lists.map((list, idx) => {
					return {
						keyIndex: idx,
						structId: list.structId,
						collectServerId: list.collectServerId,
						collectHostName: list.collectHostName,
						targetname: list.targetname,
						targettype: list.targettype,
						checked: false
					}
				});

				return state.set('toolInfoList', fromJS(newLists)).set('equipmentList', fromJS(newEquipLists));
			},
			// 함수가 생략됐을때 기본 값으론 (state, action) => state 가 설정됩니다 (state 를 그대로 반환한다는 것이죠)
		}),
	...pender({
		type: VIEW_LOAD_LOGTYPE_LIST, // type 이 주어지면, 이 type 에 접미사를 붙인 액션핸들러들이 담긴 객체를 생성합니다.

		// 요청중 / 실패 했을 때 추가적으로 해야 할 작업이 있다면 이렇게 onPending 과 onFailure 를 추가해주면됩니다.
		// onPending: (state, action) => state,
		// onFailure: (state, action) => state

		onSuccess: (state, action) => { // 성공했을때 해야 할 작업이 따로 없으면 이 함수 또한 생략해도 됩니다.
			console.log("handleActions[VIEW_LOAD_LOGTYPE_LIST]");
			const lists = action.payload.data;
			console.log("lists", lists);
			console.log(lists[0].logType);
			const newLists = lists.map((lists, idx) => {
				return {
					keyIndex: idx,
					logType: lists.logType,
					logCode: lists.code,
					logName: lists.logName,
					fileListForwarding: lists.fileListForwarding,
					checked: false
				}
			});

			console.log("newLists", newLists);
			return state.set('logInfoList', fromJS(newLists));
		}
		// 함수가 생략됐을때 기본 값으론 (state, action) => state 가 설정됩니다 (state 를 그대로 반환한다는 것이죠)
	}),

	[VIEW_CHECK_TOOL_LIST]: (state, action) => {
		console.log("handleActions[VIEW_CHECK_TOOL_LIST]");
		const toolInfoList = state.get("toolInfoList");
		const index = action.payload;

		console.log("toolInfoList", toolInfoList);
		console.log("index", index);

		return state.set("toolInfoList", toolInfoList.update(index, list => list.set("checked", !list.get("checked"))));
	},

	[VIEW_CHECK_ALL_TOOL_LIST] : (state, action) => {
		const toolInfoList = state.get("toolInfoList");
		const check = action.payload;

		const newToolList = toolInfoList.map(list => list.set("checked", check));

		return state.set("toolInfoList", newToolList);
	},

	[VIEW_CHECK_LOGTYPE_LIST]: (state, action) => {
		console.log("handleActions[VIEW_CHECK_LOGTYPE_LIST]");
		const logInfoList = state.get("logInfoList");
		const index = action.payload;

		console.log("logInfoList", logInfoList);
		console.log("index", index);

		return state.set("logInfoList", logInfoList.update(index, list => list.set("checked", !list.get("checked"))));
	},

	[VIEW_CHECK_ALL_LOGTYPE_LIST] : (state, action) => {
		const logInfoList = state.get("logInfoList");
		const check = action.payload;

		const newlogInfoList = logInfoList.map(list => list.set("checked", check));

		return state.set("logInfoList", newlogInfoList);
	},

	[VIEW_APPLY_GENRE_LIST]: (state, action) => {
		console.log("handleActions[VIEW_APPLY_GENRE_LIST]");

		const logInfoList = state.get("logInfoList");
		const { genreList } = action.payload;
		console.log("genreList", genreList.toJS());

		/*
		genreList
		{
			"dispName": "ymkwon",
			"keyName": "ymwon",
			"machine": [0, 1, 2, 3],
			"fileCat": [4, 5, 6, 7]
		}
		*/

		const fileCat = genreList.get("fileCat");
		console.log("fileCat", fileCat.toJS());

		const initLogInfoList = logInfoList.map(list => list.set("checked", false));
		console.log("initLogInfoList", initLogInfoList.toJS());

		const newLogInfoList = fileCat.reduce((pre, cur) => {
			return pre.update(cur, list => list.set("checked", true));
		}, initLogInfoList);

		console.log("newLogInfoList", newLogInfoList.toJS());

		return state.set("logInfoList", newLogInfoList);
	}
	
}, initialState)