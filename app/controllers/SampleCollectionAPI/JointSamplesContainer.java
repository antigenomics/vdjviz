package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.join.JointSample;
import com.antigenomics.vdjtools.join.OccurenceJoinFilter;
import com.antigenomics.vdjtools.overlap.OverlapType;
import com.antigenomics.vdjtools.sample.Sample;
import models.UserFile;

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
        for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
            array[i] = userFileSampleEntry.getValue();
            i++;
        }
        OverlapType overlapType = OverlapType.valueOf(joinParameters.overlapType);
        OccurenceJoinFilter occurenceJoinFilter = new OccurenceJoinFilter(joinParameters.occurenceTreshold);
        jointClonotypes = new JointSample(overlapType, array, occurenceJoinFilter);
    }
}
