package graph.JointClonotypeHistogramChart;

import com.antigenomics.vdjtools.sample.Clonotype;

/**
 * Created by bvdmitri on 22.04.15.
 */
public class ClonotypeMetadata {
    private String cdr3nt;
    private String cdr3aa;
    private String v;
    private String j;
    private String d;
    private int count;
    public int vend;
    public int jstart;
    public int dstart;
    public int dend;


    public ClonotypeMetadata(Clonotype clonotype) {
        this.cdr3aa = clonotype.getCdr3aa();
        this.cdr3nt = clonotype.getCdr3nt();
        this.v = clonotype.getV();
        this.j = clonotype.getJ();
        this.d = clonotype.getD();
        this.count = clonotype.getCount();
        this.vend = clonotype.getVEnd();
        this.jstart = clonotype.getJStart();
        this.dstart = clonotype.getDStart();
        this.dend = clonotype.getDEnd();

    }

    public int getVend() {
        return vend;
    }

    public int getJstart() {
        return jstart;
    }

    public int getDstart() {
        return dstart;
    }

    public int getDend() {
        return dend;
    }

    public int getCount() {
        return count;
    }

    public String getCdr3nt() {
        return cdr3nt;
    }

    public String getCdr3aa() {
        return cdr3aa;
    }

    public String getV() {
        return v;
    }

    public String getJ() {
        return j;
    }

    public String getD() {
        return d;
    }
}
