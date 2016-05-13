package com.mioglobal.android.ble.sdk.DFU;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import net.lingala.zip4j.util.InternalZipConstants;

public class BinInputStream {
    private int LINE_LENGTH;
    private boolean isfirst;
    private final byte[] localBuf;
    private int localPos;
    private long mCheckSum;
    private int pos;
    private RandomAccessFile raf;

    public BinInputStream(String filepath, int linLength) throws FileNotFoundException {
        this.LINE_LENGTH = 18;
        this.raf = null;
        this.isfirst = true;
        this.raf = new RandomAccessFile(filepath, InternalZipConstants.READ_MODE);
        this.LINE_LENGTH = linLength;
        this.localBuf = new byte[this.LINE_LENGTH];
        this.localPos = this.LINE_LENGTH;
        try {
            RandomAccessFilechecksum();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long available() {
        try {
            return this.raf.length();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int readPacket(byte[] buffer) throws IOException {
        int i = 0;
        int size = this.localBuf.length;
        if (this.isfirst) {
            size = readBuffer();
        }
        while (i < this.LINE_LENGTH) {
            if (this.localPos < size) {
                int i2 = i + 1;
                byte[] bArr = this.localBuf;
                int i3 = this.localPos;
                this.localPos = i3 + 1;
                buffer[i] = bArr[i3];
                i = i2;
            } else {
                size = readBuffer();
                if (size == 0) {
                    break;
                }
            }
        }
        return i;
    }

    public boolean seek(long packageindex) {
        try {
            this.raf.seek(((long) this.LINE_LENGTH) * packageindex);
            readBuffer();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int readPacket(byte[] buffer, long packageindex) throws IOException {
        int i = 0;
        int length = this.localBuf.length;
        length = readBuffer(packageindex);
        while (i < this.LINE_LENGTH) {
            if (this.localPos < length) {
                int i2 = i + 1;
                byte[] bArr = this.localBuf;
                int i3 = this.localPos;
                this.localPos = i3 + 1;
                buffer[i] = bArr[i3];
                i = i2;
            } else {
                length = readBuffer();
                if (length == 0) {
                    break;
                }
            }
        }
        return i;
    }

    private int readBuffer() throws IOException {
        if (this.pos == -1) {
            return 0;
        }
        int size = 0;
        this.isfirst = false;
        int i = 0;
        while (i < this.LINE_LENGTH) {
            try {
                this.localBuf[i] = (byte) this.raf.readUnsignedByte();
                size++;
                i++;
            } catch (EOFException e) {
                e.printStackTrace();
            }
        }
        this.localPos = 0;
        return size;
    }

    private int readBuffer(long packageindex) throws IOException {
        if (this.pos == -1) {
            return 0;
        }
        int size = 0;
        this.isfirst = false;
        this.raf.seek(((long) this.LINE_LENGTH) * packageindex);
        if (0 != ((long) this.LINE_LENGTH) * packageindex) {
            return 0;
        }
        for (int i = 0; i < this.LINE_LENGTH; i++) {
            this.localBuf[i] = (byte) this.raf.readUnsignedByte();
            size++;
        }
        this.localPos = 0;
        return size;
    }

    private int asciiToInt(int ascii) {
        if (ascii >= 65) {
            return ascii - 55;
        }
        if (ascii >= 48) {
            return ascii - 48;
        }
        return -1;
    }

    public void close() throws IOException {
        this.raf.close();
    }

    private void RandomAccessFilechecksum() throws IOException {
        long length = this.raf.length();
        this.mCheckSum = 0;
        this.raf.seek(0);
        for (long i = 0; i < length; i++) {
            this.mCheckSum += (long) this.raf.readUnsignedByte();
        }
        this.raf.seek(0);
    }

    public long getCheckSum() {
        return this.mCheckSum;
    }
}
