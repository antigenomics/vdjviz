package graph.JointClonotypeHistogramChart;

public class Point {
    private double x;
    private double y;
    private String color;

    final static String[] colors = {
            "#006837",
            "#1a9850",
            "#66bd63",
            "#a6d96a",
            "#d9ef8b",
            "#fee08b",
            "#fdae61",
            "#f46d43",
            "#d73027",
            "#a50026"
    };

    public Point(double x, double y) {
        this.x = Double.isNaN(x) ? 0 : x;
        this.y = Double.isNaN(y) ? 0 : y;
        int k = (int) Math.log10(y);
        this.color = (k < -10) ? "#dcdcdc" : colors[-k];

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getColor() {
        return color;
    }
}
