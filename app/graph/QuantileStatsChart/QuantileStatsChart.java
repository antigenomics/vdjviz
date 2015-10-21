package graph.QuantileStatsChart;

import java.util.ArrayList;
import java.util.List;

public class QuantileStatsChart {
    public List<Quantile> chart;

    public QuantileStatsChart() {
        this.chart = new ArrayList<>();
    }

    public void addQuantile(Quantile quantile) {
        chart.add(quantile);
    }

    public List<Quantile> getChart() {
        return chart;
    }

    public void inverse() {
        List<Quantile> inverseChart = new ArrayList<>();
        for (int i = chart.size() - 1; i >= 0; i--) {
            inverseChart.add(chart.get(i));
        }
        chart = inverseChart;
    }

}
