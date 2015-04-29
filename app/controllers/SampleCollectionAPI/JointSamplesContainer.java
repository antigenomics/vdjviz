package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.join.JointSample;
import com.antigenomics.vdjtools.join.OccurenceJoinFilter;
import com.antigenomics.vdjtools.overlap.OverlapType;
import com.antigenomics.vdjtools.sample.*;
import models.UserFile;

import java.util.Arrays;
import java.util.Map;

public class JointSamplesContainer {
    private JointSample jointClonotypes;

    public JointSamplesContainer() {}

    public JointSample getJointClonotypes() {
        return jointClonotypes;
    }

    public void join(Map<UserFile, Sample> samples, SampleCollectionRequest.JoinParameters joinParameters) {
        int size = samples.size();
        Sample[] array = new Sample[size];
        int i = 0;
        if (joinParameters.vGenes.length == 0 && joinParameters.jGenes.length == 0) {
            for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
                array[i] = userFileSampleEntry.getValue();
                i++;
            }
        } else {
            if (joinParameters.vGenes.length > 0 && joinParameters.jGenes.length == 0) {
                VFilter vFilter = new VFilter(false, joinParameters.vGenes);
                CompositeClonotypeFilter clonotypeFilter = new CompositeClonotypeFilter(false, vFilter);
                for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
                    array[i] = new Sample(userFileSampleEntry.getValue(), clonotypeFilter);
                    i++;
                }
            } else if (joinParameters.vGenes.length == 0 && joinParameters.jGenes.length > 0) {
                JFilter jFilter = new JFilter(false, joinParameters.jGenes);
                CompositeClonotypeFilter clonotypeFilter = new CompositeClonotypeFilter(false, jFilter);
                for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
                    array[i] = new Sample(userFileSampleEntry.getValue(), clonotypeFilter);
                    i++;
                }
            } else {
                VFilter vFilter = new VFilter(false, joinParameters.vGenes);
                JFilter jFilter = new JFilter(false, joinParameters.jGenes);
                CompositeClonotypeFilter clonotypeFilter = new CompositeClonotypeFilter(false, vFilter, jFilter);
                for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
                    array[i] = new Sample(userFileSampleEntry.getValue(), clonotypeFilter);
                    i++;
                }
            }
        }
        OverlapType overlapType = OverlapType.valueOf(joinParameters.overlapType);
        OccurenceJoinFilter occurenceJoinFilter = new OccurenceJoinFilter(joinParameters.occurenceTreshold);
        jointClonotypes = new JointSample(overlapType, array, occurenceJoinFilter);
    }
}
