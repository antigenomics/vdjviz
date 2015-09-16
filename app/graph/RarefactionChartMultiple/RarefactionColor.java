package graph.RarefactionChartMultiple;


import java.awt.*;

public class RarefactionColor {

    private int count;

    public RarefactionColor() {
        this.count = -1;
    }

    private static final String[] colors = new String[]{
            "1f77b4",
            "ff7f0e",
            "2ca02c",
            "d62728",
            "9467bd",
            "8c564b",
            "e377c2",
            "7f7f7f",
            "bcbd22",
            "17becf",
            "1f77b4",
            "ff7f0e",
            "2ca02c",
            "d62728",
            "9467bd",
            "8c564b",
            "e377c2",
            "7f7f7f",
            "bcbd22",
            "17becf",
    };

    public String getNext() {
        count++;
        return "#" + darker(colors[count % colors.length], count / colors.length);
    }

    private String darker(String hex, int scale) {
        Integer intVal = Integer.parseInt(hex, 16);
        Integer R = intVal & 0xff0000;
        Integer G = intVal & 0x00ff00;
        Integer B = intVal & 0x0000ff;
        R = (int) Math.round(R * (1 - (0.01) * scale));
        B = (int) Math.round(B * (1 - (0.01) * scale));
        G = (int) Math.round(G * (1 - (0.01) * scale));
        intVal = R + G + B;
        String strVal = Integer.toHexString(Color.decode(intVal.toString()).getRGB() & 0xffffff);
        while (strVal.length() < 6) {
            strVal = "0" + strVal;
        }
        return strVal;
    }
}
