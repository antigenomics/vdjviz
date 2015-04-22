package controllers.SampleCollectionAPI;

import graph.JointClonotypeHistogramChart.JointClonotypeHistogramChart;

import java.util.ArrayList;
import java.util.List;

public class JointClonotypesDataContainer {
    List<JointClonotypeHistogramChart> histogramCharts;

    public JointClonotypesDataContainer() {
        histogramCharts = new ArrayList<>();
    }

    public void setHistogramCharts(List<JointClonotypeHistogramChart> histogramCharts) {
        this.histogramCharts = histogramCharts;
    }

    public void addHistogramChart(JointClonotypeHistogramChart jointClonotypeHistogramChart) {
        histogramCharts.add(jointClonotypeHistogramChart);
    }

    public List<JointClonotypeHistogramChart> getHistogramCharts() {
        return histogramCharts;
    }
}
