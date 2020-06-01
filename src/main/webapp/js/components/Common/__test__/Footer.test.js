import 'babel-polyfill';
import React from "react";
import { mount } from "enzyme";
import Footer from "../Footer";

describe('Common footer test', () => {
  it('Snapshot check', () => {
      const component = mount(<Footer />);
      expect(component).toMatchSnapshot();
  });
});