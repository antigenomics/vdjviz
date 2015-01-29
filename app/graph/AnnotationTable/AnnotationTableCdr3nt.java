package graph.AnnotationTable;

public class AnnotationTableCdr3nt {
    public String cdr3nt;
    public Integer pos;
    public Integer vend;
    public Integer jstart;
    public Integer dstart;
    public Integer dend;

    public AnnotationTableCdr3nt(String cdr3nt, Integer pos, Integer vend, Integer jstart, Integer dstart, Integer dend) {
        this.cdr3nt = cdr3nt;
        this.pos = pos;
        this.vend = vend;
        this.jstart = jstart;
        this.dstart = dstart;
        this.dend = dend;
    }
}
