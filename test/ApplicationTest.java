
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.CompositeClonotypeFilter;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SequenceMatchFilter;
import org.junit.Test;
import java.io.IOException;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void clonotypeSearchTest() throws IOException {
        String fileName = "/home/bvdmitri/A2-i129.txt.gz";
        SampleFileConnection sampleFileConnection = new SampleFileConnection(fileName, Software.MiTcr);
        Sample sample = sampleFileConnection.getSample();
        SequenceMatchFilter sequenceMatchFilter = new SequenceMatchFilter("CSDGDG*YS", true, 2);
        CompositeClonotypeFilter compositeClonotypeFilter = new CompositeClonotypeFilter(sequenceMatchFilter);
        Sample sample1 = new Sample(sample, compositeClonotypeFilter);
        for (Clonotype clonotype : sample1) {
            System.out.println(clonotype.getCdr3aa());
        }
    }

    @Test
    public void speedTest() {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();
        String fileName = "/home/bvdmitri/A2-i129.txt.gz";
        SampleFileConnection sampleFileConnection = new SampleFileConnection(fileName, Software.VDJtools);
        Sample sample = sampleFileConnection.getSample();
        long finishTime = System.currentTimeMillis();
        System.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Time: " + (finishTime-startTime) + " ms");
        System.out.println("Memory: " + (memoryAfter - memoryBefore));
    }

    @Test
    public void regexTest() {
        String vGene = "TRBV*";
        String s = vGene.replaceAll("[*]", "[a-zA-z1-9-_+]+");
        String checkString = "TRBV9-2";
        System.out.println(checkString.matches(s));
    }

}
