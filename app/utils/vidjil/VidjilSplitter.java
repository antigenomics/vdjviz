package utils.vidjil;

import com.antigenomics.vdjtools.io.SampleStreamConnection;
import com.antigenomics.vdjtools.io.parser.VidjilParser;
import com.antigenomics.vdjtools.misc.Software;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataEntry;
import com.antigenomics.vdjtools.sample.metadata.MetadataTable;
import com.antigenomics.vdjtools.sample.metadata.MetadataUtil;
import com.antigenomics.vdjtools.sample.metadata.SampleMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by bvdmitri on 20.04.16.
 */
public class VidjilSplitter {

    /*public static List<Sample> convert(List<VidjilSample> samples) {
        List<Sample> convertedSamples = new ArrayList<>();

        MetadataTable metadataTable = new MetadataTable(Arrays.asList(VidjilParser.VIDJIL_SAMPLE_ID_COL));
        int i = 0;

        for (VidjilSample sample : samples) {
            InputStream stream = new ByteArrayInputStream(sample.getSample().toString().getBytes(StandardCharsets.UTF_8));

            SampleMetadata sampleMetadata = metadataTable.createRow(Integer.toString(i), Arrays.asList(Integer.toString(i)));

            Sample clonotypes = Sample.fromInputStream(stream, sampleMetadata, Software.Vidjil, -1, true, Software.Vidjil.getCollapseRequired());
            convertedSamples.add(clonotypes);

            i++;
        }

        return convertedSamples;
    }

    public static List<VidjilSample> split(JsonNode vidjil) throws Exception {
        if (vidjil == null) throw new Exception("Invalid request, unable to parse");
        List<VidjilSample> samples = new ArrayList<VidjilSample>();
        List<String> names = new ArrayList<String>();
        int samplesCount = 0;

        if (!vidjil.has("samples")) throw new Exception("'samples' field not found");
        final JsonNode samplesField = vidjil.get("samples");

        if (samplesField.has("number")) {
            samplesCount = samplesField.get("number").asInt();
        } else {
            throw new Exception("'samples.number' field not found");
        }

        if (samplesField.has("original_names")) {
            Iterator<JsonNode> original_names = samplesField.get("original_names").elements();
            while (original_names.hasNext()) names.add(original_names.next().asText());
        } else {
            throw new Exception("'samples.original_names' field not found");
        }

        if (samplesCount == 1) {
            samples.add(new VidjilSample(names.get(0), vidjil));
            return samples;
        }

        if (!vidjil.has("clones")) throw new Exception("'clones' field not found");
        JsonNode clones = vidjil.get("clones");
        if (!clones.isArray()) throw new Exception("'clones' should be an array");

        Iterator<JsonNode> elements = clones.elements();

        ObjectMapper mapper = new ObjectMapper();
        List<ArrayNode> nodes = new ArrayList<ArrayNode>();
        for (int i = 0; i < samplesCount; i++) nodes.add(mapper.createArrayNode());
        while (elements.hasNext()) {
            List<ObjectNode> objectNodeList = new ArrayList<ObjectNode>();
            for (int i = 0; i < samplesCount; i++) objectNodeList.add(mapper.createObjectNode());
            JsonNode next = elements.next();
            Iterator<Map.Entry<String, JsonNode>> fields = next.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> nextField = fields.next();
                for (int i = 0; i < samplesCount; i++) {
                    if (nextField.getValue().isArray()) {
                        objectNodeList.get(i).put(nextField.getKey(), mapper.createArrayNode().add(nextField.getValue().get(i).asInt()));
                    } else {
                        objectNodeList.get(i).put(nextField.getKey(), nextField.getValue());
                    }
                }
            }
            for (int i = 0; i < samplesCount; i++) nodes.get(i).add(objectNodeList.get(i));
        }
        for (int i = 0; i < samplesCount; i++) {
            ObjectNode clones1 = mapper.createObjectNode();
            clones1.put("clones", nodes.get(i));
            samples.add(new VidjilSample(names.get(i), clones1));
        }
        return samples;
    }*/
}
