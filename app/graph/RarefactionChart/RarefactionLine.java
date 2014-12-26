package graph.RarefactionChart;

import java.util.ArrayList;
import java.util.List;

public class RarefactionLine {
    public List<Point> values;
    public String key;
    public String color;
    public boolean area;
    public boolean hideTooltip;
    public boolean dash;
    //todo
    public double x_start;

    public RarefactionLine(String key, String color, boolean area, boolean hideTooltip) {
        this.values = new ArrayList<>();
        this.key = key;
        this.color = color;
        this.area = area;
        this.hideTooltip = hideTooltip;
    }

    public RarefactionLine(String key, String color, boolean area, boolean hideTooltip, boolean dash, double x_start) {
        this.values = new ArrayList<>();
        this.key = key;
        this.color = color;
        this.area = area;
        this.hideTooltip = hideTooltip;
        this.dash = dash;
        this.x_start = x_start;
    }

    public void addPoint(double x, double y) {
        values.add(new Point(x, y));
    }
}
