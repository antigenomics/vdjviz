package utils.vidjil;

import com.antigenomics.vdjtools.misc.Software;
import com.antigenomics.vdjtools.sample.Sample;
import com.avaje.ebean.Ebean;
import models.Account;
import models.SharedFile;
import models.SharedGroup;
import models.UserFile;
import org.apache.commons.io.FileUtils;
import play.libs.Json;
import utils.CommonUtil;
import utils.ComputationUtil;
import utils.server.Configuration;
import utils.server.LogAggregator;
import utils.server.ServerResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bvdmitri on 20.04.16.
 */
public class Vidjil {

    private static Account getVidjilAccount() throws Exception {
        if (!Configuration.isVidjilSharingEnabled()) throw new Exception("Vidjil sharing is disabled");
        return Account.findByUserName(Configuration.getVidjilUser());
    }

    public static String saveSample(Sample sample, String name) throws Exception {
        Account vidjilAccount = getVidjilAccount();

        String pattern = "^[a-zA-Z0-9_.+-]{1,40}$";
        if (name == null || !name.matches(pattern)) {
            name = "Generated_" + CommonUtil.RandomStringGenerator.generateRandomString(7, CommonUtil.RandomStringGenerator.Mode.ALPHA);
        }

        File accountDir = new File(vidjilAccount.getDirectoryPath());
        if (!accountDir.exists()) {
            Boolean accountDirCreated = accountDir.mkdir();
            if (!accountDirCreated) {
                LogAggregator.logServerError("Error creating main directory", vidjilAccount);
                throw new Exception("Error creating main directory");
            }
        }

        String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(5, CommonUtil.RandomStringGenerator.Mode.ALPHA);
        File fileDir = (new File(vidjilAccount.getDirectoryPath() + "/" + unique_name + "/"));

        //Trying to create file directory
        if (!fileDir.exists()) {
            Boolean created = fileDir.mkdir();
            if (!created) {
                LogAggregator.logServerError("Error creating file directory", vidjilAccount);
                throw new Exception("Error creating file directory");
            }
        }

        final UserFile newFile = new UserFile(vidjilAccount, name,
                unique_name, Software.Vidjil.name(),
                vidjilAccount.getDirectoryPath() + "/" + unique_name + "/" + name,
                fileDir.getAbsolutePath(), "");


        //Updating database UserFile <-> Account
        Ebean.save(newFile);

        try {
            ComputationUtil computationUtil = new ComputationUtil(newFile, sample, null);
            computationUtil.createSampleCache();
        } catch (Exception e) {
            UserFile.deleteFile(newFile);
        }

        return unique_name;
    }

    public static String shareSamples(List<String> uniqueNames) throws Exception {
        Account account = getVidjilAccount();
        String groupUniqueName = CommonUtil.RandomStringGenerator.generateRandomString(10, CommonUtil.RandomStringGenerator.Mode.ALPHANUMERIC);
        String cachePath = account.getDirectoryPath() + "/" + groupUniqueName + "/";
        File groupFolder = new File(cachePath);
        if (!groupFolder.exists()) {
            if (!groupFolder.mkdir()) {
                LogAggregator.logServerError("Error while creating directory for sharing group", account);
                throw new Exception("Error while creating directory for sharing group");
            }
        }
        String link = CommonUtil.RandomStringGenerator.generateRandomString(40, CommonUtil.RandomStringGenerator.Mode.ALPHA);
        while (SharedGroup.findByLink(link) != null) {
            link = CommonUtil.RandomStringGenerator.generateRandomString(40, CommonUtil.RandomStringGenerator.Mode.ALPHA);
        }
        List<SharedFile> sharedFiles = new ArrayList<>();
        //TODO vidjil description
        SharedGroup sharedGroup = new SharedGroup(account, groupUniqueName, cachePath, link, sharedFiles, "Samples shared from Vidjil");
        sharedGroup.save();
        List<UserFile> files = new ArrayList<>();
        for (String uniqueName : uniqueNames) {
            UserFile byUniqueNameAndAccount = UserFile.findByUniqueNameAndAccount(account, uniqueName);
            if (byUniqueNameAndAccount != null) files.add(byUniqueNameAndAccount);
        }
        for (UserFile file : files) {
            String sharedFileUniqueName = CommonUtil.RandomStringGenerator.generateRandomString(10, CommonUtil.RandomStringGenerator.Mode.ALPHANUMERIC);
            String fileDirPath = sharedGroup.getCachePath() + "/" + sharedFileUniqueName + "/";
            File fileDir = new File(fileDirPath);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    sharedGroup.deleteGroup();
                    LogAggregator.logServerError("Error while creating directory for file " + file.getFileName() + " in sharing group", account);
                    throw new Exception("Error while creating directory for file \" + file.getFileName() + \" in sharing group");
                }
            }
            FileUtils.copyDirectory(new File(file.getDirectoryPath()), new File(fileDirPath));
            SharedFile sharedFile = new SharedFile(file, sharedGroup, sharedFileUniqueName, fileDirPath + file.getFileName() + "." + file.getFileExtension(), fileDirPath);
            sharedFile.save();
            sharedGroup.addFile(sharedFile);
            UserFile.deleteFile(file);
        }
        sharedGroup.update();
        return link;
    }

}
