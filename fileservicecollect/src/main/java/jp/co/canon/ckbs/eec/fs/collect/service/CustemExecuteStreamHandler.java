package jp.co.canon.ckbs.eec.fs.collect.service;

import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.*;

public class CustemExecuteStreamHandler implements ExecuteStreamHandler {
    Thread stdOutThread;
    Thread stdErrThread;
    BufferedReader stdOutReader;
    BufferedReader stdErrReader;

    CustomOutputStreamLineHandler stdOutHandler;

    public void setOutputStreamLineHandler(CustomOutputStreamLineHandler handler){
        stdOutHandler = handler;
    }

    @Override
    public void setProcessInputStream(OutputStream os) throws IOException {

    }

    @Override
    public void setProcessErrorStream(InputStream is) throws IOException {
        stdErrReader = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void setProcessOutputStream(InputStream is) throws IOException {
        stdOutReader = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void start() throws IOException {
        stdOutThread = new Thread(() -> {
            while(true){
                try {
                    String line = stdOutReader.readLine();
                    if (line == null){
                        break;
                    }
                    if (stdOutHandler.processOutputLine(line) == false){
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        });
        stdErrThread = new Thread(()->{
            while(true){
                try {
                    String line = stdErrReader.readLine();
                    if (line == null){
                        break;
                    }
                    if (stdOutHandler.processErrorLine(line) == false){
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        });
        stdOutThread.start();
        stdErrThread.start();
    }

    @Override
    public void stop() throws IOException {

    }

    public void join() throws InterruptedException {
        if (stdErrThread != null){
            stdErrThread.join();
        }
        if (stdOutThread != null){
            stdOutThread.join();
        }
    }

}
