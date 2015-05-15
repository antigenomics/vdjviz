package graph.SearchClonotypes;

import controllers.AccountAPI;
import graph.AnnotationTable.AnnotationTableRow;
import models.UserFile;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryContainer;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeFilters.*;

import java.util.ArrayList;
import java.util.List;

public class SearchClonotypes {
    private final static int searchCount = 100;

    static class SingleSampleSearchResults {
        public List<AnnotationTableRow> rows;
        public AccountAPI.SearchClonotypesRequest parameters;

        public SingleSampleSearchResults(List<AnnotationTableRow> rows, AccountAPI.SearchClonotypesRequest parameters) {
            this.rows = rows;
            this.parameters = parameters;
        }
    }

    public static SingleSampleSearchResults searchSingleSample(UserFile file, AccountAPI.SearchClonotypesRequest searchClonotypesRequest) {
        List<AnnotationTableRow> rows = new ArrayList<>();
        ClonotypeBinaryContainer container = new ClonotypeBinaryContainer(file.getDirectoryPath() + "/clonotype.bin");

        List<BinaryClonotypeFilter> filters = new ArrayList<>();
        if (searchClonotypesRequest.vFilter.length > 0)
            filters.add(new BinaryClonotypeVFilter(searchClonotypesRequest.vFilter));
        if (searchClonotypesRequest.jFilter.length > 0)
            filters.add(new BinaryClonotypeJFilter(searchClonotypesRequest.jFilter));
        filters.add(new BinaryClonotypeSequenceFilter(searchClonotypesRequest.sequence, searchClonotypesRequest.aminoAcid));

        FilteredBinaryClonotypes filteredBinaryClonotypes = new FilteredBinaryClonotypes(container, filters);

        int index = 1;
        for (ClonotypeBinary clonotype : filteredBinaryClonotypes.getFiltered(searchCount)) {
            rows.add(new AnnotationTableRow(clonotype, index));
            index++;
        }
        return new SingleSampleSearchResults(rows, searchClonotypesRequest);
    }

}
