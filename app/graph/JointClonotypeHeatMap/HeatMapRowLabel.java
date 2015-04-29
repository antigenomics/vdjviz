package graph.JointClonotypeHeatMap;

import com.antigenomics.vdjtools.sample.Clonotype;

public class HeatMapRowLabel {
    private String cdr3aa;
    private String v;
    private String j;
    public int vend;
    public int jstart;
    public int dstart;
    public int dend;

    public HeatMapRowLabel(Clonotype clonotype) {
        this.cdr3aa = clonotype.getCdr3aa();
        this.v = clonotype.getV();
        this.j = clonotype.getJ();
        this.vend = clonotype.getVEnd();
        this.jstart = clonotype.getJStart();
        this.dstart = clonotype.getDStart();
        this.dend = clonotype.getDEnd();
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
}
