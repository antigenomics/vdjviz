import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;

public class ComputationTest {

    @Test
    public void md5Check() throws Exception {
        String testString = "This is a test";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(testString.getBytes(), 0, testString.length());
        System.out.println("MD5: " + new BigInteger(1, messageDigest.digest()).toString(16));
    }

}
