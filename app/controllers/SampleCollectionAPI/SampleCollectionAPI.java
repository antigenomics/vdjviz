package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.join.JointClonotype;
import com.antigenomics.vdjtools.join.JointSample;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.JointClonotypeHistogramChart.JointClonotypeHistogramChart;
import models.Account;
import models.LocalUser;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import securesocial.core.java.SecureSocial;


public class SampleCollectionAPI extends Controller {

    private static Object requestData(JointSamplesContainer jointSamplesContainer) {
        JointClonotypesDataContainer jointClonotypesDataContainer = new JointClonotypesDataContainer();
        JointSample jointClonotypes = jointSamplesContainer.getJointClonotypes();
        for (JointClonotype jointClonotype : jointClonotypes) {
            jointClonotypesDataContainer.addHistogramChart(new JointClonotypeHistogramChart(jointClonotype));
        }
        return jointClonotypesDataContainer;
    }

    public static WebSocket<JsonNode> open() {
        LocalUser localUser = LocalUser.find.byId(SecureSocial.currentUser().identityId().userId());
        final Account account = localUser.account;
        return new WebSocket<JsonNode>() {
            @Override
            public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
                final FilesGroup filesGroup = new FilesGroup();
                final JointSamplesContainer jointSamplesContainer = new JointSamplesContainer();
                in.onMessage(new F.Callback<JsonNode>() {
                    public void invoke(JsonNode event) {
                        SampleCollectionRequest sampleCollectionRequest;
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            sampleCollectionRequest = objectMapper.convertValue(event, SampleCollectionRequest.class);
                        } catch (Exception e) {
                            out.write(Json.toJson(new SampleCollectionResponse("Invalid request")));
                            return;
                        }
                        switch (sampleCollectionRequest.action) {
                            case "open":
                                try {
                                    filesGroup.createGroup(account, sampleCollectionRequest.names, out);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    out.write(Json.toJson(new SampleCollectionResponse("Error while opening files", "error")));
                                }
                                break;
                            case "render":
                                try {
                                    jointSamplesContainer.join(filesGroup.getSamples(), sampleCollectionRequest.joinParameters);
                                    out.write(Json.toJson(new SampleCollectionResponse(requestData(jointSamplesContainer))));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    out.write(Json.toJson(new SampleCollectionResponse("Error while joining", "error")));
                                }
                                break;
                            default:
                                out.write(Json.toJson(new SampleCollectionResponse("Invalid action")));
                        }
                    }
                });
            }
        };
    }
}
