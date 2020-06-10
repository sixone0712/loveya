import services from "../services";
import * as Define from "../define";

export const getGenreList = (props) => {
    const { genreList } = props;
    return genreList.toJS();
};

export const getGenreCnt = (props) => {
    const { totalCnt } = props.genreList;
    return totalCnt;
};

export const addGenreList = async (props, name) => {
    console.log("[addGenreList]");
    const { genreListActions, logInfoList, genreList } = props;
    const logInfoListToJS = logInfoList.toJS();

    if(name.length <= 0) {
        return Define.GENRE_SET_FAIL_EMPTY_NAME;
    }

    const fileCat = logInfoListToJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push(cur.logCode);
        return acc;
    }, []);

    const fileCatStr = fileCat.join(",");

    const sendData = new Object();
    sendData.name = name;
    sendData.category = fileCatStr;

    const { data } = await genreListActions.genreSetDbList(Define.REST_API_URL + "/genre/add", sendData)
    //console.log("data", data)
    const { result } = data;
    console.log("result", result);

    return result;
};

export const deleteGenreList = async (props, id) => {
    const { genreListActions } = props;

    const sendData = new Object();
    sendData.id = id;

    const { data } = await genreListActions.genreSetDbList(Define.REST_API_URL + "/genre/delete", sendData);
    //console.log("data", data)
    const { result } = data;
    console.log("result", result);

    return result;
};

export const editGenreList = async (props, id, name) => {
    console.log("editGenreList");

    const { genreListActions, logInfoList } = props;
    const logInfoListToJS = logInfoList.toJS();

    if(name.length <= 0) {
        return Define.GENRE_SET_FAIL_EMPTY_NAME;
    }

    const fileCat = logInfoListToJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push(cur.logCode);
        return acc;
    }, []);

    const fileCatStr = fileCat.join(",");

    //console.log("fileCatStr", fileCatStr);

    const sendData = new Object();
    sendData.id = id;
    sendData.name = name;
    sendData.category = fileCatStr;

    const { data } = await genreListActions.genreSetDbList(Define.REST_API_URL + "/genre/modify", sendData)
    //console.log("data", data)
    const { result } = data;
    console.log("result", result);

    return result;
};

export const selectGenreList = async (props, id) => {
    const { viewListActions, genreList } = props;

    if(id === 0) {
        await viewListActions.viewCheckAllLogTypeList(false);
    } else {
        await viewListActions.viewApplyGenreList({ genreList, id });
    }

    return true;
};