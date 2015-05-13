package graph.JointClonotypeHeatMap;

import com.antigenomics.vdjtools.join.JointClonotype;
import com.antigenomics.vdjtools.join.JointSample;
import models.UserFile;

import java.util.ArrayList;
import java.util.List;

public class JointClonotypeHeatMap {
    private List<JointClonotypeHeatMapCell> values;
    private List<HeatMapRowLabel> rowLabel;
    private List<String> colLabel;

    public JointClonotypeHeatMap(JointSample jointClonotypes, List<UserFile> files) {
        values = new ArrayList<>();
        rowLabel = new ArrayList<>();
        colLabel = new ArrayList<>();
        for (UserFile file : files) {
            colLabel.add(file.getFileName());
        }
        int row = 1;
        for (JointClonotype jointClonotype : jointClonotypes) {
            rowLabel.add(new HeatMapRowLabel(jointClonotype.getClonotype()));
            for (int i = 0; i < files.size(); i++) {
                values.add(new JointClonotypeHeatMapCell(jointClonotype, row, i));
            }
            row++;
        }
    }

    public List<HeatMapRowLabel> getRowLabel() {
        return rowLabel;
    }

    public List<String> getColLabel() {
        return colLabel;
    }

    public List<JointClonotypeHeatMapCell> getValues() {
        return values;
    }
}
