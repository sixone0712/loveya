package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.background.autocollect.CollectException;

@FunctionalInterface
public interface CollectPipe {
    void run() throws CollectException;
}
