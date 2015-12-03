package utils.BinaryUtils.ClonotypeBinaryUtils;

import com.antigenomics.vdjtools.sample.Clonotype;
import utils.BinaryUtils.BytesHelper.BytesReaderHelper;
import utils.BinaryUtils.BytesHelper.BytesWriterHelper;
import java.io.Serializable;

public class ClonotypeBinary implements Serializable {
    private int size;
    private int sizeCdr3aa;
    private byte[] cdr3aa;
    private int sizeCdr3nt;
    private byte[] cdr3nt;
    private int vend;
    private int jstart;
    private int dstart;
    private int dend;
    private int sizeV;
    private byte[] v;
    private int sizeJ;
    private byte[] j;
    private int sizeD;
    private byte[] d;
    private int count;
    private double freq;

    public int getSize() {
        return size;
    }

    public byte[] getCdr3aa() {
        return cdr3aa;
    }

    public byte[] getCdr3nt() {
        return cdr3nt;
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

    public byte[] getV() {
        return v;
    }

    public byte[] getJ() {
        return j;
    }

    public byte[] getD() {
        return d;
    }

    public int getCount() {
        return count;
    }

    public double getFreq() {
        return freq;
    }

    public ClonotypeBinary(Clonotype clonotype) {
        cdr3aa = clonotype.getCdr3aa().getBytes();
        sizeCdr3aa = cdr3aa.length;
        cdr3nt = clonotype.getCdr3nt().getBytes();
        sizeCdr3nt = cdr3nt.length;
        vend = clonotype.getVEnd();
        jstart = clonotype.getJStart();
        dstart = clonotype.getDStart();
        dend = clonotype.getDEnd();
        v = clonotype.getV().getBytes();
        sizeV = v.length;
        j = clonotype.getJ().getBytes();
        sizeJ = j.length;
        d = clonotype.getD().getBytes();
        sizeD = d.length;
        count = (int) clonotype.getCount();
        freq = clonotype.getFreq();
        size = (
                cdr3aa.length +
                cdr3nt.length +
                11 * 4 +
                8 +
                v.length +
                j.length +
                d.length);
    }

    public ClonotypeBinary(byte[] bytes) {
        BytesReaderHelper readerHelper = new BytesReaderHelper(bytes);
        size = readerHelper.readInt();
        sizeCdr3aa = readerHelper.readInt();
        cdr3aa = readerHelper.readArray(sizeCdr3aa);
        sizeCdr3nt = readerHelper.readInt();
        cdr3nt = readerHelper.readArray(sizeCdr3nt);
        vend = readerHelper.readInt();
        jstart = readerHelper.readInt();
        dstart = readerHelper.readInt();
        dend = readerHelper.readInt();
        sizeV = readerHelper.readInt();
        v = readerHelper.readArray(sizeV);
        sizeJ = readerHelper.readInt();
        j = readerHelper.readArray(sizeJ);
        sizeD = readerHelper.readInt();
        d = readerHelper.readArray(sizeD);
        count = readerHelper.readInt();
        freq = readerHelper.readDouble();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[size];
        BytesWriterHelper writerHelper = new BytesWriterHelper(bytes);
        writerHelper.writeInt(size);
        writerHelper.writeInt(sizeCdr3aa);
        writerHelper.writeBytes(cdr3aa);
        writerHelper.writeInt(sizeCdr3nt);
        writerHelper.writeBytes(cdr3nt);
        writerHelper.writeInt(vend);
        writerHelper.writeInt(jstart);
        writerHelper.writeInt(dstart);
        writerHelper.writeInt(dend);
        writerHelper.writeInt(sizeV);
        writerHelper.writeBytes(v);
        writerHelper.writeInt(sizeJ);
        writerHelper.writeBytes(j);
        writerHelper.writeInt(sizeD);
        writerHelper.writeBytes(d);
        writerHelper.writeInt(count);
        writerHelper.writeDouble(freq);
        return bytes;
    }

    @Override
    public String toString() {
        return "ClonotypeBinary{" +
                "size=" + size +
                ", sizeCdr3aa=" + sizeCdr3aa +
                ", cdr3aa=" + ClonotypeBinaryUtils.byteToString(cdr3aa) +
                ", sizeCdr3nt=" + sizeCdr3nt +
                ", cdr3nt=" + ClonotypeBinaryUtils.byteToString(cdr3nt) +
                ", vend=" + vend +
                ", jstart=" + jstart +
                ", dstart=" + dstart +
                ", dend=" + dend +
                ", sizeV=" + sizeV +
                ", v=" + ClonotypeBinaryUtils.byteToString(v) +
                ", sizeJ=" + sizeJ +
                ", j=" + ClonotypeBinaryUtils.byteToString(j) +
                ", sizeD=" + sizeD +
                ", d=" + ClonotypeBinaryUtils.byteToString(d) +
                ", count=" + count +
                ", freq=" + freq +
                '}';
    }
}
