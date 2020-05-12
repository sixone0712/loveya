import React from "react";
import PaginationComponent from "react-reactstrap-pagination";

 export function filePaginate(items, pageNumber, pageSize) {
     const startIndex = (pageNumber - 1) * pageSize;
     return items.slice( startIndex, startIndex +  pageSize);
}

export function renderPagination(
    pageSize,
    itemsCount,
    onPageChange,
    className
) {
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
                maxPaginationNumbers={10}
                firstPageText={"«"}
                previousPageText={"‹"}
                nextPageText={"›"}
                lastPageText={"»"}
            />
        </div>
    );
}
