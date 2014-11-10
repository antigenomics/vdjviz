package utils.ArrayUtils;

import java.util.HashMap;

public class Data {
    private String[] keys;
    private HashMap<String, Object> data;

    public Data(String[] keys) {
        this.keys = keys;
        this.data = new HashMap<>();
    }

    public void addData(Object[] items) {
        for (int i = 0; i < items.length; i++) {
            data.put(keys[i], items[i]);
        }
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void changeValue(String key, Object newValue) {
        data.put(key, newValue);
    }
}
