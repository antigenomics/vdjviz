import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CountingInputStream;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.junit.*;

import org.xml.sax.SAXException;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void analysingTest() {
        try {
            FileInputStream fileInputStream = new FileInputStream("/home/bvdmitri/42.zip");
            BodyContentHandler bodyContentHandler = new BodyContentHandler(Integer.MAX_VALUE);
            CountingInputStream countingInputStream = new CountingInputStream(fileInputStream);
            TikaInputStream tikaInputStream = TikaInputStream.get(countingInputStream);
            SecureContentHandler secureContentHandler = new SecureContentHandler(bodyContentHandler, tikaInputStream);
            AutoDetectParser autoDetectParser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            autoDetectParser.parse(countingInputStream, secureContentHandler, metadata);
            System.out.println(secureContentHandler.getMaximumCompressionRatio());
        } catch (SAXException | TikaException | IOException e) {
            System.out.println("catched");
            e.printStackTrace();
        }
    }


}
