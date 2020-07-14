/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CommandRepository.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.action;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.co.canon.ckbs.eec.fs.collect.model.DefaultLogCommandDefinitionModel;
import jp.co.canon.ckbs.eec.fs.collect.model.LogCommandDefinitionModel;
import jp.co.canon.ckbs.eec.fs.collect.model.LogUrlModel;
import jp.co.canon.ckbs.eec.fs.collect.model.ToolLogCollectUrlMapModel;
import jp.co.canon.ckbs.eec.fs.collect.xml.LogCollectComponentSaxHandler;
import jp.co.canon.ckbs.eec.fs.collect.xml.LogCollectDefinitionSaxHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 
 * ログ収集コマンドの情報を保持するクラス。
 * ログ収集定義ファイルと装置ログコンポーネントファイルを読み込んで、ログ収集コマンドを実行する情報を返す。
 * 
 * @author Tomomitsu TATEYAMA
 * ================================================================
 * 2011-11-28	T.Tateyama	関数の戻り値を正しく返していなかった	UTC-07-05
 * 2011-12-02	T.Tateyama	過去に戻しても読み込む様に修正		UTC-07-08
 */
@Component
public final class CommandRepository {
	@Value("${fileservice.configDirectory}")
	private String configDirStr;
	/** アンダーライン */
	private static final String UNDER_LINE = "_";
	/** ログ収集定義ファイルの固定名称部分 */
	private static final String LOG_COLLECT_DEF_PREFIX = "LogCollectDef_";
	/** ログ収集コンポーネントファイル名 */
	private static final String CONFIG_FILE = "configuration.xml";
	/** XMLファイルの拡張子 */
	private static final String XML_SUFFIX = ".xml";
	/** ログ収集定義ファイルが保存されているディレクトリ */
	private File definitonDir = null;
	/** 装置毎のコマンド定義が保持されているディレクトリ */
	private File commandTool = null;
	/** 装置種別毎のコマンド定義が保持されているディレクトリ */
	private File commandType = null;
	/** 全ての共通コマンド定義が保持されているディレクトリ */
	private File commandAll = null;
	
	/**
	 * 装置毎のULRのリストを内部でキャッシュするマップ
	 */
	private HashMap<String, ToolLogCollectUrlMapModel> urlsMap = new HashMap<String, ToolLogCollectUrlMapModel>();
	
