package graph.JointClonotypeHistogramChart;

import com.antigenomics.vdjtools.join.JointClonotype;
import graph.SpectratypeVChart.VColor;

import java.util.ArrayList;
import java.util.List;

public class JointClonotypeHistogramChart {
    private List<Point> values;
    private String color;
    private String key;
    private int count;

    public JointClonotypeHistogramChart(JointClonotype jointClonotype) {
        values = new ArrayList<>();
        key = jointClonotype.getClonotype().getCdr3aa();
        count = jointClonotype.getCount();
        for (int i = 0; i < jointClonotype.getParent().getNumberOfSamples(); i++) {
            values.add(new Point(i + 1, jointClonotype.getFreq(i)));
        }
    }

    public String getColor() {
        return color;
    }

    public int getCount() {
        return count;
    }

    public String getKey() {
        return key;
    }

    public List<Point> getValues() {
        return values;
    }
}
