package controllers.SampleCollectionAPI;

import graph.JointClonotypeHistogramChart.JointClonotypeHistogramChart;

import java.util.ArrayList;
import java.util.List;

public class JointClonotypesDataContainer {
    List<JointClonotypeHistogramChart> clonotypes;

    public JointClonotypesDataContainer() {
        clonotypes = new ArrayList<>();
    }

    public void setClonotypes(List<JointClonotypeHistogramChart> clonotypes) {
        this.clonotypes = clonotypes;
    }

    public void addHistogramChart(JointClonotypeHistogramChart jointClonotypeHistogramChart) {
        clonotypes.add(jointClonotypeHistogramChart);
    }

    public List<JointClonotypeHistogramChart> getClonotypes() {
        return clonotypes;
    }
}
