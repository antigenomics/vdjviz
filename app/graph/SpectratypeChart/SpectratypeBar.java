package graph.SpectratypeChart;

import java.util.ArrayList;
import java.util.List;

public class SpectratypeBar {
    public String key;
    public String color;
    public List<Point> values;
    public String name;
    public String v;
    public String j;
    public String cdr3aa;

    public SpectratypeBar(String key, String color) {
        this.key = key;
        this.color = color;
        this.values = new ArrayList<>();
    }

    public SpectratypeBar(String key) {
        this.key = key;
        this.values = new ArrayList<>();
    }

    public SpectratypeBar(String key, String color, String name, String v, String j, String cdr3aa) {
        this.key = key;
        this.color = color;
        this.name = name;
        this.v = v;
        this.j = j;
        this.cdr3aa = cdr3aa;
        this.values = new ArrayList<>();
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getSumY() {
        double sum = 0;
        for (Point value : values) {
            sum += value.y;
        }
        return sum;
    }

    public void addPoint(double x, double y) {
        values.add(new Point(x, y));
    }



}
