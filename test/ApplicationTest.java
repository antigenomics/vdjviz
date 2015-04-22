import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.join.JointClonotype;
import com.antigenomics.vdjtools.join.JointSample;
import com.antigenomics.vdjtools.join.OccurenceJoinFilter;
import com.antigenomics.vdjtools.overlap.OverlapType;
import com.antigenomics.vdjtools.sample.Sample;
import org.junit.Test;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public static void jointTest() {
        Software software = Software.VDJtools;
        SampleFileConnection sampleFileConnection = new SampleFileConnection("/home/bvdmitri/A3-i101.txt", software);
        Sample[] samples = new Sample[1];
        samples[0] = sampleFileConnection.getSample();
        JointSample jointClonotypes = new JointSample(OverlapType.Strict, samples, new OccurenceJoinFilter(2));

    }


}
