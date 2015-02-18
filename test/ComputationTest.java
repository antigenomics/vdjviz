import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.diversity.QuantileStats;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class ComputationTest {

    @Test
    public void md5Check() throws Exception {
        String testString = "This is a test";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(testString.getBytes(), 0, testString.length());
        System.out.println("MD5: " + new BigInteger(1, messageDigest.digest()).toString(16));
    }

    @Test
    public void quantileTest() {
        String path = "test/A2-i141.txt.gz";
        File file = new File(path);
        if (file.exists()) {
            List<String> sampleFileNames = new ArrayList<>();
            sampleFileNames.add(file.getPath());
            SampleCollection sampleCollection = new SampleCollection(sampleFileNames, Software.MiTcr, false);
            Sample sample = sampleCollection.getAt(0);
            QuantileStats quantileStats = new QuantileStats(sample, 5);
            System.out.println("totalFreq: " + (quantileStats.getHighOrderFreq() + quantileStats.getDoubletonFreq() + quantileStats.getSingletonFreq()));
            System.out.println("singleFreq: " + quantileStats.getSingletonFreq());
            System.out.println("doubleFreq: " + quantileStats.getDoubletonFreq());
            System.out.println("highOrderFreq: " + quantileStats.getHighOrderFreq());
            System.out.println("Same: " + (quantileStats.getQuantileFrequency(0) +
                            quantileStats.getQuantileFrequency(1) +
                            quantileStats.getQuantileFrequency(2) +
                            quantileStats.getQuantileFrequency(3) +
                            quantileStats.getQuantileFrequency(4))
            );
        }
    }

}
