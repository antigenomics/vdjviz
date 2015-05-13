package graph.AnnotationTable;


import com.antigenomics.vdjtools.sample.Sample;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinary;
import utils.BinaryUtils.ClonotypeBinaryUtils.ClonotypeBinaryUtils;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AnnotationTable {
    private static final int displayLength = 100;

    private List<AnnotationTableRow> data;
    private boolean created;
    private String cacheName;
    private UserFile file;
    private int shift;
    private int diversity = 0;
    private long numberOfPages = 0;

    public AnnotationTable(UserFile file, Integer shift) {
        this.created = false;
        this.file = file;
        this.cacheName = CacheType.annotation.getCacheFileName();
        this.data = new ArrayList<>();
        this.shift = shift;
    }

    public AnnotationTable(UserFile file) {
        this(file, 0);
    }


    class AnnotationData {
        public long sampleCount;
        public long numberOfPages;
        public long displayLength;
        public int shift;
        public List<AnnotationTableRow> rows;

        public AnnotationData(long sampleCount, long numberOfPages, long displayLength, int shift, List<AnnotationTableRow> rows) {
            this.sampleCount = sampleCount;
            this.numberOfPages = numberOfPages;
            this.displayLength = displayLength;
            this.shift = shift;
            this.rows = rows;
        }
    }

    public AnnotationData getData() {
        return new AnnotationData(diversity, numberOfPages, displayLength, shift, data);
    }

    public AnnotationTable create() {
        int index = shift * displayLength + 1;
        this.diversity = ClonotypeBinaryUtils.getDiversity(file);
        this.numberOfPages = diversity / displayLength + 1;
        List<ClonotypeBinary> clonotypeBinaries = ClonotypeBinaryUtils.openBinaryFiles(file, shift, displayLength);
        if (clonotypeBinaries != null) {
            for (ClonotypeBinary clonotypeBinary : clonotypeBinaries) {
                data.add(new AnnotationTableRow(clonotypeBinary, index));
                index++;
            }
        }
        created = true;
        return this;
    }

    public void saveCache() throws Exception {
        if (created) {
            try {
                File cache = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
                fileWriter.write(Json.stringify(Json.toJson(this)));
                fileWriter.close();
            } catch (FileNotFoundException fnfe) {
                Logger.of("user." + file.getAccount().getUserName()).error("User " + file.getAccount().getUserName() +
                        ": save cache error [" + file.getFileName() + "," + cacheName + "]");
                fnfe.printStackTrace();
            }
        } else {
            throw new Exception("You should create table");
        }
    }
}
