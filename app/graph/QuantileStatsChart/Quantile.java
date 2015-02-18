package graph.QuantileStatsChart;

import java.util.ArrayList;
import java.util.List;

public class Quantile {
    public String name;
    public double size;
    public List<Quantile> children;

    public Quantile(String name, double size) {
        this.name = name;
        this.size = size;
    }

    public Quantile(String name, List<Quantile> children) {
        this.name = name;
        this.children = children;
    }

    public void addChildren(String name, double size) {
        if (children == null) {
            this.children = new ArrayList<>();
        }
        children.add(new Quantile(name, size));

    }
}
