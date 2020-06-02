import 'babel-polyfill';
import React from 'react';
import { mount } from 'enzyme';
import { faTrashAlt } from "@fortawesome/free-solid-svg-icons";
import AlertModal from "../AlertModal";

const initProps = {
    isOpen: true,
    icon: faTrashAlt,
    message: "alert modal unit test",
    style: "primary",
    closer: jest.fn()
};

describe("Common AlertModal test", () => {
    it("Renders when value of all props is correct", () => {
        const component = mount(<AlertModal {...initProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Renders when isOpen is false", () => {
        const newProps = {
            ...initProps,
            isOpen: false
        }
        const component = mount(<AlertModal {...newProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Check onClick event", () => {
        const handleClick = jest.fn();
        const component = mount(
            <AlertModal
                isOpen={initProps.isOpen}
                icon={initProps.icon}
                message={initProps.message}
                style={initProps.style}
                closer={handleClick}
            />
        );
        const button = component.find('button');
        button.simulate('click');
        expect(handleClick).toHaveBeenCalled();
    });
});