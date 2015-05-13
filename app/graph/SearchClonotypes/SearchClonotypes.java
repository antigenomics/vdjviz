package graph.SearchClonotypes;

import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.CompositeClonotypeFilter;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SequenceMatchFilter;
import com.milaboratory.core.sequence.Alphabets;
import com.milaboratory.core.sequence.AminoAcidAlphabet;
import graph.AnnotationTable.AnnotationTableRow;
import models.UserFile;

import java.util.ArrayList;
import java.util.List;

public class SearchClonotypes {

    static class SingleSampleSearchResults {
        public List<AnnotationTableRow> rows;
        public String sequence;
        public Integer maxMismatches;

        public SingleSampleSearchResults(List<AnnotationTableRow> rows, String sequence, Integer maxMismatches) {
            this.rows = rows;
            this.sequence = sequence;
            this.maxMismatches = maxMismatches;
        }
    }

    public static SingleSampleSearchResults searchSingleSample(UserFile file, String sequence, boolean aminoAcid, Integer maxMismatches) {
        List<AnnotationTableRow> rows = new ArrayList<>();
        SampleFileConnection sampleFileConnection = new SampleFileConnection(file.getPath(), file.getSoftwareType());
        Sample sample = sampleFileConnection.getSample();
        SequenceMatchFilter sequenceMatchFilter = new SequenceMatchFilter(sequence, aminoAcid, maxMismatches);
        CompositeClonotypeFilter compositeClonotypeFilter = new CompositeClonotypeFilter(sequenceMatchFilter);
        int index = 1;
        for (Clonotype clonotype : new Sample(sample, compositeClonotypeFilter)) {

            rows.add(new AnnotationTableRow(clonotype, index));
            index++;
        }
        return new SingleSampleSearchResults(rows, sequence, maxMismatches);
    }

}
