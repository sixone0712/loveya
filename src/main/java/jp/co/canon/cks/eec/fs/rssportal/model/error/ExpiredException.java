package jp.co.canon.cks.eec.fs.rssportal.model.error;

public class ExpiredException extends RuntimeException{
    private static final long serialVersionUID = -2238030302650813813L;

    public ExpiredException() {
        super("Token has expired.");
    }
}