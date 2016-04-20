package controllers;

import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.io.parser.VidjilParser;
import com.antigenomics.vdjtools.misc.Software;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataTable;
import com.antigenomics.vdjtools.sample.metadata.SampleMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.server.Configuration;
import utils.vidjil.Vidjil;
import utils.vidjil.VidjilRequest;
import utils.vidjil.VidjilResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bvdmitri on 20.04.16.
 */

public class VidjilAPI extends Controller {

    public static Result index() {
        return ok(views.html.vidjil.render());
    }

    public static Result share() {
        if (!Configuration.isVidjilSharingEnabled()) return badRequest();

        JsonNode request = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        List<Sample> sampleList = new ArrayList<>();
        List<String> uniqueNames = new ArrayList<>();

        try {
            VidjilRequest vidjilRequest = mapper.convertValue(request, VidjilRequest.class);
            InputStream stream = new ByteArrayInputStream(vidjilRequest.getJson().toString().getBytes(StandardCharsets.UTF_8));
            MetadataTable metadataTable = new MetadataTable(Arrays.asList(VidjilParser.VIDJIL_SAMPLE_ID_COL));
            for (int i = 0; i < vidjilRequest.getSampleCount(); i++) {
                SampleMetadata sampleMetadata = metadataTable.createRow(Integer.toString(i), Arrays.asList(Integer.toString(i)));
                Sample clonotypes = Sample.fromInputStream(stream, sampleMetadata, Software.Vidjil, -1, true, Software.Vidjil.getCollapseRequired());
                sampleList.add(clonotypes);
                uniqueNames.add(Vidjil.saveSample(clonotypes, "Sample" + i));
            }
            String link = Vidjil.shareSamples(uniqueNames);
            return ok(Json.toJson(new VidjilResponse(false, link)));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(Json.toJson(new VidjilResponse(true, e.getMessage() == null ? "Server error" : e.getMessage())));
        }
    }
}
