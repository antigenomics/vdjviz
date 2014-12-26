package graph.SpectratypeVChart;

import java.util.ArrayList;
import java.util.List;

public class SpectratypeVBar {
    public List<Point> values;
    public String key;
    public String color;

    public SpectratypeVBar(String key, String color) {
        this.values = new ArrayList<>();
        this.key = key;
        this.color = color;
    }

    public SpectratypeVBar(String key) {
        this.key = key;
        this.values = new ArrayList<>();
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addPoint(double x, double y) {
        values.add(new Point(x, y));
    }
}
