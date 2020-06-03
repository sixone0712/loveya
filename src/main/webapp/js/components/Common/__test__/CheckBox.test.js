import 'babel-polyfill';
import React from 'react';
import {mount} from 'enzyme';
import CheckBox from "../CheckBox";

const initProps = {
    index: 1,
    name: "test",
    isChecked: true,
    labelClass: "machinelist-label",
    handleCheckBoxClick: jest.fn()
};

describe("Common CheckBox test", () => {
    it("Renders when value of all props is correct", () => {
        const component = mount(<CheckBox {...initProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Renders when name is null", () => {
       const newProps = {
           ...initProps,
           name: null
       }
       const component = mount(<CheckBox {...newProps} />);
       expect(component).toMatchSnapshot();
   });

    it("Renders when index is null", () => {
        const newProps = {
            ...initProps,
            index: null
        }
        const component = mount(<CheckBox {...newProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Renders when labelClass is filelist-label", () => {
        const newProps = {
            ...initProps,
            labelClass: "filelist-label"
        }
        const component = mount(<CheckBox {...newProps} />);
        expect(component).toMatchSnapshot();
    });

    it("Renders when isChecked is false", () => {
        const newProps = {
            ...initProps,
            isChecked: false
        }
       const component = mount(<CheckBox {...newProps} />);
       expect(component).toMatchSnapshot();
    });

    it("Check onChange event", () => {
        const handleChange = jest.fn();
        const component = mount(
            <CheckBox
                index={initProps.index}
                name={initProps.name}
                isChecked={initProps.isChecked}
                labelClass={initProps.labelClass}
                handleCheckboxClick={handleChange}
            />
        );
        const checkbox = component.find('input[type="checkbox"]');
        checkbox.simulate('change', { target: { checked: false } });
        expect(handleChange).toHaveBeenCalled();
    });
});