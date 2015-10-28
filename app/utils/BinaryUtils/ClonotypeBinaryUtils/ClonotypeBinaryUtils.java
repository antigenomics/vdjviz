package utils.BinaryUtils.ClonotypeBinaryUtils;

import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import models.SharedFile;
import models.UserFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClonotypeBinaryUtils {

    public static void saveClonotypesToBinaryFile(UserFile file, Sample sample) {
        try {
            String cacheName = file.getDirectoryPath() + "/clonotype.bin";
            File cachefile = new File(cacheName);
            if (cachefile.exists()) {
                cachefile.delete();
                cachefile.createNewFile();
            } else {
                cachefile.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(cachefile, false);
            writeToStream(fileOutputStream, sample);
            //Windows trick
            fileOutputStream.close();
            fileOutputStream = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ClonotypeBinary> openBinaryFiles(UserFile file, int shift, int displayLength) {
        try {
            String cacheName = file.getDirectoryPath() + "/clonotype.bin";
            File cacheFile = new File(cacheName);
            if (!cacheFile.exists()) return null;
            FileInputStream fileInputStream = new FileInputStream(cacheFile);
            List<ClonotypeBinary> clonotypeBinaries = readFromStream(fileInputStream, shift * displayLength, displayLength);
            fileInputStream.close();
            fileInputStream = null;
            return clonotypeBinaries;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ClonotypeBinary> openBinaryFiles(SharedFile file, int shift, int displayLength) {
        try {
            String cacheName = file.getFileDirPath() + "/clonotype.bin";
            File cacheFile = new File(cacheName);
            if (!cacheFile.exists()) return null;
            FileInputStream fileInputStream = new FileInputStream(cacheFile);
            List<ClonotypeBinary> clonotypeBinaries = readFromStream(fileInputStream, shift * displayLength, displayLength);
            fileInputStream.close();
            fileInputStream = null;
            return clonotypeBinaries;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDiversity(UserFile file) {
        try {
            String cacheName = file.getDirectoryPath() + "/clonotype.bin";
            File cacheFile = new File(cacheName);
            if (!cacheFile.exists()) return 0;
            FileInputStream fileInputStream = new FileInputStream(cacheFile);
            byte[] diversityArray = new byte[4];
            fileInputStream.read(diversityArray, 0, 4);
            fileInputStream.close();
            fileInputStream = null;
            return ByteBuffer.wrap(diversityArray).getInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDiversity(SharedFile file) {
        try {
            String cacheName = file.getFileDirPath() + "/clonotype.bin";
            File cacheFile = new File(cacheName);
            if (!cacheFile.exists()) return 0;
            FileInputStream fileInputStream = new FileInputStream(cacheFile);
            byte[] diversityArray = new byte[4];
            fileInputStream.read(diversityArray, 0, 4);
            fileInputStream.close();
            fileInputStream = null;
            return ByteBuffer.wrap(diversityArray).getInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String byteToString(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return String.copyValueOf(chars);
    }


    public static void writeToStream(FileOutputStream stream, Sample sample) {
        try {
            int size = sample.getDiversity();
            byte[] sizeArray = ByteBuffer.allocate(4).putInt(size).array();
            stream.write(sizeArray, 0, 4);
            stream.getChannel().position(4 + size * 8);
            int index = 0;
            for (Clonotype clonotype : sample) {
                ClonotypeBinary clonotypeBinary = new ClonotypeBinary(clonotype);
                writeToStream(stream, clonotypeBinary, index);
                index++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToStream(FileOutputStream stream, ClonotypeBinary clonotypeBinary, int index) {
        try {
            long oldPosition = stream.getChannel().position();
            stream.getChannel().position(4 + index * 8);
            stream.write(ByteBuffer.allocate(8).putLong(oldPosition).array(), 0, 8);
            stream.getChannel().position(oldPosition);
            stream.write(clonotypeBinary.getBytes(), 0, clonotypeBinary.getSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static List<ClonotypeBinary> readFromStream(FileInputStream stream) {
        try {
            List<ClonotypeBinary> clonotypeBinaries = new ArrayList<>();
            List<Long> offsets = new ArrayList<>();
            byte[] diversityArray = new byte[4];
            stream.read(diversityArray, 0, 4);
            int diversity = ByteBuffer.wrap(diversityArray).getInt();
            byte[] offsetArray = new byte[8];
            for (int i = 0; i < diversity; i++) {
                stream.read(offsetArray, 0, 8);
                offsets.add(ByteBuffer.wrap(offsetArray).getLong());
            }
            for (int i = 0; i < diversity; i++) {
                clonotypeBinaries.add(readFromStream(stream, offsets.get(i)));
            }
            return clonotypeBinaries;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ClonotypeBinary> readFromStream(FileInputStream stream, int skip, int count) {
        try {
            List<ClonotypeBinary> clonotypeBinaries = new ArrayList<>();
            List<Long> offsets = new ArrayList<>();
            byte[] diversityArray = new byte[4];
            stream.read(diversityArray, 0, 4);
            int diversity = ByteBuffer.wrap(diversityArray).getInt();
            if (skip >= diversity) throw new IndexOutOfBoundsException();
            byte[] offsetArray = new byte[8];
            int maxCount = diversity > (skip + count) ? (skip + count) : diversity;
            stream.getChannel().position(4 + skip * 8);
            for (int i = skip; i < maxCount; i++) {
                stream.read(offsetArray, 0, 8);
                offsets.add(ByteBuffer.wrap(offsetArray).getLong());
            }
            for (int i = 0; i < maxCount - skip; i++) {
                clonotypeBinaries.add(readFromStream(stream, offsets.get(i)));
            }
            return clonotypeBinaries;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClonotypeBinary readFromStream(FileInputStream stream, long offset) {
        try {
            byte[] size = new byte[4];
            stream.getChannel().position(offset);
            stream.read(size, 0, 4);
            int sizeInt = ByteBuffer.wrap(size).getInt();
            byte[] clonotypeBytes = new byte[sizeInt];
            stream.getChannel().position(stream.getChannel().position() - 4);
            stream.read(clonotypeBytes, 0, sizeInt);
            return new ClonotypeBinary(clonotypeBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
