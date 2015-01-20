package graph.VJUsageChart;

import java.util.Arrays;
import java.util.Collections;

public class MatrixMath {
    private double[][] matrix;
    private String[] rowNames;
    private String[] columnNames;
    private double[] rowVectorSum;
    private double[] columnVectorSum;
    private int rows;
    private int columns;

    public MatrixMath(double[][] matrix, String[] rowNames, String[] columnNames, double[] rowVectorSum, double[] columnVectorSum) {
        this.matrix = matrix;
        this.rowNames = rowNames;
        this.columnNames = columnNames;
        this.rowVectorSum = rowVectorSum;
        this.columnVectorSum = columnVectorSum;
        this.rows = matrix.length;
        this.columns = matrix[0].length;
    }

    private void swapRows(int i, int j) {
        String strSwap = rowNames[j];
        rowNames[j] = rowNames[i];
        rowNames[i] = strSwap;
        double jSwap = this.rowVectorSum[j];
        this.rowVectorSum[j] = this.rowVectorSum[i];
        this.rowVectorSum[i] = jSwap;
        double[] rowSwap = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = rowSwap;
    }

    private void swapColumns(int i , int j) {
        String strSwap = columnNames[j];
        columnNames[j] = columnNames[i];
        columnNames[i] = strSwap;
        double vSwap = columnVectorSum[j];
        columnVectorSum[j] = columnVectorSum[i];
        columnVectorSum[i] = vSwap;
        for (int k = 0; k < rows; k++) {
            double swap = matrix[k][i];
            matrix[k][i] = matrix[k][j];
            matrix[k][j] = swap;
        }
    }

    public MatrixMath sort() {
        /* V sort (columns)*/
        for (int i = 0; i < columns; i++) {
            int max = i;
            for (int j = i; j < columns; j++) {
                if (columnVectorSum[j] > columnVectorSum[max]) max = j;
            }
            if (i != max) swapColumns(i, max);
        }
        /* J sort (rows)*/
        for (int i = 0; i < rows; i++) {
            int max = i;
            for (int j = i; j < rows; j++) {
                if (this.rowVectorSum[j] > this.rowVectorSum[max]) max = j;
            }
            if (i != max) swapRows(i, max);
        }
        return this;
    }

    public MatrixMath transpose() {
        double[][] transposedMatrix = new double[columns][];
        for (int i = 0; i < columns; i++) {
            transposedMatrix[i] = new double[rows];
            for (int j = 0; j < rows; ++j) {
                transposedMatrix[i][j] = matrix[j][i];
            }
        }
        double[] sumSwap = columnVectorSum;
        columnVectorSum = rowVectorSum;
        rowVectorSum = sumSwap;

        String[] namesSwap = columnNames;
        columnNames = rowNames;
        rowNames = namesSwap;
        rows = transposedMatrix.length;
        columns = transposedMatrix[0].length;
        matrix = transposedMatrix;
        return this;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public String[] getRowsNames() {
        return rowNames;
    }

    public String[] getColumnsNames() {
        return columnNames;
    }
}
