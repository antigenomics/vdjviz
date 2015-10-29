package graph.JointClonotypeHeatMap;

import com.antigenomics.vdjtools.join.JointClonotype;

public class JointClonotypeHeatMapCell {
    private int row;
    private int col;
    private int value;
    private double frequency;
    private int convergence;

    public JointClonotypeHeatMapCell(JointClonotype jointClonotype, int row, int col) {
        this.row = row;
        this.col = col + 1;
        double floor = Math.floor(Math.log10(jointClonotype.getFreq(col)));
        this.value = Double.isInfinite(floor) || Double.isNaN(floor) ? -10 : (int) floor;
        if (jointClonotype.present(col)) {
            this.convergence = jointClonotype.getNumberOfVariants(col);
        } else {
            this.convergence = 0;
        }
        this.frequency = jointClonotype.getFreq(col);
        frequency = Double.isNaN(frequency) ? 0.0 : frequency;
    }

    public double getFrequency() {
        return frequency;
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

    public int getConvergence() {
        return convergence;
    }

    @Override
    public String toString() {
        return row + "\t" + col + "\t" + value + "\n";
    }
}
