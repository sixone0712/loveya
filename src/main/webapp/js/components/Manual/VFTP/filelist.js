import React, { useState, useCallback, useMemo } from "react";
import { Card, CardBody, Table, ButtonToggle, Button } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamationCircle, faDownload, faBan, faChevronCircleDown } from "@fortawesome/free-solid-svg-icons";
import { faFileAlt } from "@fortawesome/free-regular-svg-icons";
import { Select } from "antd";
import ReactTransitionGroup from "react-addons-css-transition-group";
import ScaleLoader from "react-spinners/ScaleLoader";
import { filePaginate, RenderPagination } from "../../Common/Pagination";

const { Option } = Select;

const propsCompare = (prevProps, nextProps) => {
    if (JSON.stringify(prevProps.fileList) === JSON.stringify(nextProps.fileList)) {
        return false;
    }

    return !(prevProps.checkedList.length === nextProps.checkedList.length &&
        prevProps.checkedList.sort().every((value, index) => {
            return value === nextProps.checkedList.sort()[index];
        }));
};

const calculateCount = (list) => {
    return list.length;
}

const initialFileList = [
    {
        id: "file1",
        fileName: "20110811_001234_345678_PlateNo001_ShotNo_01_MarkPosNo1_r0-0-0.bmp",
        size: "7KB"
    },
    {
        id: "file2",
        fileName: "20110811_001234_345678_PlateNo001_ShotNo_01_MarkPosNo1_r0-0-1.bmp",
        size: "56KB"
    },
    {
        id: "file3",
        fileName: "20110811_001234_345678_PlateNo001_ShotNo_01_MarkPosNo1_r0-0-2.bmp",
        size: "2KB"
    },
    {
        id: "file4",
        fileName: "20110811_001234_345678_PlateNo002_ShotNo_01_MarkPosNo1_r0-0-0.bmp",
        size: "8KB"
    },
    {
        id: "file5",
        fileName: "20110811_001234_345678_PlateNo002_ShotNo_01_MarkPosNo1_r0-0-1.bmp",
        size: "99KB"
    },
    {
        id: "file6",
        fileName: "20110811_001234_345678_PlateNo002_ShotNo_01_MarkPosNo1_r0-0-2.bmp",
        size: "40KB"
    },
    {
        id: "file7",
        fileName: "20110811_001234_345678_PlateNo003_ShotNo_01_MarkPosNo1_r0-0-0.bmp",
        size: "56KB"
    },
    {
        id: "file8",
        fileName: "20110811_001234_345678_PlateNo003_ShotNo_01_MarkPosNo1_r0-0-1.bmp",
        size: "51KB"
    },
    {
        id: "file9",
        fileName: "20110811_001234_345678_PlateNo003_ShotNo_01_MarkPosNo1_r0-0-2.bmp",
        size: "9KB"
    },
    {
        id: "file10",
        fileName: "20110811_001234_345678_PlateNo004_ShotNo_01_MarkPosNo1_r0-0-0.bmp",
        size: "104KB"
    },
    {
        id: "file11",
        fileName: "20110811_001234_345678_PlateNo004_ShotNo_01_MarkPosNo1_r0-0-1.bmp",
        size: "10.2MB"
    },
    {
        id: "file12",
        fileName: "20110811_001234_345678_PlateNo004_ShotNo_01_MarkPosNo1_r0-0-2.bmp",
        size: "7.6MB"
    },
    {
        id: "file13",
        fileName: "20110811_001234_345678_PlateNo005_ShotNo_01_MarkPosNo1_r0-0-0.bmp",
        size: "224KB"
    },
    {
        id: "file14",
        fileName: "20110811_001234_345678_PlateNo005_ShotNo_01_MarkPosNo1_r0-0-1.bmp",
        size: "22KB"
    },
    {
        id: "file15",
        fileName: "20110811_001234_345678_PlateNo005_ShotNo_01_MarkPosNo1_r0-0-2.bmp",
        size: "14MB"
    }
];

