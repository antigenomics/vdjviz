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

}
