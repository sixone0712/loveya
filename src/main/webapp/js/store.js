import {applyMiddleware, createStore, compose} from 'redux';
import modules from './modules';
import { createLogger} from "redux-logger";
import ReduxThunk from "redux-thunk";
import penderMiddleware from 'redux-pender';

const logger = createLogger();
const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(modules, /* preloadedState, */ composeEnhancers(
	applyMiddleware(logger, ReduxThunk, penderMiddleware())
));

export default store;