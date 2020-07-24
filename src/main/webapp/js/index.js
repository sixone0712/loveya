import 'react-app-polyfill/stable';
import 'react-app-polyfill/ie11';
import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import {Provider} from 'react-redux';
import store from './store';

import "bootstrap/dist/css/bootstrap.min.css";
import "rc-datetime-picker/dist/picker.min.css";
import "../css/styles.css";
import "../css/modal.scss";
import {BrowserRouter, Route} from "react-router-dom";

const rootElement = document.getElementById("root");
ReactDOM.render(
    <Provider store={store}>
        <BrowserRouter>
            <Route path={"/"} component={App} />
            {/*<App />*/}
        </BrowserRouter>
    </Provider>,
    rootElement);
