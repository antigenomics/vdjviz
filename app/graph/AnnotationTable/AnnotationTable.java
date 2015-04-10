package graph.AnnotationTable;


import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AnnotationTable {
    private static final int displayLength = 1000;

    private List<AnnotationTableRow> data;
    private Sample sample;
    private boolean created;
    private String cacheName;
    private UserFile file;
    private int shift;
    private long numberOfPages;

    public AnnotationTable(UserFile file, Sample sample, Integer shift) {
        this.created = false;
        this.sample = sample;
        this.file = file;
        this.cacheName = CacheType.annotation.getCacheFileName();
        this.data = new ArrayList<>();
        this.shift = shift;
        this.numberOfPages = (sample.getCount() / displayLength) + 1;
    }

    public AnnotationTable(UserFile file, Sample sample) {
        this(file, sample, 0);
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
        return new AnnotationData(sample.getCount(), numberOfPages, displayLength, shift, data);
    }

    public AnnotationTable create() {
        int count = 0, i;
        if (shift < numberOfPages) {
            for (i = displayLength * shift; i < sample.getCount(); i++) {
                data.add(new AnnotationTableRow(sample.getAt(i), i + 1));
                count++;
                if (count >= displayLength) break;
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
