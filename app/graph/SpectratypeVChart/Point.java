package graph.SpectratypeVChart;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = Double.isNaN(x) ? 0 : x;
        this.y = Double.isNaN(y) ? 0 : y;
    }
}
