package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;

public class CollectMpaException extends Exception {

    private final String machine;

    public CollectMpaException(String machine) {
        this.machine = machine;
    }

    public CollectMpaException(String machine, String message) {
        super(message);
        this.machine = machine;
    }

    public String getMachine() {
        return machine;
    }
}
