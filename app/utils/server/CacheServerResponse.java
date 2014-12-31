package utils.server;


public class CacheServerResponse {
    public String result;
    public String message;
    public Object data;

    public CacheServerResponse(String result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public CacheServerResponse(String result, Object data) {
        this.data = data;
        this.result = result;
    }
}
