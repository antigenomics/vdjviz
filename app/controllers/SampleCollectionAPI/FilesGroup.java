package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataUtil;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.UserFile;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesGroup {
    private List<UserFile> files;
    private Map<UserFile, Sample> samples;

    public FilesGroup() {
        this.samples = new HashMap<>();
        this.files = new ArrayList<>();
    }

    public List<UserFile> getFiles() {
        return files;
    }

    public Map<UserFile, Sample> getSamples() {
        return samples;
    }

    private void addFile(UserFile file) {
        SampleFileConnection sampleFileConnection = new SampleFileConnection(file.getPath(), file.getSoftwareType(), MetadataUtil.createSampleMetadata(MetadataUtil.fileName2id(file.getFileName())), true, false);
        samples.put(file, sampleFileConnection.getSample());
        files.add(file);
    }



    public void createGroup(Account account, String[] names, WebSocket.Out<JsonNode> out) {
        files.clear();
        samples.clear();
        int shift = 100 / names.length;
        int progress = shift;
        for (String name: names) {
            out.write(Json.toJson(new SampleCollectionResponse(name, "open", progress)));
            UserFile userFile = UserFile.fyndByNameAndAccount(account, name);
            if (userFile != null) {
                this.addFile(userFile);
            }
            progress += shift;
        }
        out.write(Json.toJson(new SampleCollectionResponse("", "opened", 100)));
    }

}
