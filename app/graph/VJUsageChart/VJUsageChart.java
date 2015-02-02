package graph.VJUsageChart;

import graph.SpectratypeVChart.VColor;

import java.util.ArrayList;
import java.util.List;

public class VJUsageChart {
    public double[][] matrix;
    public List<String> labels;
    public List<String> colors;

    public VJUsageChart(double[][] matrix, List<String> labels) {
        this.matrix = matrix;
        this.labels = labels;
        this.colors = new ArrayList<>();
        for (String label : labels) {
            colors.add(VColor.getColor(label));
        }
    }
}
