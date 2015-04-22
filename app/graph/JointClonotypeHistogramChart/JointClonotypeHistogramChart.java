package graph.JointClonotypeHistogramChart;

import com.antigenomics.vdjtools.join.JointClonotype;
import graph.SpectratypeVChart.VColor;

import java.util.ArrayList;
import java.util.List;

public class JointClonotypeHistogramChart {
    private List<Point> values;
    private String key;
    private ClonotypeMetadata clonotypeMetadata;

    public JointClonotypeHistogramChart(JointClonotype jointClonotype) {
        values = new ArrayList<>();
        key = jointClonotype.getClonotype().getCdr3aa();
        clonotypeMetadata = new ClonotypeMetadata(jointClonotype.getClonotype());
        for (int i = 0; i < jointClonotype.getParent().getNumberOfSamples(); i++) {
            values.add(new Point(i + 1, jointClonotype.getFreq(i)));
        }
    }

    public ClonotypeMetadata getClonotypeMetadata() {
        return clonotypeMetadata;
    }

    public String getKey() {
        return key;
    }

    public List<Point> getValues() {
        return values;
    }
}
