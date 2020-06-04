import 'babel-polyfill';
import React from 'react';
import { mount } from 'enzyme';
import { faTrashAlt } from "@fortawesome/free-solid-svg-icons";
import ConfirmModal from "../ConfirmModal";

const handleClick = jest.fn();

const initProps = {
    isOpen: true,
    icon: faTrashAlt,
    message: "alert modal unit test",
    style: "primary",
    leftBtn: "OK",
    rightBtn: "Cancel",
    actionBg: jest.fn(),
    actionLeft: jest.fn(),
    actionRight: jest.fn()
};

describe("Common Confirm test", () => {
    it("Renders when value of all props is correct", () => {
        const component = mount(<ConfirmModal {...initProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Renders when isOpen is false", () => {
        const newProps = {
            ...initProps,
            isOpen: false
        }
        const component = mount(<ConfirmModal {...newProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Check actionBg onClick event", () => {
        const component = mount(
            <ConfirmModal
                isOpen={initProps.isOpen}
                icon={initProps.icon}
                message={initProps.message}
                leftBtn={initProps.leftBtn}
                rightBtn={initProps.rightBtn}
                style={initProps.style}
                actionBg={handleClick}
                actionLeft={initProps.actionLeft}
                actionRight={initProps.actionRight}
            />
        );
        const element = component.find('.Custom-modal-overlay');
        element.simulate('click');
        expect(handleClick).toHaveBeenCalled();
    });

    it("Check actionLeft onClick event", () => {
        const component = mount(
            <ConfirmModal
                isOpen={initProps.isOpen}
                icon={initProps.icon}
                message={initProps.message}
                leftBtn={initProps.leftBtn}
                rightBtn={initProps.rightBtn}
                style={initProps.style}
                actionBg={initProps.actionBg}
                actionLeft={handleClick}
                actionRight={initProps.actionRight}
            />
        );
        const button = component.find('button').at(0);
        button.simulate('click');
        expect(handleClick).toHaveBeenCalled();
    });

    it("Check actionRight onClick event", () => {
        const component = mount(
            <ConfirmModal
                isOpen={initProps.isOpen}
                icon={initProps.icon}
                message={initProps.message}
                leftBtn={initProps.leftBtn}
                rightBtn={initProps.rightBtn}
                style={initProps.style}
                actionBg={initProps.actionBg}
                actionLeft={initProps.actionLeft}
                actionRight={handleClick}
            />
        );
        const button = component.find('button').at(1);
        button.simulate('click');
        expect(handleClick).toHaveBeenCalled();
    });
});