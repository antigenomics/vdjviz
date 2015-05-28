package models;

import org.apache.commons.io.FileUtils;
import play.Logger;
import play.db.ebean.Model;
import utils.CacheType.CacheType;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public class SharedGroup extends Model {
    @Id
    private Long id;
    @ManyToOne
    private Account account;
    private String uniqueName;
    private String cachePath;
    private String link;
    @OneToMany(mappedBy = "group")
    private List<SharedFile> files;
    @Column(columnDefinition = "TEXT")
    private String description;

    public SharedGroup(Account account, String uniqueName, String cachePath, String link, List<SharedFile> files, String description) {
        this.account = account;
        this.uniqueName = uniqueName;
        this.cachePath = cachePath;
        this.link = link;
        this.files = files;
        this.description = description;
    }

    public static class GroupInformation {
        public String sharedFiles;
        public String description;
        public List<String> sharedFilesNames;
        public String link;
        public Boolean rarefactionCached;

        public GroupInformation(SharedGroup s) {
            SharedGroup sharedGroup = SharedGroup.findByLink(s.getLink());
            this.sharedFilesNames = new ArrayList<>();
            this.description = sharedGroup.getDescription().length() > 256 ?
                    sharedGroup.getDescription().substring(0, 245) + "..." : sharedGroup.getDescription();
            String sharedFiles = "";
            for (int i = 0; i < sharedGroup.getFiles().size() - 1; i++) {
                sharedFiles += (sharedGroup.getFiles().get(i).getFileName() + ", ");
                sharedFilesNames.add(sharedGroup.getFiles().get(i).getFileName());
            }
            sharedFiles += (sharedGroup.getFiles().get(sharedGroup.getFiles().size() - 1).getFileName() + ".");
            sharedFilesNames.add(sharedGroup.getFiles().get(sharedGroup.getFiles().size() - 1).getFileName());
            this.sharedFiles = sharedFiles;
            this.link = s.getLink();
            File jsonFile = new File(s.cachePath + "/" + CacheType.rarefaction.getCacheFileName() + ".cache");
            this.rarefactionCached = jsonFile.exists();
        }
    }

    public GroupInformation getGroupInformation() {
        return new GroupInformation(this);
    }

    public long getMaxSampleCount() {
        long max = 0l;
        for (SharedFile sharedFile : files) {
            max = sharedFile.getSampleCount() > max ? sharedFile.getSampleCount() : max;
        }
        return max;
    }

    public List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        for (SharedFile file : files) {
            fileNames.add(file.getFileName());
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    public List<Long> getFileSizes() {
        List<Long> sizes = new ArrayList<>();
        for (SharedFile file : files) {
            File file1 = new File(file.getFilePath());
            sizes.add(file1.length());
        }
        Collections.sort(sizes);
        return sizes;
    }

    public String getLink() {
        return link;
    }

    public List<SharedFile> getFiles() {
        return files;
    }

    public Account getAccount() {
        return account;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getCachePath() {
        return cachePath;
    }

    public String getDescription() {
        return description;
    }

    public static List<SharedGroup> findByAccount(Account account) {
        return find().where().eq("account", account).findList();
    }

    public SharedFile findSharedFileByName(String fileName) {
        for (SharedFile file : files) {
            if (Objects.equals(fileName, file.getFileName()))
                return file;
        }
        return null;
    }

    public static SharedGroup findByLink(String link) {
        return find().where().eq("link", link).findUnique();
    }

    public void addFile(SharedFile file) {
        files.add(file);
    }

    public void deleteGroup() {
        SharedGroup sharedGroup = SharedGroup.findByLink(this.link);
        for (SharedFile file : sharedGroup.files) {
            file.deleteFile();
        }
        try {
            FileUtils.deleteDirectory(new File(this.cachePath));
        } catch (IOException e) {
            Logger.error("Error while deleting shared group directory");
            e.printStackTrace();
        }
        this.delete();
    }

    public static Model.Finder<Long, SharedGroup> find() {
        return new Model.Finder<>(Long.class, SharedGroup.class);
    }
}
