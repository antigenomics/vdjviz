import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.join.JointClonotype;
import com.antigenomics.vdjtools.join.JointSample;
import com.antigenomics.vdjtools.join.OccurenceJoinFilter;
import com.antigenomics.vdjtools.overlap.OverlapType;
import com.antigenomics.vdjtools.sample.Sample;
import graph.JointClonotypeHeatMap.JointClonotypeHeatMap;
import graph.JointClonotypeHeatMap.JointClonotypeHeatMapCell;
import org.junit.Test;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void jointTest() {
        Software software = Software.MiTcr;
        SampleFileConnection sampleFileConnection1 = new SampleFileConnection("/home/bvdmitri/Документы/bio/aging_lite/samples/A2-i129.txt.gz", software);
        SampleFileConnection sampleFileConnection2 = new SampleFileConnection("/home/bvdmitri/Документы/bio/aging_lite/samples/A2-i131.txt.gz", software);
        SampleFileConnection sampleFileConnection3 = new SampleFileConnection("/home/bvdmitri/Документы/bio/aging_lite/samples/A2-i132.txt.gz", software);
        SampleFileConnection sampleFileConnection4 = new SampleFileConnection("/home/bvdmitri/Документы/bio/aging_lite/samples/A2-i133.txt.gz", software);
        SampleFileConnection sampleFileConnection5 = new SampleFileConnection("/home/bvdmitri/Документы/bio/aging_lite/samples/A2-i134.txt.gz", software);
        Sample[] samples = new Sample[5];
        samples[0] = sampleFileConnection1.getSample();
        samples[1] = sampleFileConnection2.getSample();
        samples[2] = sampleFileConnection3.getSample();
        samples[3] = sampleFileConnection4.getSample();
        samples[4] = sampleFileConnection5.getSample();
        JointSample jointClonotypes = new JointSample(OverlapType.Strict, samples, new OccurenceJoinFilter(2));
        JointClonotypeHeatMap jointClonotypeHeatMap = new JointClonotypeHeatMap(jointClonotypes, 5);
        for (JointClonotypeHeatMapCell jointClonotypeHeatMapCell : jointClonotypeHeatMap.getValues()) {
            System.out.println(jointClonotypeHeatMapCell.toString());
        }
    }


}
