package controllers.SampleCollectionAPI;

import com.antigenomics.vdjtools.join.JointSample;
import com.antigenomics.vdjtools.join.OccurenceJoinFilter;
import com.antigenomics.vdjtools.overlap.OverlapType;
import com.antigenomics.vdjtools.sample.*;
import graph.SearchClonotypes.JFilterRegex;
import graph.SearchClonotypes.VFilterRegex;
import models.UserFile;

import java.util.*;

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
        List<ClonotypeFilter> filterList = new ArrayList<>();
        if (joinParameters.vFilter.length > 0)
            filterList.add(new VFilterRegex(joinParameters.vFilter));
        if (joinParameters.jFilter.length > 0)
            filterList.add(new JFilterRegex(joinParameters.jFilter));
        if (filterList.size() > 0) {
            CompositeClonotypeFilter compositeClonotypeFilter = new CompositeClonotypeFilter(filterList.toArray(new ClonotypeFilter[filterList.size()]));
            for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
                array[i] = new Sample(userFileSampleEntry.getValue(), compositeClonotypeFilter);
                i++;
            }
        } else {
            for (Map.Entry<UserFile, Sample> userFileSampleEntry : samples.entrySet()) {
                array[i] = userFileSampleEntry.getValue();
                i++;
            }
        }
        OverlapType overlapType = OverlapType.valueOf(joinParameters.overlapType);
        OccurenceJoinFilter occurenceJoinFilter = new OccurenceJoinFilter(joinParameters.occurenceTreshold);
        jointClonotypes = new JointSample(overlapType, array, occurenceJoinFilter);
    }
}
