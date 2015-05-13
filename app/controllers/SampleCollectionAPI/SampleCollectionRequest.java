package controllers.SampleCollectionAPI;

public class SampleCollectionRequest {

    public class JoinParameters {
        public String overlapType;
        public int occurenceTreshold;
        public String[] vGenes;
        public String[] jGenes;
    }

    public String[] names;
    public String action;
    public JoinParameters joinParameters;
}
