package controllers.SampleCollectionAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.JointClonotypeHeatMap.JointClonotypeHeatMap;
import models.Account;
import models.LocalUser;
import models.SharedGroup;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import securesocial.core.java.SecureSocial;



public class SampleCollectionAPI extends Controller {

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
                                    Object data = new JointClonotypeHeatMap(jointSamplesContainer.getJointClonotypes(), filesGroup.getFiles());
                                    out.write(Json.toJson(new SampleCollectionResponse(data)));
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

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        out.close();
                    }
                });
            }



        };
    }

    public static WebSocket<JsonNode> openShared(final String link) {
        final SharedGroup sharedGroup = SharedGroup.findByLink(link);
        if (sharedGroup == null)
            return null;
        return new WebSocket<JsonNode>() {
            @Override
            public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
                final FilesGroupShared filesGroup = new FilesGroupShared();
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
                                    filesGroup.createGroup(sharedGroup, sampleCollectionRequest.names, out);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    out.write(Json.toJson(new SampleCollectionResponse("Error while opening files", "error")));
                                }
                                break;
                            case "render":
                                try {
                                    jointSamplesContainer.joinShared(filesGroup.getSamples(), sampleCollectionRequest.joinParameters);
                                    Object data = new JointClonotypeHeatMap(jointSamplesContainer.getJointClonotypes(), filesGroup.getFiles(), 0);
                                    out.write(Json.toJson(new SampleCollectionResponse(data)));
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

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        out.close();
                    }
                });
            }



        };
    }
}