	@PostConstruct
	private void postConstruct(){
		File r = new File(configDirStr);
		initialize(r);
	}
	/**
	 * ファイルをパースする。
	 *
	 * Parameters/Result:
	 *　 @param targetFile	対象のファイル
	 *　 @param handler		パースする際のハンドラ
	 *　 @throws ParserConfigurationException		パーサー設定に問題がある場合に発生
	 *　 @throws SAXException						Saxでエラーが発生
	 *　 @throws IOException						ファイル読み込み等でエラーが発生
	 */
	private void parseFile(File targetFile, DefaultHandler handler) throws ParserConfigurationException
																	, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		factory.setFeature("http://apache.org/xml/features/validation/schema", true);
		SAXParser parser = factory.newSAXParser();
		parser.parse(targetFile, handler);
	}
	/**
	 * ログ収集ファイルのパース
	 *
	 * Parameters/Result:
	 *　 @param targetFile	パースするXMLファイル
	 *　 @return	パースした結果を返す。ファイルが存在しない場合はnullを返す。
	 *　 @throws ConfigurationException	パースでエラーが発生した場合に返す。
	 */
	private ToolLogCollectUrlMapModel parseLogCollectFile(File targetFile) throws ConfigurationException {
		if (targetFile.exists()) {
			try {
				LogCollectDefinitionSaxHandler handler = new LogCollectDefinitionSaxHandler(targetFile.lastModified());
				parseFile(targetFile, handler);
				return handler.getModel();
			} catch (ParserConfigurationException e) {
				throw new ConfigurationException("", e, "E-504");
			} catch (SAXException e) {
				throw new ConfigurationException("", e, "E-505");
			} catch (IOException e) {
				throw new ConfigurationException("", e, "E-506");
			}
		}
		return null;
	}
	/**
	 * 収集コンポーネントファイルのパース処理
	 *
	 * Parameters/Result:
	 *　 @param logUrl		ログ収集ファイルから読み込んだログのURL情報
	 *　 @param targetFile	パースするXMLファイル
	 *　 @return	ログコマンドの定義モデルを返す。
	 *　 @throws ConfigurationException	パースでエラーが発生した場合に返す。
	 */
	private LogCommandDefinitionModel getLogDefinition(LogUrlModel logUrl, File targetFile) throws ConfigurationException {
		try {
			LogCollectComponentSaxHandler handler = new LogCollectComponentSaxHandler();
			parseFile(targetFile, handler);
			DefaultLogCommandDefinitionModel r = new DefaultLogCommandDefinitionModel();
			r.setCommand(handler.getExecuteCommand());
			r.setCommandCA(handler.getExecuteCommandCA());
			r.setLogId(handler.getCompoId());
			r.setLogUrl(logUrl);
			return r;
		} catch (ParserConfigurationException e) {
			throw new ConfigurationException("", e, "E-501");
		} catch (SAXException e) {
			throw new ConfigurationException("", e, "E-502");
		} catch (IOException e) {
			throw new ConfigurationException("", e, "E-503");
		}
		// return null;		2011-11-28
	}
	/**
	 *	装置とログＩＤを指定して、対象の装置ログコマンド定義を取得する。
	 *
	 * Parameters/Result:
	 *　 @param toolId	装置ＩＤ
	 *　 @param logId		ログＩＤ
	 *　 @return	ログ収集コマンド定義モデルを返す。
	 * @throws ConfigurationException	設定上に問題が発生 
	 */
	public LogCommandDefinitionModel getCommandDefinition(String toolId, final String logId) throws ConfigurationException {
		// 対象のコマンドをチェックする。
		File targetFile = new File(definitonDir, new StringBuilder()
			.append(LOG_COLLECT_DEF_PREFIX)
			.append(toolId).append(XML_SUFFIX).toString());
		ToolLogCollectUrlMapModel rep = urlsMap.get(toolId);
		if (rep == null) {
			rep = parseLogCollectFile(targetFile);
		} else {
			if (rep.getTimestamp() != targetFile.lastModified()) {	// 2011-12-02 
				rep = parseLogCollectFile(targetFile);
			}
		}
		urlsMap.put(toolId, rep);
		if (rep == null) {
			throw new ConfigurationException("log correct definition file is not found.:" + toolId + "-" + logId, "W-503");
		}
		// 対象のログＩＤを取得する。
		LogUrlModel urlmodel = rep.getLogUrlMap().get(logId);
		if (urlmodel != null) {
			File commandDir = new File(commandTool, rep.getToolName());
			if (!commandDir.exists()) {
				commandDir = new File(commandType, rep.getToolType());
				if (!commandDir.exists()) {
					commandDir = commandAll;
				}
			}
			//---------------------------
			File[] list = commandDir.listFiles(new FileFilter() {
				public boolean accept(File arg0) {
					return (arg0.getName().startsWith(logId + UNDER_LINE));
				}
			});
			if (list.length > 0) {
				File confFile = new File(list[0], CONFIG_FILE);
				if (confFile.exists()) {
					return getLogDefinition(urlmodel, confFile);					
				}
				throw new ConfigurationException("configuration file is not found.:" + confFile.getAbsolutePath(), "W-504");
			} 
			throw new ConfigurationException("command proguram not found.:" + toolId + "-" + logId, "W-502");
		}
		throw new ConfigurationException("logId not found in url list.:" + toolId + "-" + logId, "W-501");
	}
	/**
	 * 
	 *	初期化処理
	 *
	 * Parameters/Result:
	 *　 @param root	設定を保持するルートディレクトリ
	 */
	private void initialize(File configDir) {
		// 初期ディレクトリの設定
		definitonDir = new File(configDir, "definitions");
		if (!definitonDir.exists()) {
			definitonDir.mkdir();
		}
		File command = new File(configDir, "commands");
		if (!command.exists()) {
			command.mkdir();
		}
		commandTool = new File(command, "Tools");
		if (!commandTool.exists()) {
			commandTool.mkdir();
		}
		commandType = new File(command, "ToolTypes");
		if (!commandType.exists()) {
			commandType.mkdir();
		}
		commandAll = new File(command, "All");
		if (!commandAll.exists()) {
			commandAll.mkdir();
		}
	}
}
