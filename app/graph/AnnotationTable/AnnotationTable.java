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
    public List<AnnotationTableRow> data;

    private Sample sample;
    private Boolean created;
    private String cacheName;
    private UserFile file;
    private Integer shift;

    public AnnotationTable(UserFile file, Sample sample, Integer shift) {
        this.created = false;
        this.sample = sample;
        this.file = file;
        this.cacheName = CacheType.annotation.getCacheFileName();
        this.data = new ArrayList<>();
        this.shift = shift;
    }

    public AnnotationTable(UserFile file, Sample sample) {
        this(file, sample, 0);
    }



    public List<AnnotationTableRow> getData() {
        return data;
    }

    public AnnotationTable create() {
        int count = 0;
        for (int i = 1000 * shift; i < sample.getCount(); i++) {
            data.add(new AnnotationTableRow(sample.getAt(i), count + shift * 1000 + 1));
            count++;
            if (count > 1000) break;
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
