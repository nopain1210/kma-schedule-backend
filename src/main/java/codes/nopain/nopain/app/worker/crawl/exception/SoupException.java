package codes.nopain.nopain.app.worker.crawl.exception;

public class SoupException extends Exception {
    public SoupException(int errorCode) {
        super(String.valueOf(errorCode));
    }
}
