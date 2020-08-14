import React, {useState, useCallback, useMemo, useEffect, useRef} from "react";
import { Card, CardBody, Table, ButtonToggle, Button } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamationCircle, faDownload, faBan, faChevronCircleDown } from "@fortawesome/free-solid-svg-icons";
import { faFileAlt } from "@fortawesome/free-regular-svg-icons";
import { Select } from "antd";
import ReactTransitionGroup from "react-addons-css-transition-group";
import ScaleLoader from "react-spinners/ScaleLoader";
import { filePaginate, RenderPagination } from "../../Common/Pagination";
import _ from "lodash";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as sssActions from "../../../modules/vftpSss";
import * as API from "../../../api";
import services from "../../../services"
import * as Define from "../../../define";

const { Option } = Select;

/*
const propsCompare = (prevProps, nextProps) => {
    if (JSON.stringify(prevProps.fileList) === JSON.stringify(nextProps.fileList)) {
        return false;
    }

    return !(prevProps.checkedList.length === nextProps.checkedList.length &&
        prevProps.checkedList.sort().every((value, index) => {
            return value === nextProps.checkedList.sort()[index];
        }));
};
*/

const geneDownloadStatus = function* (func) {
    while (true) {
        let response = yield func();

        if (response.status === 200) {
            if (response.data.status == "done") return response;
        } else {
            return response
        }

        yield new Promise((resolve) => {
            setTimeout(resolve, 300);
        })
    }
};

const initialDownStatus = {
    downloadId: "",
    downloadUrl: "",
    downloadedFiles: 0,
    status: "",
    totalFiles: 0,
};

