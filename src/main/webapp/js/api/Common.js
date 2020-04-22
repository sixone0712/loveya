import * as Define from "../define";

export const convertErrMsg = (error) => {
    let msg;
    switch (error) {
        case Define.GENRE_SET_FAIL_NO_ITEM: msg = "Please choose a category."; break;
        case Define.GENRE_SET_FAIL_SAME_NAME: msg = "The genre name is duplicated."; break;
        case Define.GENRE_SET_FAIL_EMPTY_NAME: msg = "Please input genre name."; break;
        case Define.GENRE_SET_FAIL_SEVER_ERROR: msg = "Network connection error."; break;
        case Define.GENRE_SET_FAIL_NOT_SELECT_GENRE: msg = "Please choose a genre."; break;
        case Define.GENRE_SET_FAIL_NOT_EXIST_GENRE: msg = "The selected genre does not exist in the DB. Update the DB."; break;
        case Define.GENRE_SET_FAIL_NEED_UPDATE: msg = "DB has been changed. Update the DB."; break;
        case Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY: msg = "Please choose a machine and category."; break;
        case Define.SEARCH_FAIL_NO_MACHINE: msg = "Please choose a machine."; break;
        case Define.SEARCH_FAIL_NO_CATEGORY: msg = "Please choose a category."; break;
        case Define.SEARCH_FAIL_DATE: msg = "Please set the start time before the end time."; break;
        case Define.SEARCH_FAIL_SERVER_ERROR: msg = "Network connection error."; break;
        case Define.FILE_FAIL_NO_ITEM: msg = "Please choose a file."; break;
        default: msg="what's error : " + error; break;
    }
    return msg;
};


export const getErrorMsg = (error) => {
    let msg = "";
    switch (error) {
        case Define.GENRE_SET_FAIL_NO_ITEM: msg = "Please choose a category."; break;
        case Define.GENRE_SET_FAIL_SAME_NAME: msg = "The genre name is duplicated."; break;
        case Define.GENRE_SET_FAIL_EMPTY_NAME: msg = "Please input genre name."; break;
        case Define.GENRE_SET_FAIL_SEVER_ERROR: msg = "Network connection error."; break;
        case Define.GENRE_SET_FAIL_NOT_SELECT_GENRE: msg = "Please choose a genre."; break;
        case Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY: msg = "Please choose a machine and category."; break;
        case Define.SEARCH_FAIL_NO_MACHINE: msg = "Please choose a machine."; break;
        case Define.SEARCH_FAIL_NO_CATEGORY: msg = "Please choose a category."; break;
        case Define.SEARCH_FAIL_DATE: msg = "Please set the start time before the end time."; break;
        case Define.SEARCH_FAIL_SERVER_ERROR: msg = "Network connection error."; break;
        case Define.FILE_FAIL_NO_ITEM: msg = "Please choose a file."; break;
        case Define.FILE_FAIL_SERVER_ERROR: msg = "Network connection error."; break;
        default: break;
    }

    return msg;
};

