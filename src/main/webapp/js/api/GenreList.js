import services from "../services";
import * as Define from "../define";

export const setGenreList =  async (props)  => {
    console.log("###setGenreList Start ");
    console.log("genreList", props.genreList.toJS());

    const result = await services.axiosAPI.postByObject("api/setGenre", props.genreList.toJS())
        .then((data) => Define.RSS_SUCCESS)
        .catch((error) => {
            console.log("[setGenreList]error", error);
            return Define.GENRE_SET_FAIL_SEVER_ERROR;
        });
    console.log("result", result);
    console.log("###setGenreList End ");
    return result;
};

export const getGenreList = (props) => {
    const { genreList } = props;
    return genreList.toJS();
};

export const getGenreCnt = (props) => {
    const { genreCnt } = props;
    return genreCnt;
};

export const addGenreList = (props, dispName, keyName) => {
    console.log("###addGenreList Start");
    const { genreListActions } = props;
    const { logInfoList } = props;
    const { genreList } = props;
    const logInfoListToJS = logInfoList.toJS();
    const genreListJS = genreList.toJS();

    if(dispName.length <= 0) {
        return Define.GENRE_SET_FAIL_EMPTY_NAME;
    }

    const findList = genreListJS.filter(function(item){
        return item.keyName === dispName;
    });

    console.log('findList.length', findList.length);
    if(findList.length > 0) {
        console.log("###addGenreList END");
        return Define.GENRE_SET_FAIL_SAME_NAME;
    }

    const fileCat = logInfoListToJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push(idx);
        return acc;
    }, []);

    console.log("fileCat", fileCat);

    const store = genreListActions.genreAddList({dispName, keyName, machine: [], fileCat});
    console.log('store', store);

    //setGenreList(props);
    console.log("###addGenreList END");
    return Define.RSS_SUCCESS;
};

export const deleteGenreList = (props, keyName) => {
    const { genreListActions } = props;
    genreListActions.genreDeleteList({ keyName });

    //setGenreList(props);
    return Define.RSS_SUCCESS;
};

export const editGenreList = (props, dispName, keyName) => {
    const { genreListActions } = props;
    const { logInfoList } = props;
    const { genreList } = props;
    const logInfoListToJS = logInfoList.toJS();
    const genreListJS = genreList.toJS();

    console.log("editGenreList");
    console.log("logInfoListToJS", logInfoListToJS);
    console.log("dispName", dispName);
    console.log("keyName", keyName);

    if(dispName.length <= 0) {
        return Define.GENRE_SET_FAIL_EMPTY_NAME;
    }

    const findList = genreListJS.filter(function(item){

        console.log("item.keyName", item.keyName, "dispName", dispName);
        return item.dispName === dispName && item.keyName !== keyName;
    });

    console.log("findList", findList);
    console.log("findList.length", findList.length);

    // 같은 이름이 있을 경우에는 에러 리턴
    if(findList.length > 0) {
        return Define.GENRE_SET_FAIL_SAME_NAME;
    }

    const fileCat = logInfoListToJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push(idx);
        return acc;
    }, []);

    console.log("fileCat", fileCat);
    console.log("dispName", dispName);
    console.log("keyName", keyName);

    genreListActions.genreEditList({dispName, keyName, machine: [], fileCat});
    //setGenreList(props);
    return Define.RSS_SUCCESS;
};

export const selectGenreList = (props, keyName) => {
    const { viewListActions } = props;
    const { genreList } = props;
    //console.log("genreList", genreList.toJS());

    //const selectedGenre =  genreList.filter(list => list.keyName === keyName);
    //console.log("selectedGenre", selectedGenre);

    if(keyName === "selectGenre") {
        const { logInfoList } = props;
        viewListActions.viewCheckAllLogTypeList(false);
    } else {
        viewListActions.viewApplyGenreList({ genreList, keyName });
    }
};