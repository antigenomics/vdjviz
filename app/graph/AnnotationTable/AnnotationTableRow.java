package graph.AnnotationTable;

import com.antigenomics.vdjtools.Clonotype;

public class AnnotationTableRow {
    public Integer index;
    public Double freq;
    public Integer count;
    public AnnotationTableCdr3nt cdr3nt;
    public AnnotationTableCdr3aa cdr3aa;
    public String v;
    public String j;

    public AnnotationTableRow(Clonotype clonotype, Integer index) {
        this.index = index;
        this.cdr3aa = new AnnotationTableCdr3aa(clonotype.getCdr3aa(), -1, clonotype.getVEnd(), clonotype.getJStart(), clonotype.getDStart(), clonotype.getDEnd());
        this.freq = clonotype.getFreq();
        this.count = clonotype.getCount();
        this.v = clonotype.getV();
        this.j = clonotype.getJ();
        this.cdr3nt = new AnnotationTableCdr3nt(clonotype.getCdr3nt(), -1, clonotype.getVEnd(), clonotype.getJStart(), clonotype.getDStart(), clonotype.getDEnd());
    }
}
