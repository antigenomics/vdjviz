package utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters;

import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryContainer;

import java.util.ArrayList;
import java.util.List;

public class FilteredBinaryClonotypes {
    private ClonotypeBinaryContainer clonotypeBinaries;
    private List<BinaryClonotypeFilter> filterList;
    private List<ClonotypeBinary> filtered = new ArrayList<>();

    public FilteredBinaryClonotypes(ClonotypeBinaryContainer clonotypeBinaries, List<BinaryClonotypeFilter> filterList) {
        this.clonotypeBinaries = clonotypeBinaries;
        this.filterList = filterList;
    }

    public List<ClonotypeBinary> getFiltered(int count) {
        filtered.clear();
        int found = 0;
        for (ClonotypeBinary clonotypeBinary : clonotypeBinaries) {
            boolean match = true;
            for (BinaryClonotypeFilter binaryClonotypeFilter : filterList) {
                match = binaryClonotypeFilter.checkPass(clonotypeBinary);
                if (!match) break;
            }
            if (match) {
                filtered.add(clonotypeBinary);
                found++;
                if (found == count) break;
            }
        }
        return filtered;
    }


}
