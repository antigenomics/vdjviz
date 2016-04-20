package utils.vidjil;

/**
 * Created by bvdmitri on 20.04.16.
 */
public class VidjilResponse {
    private Boolean error;
    private String message;

    public VidjilResponse(Boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
