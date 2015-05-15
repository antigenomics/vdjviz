package utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters;

import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;

public abstract class BinaryClonotypeFilter {
    protected BinaryClonotypeFilter() {};

    protected abstract boolean checkPass(ClonotypeBinary clonotype);
}
