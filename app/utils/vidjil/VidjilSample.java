package utils.vidjil;

import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;

import java.util.*;

/**
 * Created by bvdmitri on 26.04.16.
 */

public class VidjilSample {
    public class SamplesField {
        public int number;
        public String[] original_names;

        public SamplesField(String name) {
            this.number = 1;
            this.original_names = new String[1];
            original_names[0] = name;
        }
    }
    public class ClonesField {
        public String id;
        public String sequence;
        public Map<String, String> seg;
        public Map<String, String> junction;
        public long[] reads;

        public ClonesField(Clonotype clonotype, String id) {
            this.id = id;
            this.sequence = clonotype.getCdr3nt();
            this.reads = new long[1];
            this.reads[0] = clonotype.getCount();
            this.junction = new HashMap<>();
            junction.put("aa", clonotype.getCdr3aa());
            junction.put("start", "0");
            this.seg = new HashMap<>();
            seg.put("5", clonotype.getV());
            seg.put("4", clonotype.getD());
            seg.put("3", clonotype.getJ());
            seg.put("5end", String.valueOf(clonotype.getVEnd()));
            seg.put("4start", String.valueOf(clonotype.getDStart()));
            seg.put("4end", String.valueOf(clonotype.getDEnd()));
            seg.put("3start", String.valueOf(clonotype.getJStart()));
        }
    }

    public String timestamp;
    public String vidjil_json_version;
    public SamplesField samples;
    public List<ClonesField> clones;

    public VidjilSample(Sample sample, String name) {
        this.timestamp = new Date().toString();
        this.vidjil_json_version = "2016a";
        this.samples = new SamplesField(name);
        this.clones = new ArrayList<>();
        int id = 0;
        for (Clonotype clonotype : sample) {
            clones.add(new ClonesField(clonotype, "clone_" + String.valueOf(id)));
            id++;
        }
    }

}
