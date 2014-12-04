package utils.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class xyValues {
    private List<HashMap<String, Object>> values;

    public xyValues() {
        this.values = new ArrayList<>();
    }

    public void addValue(Object x, Object y) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("x", x);
        value.put("y", y);
        values.add(value);
    }

    public void addValueToPosition(Integer pos, Object x, Object y) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("x", x);
        value.put("y", y);
        values.add(pos, value);
    }

    public static Double getSumY(Object values) {
        List<HashMap<String, Object>> val = (List<HashMap<String, Object>>) values;
        double sumY = 0;
        for (HashMap<String, Object> map: val) {
            sumY += (double) map.get("y");
        }
        return sumY;
    }

    public List<HashMap<String, Object>> getValues() {
        return values;
    }
}
