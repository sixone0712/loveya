import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS } from 'immutable';
import { pender } from 'redux-pender';
import services from '../services';
import moment from "moment";

const VIEW_INIT_ALL_LIST= 'viewList/VIEW_INIT_ALL_LIST';
const VIEW_LOAD_CONSTRUCT_DISPLAY= 'viewList/VIEW_LOAD_CONSTRUCT_DISPLAY';
const VIEW_LOAD_TOOLINFO_LIST= 'viewList/VIEW_LOAD_TOOLINFO_LIST';
const VIEW_LOAD_LOGTYPE_LIST= 'viewList/VIEW_LOAD_LOGTYPE_LIST';
const VIEW_CHECK_TOOL_LIST = 'viewList/VIEW_CHECK_TOOL_LIST';
const VIEW_CHECK_ALL_TOOL_LIST = 'viewList/VIEW_CHECK_ALL_TOOL_LIST';
const VIEW_CHECK_LOGTYPE_LIST= 'viewList/VIEW_CHECK_LOGTYPE_LIST';
const VIEW_CHECK_ALL_LOGTYPE_LIST= 'viewList/VIEW_CHECK_ALL_LOGTYPE_LIST';
const VIEW_APPLY_GENRE_LIST= 'viewList/VIEW_APPLY_GENRE_LIST';
const VIEW_SET_EDIT_PLAN_LIST= 'viewList/VIEW_SET_EDIT_PLAN_LIST';

export const viewInitAllList = createAction(VIEW_INIT_ALL_LIST);
export const viewLoadConstructDisplay = createAction(VIEW_LOAD_CONSTRUCT_DISPLAY, services.axiosAPI.get);	// getURL
export const viewLoadToolInfoList = createAction(VIEW_LOAD_TOOLINFO_LIST, services.axiosAPI.get);	// getURL
export const viewLoadLogTypeList = createAction(VIEW_LOAD_LOGTYPE_LIST, services.axiosAPI.get);		// getURL
export const viewCheckToolList = createAction(VIEW_CHECK_TOOL_LIST); 	// index
export const viewCheckAllToolList = createAction(VIEW_CHECK_ALL_TOOL_LIST);		// check
export const viewCheckLogTypeList = createAction(VIEW_CHECK_LOGTYPE_LIST); 	// index
export const viewCheckAllLogTypeList = createAction(VIEW_CHECK_ALL_LOGTYPE_LIST);		// check
export const viewApplyGenreList = createAction(VIEW_APPLY_GENRE_LIST); 	// { index, genrelist }
export const viewSetEditPlanList = createAction(VIEW_SET_EDIT_PLAN_LIST);

const initialState = Map({
	gotReady: false,	// flag for loaded data

	constructDisplay:  List([
		Map({
			name: "",
			id: ""
		})
	]),

	equipmentList: List([
		Map({
			keyIndex: 0,
			structId: ""
		})
	]),

	toolInfoListCheckCnt: 0,
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

	logInfoListCheckCnt: 0,
	logInfoList: List([
		Map({
			keyIndex: 0,
			logType: 0,
			logCode: "",
			logName: "",
			fileListForwarding: null,
			checked: false
		})
	]),

	editPlanList: Map({
		id: "",
		planId: "",
		planDescription: "",
		planTarget: "",
		planPeriod: "",
		planStatus: "",
		planLastRun: "",
		planDetail: "",
		tool: List([]),
		logType: List([]),
	})

});

