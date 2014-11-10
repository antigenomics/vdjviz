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

    public List<HashMap<String, Object>> getValues() {
        return values;
    }
}
