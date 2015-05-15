
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.CompositeClonotypeFilter;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SequenceMatchFilter;
import graph.SearchClonotypes.JFilterRegex;
import graph.SearchClonotypes.VFilterRegex;
import org.junit.Test;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryContainer;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void clonotypeBinaryIterationTest() {
        long startTime = System.currentTimeMillis();
        String path = "/home/bvdmitri/clonotype.bin";
        ClonotypeBinaryContainer container = new ClonotypeBinaryContainer(path);
        List<BinaryClonotypeFilter> filters = new ArrayList<>();
        filters.add(new BinaryClonotypeVFilter("TRBV4-1", "TRBV4-2"));
        filters.add(new BinaryClonotypeJFilter("TRBJ2-3"));
        filters.add(new BinaryClonotypeSequenceFilter("CASSQEVREPSTDTQYF", true));
        FilteredBinaryClonotypes filteredBinaryClonotypes = new FilteredBinaryClonotypes(container, filters);
        for (ClonotypeBinary clonotypeBinary : filteredBinaryClonotypes.getFiltered(100)) {
            System.out.println(
                    ClonotypeBinaryUtils.byteToString(clonotypeBinary.getCdr3aa()) + "| |" +
                    ClonotypeBinaryUtils.byteToString(clonotypeBinary.getV()) + "| |" + ClonotypeBinaryUtils.byteToString(clonotypeBinary.getJ()));
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    @Test
    public void gg() {
        long startTime = System.currentTimeMillis();
        String path = "/home/bvdmitri/A2-i129.txt.gz";
        SampleFileConnection sampleFileConnection = new SampleFileConnection(path, Software.MiTcr);
        Sample sample = sampleFileConnection.getSample();
        VFilterRegex vFilterRegex = new VFilterRegex("TRBV4-1", "TRBV4-2");
        JFilterRegex jFilterRegex = new JFilterRegex("TRBJ2-3");
        SequenceMatchFilter sequenceMatchFilter = new SequenceMatchFilter("CASSQEVREPSTDTQYF", true, 2);
        CompositeClonotypeFilter compositeClonotypeFilter = new CompositeClonotypeFilter(vFilterRegex, jFilterRegex, sequenceMatchFilter);
        Sample filteredSample = new Sample(sample, compositeClonotypeFilter);
        for (Clonotype clonotype : filteredSample) {
            System.out.println(clonotype.getCdr3aa() + "| |" + clonotype.getV() + "| |" + clonotype.getJ());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }
}