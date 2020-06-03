import 'babel-polyfill';
import React from 'react';
import { mount } from 'enzyme';
import { filePaginate, RenderPagination } from "../Pagination";

const data = [
    { name: "dummy", index: 0 },
    { name: "dummy", index: 1 },
    { name: "dummy", index: 2 },
    { name: "dummy", index: 3 },
    { name: "dummy", index: 4 },
    { name: "dummy", index: 5 },
    { name: "dummy", index: 6 },
    { name: "dummy", index: 7 },
    { name: "dummy", index: 8 },
    { name: "dummy", index: 9 },
    { name: "dummy", index: 10 },
    { name: "dummy", index: 11 },
    { name: "dummy", index: 12 },
    { name: "dummy", index: 13 },
    { name: "dummy", index: 14 }
];

const handleClick = jest.fn();

const initProps = {
    pageSize: 10,
    itemsCount: 100,
    onPageChange: jest.fn(),
    currentPage: 1,
    className: "custom-pagination"
};

describe("Common Pagination test", () => {
    it("Check filePaginate", () => {
        filePaginate(data, 2, 10);
    });

    it("Renders when value of all props is correct", () => {
        const component = mount(<RenderPagination {...initProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Renders when pageSize and itemsCount are the same", () => {
        const newProps = {
            ...initProps,
            itemsCount: 10
        }
        const component = mount(<RenderPagination {...newProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Check onClick event", () => {
        const component = mount(
            <RenderPagination
                pageSize={initProps.pageSize}
                itemsCount={initProps.itemsCount}
                onPageChange={handleClick}
                currentPage={initProps.currentPage}
                className={initProps.className}
            />
        );
        const button = component.find('button').at(7);
        button.simulate('click');
        expect(handleClick).toHaveBeenCalled();
    });
});