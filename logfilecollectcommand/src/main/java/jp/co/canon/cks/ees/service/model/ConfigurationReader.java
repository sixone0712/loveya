/**
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : XMLReader.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import jp.co.canon.cks.ees.service.model.xml.ConfigurationSaxParser;
import jp.co.canon.cks.ees.service.model.xml.FilePurgeConfigurationSaxHander;
import jp.co.canon.cks.ees.service.model.xml.FilterConfigurationSaxParser;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	各コンフィグレーションを読み込むためのクラス。読み込めるコンフィグレーションは、装置ログと、ファイル削除設定となる
 *
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-11-20 T.Tateyama	設定ファイル時に上書きできる情報を追加する。
 * 2011-11-27 T.Tateyama	ユーザ情報が無い設定ファイルにユーザを追加するとnullpointerexceptionが発生
 * 2012-11-01 T.TATEYAMA	ファイル削除定義を複数返す様に修正
 */
public class ConfigurationReader {
    private static Logger logger = Logger.getLogger(ConfigurationReader.class.getName());

    /**
     * デフォルトコンストラクタ
     */
    public ConfigurationReader() {
    }
    /**
     * XMLファイルをパースします。
     *
     * Parameter:
     * 	@param	target		対象のXMLファイル
     * 	@param	saxHander	処理するのハンドラ
     */
    private void read(File target, DefaultHandler saxHandler) throws ConfigurationException {
        try {
            if (target.exists()) {
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                parserFactory.setValidating(false);
                parserFactory.setNamespaceAware(false);
                SAXParser parser = parserFactory.newSAXParser();
                parser.parse(target, saxHandler);
                return;
            }
            throw new IllegalArgumentException("file is not found.");
        } catch (IOException ex) {
        	logger.log(Level.SEVERE, "happen the IOException", ex);
        	throw new ConfigurationException("file error", ex);
        } catch (SAXException ex) {
        	logger.log(Level.SEVERE, "happen the SAXException", ex);
        	throw new ConfigurationException("xml error", ex);
        } catch (FactoryConfigurationError ex) {
        	logger.log(Level.SEVERE, "happen the FactoryConfigurationError", ex);
        	throw new ConfigurationException("xml error", ex);
        } catch (ParserConfigurationException ex) {
        	logger.log(Level.SEVERE, "happen the ParserConfigurationException", ex);
        	throw new ConfigurationException("xml error", ex);
        }
    }
    /**
     * 指定したファイルを読み込み、ConfigurationModelを返す。
     * 設定ファイルはXMLファイルを利用する。
     *
     * Parameter:
     *	@param	target		対象の設定ファイル
     *　  @param 	url			null以外の場合、上書きするFileLoation位置指定
     *　  @param 	user		null以外の場合、上書きするユーザ指定
     *　  @param 	passwd		null以外の場合、上書きするパスワード指定
     *　  @param 	ftpmode		null以外の場合、上書きするFTPモード指定　<FS収集機能のActive対応(CITS)>
     *　  @param 	adminport	null以外の場合、上書きする管理ポート番号指定
     *	@param	toolID		装置名称
     * Result:
     *	@return ConfigurationModel	読み込んだ結果のConfigurationModelを返す
     *　@throws	ConfigurationException	読み込み時にエラーが発生した（詳細は親の例外を参照）
     *　@throws	IllegalArgumentException ファイルが存在しない
     */
    public ConfigurationModel getConfiguration(File target, URL url, String user, String passwd, String ftpmode, int adminport, String toolID)
//    public ConfigurationModel getConfiguration(File target, URL url, String user, String passwd, int adminport, String toolID)
    																				throws ConfigurationException {
    	ConfigurationSaxParser saxHandler = new ConfigurationSaxParser();
    	read(target, saxHandler);
    	if (saxHandler.getFilterDefinePath() != null) {
    		FilterConfigurationSaxParser filterParser = new FilterConfigurationSaxParser();
    		File ffile = new File(saxHandler.getFilterDefinePath());
    		if (!ffile.exists()) {
    			throw new ConfigurationException("filter configuration not found.", new FileNotFoundException());
    		}
    		read(ffile, filterParser);
    		saxHandler.getConfiguration().setFilter(filterParser.getFilterModel());
    	}
    	if (url != null) {
    		saxHandler.getConfiguration().setFileLocation(url);
    	}
    	if (user != null) {
    		// 2011-11-27 T.Tateyama bug UTL-03-2
    		if (saxHandler.getConfiguration().getAuthorize() == null) {
    			saxHandler.getConfiguration().setAuthorize(new UserInfo());
    		}
    		saxHandler.getConfiguration().getAuthorize().setUser(user);
    	}
    	if (passwd != null) {
    		// 2011-11-27 T.Tateyama bug UTL-03-2
    		if (saxHandler.getConfiguration().getAuthorize() == null) {
    			saxHandler.getConfiguration().setAuthorize(new UserInfo());
    		}
    		saxHandler.getConfiguration().getAuthorize().setPasswd(passwd.toCharArray());
    	}
    	// <FS収集機能のActive対応(CITS)>
    	if (ftpmode != null) {
    		int ftpDataConnectionMode = ConfigurationModel.FTP_PASSIVE;
        	final String ACTV = "active";
    		if(ftpmode.equalsIgnoreCase(ACTV))
    			ftpDataConnectionMode = ConfigurationModel.FTP_ACTIVE;
    		saxHandler.getConfiguration().setFtpDataConnectionMode(ftpDataConnectionMode);
    	}
    	if (adminport != -1) {
    		saxHandler.getConfiguration().setAdminiPort(adminport);
    	}

    	String eesp_var = System.getenv("EESP_VAR");
    	if(eesp_var != null){
        	saxHandler.getConfiguration().setEESP_VAR(eesp_var);
    	} else {
    		// キャッシュを使用するにもかかわらず、EESP_VAR変数がない場合はエラー
    		if(saxHandler.getConfiguration().isUseCache()){
    			throw new ConfigurationException("\"EESP_VAR\" is not set!");
    		}
    	}

    	saxHandler.getConfiguration().setToolId(toolID);

        return saxHandler.getConfiguration();
    }
    /**
     * 指定したファイルを読み込み、FilePurgeConfigurationModelを返す。
     * 設定ファイルはXMLファイルを利用する。
     *
     * Parameter:
     *	@param	target	対象の設定ファイル
     * Result:
     * @return List<FilePurgeConfigurationModel>	読み込んだ結果のFilePurgeConfigurationModelのリストを返す
     *　@throws	ConfigurationException	読み込み時にエラーが発生した（詳細は親の例外を参照）
     *　@throws	IllegalArgumentException ファイルが存在しない
     */
    public List<FilePurgeConfigurationModel> getFilePurgeConfigurationList(File target) throws ConfigurationException  {
    	FilePurgeConfigurationSaxHander saxHandler = new FilePurgeConfigurationSaxHander();
    	read(target, saxHandler);
        return saxHandler.getConfigurationList();
    }
}
