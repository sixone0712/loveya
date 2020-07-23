/**
 * Title: Equipment Engineering Support System.
 *         - Log Upload Service
 *
 * File : CommandExecutor.java
 *
 * Author:
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.ckbs.eec.fs.collect.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 
 * 外部コマンドを実行する。実行した結果をファイルにリダイレクトし、内容を読み込み込み返す。
 * 
 * @author　Tomokazu OKUMURA
 * ===========================================================
 * 2011-11-29	T.Tateyama	ProcessBuilderによるコマンド解析方法が間違っているのでこちら側で対応　UTC-05-04
 * 2011-11-29 	T.Tateyama	Commandの結果文字列に対して終了判定処理が誤っている。　	UTC-05-07
 */
@Slf4j
public class CommandExecutor {
    /** System.getProperty("os.name") */
    private static final String KEY_OS_NAME = "os.name";
    /** Windows series */
    private static final String OS_WIN_XX = "windows";
    /** Windows 9X */
    //private static final String OS_WIN_9X = "windows 9";
    /** Windows NT */
    //private static final String OS_WIN_NT = "nt";
    /** Windows 20XX */
    //private static final String OS_WIN_20XX = "windows 20";
    /** Windows XP */
    //private static final String OS_WIN_XP = "windows xp";
    /** cmd.exe */
    private static final String CMDEXE = "cmd.exe /c ";
    // command.com
    //private static final String COMCOM = "command.com /C ";
    /** コマンド実行が正常終了の場合の出力文字  */
    private static final String COMMEND_SUCCESS = "Command Successful";
    /** コマンド実行がエラーの場合の出力文字 */
    private static final String COMMEND_ERROR = "Command Error";
    /** 出力するファイルの名称 */
    private static final String FILENAME = "ExtProc";
    /** 作業ディレクトリ */
    private File workDir = null;

    /**
     * 作業ディレクトリを指定して構築する
     * @param filebase 作業ディレクトリ
     */
    public CommandExecutor(File filebase) {
    	workDir = filebase;
    }
    /**
     * コマンド文字列をListに変更
     *
     * Parameters/Result:
     *　 @param in 入力文字列
     *　 @return	入力文字列を解析した結果をリストで返す。
     */
    private List<String> createCommandArrays(String in) {
    	java.util.ArrayList<String>  out = new java.util.ArrayList<String>();
    	StringBuilder buffer = new StringBuilder();
    	boolean open = false;
    	for (int i = 0; i < in.length(); i++) {
    		char x = in.charAt(i);
    		if (x == '"') {
    			open = !open;
    			continue;
    		} else if (!open && x == ' ') {
    			// 区切り
    			if (buffer.length() > 0) {
    				out.add(buffer.toString());
    				buffer = new StringBuilder();
    			}
    			continue;
    		}
    		buffer.append(x);
    	}
    	// 残りを出力
		if (buffer.length() > 0) {
			out.add(buffer.toString());
		}
    	return out;
    }
    /**
     * コマンドを実行し、その出力を文字列で返す。
     * 実行が終了するまで待機する
     *
     * Parameters/Result:
     *　 @param cmd	実行するコマンドの文字列
     *　 @return	コマンドの標準出力結果を返す。
     *　 @throws Exception	実行時にエラーが発生した場合
     */
    public final String execute(String cmd) throws Exception {
    	return execute(cmd, null);
    }
    
    /**
     * nice値を指定してコマンドを実行し、その出力を文字列で返す。
     * 実行が終了するまで待機する
     *
     * Parameters/Result:
     *　 @param cmd	実行するコマンドの文字列
     * @param niceVal ナイス値
     *　 @return	コマンドの標準出力結果を返す。
     *　 @throws Exception	実行時にエラーが発生した場合
     */
    public final String execute(String cmd, Integer niceVal) throws Exception {
        // ここで重複しないようなファイル名を決めて
        final File file = File.createTempFile(FILENAME, null, workDir);
        String osName = System.getProperty(KEY_OS_NAME).toLowerCase();

        // win9xは無視する場合
        List<String> cmdList;
        if (osName.startsWith(OS_WIN_XX)) {
        	cmdList = createCommandArrays(cmd);
        	if (!cmd.toLowerCase().startsWith(CMDEXE)) {
        		cmdList.add(0, "/c");
        		cmdList.add(0, "cmd.exe");
        	}
        	
        	cmdList.add(">");
        	cmdList.add(file.getAbsolutePath());
        } else {
            cmdList = new java.util.ArrayList<String>(3);
            if(niceVal != null){
            	cmdList.add("nice");
            	cmdList.add("-n");
            	cmdList.add(niceVal.toString());
            }
        	cmdList.add("sh");
        	cmdList.add("-c");
            cmdList.add(cmd + " > " + file.getAbsolutePath());
        }

        // コマンド実行
        log.info("Execute: " + cmdList);

        // 実行するコマンドにファイル名を渡す。
        // コマンドでは出力を指定されたファイルに書くようにする。
        ProcessBuilder builder = new ProcessBuilder(cmdList);
        Process proc = builder.start();

        final BufferedReader stderr;
        InputStream is = proc.getErrorStream();
        stderr = new BufferedReader(new InputStreamReader(is));

        // エラー出力
        Thread stderrThread = new Thread() {
            public void run() {
                try {
                    while (true) {
                    	String line = stderr.readLine(); 
                    	if (line == null) {
                    		break;
                    	}
                    }
                    stderr.close();
                } catch (IOException ex) {
                    log.warn("failed in stdErr.", ex);
                }
            }
        };
        stderrThread.start();
        //stderr.close();
        //is.close();

        // プロセスが終わるまで待機
        proc.waitFor();

        // ファイルを読み込む。
        if (!file.exists()) {
            throw new IOException("File not exists:" + file.getAbsolutePath());
        }
        BufferedReader bReader = new BufferedReader(new FileReader(file));
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.getProperty("line.separator"));
            if (line.equals(COMMEND_SUCCESS) || line.equals(COMMEND_ERROR)) {
                break;
            }
        }
        bReader.close();

        // 読み込んだらファイルはいらないので削除
        file.delete();

        // 結果を返す
        return buffer.toString();
    }
}
