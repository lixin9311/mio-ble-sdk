package com.mioglobal.android.ble.sdk.DFU;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HexInputStream extends FilterInputStream {
    private final int LINE_LENGTH;
    private final byte[] localBuf;
    private int localPos;
    private int pos;

    public HexInputStream(InputStream in) {
        super(in);
        this.LINE_LENGTH = 16;
        this.localBuf = new byte[16];
        this.localPos = 16;
    }

    public int readPacket(byte[] buffer) throws IOException {
        int i = 0;
        int size = this.localBuf.length;
        while (i < buffer.length) {
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

    public int read() throws IOException {
        throw new UnsupportedOperationException("Please, use readPacket() method instead");
    }

    public int read(byte[] buffer) throws IOException {
        return readPacket(buffer);
    }

    public int read(byte[] buffer, int offset, int count) throws IOException {
        throw new UnsupportedOperationException("Please, use readPacket() method instead");
    }

    public int available() throws IOException {
        int dataSize = (this.in.available() - 17) - 34;
        int lastDataLineSize = dataSize % 45;
        return ((lastDataLineSize > 0 ? lastDataLineSize - 13 : 0) + ((dataSize / 45) * 32)) / 2;
    }

    private int readBuffer() throws IOException {
        if (this.pos == -1) {
            return 0;
        }
        int b;
        if (this.pos == 0) {
            this.pos = (int) (((long) this.pos) + this.in.skip(15));
        }
        while (true) {
            b = this.in.read();
            this.pos++;
            if (b != 10 && b != 13) {
                break;
            }
        }
        checkComma(b);
        int lineSize = readByte();
        this.pos += 2;
        this.pos = (int) (((long) this.pos) + this.in.skip(4));
        int type = readByte();
        this.pos += 2;
        if (type != 0) {
            this.pos = -1;
            return 0;
        }
        int i = 0;
        while (i < this.localBuf.length && i < lineSize) {
            b = readByte();
            this.pos += 2;
            this.localBuf[i] = (byte) b;
            i++;
        }
        this.pos = (int) (((long) this.pos) + this.in.skip(2));
        this.localPos = 0;
        return lineSize;
    }

    public synchronized void reset() throws IOException {
        super.reset();
        this.pos = 0;
        this.localPos = 0;
    }

    private void checkComma(int comma) throws IOException {
        if (comma != 58) {
            throw new IOException("Not a HEX file");
        }
    }

    private int readByte() throws IOException {
        int first = asciiToInt(this.in.read());
        return (first << 4) | asciiToInt(this.in.read());
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
}
