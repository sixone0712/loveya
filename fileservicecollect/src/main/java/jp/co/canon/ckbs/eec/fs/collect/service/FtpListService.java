package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.action.CommandExecutionException;
import jp.co.canon.ckbs.eec.fs.collect.action.CommandExecutor;
import jp.co.canon.ckbs.eec.fs.collect.action.CommandRepository;
import jp.co.canon.ckbs.eec.fs.collect.action.ConfigurationException;
import jp.co.canon.ckbs.eec.fs.collect.model.DefaultFileInfoModel;
import jp.co.canon.ckbs.eec.fs.collect.model.FileInfoModel;
import jp.co.canon.ckbs.eec.fs.collect.model.LogCommandDefinitionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
@Component
public class FtpListService {
    private static final String FTP_ERROR_CODE = "ERR-0200";
    private static final String COMMEND_SUCCESS = "Command Successful";
    private static final String COMMEND_ERROR = "Command Error";
    private static final String DELIM = ",";
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Autowired
    CommandRepository commandRepository;

    @Value("${fileservice.configDirectory}")
    String configDirectory;

    File workingDir = null;

    @PostConstruct
    private void postConstruct(){
        File configDir = new File(configDirectory);
        workingDir = new File(configDir, "Working");
        if (!workingDir.exists()){
            workingDir.mkdirs();
        }
    }

    private File getWorkingDir() {
        return workingDir;
    }

    private StringBuilder createListCommand(String machine, String category, String keyword,
                                            String startdate, String enddate) throws ConfigurationException {
        // 実行コマンドを取得する
        LogCommandDefinitionModel logCommand
                = commandRepository.getCommandDefinition(machine, category);
        if (logCommand.getCommand() == null || logCommand.getCommand().length() == 0) {
            throw new ConfigurationException("Execute command not found.", "E-610");
        }
        // 呼び出すコマンドを生成する
        StringBuilder command = new StringBuilder();
        command.append(logCommand.getCommand()).append(" list ");
        // url
        command.append("-url ")
                .append(logCommand.getLogUrl().getUrlString()).append(" ");
        // user/password
        command.append(" -u ")
                .append(logCommand.getLogUrl().getUserId()).append("/")
                .append(logCommand.getLogUrl().getPassword()).append(" ");
        // ftpmode <FS収集機能のActive対応(CITS)>
        command.append(" -md ")
                .append(logCommand.getLogUrl().getFtpmode()).append(" ");
        if (keyword != null && keyword.length() > 0) {
            command.append("-k ").append("\"").append(keyword).append("\" ");
        }
        if (startdate != null) {
            if (enddate == null) {
                throw new IllegalArgumentException("endddate not found!");
            }
            command.append("-p ").append(startdate)
                    .append(",").append(enddate);
        }
        return command;
    }

    private List<FileInfoModel> convertResult(String result, int delimCnt) {
        List<FileInfoModel> list = new ArrayList<FileInfoModel>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringTokenizer st = new StringTokenizer(result, System.getProperty("line.separator"));
        for (; st.hasMoreTokens();) {

            // 1行分を取得する
            String line = st.nextToken();
            if (COMMEND_SUCCESS.equals(line) || COMMEND_ERROR.equals(line)) {
                continue;
            }
            // 2011.01.12 デリミタで分割します
            String[] values = getSplitFileName(line, DELIM, delimCnt);	// 引数からもらうように変更
            if (values == null || values.length < 3) {			//	2011-11-29
                if (line.startsWith(FTP_ERROR_CODE)) {
                    throw new CommandExecutionException(line);
                }
                continue;
            }
            DefaultFileInfoModel info = new DefaultFileInfoModel();
            info.setName(values[0]);
            try {
                Date cal = f.parse(values[1]);
                info.setTimestamp(dateFormat.format(cal));
            } catch (ParseException e) {
                log.warn("parse exception", e);
            }
            info.setSize(Long.parseLong(values[2]));
            if (values.length > 3) {
                info.setType(values[3]);
            }
            list.add(info);
        }
        return list;
    }

    public List<FileInfoModel> getServerFileList(String machine, String category, String keyword,
                                                 String startdate, String enddate) throws ConfigurationException {
        StringBuilder command = createListCommand(machine, category, keyword, startdate, enddate);

        // コマンド実行
        CommandExecutor proc = new CommandExecutor(getWorkingDir());
        String result = null;
        try {
            result = proc.execute(command.toString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // 結果取得
        if (result != null) {
            return convertResult(result, 3);	 // 2011-11-29
        }

        return new ArrayList<FileInfoModel>() ;
    }

    public List<FileInfoModel> getServerFileListInDir(String machine, String category, String dir, String keyword,
                                                      String startdate, String enddate) throws ConfigurationException {

        // 実行コマンドを取得する
        StringBuilder command = createListCommand(machine, category, keyword, startdate, enddate);
        if (dir != null && dir.length() > 0) {
            command.append(" -d ").append(dir);
        }

        // コマンド実行
        CommandExecutor proc = new CommandExecutor(getWorkingDir());
        String result = null;
        try {
            result = proc.execute(command.toString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // 結果取得
        if (result != null) {
            return convertResult(result, 3);
        }

        return new ArrayList<FileInfoModel>() ;
    }

    protected String[] getSplitFileName(String line, String delim, int endcount) {
        // 引数エラー
        if (endcount < 0 || line == null || line.length() == 0 || delim == null || delim.length() == 0) {
            return null;
        }

        String result = line;
        int index = -1;
        List<String> l = new ArrayList<String>();
        // 終了まで繰り返し
        for (int il = 0; il < endcount; il++) {

            index = result.lastIndexOf(delim);
            // 途中で見つからなくなった
            if (index == -1) {
                break;
            }
            String value = result.substring(index + 1);
            l.add(0, value); // 先頭に格納する
            // 次回用に生成する
            result = result.substring(0, index + 1 - 1);
        }
        // 最後の分を格納する
        String value = result;
        l.add(0, value); // 先頭に格納する
        String[] newList = l.toArray(new String[0]);
        return newList;
    }
}
