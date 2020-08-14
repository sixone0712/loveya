package jp.co.canon.ckbs.eec.fs.collect.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import java.io.IOException;

@Slf4j
public class CustomExecutor {
    ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);

    public void execute(CommandLine cmdLine, CustomOutputStreamLineHandler streamLineHandler){
        log.trace("execute command : {}", cmdLine.toString());
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);
        CustemExecuteStreamHandler streamHandler = new CustemExecuteStreamHandler();
        streamHandler.setOutputStreamLineHandler(streamLineHandler);
        executor.setStreamHandler(streamHandler);

        try {
            executor.execute(cmdLine, resultHandler);
        } catch (IOException e) {
            log.error("failed to execute command({}), with exception({})", cmdLine.toString(), e.getMessage());
        }

        try {
            resultHandler.waitFor();
        } catch (InterruptedException e) {
            log.error("interruped exception occurred({}) while wait process end.", e.getMessage());
        }

        try {
            streamHandler.join();
        } catch (InterruptedException e) {
            log.error("interruped exception occurred({}) while stream handler end.", e.getMessage());
        }
    }

    public void stop(){
        log.trace("stop executor :{}", this);
        watchdog.destroyProcess();
    }
}
