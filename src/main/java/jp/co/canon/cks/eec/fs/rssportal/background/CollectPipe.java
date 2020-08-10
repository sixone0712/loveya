package jp.co.canon.cks.eec.fs.rssportal.background;

@FunctionalInterface
public interface CollectPipe {
    void run() throws CollectException;
}
