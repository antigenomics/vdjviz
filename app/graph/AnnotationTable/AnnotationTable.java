package graph.AnnotationTable;


import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import models.Account;
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
    private Account account;

    public AnnotationTable(Account account, UserFile file,Sample sample) {
        this.created = false;
        this.sample = sample;
        this.account = account;
        this.file = file;
        this.cacheName = CacheType.annotation.getCacheFileName();
        this.data = new ArrayList<>();
    }

    public List<AnnotationTableRow> getData() {
        return data;
    }

    public AnnotationTable create() {
        int count = 0;
        for (Clonotype clonotype : sample) {
            data.add(new AnnotationTableRow(clonotype));
            count++;
            if (count >= 1000) break;
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
                Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                        ": save cache error [" + file.getFileName() + "," + cacheName + "]");
                fnfe.printStackTrace();
            }
        } else {
            throw new Exception("You should create table");
        }
    }
}
