package graph.RarefactionChart;

import java.util.HashMap;
import java.util.Map;

public class RarefactionChart {
    public Map<Integer, Integer> freqTableCache;
    public RarefactionLine line;
    public RarefactionLine areaLine;

    public RarefactionChart(Map<Long, Long> freqTableCache, RarefactionLine line, RarefactionLine areaLine) {
        Map<Integer, Integer> casted = new HashMap<>();
        //HORIBLE TODO
        for (Map.Entry<Long, Long> longLongEntry : freqTableCache.entrySet()) {
            casted.put(Integer.valueOf(String.valueOf(longLongEntry.getKey())), Integer.valueOf(String.valueOf(longLongEntry.getValue())));
        }
        this.freqTableCache = casted;
        this.line = line;
        this.areaLine = areaLine;
    }

}
