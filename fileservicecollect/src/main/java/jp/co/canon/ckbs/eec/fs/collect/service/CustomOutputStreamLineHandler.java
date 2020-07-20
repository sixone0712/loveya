package jp.co.canon.ckbs.eec.fs.collect.service;

public interface CustomOutputStreamLineHandler {
    boolean processOutputLine(String line);
    boolean processErrorLine(String line);
}
