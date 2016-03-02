package utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters;

import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

import static com.antigenomics.vdjtools.misc.CommonUtil.inFrame;
import static com.antigenomics.vdjtools.misc.CommonUtil.noStop;

public class BinaryClonotypeNonFunctionalFilter  extends BinaryClonotypeFilter {

    public BinaryClonotypeNonFunctionalFilter() {
        super();
    }

    @Override
    protected boolean checkPass(ClonotypeBinary clonotype) {
        String seq = ClonotypeBinaryUtils.byteToString(clonotype.getCdr3aa());

        return !inFrame(seq) || !noStop(seq);
    }
}
