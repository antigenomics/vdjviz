package graph.QuantileStatsChart;

import java.util.ArrayList;
import java.util.List;

public class Quantile {
    public String name;
    public double size;
    public List<Quantile> children;
    public boolean clonotype = false;

    public Quantile(String name, double size) {
        this.name = name;
        this.size = size;
    }

    public Quantile(String name, double size, boolean clonotype) {
        this.name = name;
        this.size = size;
        this.clonotype = clonotype;
    }

    public Quantile(String name, List<Quantile> children) {
        this.name = name;
        this.children = children;
    }

    public void inverse() {
        List<Quantile> newChildren = new ArrayList<>();
        for (int i = children.size() - 1; i >= 0; i--) {
            newChildren.add(children.get(i));
        }
        children = newChildren;
    }

    public void addChildren(String name, double size) {
        addChildren(name, size, false);
    }

    public void addChildren(String name, double size, boolean clonotype) {
        if (children == null) {
            this.children = new ArrayList<>();
        }
        children.add(new Quantile(name, size, clonotype));
    }
}
