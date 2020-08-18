import React from "react";
import PaginationComponent from "react-reactstrap-pagination";

export const filePaginate = (items, pageNumber, pageSize) => {
    const startIndex = (pageNumber - 1) * pageSize;
    return items.slice( startIndex, startIndex +  pageSize);
}

export const RenderPagination = ({pageSize, itemsCount, onPageChange, currentPage, className}) => {
    const pageCount = Math.ceil(itemsCount / pageSize);

    if (pageCount === 1) {
        return null;
    }

    return (
        <div className={className}>
            <PaginationComponent
                totalItems={itemsCount}
                pageSize={pageSize}
                onSelect={onPageChange}
                defaultActivePage={currentPage}
                maxPaginationNumbers={10}
                firstPageText={"«"}
                previousPageText={"‹"}
                nextPageText={"›"}
                lastPageText={"»"}
            />
        </div>
    );
}

export const propsCompare = (prevProps, nextProps) => {
    return JSON.stringify(prevProps) === JSON.stringify(nextProps);
};