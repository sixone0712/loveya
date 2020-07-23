package jp.co.canon.ckbs.eec.fs.collect.executor;

public interface CustomOutputStreamLineHandler {
    boolean processOutputLine(String line);
    boolean processErrorLine(String line);
}
