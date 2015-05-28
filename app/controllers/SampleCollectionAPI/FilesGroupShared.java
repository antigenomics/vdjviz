package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataUtil;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.SharedFile;
import models.SharedGroup;
import models.UserFile;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.*;

public class FilesGroupShared {
    private List<SharedFile> files;
    private Map<SharedFile, Sample> samples;
    private Set<String> vGenes;
    private Set<String> jGenes;

    public FilesGroupShared() {
        this.samples = new HashMap<>();
        this.files = new ArrayList<>();
        this.vGenes = new HashSet<>();
        this.jGenes = new HashSet<>();
    }

    public List<SharedFile> getFiles() {
        return files;
    }

    public Map<SharedFile, Sample> getSamples() {
        return samples;
    }

    private void addFile(SharedFile file) {
        SampleFileConnection sampleFileConnection = new SampleFileConnection(file.getFilePath(), file.getSoftwareType(), MetadataUtil.createSampleMetadata(MetadataUtil.fileName2id(file.getFileName())), true, false);
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

    public void createGroup(SharedGroup sharedGroup, String[] names, WebSocket.Out<JsonNode> out) {
        files.clear();
        samples.clear();
        int shift = 100 / names.length;
        int progress = shift;
        for (String name: names) {
            out.write(Json.toJson(new SampleCollectionResponse(name, "open", progress)));
            SharedFile sharedFileByName = sharedGroup.findSharedFileByName(name);
            if (sharedFileByName != null) {
                this.addFile(sharedFileByName);
            }
            progress += shift;
        }
        out.write(Json.toJson(new SampleCollectionResponse("", "opened", new GenesLists(vGenes, jGenes), 100)));
    }

}
