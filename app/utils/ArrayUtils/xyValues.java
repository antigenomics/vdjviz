package utils.ArrayUtils;

import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class xyValues {

    private double[] x;
    private double[] y;
    private int size;
    private int count;
    private List<HashMap<String, Double>> values;

    public xyValues(Integer size) {
        this.x = new double[size];
        this.y = new double[size];
        this.count = 0;
        this.size = size;
        this.values = new ArrayList<>();
    }

    public xyValues() {
        this.size = 0;
        this.values = new ArrayList<>();
    }

    public void addValue(Double x_val, Double y_val) {
        if (size > 0) {
            x[count] = x_val;
            y[count] = y_val;
        }
        HashMap<String, Double> newValue = new HashMap<>();
        newValue.put("x", x_val);
        newValue.put("y", y_val);
        values.add(newValue);
        count++;
    }

    public int getSize() {
        return size;
    }

    public static Double getSumY(Object values) {
        List<HashMap<String, Object>> val = (List<HashMap<String, Object>>) values;
        double sumY = 0;
        for (HashMap<String, Object> map: val) {
            sumY += (double) map.get("y");
        }
        return sumY;
    }

    public xyValues interpolate() throws Exception {
        if (size != values.size()) {
            size = values.size();
            x = new double[size];
            y = new double[size];
            for (int i = 0; i < size; i++) {
                x[i] = values.get(i).get("x");
                y[i] = values.get(i).get("y");
            }
        }
        LoessInterpolator loessInterpolator = new LoessInterpolator();
        double[] z = loessInterpolator.smooth(x, y);
        for (int i = 0; i < size; i++) {
            values.get(i).put("y", z[i]);
        }
        return this;
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    public int getCount() {
        return count;
    }

    public List<HashMap<String, Double>> getValues() {
        return  values;
    }
}
