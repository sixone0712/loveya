package jp.co.canon.cks.eec.fs.rssportal.model.error;

import java.util.HashMap;
import java.util.Map;

public class RSSError {

    private Map<String, Object> error;

    public RSSError() {
        error = new HashMap<>();
    }

    public void setReason(String reason) {
       this.error.put("reason", reason);
    }

    public void setMessage(String message) {
        this.error.put("message", message);
    }

    public Map<String, Object> getRSSError() {
        return this.error;
    }
}
