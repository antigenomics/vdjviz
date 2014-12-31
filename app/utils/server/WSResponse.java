package utils.server;

public class WSResponse {
    public String result;
    public String action;
    public String fileName;
    public String message;

    public WSResponse(String result, String action, String fileName, String message) {
        this.result = result;
        this.action = action;
        this.fileName = fileName;
        this.message = message;
    }
}
