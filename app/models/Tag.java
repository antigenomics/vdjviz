package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Tag extends Model {
    @Id
    private Long id;
    @ManyToOne
    private Account account;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String color;
    private String tagName;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<UserFile> fileList;

    public Tag(Account account, String description, String color, String tagName) {
        this.account = account;
        this.description = description;
        this.color = color;
        this.tagName = tagName;
        this.fileList = new ArrayList<>();
    }

    public static class TagInformation {
        public Long id;
        public String description;
        public String color;
        public String tagName;
        public List<String> files;

        public TagInformation(Tag tag) {
            this.id = tag.id;
            this.description = tag.description;
            this.color = tag.color;
            this.tagName = tag.tagName;
            this.files = new ArrayList<>();
            for (UserFile userFile : tag.fileList) {
                files.add(userFile.getFileName());
            }
        }
    }

    public void tagFiles(String[] selectedFiles) {
        for (UserFile userFile : this.fileList) {
            userFile.deleteTag(this);
        }
        this.fileList.clear();
        this.saveManyToManyAssociations("fileList");
        this.update();
        for (String selectedFile : selectedFiles) {
            UserFile userFile = UserFile.fyndByNameAndAccount(account, selectedFile);
            if (userFile != null) {
                this.fileList.add(userFile);
                this.saveManyToManyAssociations("fileList");
                this.update();
                userFile.addTag(this);
            }
        }
    }

    public Account getAccount() {
        return account;
    }

    public List<UserFile> getFileList() {
        return fileList;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public String getTagName() {
        return tagName;
    }


    public void deleteTag() {
        for (UserFile userFile : this.account.getUserfiles()) {
            userFile.deleteTag(this);
        }
        this.delete();
    }

    public static List<Tag> findByAccount(Account account) {
        return find().where().eq("account", account).findList();
    }

    public static Tag findById(Long id, Account account) {
        Tag tag = find().where().eq("id", id).findUnique();
        if (!Objects.equals(account, tag.account)) return null;
        return tag;
    }

    public static Model.Finder<Long, Tag> find() {
        return new Model.Finder<>(Long.class, Tag.class);
    }
}
