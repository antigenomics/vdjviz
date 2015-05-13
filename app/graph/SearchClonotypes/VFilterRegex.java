package graph.SearchClonotypes;

import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.ClonotypeFilter;

import java.util.*;

public class VFilterRegex extends ClonotypeFilter {
    private final List<String> vSegmentList = new ArrayList<>();

    public VFilterRegex(boolean negative, String... vSegment) {
        super(negative);
        for (String s : vSegment) {
            vSegmentList.add(s.replaceAll("[*]", "[a-zA-z0-9-_+]*"));
        }
    }

    public VFilterRegex(String... vSegment) {
        this(false, vSegment);
    }

    @Override
    protected boolean checkPass(Clonotype clonotype) {
        boolean match = false;
        for (String s : vSegmentList) {
            if (clonotype.getV().matches(s)) {
                match = true;
                break;
            }
        }
        return match;
    }

    public List<String> getvSegmentList() {
        return Collections.unmodifiableList(vSegmentList);
    }
}
