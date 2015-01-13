import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ColorTest {

    @Test
    public void vColorTest() {
        String[] seed = {
                "TRBV14-23",
                "TRBV14-24",
                "TRBV1-2",
                "TRBV2-3",
                "TRBV3",
                "TRBV10-7",
                "TRBV10-8",
                "TRBV10-9",
                "TRBV10-10",
                "TRBV10-11",
                "TRBV10-12",
                "TRBV27",
                "TRBV7-6",
                "TRBV7-9",
                "TRBV5-1",
                "TRBV9",
                "TRBV20-1",
                "TRBV7-2",
                "TRBV28",
                "TRBV6-3",
        };
        float[] values = new float[seed.length];
        for (int i = 0; i < seed.length; ++i) {
            float goldenRationConjugate = 0.618033988749895f;
            float h = (cast(hash(seed[i].getBytes())) + goldenRationConjugate) % 1;
            values[i] = h;
        }
        System.out.println(minR(values));
        System.out.println(averageR(values));
    }


    private float minR(float[] values) {
        float min = 1;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                if (i == j) continue;
                    if (Math.abs(values[i] - values[j]) < min) min = Math.abs(values[i] - values[j]);
            }
        }
        return min;
    }

    private float averageR(float[] values) {
        float s = 0;
        for (int i = 0; i < values.length; i++) {
            float a = 0;
            for (int j = 0; j < values.length; j++) {
                if (i == j) continue;
                a += Math.abs(values[i] - values[j]);
            }
            a /= values.length;
            s += a;
        }
        s /= values.length;
        return s;
    }


    private float cast(long hash) {
        return Float.parseFloat("0." + String.valueOf(Math.abs(hash)));
    }

    private long hash(byte[] data) {
        int p = 458489;
        int hash = 930937;

        for (byte b : data) {
            hash = (hash * hash + (b * p) + p) % 100215;
            p *= p;

        }
        return hash;
    }

}
