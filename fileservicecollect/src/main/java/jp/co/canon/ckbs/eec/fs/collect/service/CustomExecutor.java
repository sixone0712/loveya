package jp.co.canon.ckbs.eec.fs.collect.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import java.io.IOException;

public class CustomExecutor {
    ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);

    public void execute(CommandLine cmdLine, CustomOutputStreamLineHandler streamLineHandler){
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);
        CustemExecuteStreamHandler streamHandler = new CustemExecuteStreamHandler();
        streamHandler.setOutputStreamLineHandler(streamLineHandler);
        executor.setStreamHandler(streamHandler);

        try {
            executor.execute(cmdLine, resultHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            resultHandler.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            streamHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        watchdog.destroyProcess();
    }
}
