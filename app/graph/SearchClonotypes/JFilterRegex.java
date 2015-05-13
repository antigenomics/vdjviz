package graph.SearchClonotypes;

import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.ClonotypeFilter;

import java.util.*;

public class JFilterRegex extends ClonotypeFilter {
    private final List<String> jSegmentList = new ArrayList<>();

    public JFilterRegex(boolean negative, String... jSegment) {
        super(negative);
        for (String s : jSegment) {
            jSegmentList.add(s.replaceAll("[*]", "[a-zA-z0-9-_+]*"));
        }
    }

    public JFilterRegex(String... jSegment) {
        this(false, jSegment);
    }

    @Override
    protected boolean checkPass(Clonotype clonotype) {
        boolean match = false;
        for (String s : jSegmentList) {
            if (clonotype.getJ().matches(s)) {
                match = true;
                break;
            }
        }
        return match;
    }

    public List<String> getvSegmentList() {
        return Collections.unmodifiableList(jSegmentList);
    }
}
