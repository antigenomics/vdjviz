package graph.SpectratypeVChart;

import java.awt.*;

public class VColor {

    public static String getColor(String seed) {
        if (seed.equals("other")) return "#dcdcdc";
        float goldenRationConjugate = 0.618033988749895f;
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
                return Color.getHSBColor(h, 1f, 0.8f);
            case 1:
                return Color.getHSBColor(h, 0.8f, 0.8f);
            case 2:
                return Color.getHSBColor(h, 0.6f, 1f);
            case 3:
                return Color.getHSBColor(h, 0.6f, 0.85f);
            case 4:
                return Color.getHSBColor(h, 0.7f, 0.95f);
            case 5:
                return Color.getHSBColor(h, 1f, 1f);
            default:
                System.out.println(k);
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
