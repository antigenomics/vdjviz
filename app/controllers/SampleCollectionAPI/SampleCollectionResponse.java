package controllers.SampleCollectionAPI;

public class SampleCollectionResponse {
    public String message;
    public String action;
    public Object data;
    public int progress;

    public SampleCollectionResponse(String message) {
        this.message = message;
    }

    public SampleCollectionResponse(String action, int progress) {
        this.action = action;
        this.progress = progress;
    }

    public SampleCollectionResponse(Object data) {
        this.action = "rendered";
        this.data = data;
    }

    public SampleCollectionResponse(String message, String action) {
        this.message = message;
        this.action = action;
    }

    public SampleCollectionResponse(String message, String action, int progress) {
        this.message = message;
        this.action = action;
        this.progress = progress;
    }
}