const RSSvftpFilelist = ({
     responseList,
     responseListCnt,
     downloadCnt,
     downloadAll,
     isNewResponseList,
     sssActions
}) => {
    const [pageSize, setPageSize] = useState(10);
    const [currentPage, setCurrentPage] = useState(1);
    const [sortDirection, setSortDirection] = useState("");
    const [sortKey, setSortKey] = useState("");
    const [sortedList, setSortedList] = useState([]);
    const [isDownloadConfirm, setIsDownloadConfirm] = useState(false);
    const [isDownloadStart, setIsDownloadStart] = useState(false);
    const [isDownloadCancel, setIsDownloadCancel] = useState(false);
    const [isDownloadComplete, setIsDownloadComplete] = useState(false);
    const [isDownloadError, setIsDownloadError] = useState(false);
    const cancelRef = useRef(false);

    const [downStatus, setDownStatus] = useState(initialDownStatus);

    useEffect(() => {
        console.log("useEffect")
        console.log("isNewResponseList", isNewResponseList)
        console.log("responseList", responseList !== undefined ?  responseList.toJS() : undefined);

        if(responseList !== undefined) {
            if(isNewResponseList) {
                setSortDirection("");
                setSortKey("");
                sssActions.vftpSssSetIsNewResponseList(false);
            }
            const sortList = _.orderBy(responseList.toJS(), sortKey, sortDirection)
            setSortedList(sortList)
        }
    }, [responseList, isNewResponseList])

    const initDownStatus = useCallback(() => {
        cancelRef.current = false;
        setDownStatus(initialDownStatus);
    }, []);

    const openDownloadConfirm = useCallback(() => {
        if (downloadCnt === 0) {
            setIsDownloadError(true);
        } else {
            setIsDownloadConfirm(true);
        }
    }, [downloadCnt]);

    const closeDownloadConfirm = useCallback(() => {
        setIsDownloadConfirm(false);
    }, []);


    const requestDownload = async () => {
        initDownStatus();
        const responseListJS = responseList.toJS();
        const reqData = responseListJS.filter(item => item.checked === true)
        let res;
        try {
            res = await services.axiosAPI.requestPost('/rss/api/vftp/sss/download', { lists: reqData });
        } catch (error) {
            console.error(error);
        }

        console.log("res.data.downloadId", res.data.downloadId);
        const statusFunc = () => {
            return services.axiosAPI.requestGet('/rss/api/vftp/sss/download/' + res.data.downloadId);
        }

        const iterator = geneDownloadStatus(statusFunc);
        const next = ({ value, done }) => {
            console.log('done', done);
            console.log('cancelRef.current', cancelRef.current);
            if (cancelRef.current) {
                return;
            }
            if (done) {
                console.log("value", value);
                if (value.status === 200) {
                    openDownloadComplete();
                } else {
                    // 에러인 경우 처리..
                }
            } else {
                console.log("success");
                value.then((res) => {
                    console.log("then.value", res);
                    setDownStatus(res.data);
                    next(iterator.next(res))
                }).catch(err => {
                    console.log("error.value", err);
                    next(iterator.next(err.response))
                })
            }
        }

        next(iterator.next());
    };

    const openDownloadStart = () => {
        setIsDownloadConfirm(false);
        requestDownload();
        setTimeout(() => { setIsDownloadStart(true); }, 400);
    }

    const openDownloadCancel = useCallback(() => {
        setIsDownloadStart(false);
        setTimeout(() => { setIsDownloadCancel(true); }, 400);
    }, []);

    const closeDownloadCancel = useCallback((type) => {
        setIsDownloadCancel(false);
        if (type !== "OK") {
           if (downStatus.status === "done") {
               setTimeout(() => { openDownloadComplete(true); }, 400);
           } else {
               setTimeout(() => { setIsDownloadStart(true); }, 400);
           }
        } else {
            cancelRef.current = true;
            setIsDownloadConfirm(false);
            setIsDownloadStart(false);
            setIsDownloadCancel(false);
            setIsDownloadComplete(false);
            setIsDownloadError(false);
            if (downStatus.status === "done") {
                API.addDlHistory(Define.RSS_TYPE_VFTP_SSS ,"unknown", "User Cancel")
                    .then(r => r)
                    .catch(e => console.error(e));
            }
        }
    }, [downStatus, cancelRef])

    const openDownloadComplete = useCallback(() => {
        setIsDownloadStart(false)
        if(!isDownloadCancel) setIsDownloadComplete(true);
    }, [isDownloadCancel]);

    // save file
    const closeDownloadComplete = useCallback(async (isSave) => {
        setIsDownloadComplete(false);
        console.log("closeDownloadComplete");
        console.log("isSave", isSave);
        console.log("downStatus");
        if(isSave) {
            try {
                const res = await services.axiosAPI.downloadFile(downStatus.downloadUrl);
                await API.addDlHistory(Define.RSS_TYPE_VFTP_SSS ,res.fileName, "Download Completed");
            } catch (e) {
                console.error(e);
                await API.addDlHistory(Define.RSS_TYPE_VFTP_COMPAT , "unknown", "Download Fail");
            }
        } else {
            await API.addDlHistory(Define.RSS_TYPE_VFTP_SSS ,"unknown", "User Cancel");
        }

    }, [downStatus]);

    const closeDownloadError = useCallback(() => {
        setIsDownloadError(false);
    }, []);

    const handleCheckboxClick = e => {
         if(e !== null && e !== undefined) {
            const idx = e.target.id;
            if (idx !== null && idx !== undefined) {
                sssActions.vftpSssCheckResponseList(idx);
            }
            e.stopPropagation();
        }
    };

    const handleTrClick = e => {
           if(e !== null && e !== undefined) {
            const id = e.target.parentElement.getAttribute("cbinfo");
            if (id !== null && id !== undefined) {
                //API.checkResponseList(this.props, id);
                sssActions.vftpSssCheckResponseList(id);
            }
            e.stopPropagation();
        }
    };

    const handleThClick = useCallback(key => {
        let changeDirection = "asc";

        if (sortKey === key && sortDirection === "asc") {
            changeDirection = "desc";
        }

        // changed to use lodash
        /*
        const list = sortedList.sort((a, b) => {
            const preVal = a[key].toLowerCase();
            const nextVal = b[key].toLowerCase();

            if (changeDirection === "asc") {
                return preVal.localeCompare(nextVal, "en", { numeric: true });
            } else {
                return nextVal.localeCompare(preVal, "en", { numeric: true });
            }
        });
        */
        const list = _.orderBy(sortedList, key, changeDirection);

        setSortedList(list);
        setSortKey(key);
        setSortDirection(changeDirection);
    },[sortedList, sortKey, sortDirection]);

    const handlePageChange = useCallback(page => {
        setCurrentPage(page);
    }, []);

    const handleSelectBoxChange = useCallback(value => {
        setPageSize(value);
    }, []);

    const sortIconRender = name => {
        const style = "sort-icon";
        return sortKey === name ? style + " sort-active " + sortDirection : style;
    };

    if (sortedList.length === 0) {
        return (
            <div className="filelist-container">
                <Card className="ribbon-wrapper filelist-card">
                    <CardBody className="filelist-card-body">
                        <div className="ribbon ribbon-clip ribbon-info">File</div>
                        <div className="filelist-no-search">
                            <p>
                                <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                            </p>
                            <p>File not found.</p>
                        </div>
                    </CardBody>
                </Card>
            </div>
        );
    } else {
        return (
            <>
                <CreateModal
                    isConfirm={isDownloadConfirm}
                    isStart={isDownloadStart}
                    isCancel={isDownloadCancel}
                    isComplete={isDownloadComplete}
                    isError={isDownloadError}
                    confirmClose={closeDownloadConfirm}
                    cancelClose={closeDownloadCancel}
                    completeClose={closeDownloadComplete}
                    errorClose={closeDownloadError}
                    confirmAction={openDownloadStart}
                    startAction={openDownloadCancel}
                    completeAction={closeDownloadComplete}
                    downStatus={downStatus}
                />
                <div className="filelist-container">
                    <Card className="ribbon-wrapper filelist-card">
                        <CardBody className="filelist-card-body">
                            <div className="ribbon ribbon-clip ribbon-info">File</div>
                            <Table className="vftp-sss">
                                <thead>
                                    <tr>
                                        <th>
                                            <div>
                                                <ButtonToggle
                                                    outline
                                                    size="sm"
                                                    color="info"
                                                    className={"filelist-btn filelist-btn-toggle" + (downloadAll ? " active" : "")}
                                                    onClick={() => sssActions.vftpSssCheckAllResponseList(!downloadAll)}
                                                >
                                                    All
                                                </ButtonToggle>
                                            </div>
                                        </th>
                                        <th onClick={() => handleThClick("fileName")}>
                                            <span className="sortLabel-root">
                                                File Name
                                                <span className={sortIconRender("fileName")}>➜</span>
                                            </span>
                                        </th>
                                        <th onClick={() => handleThClick("fileSize")}>
                                            <span className="sortLabel-root">
                                                Size
                                                <span className={sortIconRender("fileSize")}>➜</span>
                                            </span>
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <CreateFileList
                                        fileList={filePaginate(sortedList, currentPage, pageSize)}
                                        downloadCnt={downloadCnt}
                                        trClick={handleTrClick}
                                        checkboxClick={handleCheckboxClick}
                                    />
                                </tbody>
                            </Table>
                        </CardBody>
                        <RenderPagination
                            pageSize={pageSize}
                            itemsCount={responseListCnt}
                            onPageChange={handlePageChange}
                            className="custom-pagination"
                        />
                        <div className="filelist-info-area">
                            <label>{downloadCnt} File Selected</label>
                        </div>
                        <div className="filelist-item-area">
                            <label>Rows per page : </label>
                            <Select
                                defaultValue={pageSize}
                                onChange={handleSelectBoxChange}
                                className="filelist"
                            >
                                <Option value={10}>10</Option>
                                <Option value={30}>30</Option>
                                <Option value={50}>50</Option>
                                <Option value={100}>100</Option>
                            </Select>
                            <Button
                                outline
                                size="sm"
                                color="info"
                                className="filelist-btn"
                                onClick={openDownloadConfirm}
                            >
                                Download
                            </Button>
                        </div>
                    </Card>
                </div>
            </>
        );
    }
};

