package utils.RarefactionColor;


public class RarefactionColor {

    private static final String[] colors = new String[]{
            "#313695",
            "#fdae61",
            "#66bd63",
            "#f4a582",
            "#5aae61",
            "#8073ac",
            "#abdda4",
            "#35978f",
            "#ff7f00",
            "#33a02c",
            "#fb9a99",
            "#e31a1c",
            "#ffff99",
            "#a6cee3",
            "#1f78b4",
            "#b15928",
            "#ff7f00"
    };

    public static String getColor(int i) {
        return colors[i % colors.length];
    }

}