export default handleActions({
	...pender({
		type: VIEW_LOAD_CONSTRUCT_DISPLAY,
		onSuccess: (state, action) => {
			console.log("handleActions[VIEW_LOAD_CONSTRUCT_DISPLAY]");
			const lists = action.payload.data;
			const tree = lists.ConstructDisplay.Tree;
			const equipments = tree.find(item => item.name === "Equipments");
			console.log("lists", lists);
			console.log("tree", tree);
			console.log("equipments", equipments);
			console.log("equipments.Child", equipments.Child);
			const newEquipments = equipments.Child.map(item => ({
				name: String(item.name),
				id: String(item.id)
			}))
			console.log("newEquipments.Child", newEquipments);
			return state.set("constructDisplay",fromJS(newEquipments));
		}
	}),
	...pender(
		{
			type: VIEW_LOAD_TOOLINFO_LIST, // If type is given, create an object containing action handlers suffixed to this type.

			// In case of request / failure, if there is additional work to be done, add onPending and onFailure like this.
			// onPending: (state, action) => state,
			// onFailure: (state, action) => state

			onSuccess: (state, action) => { // If there is nothing else to do when successful, this function can also be omitted.
				console.log("handleActions[VIEW_LOAD_TOOLINFO_LIST]");
				const lists = action.payload.data;
				const constructDisplay = state.get("constructDisplay").toJS();
				console.log("lists", lists);
				console.log("constructDisplay", constructDisplay);

				const newList = lists.reduce((acc, cur) => {
					const find = constructDisplay.find(item => {
						return item.id === cur.structId
					});
					if(find !== undefined){
						const newCur = {
							...cur,
							structId: find.name
						}
						acc.push(newCur);
					}
					return acc;
				}, []);

				console.log("newList", newList);

				const newEquipLists = constructDisplay.map((item, idx) => {
					return {
						keyIndex: idx,
						equipmentId: item.name
					}
				});

				console.log("newEquipLists", newEquipLists);

				const newToolInfoList = newList.map((list, idx) => {
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

				console.log("newToolInfoList", newToolInfoList);

				return state.set('toolInfoList', fromJS(newToolInfoList)).set('equipmentList', fromJS(newEquipLists));
			},
			// When a function is omitted, the default value (state, action) => state is set (that is, it returns the state as it is)
		}),
	...pender({
		type: VIEW_LOAD_LOGTYPE_LIST,
		onSuccess: (state, action) => {
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
	}),

	[VIEW_INIT_ALL_LIST]: (state, action) => {
		return initialState;
	},

	[VIEW_CHECK_TOOL_LIST]: (state, action) => {
		console.log("handleActions[VIEW_CHECK_TOOL_LIST]");
		const toolInfoList = state.get("toolInfoList");
		let toolInfoListCheckCnt = state.get("toolInfoListCheckCnt");
		const index = action.payload;

		console.log("toolInfoList", toolInfoList);
		console.log("index", index);

		const check =  toolInfoList.getIn([index, "checked"]);
		console.log("check", check);
		if(check){
			toolInfoListCheckCnt--;
		} else {
			toolInfoListCheckCnt++;
		}

		return state.set("toolInfoList", toolInfoList.update(index, list => list.set("checked", !list.get("checked"))))
					.set("toolInfoListCheckCnt", toolInfoListCheckCnt);
	},

	[VIEW_CHECK_ALL_TOOL_LIST] : (state, action) => {
		const toolInfoList = state.get("toolInfoList");
		const check = action.payload;
		let toolInfoListCheckCnt = 0;

		const newToolList = toolInfoList.map(list => list.set("checked", check));

		if(check){
			toolInfoListCheckCnt = newToolList.size;
		}

		return state.set("toolInfoList", newToolList)
					.set("toolInfoListCheckCnt", toolInfoListCheckCnt);
	},

	[VIEW_CHECK_LOGTYPE_LIST]: (state, action) => {
		console.log("handleActions[VIEW_CHECK_LOGTYPE_LIST]");
		const logInfoList = state.get("logInfoList");
		let logInfoListCheckCnt = state.get("logInfoListCheckCnt");
		const index = action.payload;

		console.log("logInfoList", logInfoList.toJS());
		console.log("index", index);
		const check =  logInfoList.getIn([index, "checked"]);
		console.log("check", check);
		if(check){
			logInfoListCheckCnt--;
		} else {
			logInfoListCheckCnt++;
		}

		return state.set("logInfoList", logInfoList.update(index, list => list.set("checked", !list.get("checked"))))
					.set("logInfoListCheckCnt", logInfoListCheckCnt);
	},

	[VIEW_CHECK_ALL_LOGTYPE_LIST] : (state, action) => {
		const logInfoList = state.get("logInfoList");
		const check = action.payload;
		let logInfoListCheckCnt = 0;

		const newlogInfoList = logInfoList.map(list => list.set("checked", check));

		if(check){
			logInfoListCheckCnt = newlogInfoList.size;
		}

		return state.set("logInfoList", newlogInfoList)
					.set("logInfoListCheckCnt", logInfoListCheckCnt);
	},

	[VIEW_APPLY_GENRE_LIST]: (state, action) => {
		console.log("handleActions[VIEW_APPLY_GENRE_LIST]");

		const logInfoList = state.get("logInfoList");
		const { genreList, id } = action.payload;

		// find selected genre list
		const selectedGenre =  genreList.get("list").find(item => {
			return item.get("id") == id;
		});

		let logInfoListCheckCnt = 0;
		// get category list
		const fileCat = selectedGenre.get("category");

		// all loginfo list init -> unchecked
		const initLogInfoList = logInfoList.map(list => list.set("checked", false));

		// check genre list
		const newLogInfoList = fileCat.reduce((pre, cur) => {
			const findList = initLogInfoList.find(item => item.get("logCode") == cur);
			return pre.update(findList.get("keyIndex"), list => {
				logInfoListCheckCnt++;
				return list.set("checked", true);});
		}, initLogInfoList);

		return state.set("logInfoList", newLogInfoList)
					.set("logInfoListCheckCnt", logInfoListCheckCnt);
	},
	[VIEW_SET_EDIT_PLAN_LIST] : (state, action) => {
		console.log("handleActions[VIEW_SET_EDIT_PLAN_LIST]");
		console.log("action.payload", action.payload);
		const { tool, logCode } = action.payload;
		console.log("tool", tool);
		console.log("logCode", logCode);
		const toolArray = tool.split(",");
		const logArray = logCode.split(",");

		console.log("toolArray", toolArray);
		console.log("logArray", logArray);

		const toolInfoList = state.get("toolInfoList");
		const logInfoList = state.get("logInfoList");
		let logInfoListCheckCnt = 0;
		let toolInfoListCheckCnt = 0;

		// all toolinfo list init -> unchecked
		const initToolInfoList = toolInfoList.map(list => list.set("checked", false));
		// check Edit list
		const newToolInfoList = toolArray.reduce((pre, cur) => {
			const findList = initToolInfoList.find(item => item.get("targetname") == cur);
			return pre.update(findList.get("keyIndex"), list => {
				toolInfoListCheckCnt++;
				return list.set("checked", true);});
		}, initToolInfoList);


		// all loginfo list init -> unchecked
		const initLogInfoList = logInfoList.map(list => list.set("checked", false));

		// check Edit list
		const newLogInfoList = logArray.reduce((pre, cur) => {
			const findList = initLogInfoList.find(item => item.get("logCode") == cur);
			return pre.update(findList.get("keyIndex"), list => {
				logInfoListCheckCnt++;
				return list.set("checked", true);});
		}, initLogInfoList);


		return state.set("logInfoList", newLogInfoList)
					.set("logInfoListCheckCnt", logInfoListCheckCnt)
					.set("toolInfoList", newToolInfoList)
					.set("toolInfoListCheckCnt", toolInfoListCheckCnt);
	}
}, initialState)