package graph.SpectratypeChart;

import java.util.*;

public class SpectratypeChart {
    private List<SpectratypeBar> chart;

    public SpectratypeChart() {
        this.chart = new ArrayList<>();
    }

    public void addBar(SpectratypeBar bar) {
        chart.add(bar);
    }

    public List<SpectratypeBar> getChart() {
        return chart;
    }

    public SpectratypeChart sortByHeight() {
        Collections.sort(chart, new Comparator<SpectratypeBar>() {
            @Override
            public int compare(SpectratypeBar s1, SpectratypeBar s2) {
                if (s1.key.equals("Other")) return -1;
                if (s2.key.equals("Other")) return 1;
                if (s1.getSumY() > s2.getSumY()) return 1;
                return -1;
            }
        });
        return this;
    }

    public SpectratypeChart setColors(String[] colors) {
        int color = 0;
        for (int i = chart.size() - 1; i >= 0; --i) {
            chart.get(i).setColor(colors[color % colors.length]);
            color++;
        }
        return this;
    }

}
