package models;

import java.util.*;

import play.Play;
import play.mvc.PathBindable;
import static play.data.validation.Constraints.*;
import javax.persistence.*;
import play.db.ebean.Model;
import models.UserFile.FileInformation;


@Entity
public class Account extends Model {

    @Id
    private Long id;
    @Required
    private String userName;
    @OneToOne
    private LocalUser user;
    private String userDirPath;
    @OneToMany(mappedBy="account")
    private List<UserFile> userfiles;

    public Account(LocalUser user, String userName, String userDirPath) {
        this.user = user;
        this.userName = userName;
        this.userDirPath = userDirPath;
        this.userfiles = new ArrayList<>();
    }

    public class AccountInformation {
        //Data accountInformation = new Data(new String[]{"email", "firstName", "lastName", "userName", "filesCount"});
        public String email;
        public String firstName;
        public String lastName;
        public String userName;
        public Integer filesCount;

        public AccountInformation(String email, String firstName, String lastName, String userName, Integer filesCount) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.userName = userName;
            this.filesCount = filesCount;
        }
    }

    public class FilesInformation {
        public List<UserFile.FileInformation> files;
        public Integer maxFileSize;
        public Integer maxFilesCount;

        public FilesInformation(List<UserFile.FileInformation> files, Integer maxFileSize, Integer maxFilesCount) {
            this.files = files;
            this.maxFileSize = maxFileSize;
            this.maxFilesCount = maxFilesCount;
        }
    }

    public FilesInformation getFilesInformation() {
        List<UserFile.FileInformation> files = new ArrayList<>();
        for (UserFile userFile : getUserfiles()) {
            String state;
            if (userFile.isRendered()) {
                state = "rendered";
            } else if (userFile.isRendering()) {
                state = "rendering";
            } else {
                state = "wait";
            }
            files.add(new UserFile.FileInformation(userFile.getFileName(), userFile.getSoftwareTypeName(), state));
        }
        Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
        Integer maxFileSize = Play.application().configuration().getInt("maxFileSize");
        return new FilesInformation(files, maxFileSize, maxFilesCount);
    }


    public AccountInformation getAccountInformation() {
        return new AccountInformation(this.user.email, this.user.firstName, this.user.lastName, this.userName, this.getFilesCount());
    }

    public List<UserFile> getUserfiles() {
        return userfiles;
    }

    public String getUserName() {
        return userName;
    }

    public LocalUser getUser() {
        return user;
    }

    public String getDirectoryPath() {
        return userDirPath;
    }

    public Integer getFilesCount() {
        return getUserfiles().size();
    }

    public Long getId() {
        return id;
    }

    public String toString() {
        return String.format("%s", userName);
    }

    public static List<Account> findAll() {
        return find().all();
    }

    public static Account findByUserName(String userName) {
        return find().where().eq("userName", userName).findUnique();
    }

    public static Account findById(Long database_id) {
        return find().where().eq("id", database_id).findUnique();
    }

    public static Model.Finder<Long, Account> find() {
        return new Model.Finder<>(Long.class, Account.class);
    }
}
