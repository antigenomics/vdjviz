package graph.JointClonotypeHistogramChart;

public class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = Double.isNaN(x) ? 0 : x;
        this.y = Double.isNaN(y) ? 0 : y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
