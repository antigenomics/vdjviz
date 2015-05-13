package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataUtil;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.UserFile;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.*;

public class FilesGroup {
    private List<UserFile> files;
    private Map<UserFile, Sample> samples;
    private Set<String> vGenes;
    private Set<String> jGenes;

    public FilesGroup() {
        this.samples = new HashMap<>();
        this.files = new ArrayList<>();
        this.vGenes = new HashSet<>();
        this.jGenes = new HashSet<>();
    }

    public List<UserFile> getFiles() {
        return files;
    }

    public Map<UserFile, Sample> getSamples() {
        return samples;
    }

    private void addFile(UserFile file) {
        SampleFileConnection sampleFileConnection = new SampleFileConnection(file.getPath(), file.getSoftwareType(), MetadataUtil.createSampleMetadata(MetadataUtil.fileName2id(file.getFileName())), true, false);
        Sample sample = sampleFileConnection.getSample();
        for (Clonotype clonotype : sample) {
            vGenes.add(clonotype.getV());
            jGenes.add(clonotype.getJ());
        }
        samples.put(file, sample);
        files.add(file);
    }


    class GenesLists {
        private List<String> vGenesList;
        private List<String> jGenesList;

        public GenesLists(Set<String> vGenes, Set<String> jGenes) {
            vGenesList = new ArrayList<>();
            for (Object o : vGenes.toArray()) {
                vGenesList.add((String) o);
            }
            Collections.sort(vGenesList);
            jGenesList = new ArrayList<>();
            for (Object o : jGenes.toArray()) {
                jGenesList.add((String) o);
            }
            Collections.sort(jGenesList);
        }

        public List<String> getvGenesList() {
            return vGenesList;
        }

        public List<String> getjGenesList() {
            return jGenesList;
        }
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
        out.write(Json.toJson(new SampleCollectionResponse("", "opened", new GenesLists(vGenes, jGenes), 100)));
    }

}
