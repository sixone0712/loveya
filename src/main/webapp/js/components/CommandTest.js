{/* chpark_command_test */}
import React, {useEffect} from 'react';
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as commandActions from "../modules/command";
import services from '../services';
import moment from "moment";

const CommandTest = ({ command, commandActions }) => {

	useEffect(() => {
		console.log("useEffect")
		commandActions.commandInit();
		commandActions.commandLoadList("/rss/api/vftp/command");
	}, [])


	const loadCommandList = async () => {
		await commandActions.commandLoadList("/rss/api/vftp/command");
	}

	const addCommandList = async () => {
		const addData = {
			cmd_name: "test" + moment().format(),
			cmd_type: "vftp_sss"
		}
		try {
			const res = await services.axiosAPI.requestPost("/rss/api/vftp/command", addData);
			console.log(res)
		} catch (e) {
			console.log(e.message())
		}
		await commandActions.commandLoadList("/rss/api/vftp/command");
	}

	const removeCommnadList = async () => {
		const lists = command.get("lists").toJS();
		const removeList = lists.filter(item => item.checked === true);
		console.log("removeList", removeList);
		for (let item of removeList) {
			try {
				const res = await services.axiosAPI.requestDelete(`/rss/api/vftp/command/${item.id}`);
				console.log(res)
			} catch (e) {
				console.log(e.message())
			}
		}

		await commandActions.commandLoadList("/rss/api/vftp/command");
	}

	const editCommandList = async () => {
		const lists = command.get("lists").toJS();
		const editList = lists.find(item => item.checked === true);
		console.log("removeList", editList);
		const editItem = {
			cmd_name: "test" + moment().format(),
		}
		try {
			const res = await services.axiosAPI.requestPut(`/rss/api/vftp/command/${editList.id}`, editItem);
			console.log(res)
		} catch (e) {
			console.log(e.message())
		}

		await commandActions.commandLoadList("/rss/api/vftp/command");
	}

	const onClickOne = (e) => {
		console.log(e);
		commandActions.commandCheckOnlyOneList(parseInt(e.target.id));
	}

	const onMultipleClickOne = (e) => {
		console.log(e);
		console.log("typeof id", typeof  e.target.id)
		commandActions.commandCheckList(parseInt(e.target.id));
	}

	const lists = command.get("lists").toJS();
	console.log("lists", lists);

	return (
		<div style={{marginTop: "100px"}}>
			<button style={{backgroundColor: "red"}} onClick={loadCommandList}>Load Command List</button>

			<div>totalCnt: {command.get("totalCnt")}</div>
			<div>checkedCnt: {command.get("checkedCnt")}</div>

			<div>
			{lists.map((item) =>
				<div key={item.id}>
					<span>one</span>
					<input
						type="checkbox"
						id={item.index}
						checked={item.checked}
						onChange={onClickOne}
					/>
					<span>multiple</span>
					<input
						type="checkbox"
						id={item.index}
						checked={item.checked}
						onChange={onMultipleClickOne}
					/>
					{item.id} {item.cmd_name} {item.cmd_type} {item.checked ? "checked" : "unchecked"}
				</div>
			)}
			</div>
				<p></p>
				<button onClick={addCommandList}>add</button>
				<p></p>
				<button onClick={removeCommnadList}>remove</button>
				<p></p>
				<button onClick={editCommandList}>edit</button>

		</div>
	);
};

export default connect(
	(state) => ({
		command: state.command.get('command'),
	}),
	(dispatch) => ({ commandActions: bindActionCreators(commandActions, dispatch) })
)(CommandTest);