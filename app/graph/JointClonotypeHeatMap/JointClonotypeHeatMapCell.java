package graph.JointClonotypeHeatMap;

import com.antigenomics.vdjtools.join.JointClonotype;

public class JointClonotypeHeatMapCell {
    private int row;
    private int col;
    private int value;

    public JointClonotypeHeatMapCell(JointClonotype jointClonotype, int row, int col) {
        this.row = row;
        this.col = col + 1;
        double floor = Math.floor(Math.log10(jointClonotype.getFreq(col)));
        this.value = Double.isInfinite(floor) ? -10 : (int) floor;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return row + "\t" + col + "\t" + value + "\n";
    }
}
