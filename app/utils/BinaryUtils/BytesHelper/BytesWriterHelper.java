package utils.BinaryUtils.BytesHelper;

import java.nio.ByteBuffer;

public class BytesWriterHelper {
    private byte[] bytes;
    private int currentOffset;
    private int length;

    public BytesWriterHelper(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
        this.currentOffset = 0;
    };

    public void writeInt(int value) {
        if (currentOffset + 4 > length) throw new IndexOutOfBoundsException();
        System.arraycopy(ByteBuffer.allocate(4).putInt(value).array(), 0, bytes, currentOffset, 4);
        currentOffset += 4;
    }

    public void writeDouble(double value) {
        if (currentOffset + 8 > length) throw new IndexOutOfBoundsException();
        System.arraycopy(ByteBuffer.allocate(8).putDouble(value).array(), 0, bytes, currentOffset, 8);
        currentOffset += 8;
    }

    public void writeBytes(byte[] b) {
        if (currentOffset + b.length > length) throw new IndexOutOfBoundsException();
        System.arraycopy(b, 0, bytes, currentOffset, b.length);
        currentOffset += b.length;
    }
}
