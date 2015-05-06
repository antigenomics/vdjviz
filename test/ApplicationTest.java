import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.join.JointClonotype;
import com.antigenomics.vdjtools.join.JointSample;
import com.antigenomics.vdjtools.join.OccurenceJoinFilter;
import com.antigenomics.vdjtools.overlap.OverlapType;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import graph.JointClonotypeHeatMap.JointClonotypeHeatMap;
import graph.JointClonotypeHeatMap.JointClonotypeHeatMapCell;
import org.junit.Test;
import utils.BinaryUtils.BytesHelper.BytesReaderHelper;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void binaryTest() throws IOException {
        SampleFileConnection sampleFileConnection = new SampleFileConnection("/home/bvdmitri/test.txt.gz", Software.MiTcr);
        Sample sample = sampleFileConnection.getSample();
        File file = new File("/home/bvdmitri/clonotype.bin");
        if (!file.exists()) {
            assert file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            ClonotypeBinaryUtils.writeToStream(fileOutputStream, sample);
            System.out.println("Writed");
        } else {
            for (int i = 0; i < 10; i++) {
                System.out.println(new ClonotypeBinary(sample.getAt(i)).toString());
            }
            System.out.println("\n\n");
            FileInputStream fileInputStream = new FileInputStream(file);
            List<ClonotypeBinary> clonotypeBinaries = ClonotypeBinaryUtils.readFromStream(fileInputStream, 9900, 1000);
            for (ClonotypeBinary clonotypeBinary : clonotypeBinaries) {
                System.out.println(clonotypeBinary.toString());
            }

        }
    }

    @Test
    public void intBinaryTest() {

    }
}
