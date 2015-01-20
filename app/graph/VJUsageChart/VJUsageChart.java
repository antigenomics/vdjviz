package graph.VJUsageChart;

import java.util.ArrayList;
import java.util.List;

public class VJUsageChart {
    public double[][] matrix;
    public List<String> labels;

    public VJUsageChart(double[][] matrix, List<String> labels) {
        this.matrix = matrix;
        this.labels = labels;
    }
}
