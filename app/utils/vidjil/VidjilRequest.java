package utils.vidjil;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by bvdmitri on 20.04.16.
 */
public class VidjilRequest {
    private String token;
    private Integer sampleCount;
    private JsonNode json;

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }

    public Integer getSampleCount() {

        return sampleCount;
    }

    public String getToken() {
        return token;
    }

    public JsonNode getJson() {
        return json;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setJson(JsonNode json) {
        this.json = json;
    }
}
