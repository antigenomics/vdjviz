package utils.BinaryUtils.ClonotypeBinaryUtils;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class ClonotypeBinaryContainer implements Iterable<ClonotypeBinary> {
    private FileInputStream stream;
    private long currentOffset = 0l;
    private int currentClonotypeIndex = 1;
    private int diversity;


    public ClonotypeBinaryContainer(String path) {
        try {
            stream = new FileInputStream(path);
            byte[] diversityArray = new byte[4];
            stream.read(diversityArray, 0, 4);
            diversity = ByteBuffer.wrap(diversityArray).getInt();
            byte[] offsetArray = new byte[8];
            stream.read(offsetArray, 0, 8);
            currentOffset = ByteBuffer.wrap(offsetArray).getLong();
            stream.getChannel().position(currentOffset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<ClonotypeBinary> iterator() {
        return new Iterator<ClonotypeBinary>() {
            @Override
            public void remove() {
                throw new RuntimeException("Remove is not allowed");
            }

            @Override
            public boolean hasNext() {
                return currentClonotypeIndex != diversity;
            }

            @Override
            public ClonotypeBinary next() {
                try {
                    currentClonotypeIndex++;
                    byte[] size = new byte[4];
                    stream.read(size, 0, 4);
                    int sizeInt = ByteBuffer.wrap(size).getInt();
                    byte[] clonotypeBytes = new byte[sizeInt];
                    stream.getChannel().position(stream.getChannel().position() - 4);
                    stream.read(clonotypeBytes, 0, sizeInt);
                    currentOffset += sizeInt;
                    return new ClonotypeBinary(clonotypeBytes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