const CreateFileList = React.memo(
    ({ fileList, downloadCnt, trClick, checkboxClick }) => {
        return fileList.map(file => {
            return (
                <tr key={file.index} onClick={trClick} cbinfo={file.index}>
                    <td>
                        <div className="custom-control custom-checkbox">
                            <input
                                type="checkbox"
                                className="custom-control-input"
                                id={file.index}
                                value={file.fileName}
                                checked={file.checked}
                                onChange={checkboxClick}
                            />
                            <label className="custom-control-label filelist-label" htmlFor={file.index}/>
                        </div>
                    </td>
                    <td><FontAwesomeIcon icon={faFileAlt} /> {file.fileName}</td>
                    <td>{API.bytesToSize(file.fileSize)}</td>
                </tr>
            );
        });
    },
    // propsCompare
);

const CreateModal = React.memo(
    ({
         isConfirm,
         isStart,
         isCancel,
         isComplete,
         isError,
         confirmClose,
         cancelClose,
         completeClose,
         errorClose,
         confirmAction,
         startAction,
         completeAction,
         downStatus
     }) => {
        if (isConfirm) {
            return (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <p><FontAwesomeIcon icon={faDownload} size="6x" /></p>
                            <p>Do you want to download the selected file?</p>
                        </div>
                        <div className="button-wrap">
                            <button className="secondary form-type left-btn" onClick={confirmAction}>
                                Download
                            </button>
                            <button className="secondary form-type right-btn" onClick={confirmClose}>
                                Cancel
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            );
        } else if (isStart) {
            return (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <div className="spinner-area">
                                <ScaleLoader loading={true} height={45} width={16} radius={30} margin={5} />
                            </div>
                            <p style={{ marginBottom: "0", paddingBottom: "0" }}>
                                Downloading...
                            </p>
                            <p>{`${downStatus.downloadedFiles}/${downStatus.totalFiles}`}</p>
                        </div>
                        <div className="button-wrap">
                            <button className="secondary alert-type" onClick={startAction}>
                                Cancel
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            );
        } else if (isCancel) {
            return (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <p><FontAwesomeIcon icon={faBan} size="6x" /></p>
                            <p>Are you sure you want to cancel the download?</p>
                        </div>
                        <div className="button-wrap">
                            <button className="secondary form-type left-btn" onClick={() => cancelClose("OK")}>
                                Yes
                            </button>
                            <button className="secondary form-type right-btn" onClick={() => cancelClose("Cancel")}>
                                No
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            );
        } else if (isComplete) {
            return (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <p><FontAwesomeIcon icon={faChevronCircleDown} size="6x" /></p>
                            <p>Download Complete!</p>
                        </div>
                        <div className="button-wrap">
                            <button className="secondary form-type left-btn" onClick={() => completeAction(true)}>
                                Save
                            </button>
                            <button className="secondary form-type right-btn" onClick={() => completeClose(false)}>
                                Cancel
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            );
        } else if (isError) {
            return (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <p><FontAwesomeIcon icon={faExclamationCircle} size="6x" /></p>
                            <p>Please choose a file.</p>
                        </div>
                        <div className="button-wrap">
                            <button className="secondary alert-type" onClick={errorClose}>
                                Close
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            );
        } else {
            return (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                />
            );
        }
    }
);

export default connect(
    (state) => ({
        responseList: state.vftpSss.get('responseList'),
        responseListCnt: state.vftpSss.get('responseListCnt'),
        downloadCnt: state.vftpSss.get('downloadCnt'),
        downloadAll: state.vftpSss.get('downloadAll'),
        isNewResponseList: state.vftpSss.get('isNewResponseList'),
    }),
    (dispatch) => ({
        sssActions: bindActionCreators(sssActions, dispatch),
    })
)(RSSvftpFilelist);
