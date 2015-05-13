package graph.SearchClonotypes;

import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.*;
import com.milaboratory.core.sequence.Alphabets;
import com.milaboratory.core.sequence.AminoAcidAlphabet;
import controllers.AccountAPI;
import graph.AnnotationTable.AnnotationTableRow;
import models.UserFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchClonotypes {

    static class SingleSampleSearchResults {
        public List<AnnotationTableRow> rows;
        public AccountAPI.SearchClonotypesRequest parameters;

        public SingleSampleSearchResults(List<AnnotationTableRow> rows, AccountAPI.SearchClonotypesRequest parameters) {
            this.rows = rows;
            this.parameters = parameters;
        }
    }

    public static SingleSampleSearchResults searchSingleSample(UserFile file, AccountAPI.SearchClonotypesRequest searchClonotypesRequest) {
        List<AnnotationTableRow> rows = new ArrayList<>();
        SampleFileConnection sampleFileConnection = new SampleFileConnection(file.getPath(), file.getSoftwareType());
        Sample sample = sampleFileConnection.getSample();
        SequenceMatchFilter sequenceMatchFilter = new SequenceMatchFilter(searchClonotypesRequest.sequence, searchClonotypesRequest.aminoAcid, searchClonotypesRequest.maxMismatches);
        CompositeClonotypeFilter compositeClonotypeFilter;
        if (searchClonotypesRequest.vFilter.length > 0 && searchClonotypesRequest.jFilter.length > 0) {
            System.out.println(1);
            VFilter vFilter = new VFilter(searchClonotypesRequest.vFilter);
            JFilter jFilter = new JFilter(searchClonotypesRequest.jFilter);
            compositeClonotypeFilter = new CompositeClonotypeFilter(sequenceMatchFilter, vFilter, jFilter);
        } else if (searchClonotypesRequest.vFilter.length > 0) {
            System.out.println(2);
            VFilter vFilter = new VFilter(searchClonotypesRequest.vFilter);
            compositeClonotypeFilter = new CompositeClonotypeFilter(sequenceMatchFilter, vFilter);
        } else if (searchClonotypesRequest.jFilter.length > 0) {
            System.out.println(3);
            JFilter jFilter = new JFilter(searchClonotypesRequest.jFilter);
            compositeClonotypeFilter = new CompositeClonotypeFilter(sequenceMatchFilter, jFilter);
        } else {
            System.out.println(4);
            compositeClonotypeFilter = new CompositeClonotypeFilter(sequenceMatchFilter);
        }

        int index = 1;
        for (Clonotype clonotype : new Sample(sample, compositeClonotypeFilter)) {

            rows.add(new AnnotationTableRow(clonotype, index));
            index++;
        }
        return new SingleSampleSearchResults(rows, searchClonotypesRequest);
    }

}
