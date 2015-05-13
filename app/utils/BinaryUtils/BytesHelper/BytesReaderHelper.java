package utils.BinaryUtils.BytesHelper;

import java.nio.ByteBuffer;

public class BytesReaderHelper {
    private byte[] bytes;
    private int length;
    private int currentOffset;

    public BytesReaderHelper(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
        this.currentOffset = 0;
    }

    public int readInt() {
        if (currentOffset + 4 > bytes.length) throw new IndexOutOfBoundsException();
        byte[] result = new byte[4];
        int j = 0;
        for (int i = currentOffset; i < currentOffset + 4; i++) {
            result[j] = bytes[i];
            j++;
        }
        currentOffset += 4;
        return ByteBuffer.wrap(result).getInt();
    }

    public double readDouble() {
        if (currentOffset + 8 > bytes.length) throw new IndexOutOfBoundsException();
        byte[] result = new byte[8];
        int j = 0;
        for (int i = currentOffset; i < currentOffset + 8; i++) {
            result[j] = bytes[i];
            j++;
        }
        currentOffset += 8;
        return ByteBuffer.wrap(result).getDouble();
    }

    public byte[] readArray(int size) {
        if(currentOffset + size > bytes.length) throw new IndexOutOfBoundsException();
        byte[] result = new byte[size];
        int j = 0;
        for (int i = currentOffset; i < currentOffset + size; i++) {
            result[j] = bytes[i];
            j++;
        }
        currentOffset += size;
        return result;
    }
}
