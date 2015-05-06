package graph.AnnotationTable;

import com.antigenomics.vdjtools.sample.Clonotype;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

public class AnnotationTableCdr {

    public String cdr3aa;
    public String cdr3nt;
    public int vend;
    public int jstart;
    public int dstart;
    public int dend;


    public AnnotationTableCdr(ClonotypeBinary clonotypeBinary) {
        this.cdr3aa = ClonotypeBinaryUtils.byteToString(clonotypeBinary.getCdr3aa());
        this.cdr3nt = ClonotypeBinaryUtils.byteToString(clonotypeBinary.getCdr3nt());
        this.vend = clonotypeBinary.getVend();
        this.jstart = clonotypeBinary.getJstart();
        this.dstart  = clonotypeBinary.getDstart();
        this.dend = clonotypeBinary.getDend();
    }

    public AnnotationTableCdr(Clonotype clonotype) {
        this.cdr3aa = clonotype.getCdr3aa();
        this.cdr3nt = clonotype.getCdr3nt();
        this.vend = clonotype.getVEnd();
        this.jstart = clonotype.getJStart();
        this.dstart = clonotype.getDStart();
        this.dend = clonotype.getDEnd();
    }

}
