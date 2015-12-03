package graph.AnnotationTable;

import com.antigenomics.vdjtools.sample.Clonotype;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

public class AnnotationTableRow {
    public Integer index;
    public Double freq;
    public long count;
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

    public AnnotationTableRow(ClonotypeBinary clonotypeBinary, Integer index) {
        this.index = index;
        this.freq = clonotypeBinary.getFreq();
        this.count = clonotypeBinary.getCount();
        this.cdr = new AnnotationTableCdr(clonotypeBinary);
        this.v = ClonotypeBinaryUtils.byteToString(clonotypeBinary.getV());
        this.j = ClonotypeBinaryUtils.byteToString(clonotypeBinary.getJ());
        this.d = ClonotypeBinaryUtils.byteToString(clonotypeBinary.getD());
    }
}