const RSSvftpFilelist = () => {
    const [pageSize, setPageSize] = useState(10);
    const [checkedList, setCheckedList] = useState([]);
    const [itemsChecked, setItemsChecked] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [sortDirection, setSortDirection] = useState("");
    const [sortKey, setSortKey] = useState("");
    const [sortedList, setSortedList] = useState(initialFileList);
    const [isDownloadConfirm, setIsDownloadConfirm] = useState(false);
    const [isDownloadStart, setIsDownloadStart] = useState(false);
    const [isDownloadCancel, setIsDownloadCancel] = useState(false);
    const [isDownloadComplete, setIsDownloadComplete] = useState(false);
    const [isDownloadError, setIsDownloadError] = useState(false);

    const openDownloadConfirm = useCallback(() => {
        if (checkedList.length === 0) {
            setIsDownloadError(true);
        } else {
            setIsDownloadConfirm(true);
        }
    }, [checkedList]);

    const closeDownloadConfirm = useCallback(() => {
        setIsDownloadConfirm(false);
    }, []);

    const openDownloadStart = useCallback(() => {
        setIsDownloadConfirm(false);
        setTimeout(() => { setIsDownloadStart(true); }, 400);
    }, []);

    const openDownloadCancel = useCallback(() => {
        setIsDownloadStart(false);
        setTimeout(() => { setIsDownloadCancel(true); }, 400);
    }, []);

    const closeDownloadCancel = useCallback(type => {
        setIsDownloadCancel(false);

        if (type !== "OK") {
            setTimeout(() => { setIsDownloadStart(true); }, 400);
        }
    }, []);

    const openDownloadComplete = useCallback(() => {
        setIsDownloadComplete(true);
    }, []);

    const closeDownloadComplete = useCallback(() => {
        setIsDownloadComplete(false);
    }, []);

    const closeDownloadError = useCallback(() => {
        setIsDownloadError(false);
    }, []);

    const selectItem = useCallback(() => {
        const collection = [];

        if (!itemsChecked) {
            initialFileList.map(file => collection.push(file.id));
        }

        setCheckedList(collection);
        setItemsChecked(!itemsChecked);
    }, [itemsChecked]);

    const handleCheckboxClick = useCallback(e => {
        const { id, checked } = e.target;
        let newList = [];

        if (checked) {
            newList = [...checkedList, id];
        } else {
            newList = checkedList.filter(checkedId => checkedId !== id);
        }

        setCheckedList(newList);
        setItemsChecked(initialFileList.length === newList.length);
    },[checkedList]);

    const handleTrClick = useCallback(e => {
        const id = e.target.parentElement.getAttribute("cbinfo");
        const isChecked = checkedList.filter(checkedId => checkedId === id);
        let newList = [];

        if (isChecked.length === 0) {
            newList = [...checkedList, id];
        } else {
            newList = checkedList.filter(checkedId => checkedId !== id);
        }

        setCheckedList(newList);
        setItemsChecked(initialFileList.length === newList.length);
        e.stopPropagation();
    },[checkedList]);

    const handleThClick = useCallback(key => {
        let changeDirection = "asc";

        if (sortKey === key && sortDirection === "asc") {
            changeDirection = "desc";
        }

        const list = sortedList.sort((a, b) => {
            const preVal = a[key].toLowerCase();
            const nextVal = b[key].toLowerCase();

            if (changeDirection === "asc") {
                return preVal.localeCompare(nextVal, "en", { numeric: true });
            } else {
                return nextVal.localeCompare(preVal, "en", { numeric: true });
            }
        });

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

    const selectedFile = useMemo(() => calculateCount(checkedList), [checkedList]);

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
                                                    className={"filelist-btn filelist-btn-toggle" + (itemsChecked ? " active" : "")}
                                                    onClick={selectItem}
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
                                        <th onClick={() => handleThClick("size")}>
                                            <span className="sortLabel-root">
                                                Size
                                                <span className={sortIconRender("size")}>➜</span>
                                            </span>
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <CreateFileList
                                        fileList={filePaginate(initialFileList, currentPage, pageSize)}
                                        checkedList={checkedList}
                                        trClick={handleTrClick}
                                        checkboxClick={handleCheckboxClick}
                                    />
                                </tbody>
                            </Table>
                        </CardBody>
                        <RenderPagination
                            pageSize={pageSize}
                            itemsCount={initialFileList.length}
                            onPageChange={handlePageChange}
                            className="custom-pagination"
                        />
                        <div className="filelist-info-area">
                            <label>{selectedFile} File Selected</label>
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
    ({ fileList, checkedList, trClick, checkboxClick }) => {
        return fileList.map(file => {
            return (
                <tr key={file.id} onClick={trClick} cbinfo={file.id}>
                    <td>
                        <div className="custom-control custom-checkbox">
                            <input
                                type="checkbox"
                                className="custom-control-input"
                                id={file.id}
                                value={file.name}
                                checked={checkedList.includes(file.id)}
                                onChange={checkboxClick}
                            />
                            <label className="custom-control-label filelist-label" htmlFor={file.id}/>
                        </div>
                    </td>
                    <td><FontAwesomeIcon icon={faFileAlt} /> {file.fileName}</td>
                    <td>{file.size}</td>
                </tr>
            );
        });
    },
    propsCompare
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
         completeAction
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
                            <p>(10/100)</p>
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
                            <button className="secondary form-type left-btn" onClick={completeAction}>
                                Save
                            </button>
                            <button className="secondary form-type right-btn" onClick={completeClose}>
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

export default RSSvftpFilelist;
