
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Clonotype;
import org.junit.Test;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    class ClonotypeSerializable implements Serializable {
        private String cdr3aa, cdr3nt, v, j, d;
        private int vend, jstart, dstart,  dend;

        public ClonotypeSerializable(Clonotype clonotype) {
            this.cdr3aa = clonotype.getCdr3aa();
            this.cdr3nt = clonotype.getCdr3nt();
            this.vend = clonotype.getVEnd();
            this.jstart = clonotype.getJStart();
            this.dstart = clonotype.getDStart();
            this.dend = clonotype.getDEnd();
            this.v = clonotype.getV();
            this.j = clonotype.getJ();
            this.d = clonotype.getD();
        }
    }

    @Test
    public void mapDBTest() throws IOException {
        String fileName = "/home/bvdmitri/test.txt";
        String dbName = "/home/bvdmitri/test.db";
        File file = new File(dbName);
        if (!file.exists()) file.createNewFile();
        DB db = DBMaker.newFileDB(file).make();
        boolean created  = false;
        if (!created) {
            HTreeMap<Object, Object> clonotypes = db.createHashMap("clonotypes").make();
            SampleFileConnection sampleFileConnection = new SampleFileConnection(fileName, Software.VDJtools);
            int index = 0;
            for (Clonotype clonotype : sampleFileConnection.getSample()) {
                clonotypes.put(index, new ClonotypeSerializable(clonotype));
                index++;
            }
            db.commit();
            db.compact();
            db.close();
        } else {
            HTreeMap<Object, Object> clonotypes = db.getHashMap("clonotypes");
            for (Map.Entry<Object, Object> objectObjectEntry : clonotypes.entrySet()) {
                System.out.println(objectObjectEntry.getValue().toString());
            }
            db.close();
        }

    }

}
