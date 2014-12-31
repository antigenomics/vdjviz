package models;


import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import play.data.validation.Constraints;
import play.mvc.PathBindable;
import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

@Entity
public class UserFile extends Model {

    @Id
    private Long id;
    @ManyToOne
    private Account account;
    private String fileName;
    private String uniqueName;
    private Long sampleCount;
    private Software softwareType;
    @Constraints.Required
    private String softwareTypeName;
    private String filePath;
    private String fileDirPath;
    private String fileExtension;
    private Boolean rendered;
    private Boolean rendering;

    public UserFile(Account account, String fileName,
                    String uniqueName, String softwareTypeName,
                    String filePath, String fileDirPath, String fileExtension) {

        this.account = account;
        this.fileName = fileName;
        this.uniqueName = uniqueName;
        this.softwareType = Software.byName(softwareTypeName);
        this.softwareTypeName = softwareTypeName;
        this.filePath = filePath;
        this.fileDirPath = fileDirPath;
        this.rendered = false;
        this.rendering = false;
        this.fileExtension = fileExtension;


        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(filePath);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, softwareType, false);
        this.sampleCount = sampleCollection.getAt(0).getCount();
    }

    public static class FileInformation {
        public String fileName;
        public String softwareTypeName;
        public String state;

        public FileInformation(String fileName, String softwareTypeName, String state) {
            this.fileName = fileName;
            this.softwareTypeName = softwareTypeName;
            this.state = state;
        }
    }

    public Account getAccount() {
        return account;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getSampleCount() {
        return sampleCount;
    }

    public static Long getMaxSampleCount() {
        Long max = 0L;
        for (UserFile userFile : UserFile.find().all()) {
            if (userFile.getSampleCount() > max) max = userFile.getSampleCount();
        }
        return max;
    }


    public String getPath() {
        return filePath;
    }

    public String getDirectoryPath() {
        return fileDirPath;
    }

    public Boolean isRendered() {
        return rendered;
    }

    public Boolean isRendering() {
        return rendering;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public Software getSoftwareType() {
        return softwareType;
    }

    public String getSoftwareTypeName() {
        return softwareTypeName;
    }

    public Long getId() {
        return id;
    }

    public void changeRenderedState(Boolean state) {
        rendered = state;
    }

    public void changeRenderingState(Boolean state) {
        rendering = state;
    }

    public static UserFile findById(Long id) {
        return find().where().eq("id", id).findUnique();
    }


    public static List<UserFile> findByAccount(Account account) {
        return find().where().eq("account", account).findList();
    }

    public static UserFile fyndByNameAndAccount(Account account, String fileName) {
        return find().where().eq("account", account).eq("fileName", fileName).findUnique();
    }

    public static Model.Finder<Long, UserFile> find() {
        return new Model.Finder<>(Long.class, UserFile.class);
    }

    public static void deleteFile(UserFile file) {
        File fileDir = new File(file.fileDirPath);
        File[] files = fileDir.listFiles();
        if (files == null) {
            fileDir.delete();
            Ebean.delete(file);
            return;
        }
        for (File cache : files) {
            try {
                Files.deleteIfExists(cache.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileDir.delete();
        Ebean.delete(file);
    }

    public static void asyncDeleteFile(UserFile file) {
        UserFile f = UserFile.findById(file.id);
        deleteFile(f);
    }
}