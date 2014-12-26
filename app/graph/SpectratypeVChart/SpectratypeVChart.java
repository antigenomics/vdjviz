package graph.SpectratypeVChart;

import java.util.ArrayList;
import java.util.List;

public class SpectratypeVChart {
    public List<SpectratypeVBar> chart;

    public SpectratypeVChart() {
        this.chart = new ArrayList<>();
    }

    public List<SpectratypeVBar> getChart() {
        return chart;
    }

    public void addBar(SpectratypeVBar spectratypeVBar) {
        if (spectratypeVBar.key.equals("other")) {
            chart.add(0, spectratypeVBar);
        } else {
            chart.add(spectratypeVBar);
        }
    }
}
