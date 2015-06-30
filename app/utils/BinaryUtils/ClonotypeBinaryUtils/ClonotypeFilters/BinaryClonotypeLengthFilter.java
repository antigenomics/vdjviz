package utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters;

import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

public class BinaryClonotypeLengthFilter extends BinaryClonotypeFilter {
    public enum LengthType {
        LESS,
        EQUAL,
        OVER
    }

    private int length = 0;
    private LengthType lengthType = LengthType.EQUAL;
    private boolean aminoAcid = true;

    public BinaryClonotypeLengthFilter(boolean negative, int length, LengthType lengthType, boolean aminoAcid) {
        super();
        this.length = length;
        this.lengthType = lengthType;
        this.aminoAcid = aminoAcid;
    }

    public BinaryClonotypeLengthFilter(int length, LengthType lengthType, boolean aminoAcid) {
        this(false, length, lengthType, aminoAcid);
    }

    @Override
    protected boolean checkPass(ClonotypeBinary clonotype) {
        int cdrLength = 0;
        if (aminoAcid) {
            cdrLength = ClonotypeBinaryUtils.byteToString(clonotype.getCdr3aa()).length();
        } else {
            cdrLength = ClonotypeBinaryUtils.byteToString(clonotype.getCdr3nt()).length();
        }
        switch (lengthType) {
            case LESS:
                if (cdrLength <= length) return true;
                break;
            case EQUAL:
                if (cdrLength == length) return true;
                break;
            case OVER:
                if (cdrLength >= length) return true;
                break;
            default:
                return false;
        }
        return false;
    }
}
