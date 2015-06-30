package utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters;

import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BinaryClonotypeDFilter extends BinaryClonotypeFilter {
    private final List<Pattern> patterns = new ArrayList<>();

    public BinaryClonotypeDFilter(String... segments) {
        super();
        for (String s : segments) {
            patterns.add(Pattern.compile(s.replaceAll("[*]", "[a-zA-z0-9-_+]*")));
        }
    }

    @Override
    protected boolean checkPass(ClonotypeBinary clonotype) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(ClonotypeBinaryUtils.byteToString(clonotype.getD())).find()) {
                return true;
            }
        }
        return false;
    }
}
