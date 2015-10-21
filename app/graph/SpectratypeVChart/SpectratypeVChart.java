package graph.SpectratypeVChart;

import java.util.*;

public class SpectratypeVChart {
    private List<SpectratypeVBar> chart;
    private int xMin = 1000;
    private int xMax = -1;

    public SpectratypeVChart() {
        this.chart = new ArrayList<>();
    }

    public List<SpectratypeVBar> getChart() {
        return chart;
    }

    public int getxMin() {
        return xMin;
    }

    public int getxMax() {
        return xMax;
    }

    public void setxMin(int xMin) {
        this.xMin = xMin;
    }

    public void setxMax(int xMax) {
        this.xMax = xMax;
    }

    public void addBar(SpectratypeVBar spectratypeVBar) {
        if (spectratypeVBar.key.equals("other")) {
            chart.add(0, spectratypeVBar);
        } else {
            chart.add(spectratypeVBar);
        }
    }

    public void sort() {
        Collections.sort(chart, new Comparator<SpectratypeVBar>() {
            @Override
            public int compare(SpectratypeVBar o1, SpectratypeVBar o2) {
                if (Objects.equals("other", o1.key)) return -1;
                if (Objects.equals("other", o2.key)) return 1;
                return o1.key.compareTo(o2.key);
            }
        });
    }
}
