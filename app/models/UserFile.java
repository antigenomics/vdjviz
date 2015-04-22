package models;


import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserFile extends Model {

    enum RenderState {
        RENDERED,
        RENDERING,
        WAIT
    }

    @Id
    private Long id;
    @ManyToOne
    private Account account;
    private String fileName;
    private String uniqueName;
    private Integer clonotypesCount;
    private Long sampleCount;
    private Software softwareType;
    @Constraints.Required
    private String softwareTypeName;
    private String filePath;
    private String fileDirPath;
    private String fileExtension;
    private RenderState renderState;
    private DateTime createdAt;

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
        this.fileExtension = fileExtension;
        this.clonotypesCount = 0;
        this.renderState = RenderState.WAIT;
        this.createdAt = new DateTime();

    }

    public static class FileInformation {
        public String fileName;
        public String softwareTypeName;
        public Integer state;

        public FileInformation(String fileName, String softwareTypeName, Integer state) {
            this.fileName = fileName;
            this.softwareTypeName = softwareTypeName;
            this.state = state;
        }
    }

    public FileInformation getFileInformation() {
        return new FileInformation(fileName, softwareTypeName, renderState.ordinal());
    }

    public RenderState getRenderState() {
        return renderState;
    }

    public void setSampleCount() {
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(getPath());
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, softwareType, false);
        this.clonotypesCount = sampleCollection.getAt(0).getDiversity();
        this.sampleCount = sampleCollection.getAt(0).getCount();
    }

    public void setSampleCount(int diversity, long count) {
        this.clonotypesCount = diversity;
        this.sampleCount = count;
    }

    public Integer getClonotypesCount() {
        return clonotypesCount;
    }

    public Long getCreatedAtTimeInMillis() {
        return createdAt.getMillis();
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

    public String getPath() {
        return filePath;
    }

    public String getDirectoryPath() {
        return fileDirPath;
    }

    public Boolean isRendered() {
        return renderState.equals(RenderState.RENDERED);
    }

    public Boolean isRendering() {
        return renderState.equals(RenderState.RENDERING);
    }

    public Boolean isWait() { return renderState.equals(RenderState.WAIT); }

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

    public void rendered() {
        renderState = RenderState.RENDERED;
    }

    public void rendering() {
        renderState = RenderState.RENDERING;
    }

    public void waitRender() {
        renderState = RenderState.WAIT;
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

    public static void cleanTemporaryFiles(UserFile file) {
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
    }

    public static void asyncDeleteFile(UserFile file) {
        UserFile f = UserFile.findById(file.id);
        deleteFile(f);
    }
}