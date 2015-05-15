package utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters;

import org.apache.commons.lang3.StringUtils;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;

import java.util.regex.Pattern;

public class BinaryClonotypeSequenceFilter extends BinaryClonotypeFilter {
    private final static Pattern aaAccepted = Pattern.compile("[FLSYCWPHQRIMTNKVADEGX\\*_\\[\\]]+"),
            ntAccepted = Pattern.compile("[ATGCN\\[\\]]+");

    private final Pattern pattern;
    private final boolean aminoAcid;

    public BinaryClonotypeSequenceFilter(String patternString, boolean aminoAcid) {
        super();
        patternString = patternString.toUpperCase();

        if (!(aminoAcid ? aaAccepted : ntAccepted).matcher(patternString).matches()) {
            throw new IllegalArgumentException("Bad sequence pattern: " + patternString);
        }

        if (StringUtils.countMatches(patternString, '[') !=
                StringUtils.countMatches(patternString, ']')) {
            throw new IllegalArgumentException("Bad sequence pattern: " + patternString);
        }

        patternString = patternString.replaceAll("[XN]", ".");

        this.pattern = Pattern.compile(patternString);
        this.aminoAcid = aminoAcid;
    }

    @Override
    protected boolean checkPass(ClonotypeBinary clonotype) {
        String query = aminoAcid ? ClonotypeBinaryUtils.byteToString(clonotype.getCdr3aa()) : ClonotypeBinaryUtils.byteToString(clonotype.getCdr3nt());

        return pattern.matcher(query).find();
    }
}
