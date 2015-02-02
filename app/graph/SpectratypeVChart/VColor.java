package graph.SpectratypeVChart;

import java.awt.*;

public class VColor {

    public static String getColor(String seed) {
        if (seed.equals("other")) return "#dcdcdc";
        float goldenRationConjugate = 0.818033988749895f;
        int hash = hash(seed.getBytes());
        float h = (nextFloat(hash(seed.getBytes())) + goldenRationConjugate) % 1;
        Color color = hsb(hash % 6, h);
        return "#" + Integer.toHexString(color.getRGB()).substring(2,8);
    }

    private static float nextFloat(long hash) {
        return Float.parseFloat("0." + String.valueOf(Math.abs(hash)));
    }

    private static Color hsb(int k, float h) {
        k = Math.abs(k);
        switch (k) {
            case 0:
                return Color.getHSBColor(h, 0.85f, 0.65f);
            case 1:
                return Color.getHSBColor(h, 0.65f, 0.65f);
            case 2:
                return Color.getHSBColor(h, 0.45f, 0.85f);
            case 3:
                return Color.getHSBColor(h, 0.45f, 0.7f);
            case 4:
                return Color.getHSBColor(h, 0.55f, 0.8f);
            case 5:
                return Color.getHSBColor(h, 0.85f, 0.85f);
            default:
                return null;
        }
    }

    private static int hash(byte[] data) {
        int p = 458489;
        int hash = 930937;

        for (byte b : data) {
            hash = (hash * hash + (b * p) + p) % 100000;
            p *= p;

        }
        return hash;
    }
}
