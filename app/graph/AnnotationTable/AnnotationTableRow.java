package graph.AnnotationTable;

import com.antigenomics.vdjtools.sample.Clonotype;

public class AnnotationTableRow {
    public Integer index;
    public Double freq;
    public Integer count;
    public AnnotationTableCdr cdr;
    public String v;
    public String j;
    public String d;

    public AnnotationTableRow(Clonotype clonotype, Integer index) {
        this.index = index;
        this.freq = clonotype.getFreq();
        this.count = clonotype.getCount();
        this.cdr = new AnnotationTableCdr(clonotype);
        this.v = clonotype.getV();
        this.j = clonotype.getJ();
        this.d = clonotype.getD();
    }
}
