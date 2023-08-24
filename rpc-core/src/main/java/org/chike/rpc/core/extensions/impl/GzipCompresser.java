package org.chike.rpc.core.extensions.impl;

import org.chike.rpc.core.extensions.Compresser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompresser implements Compresser {
    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] source) {
        if (source == null) {
            throw new NullPointerException("source bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(source);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] compressed) {
        if (compressed == null) {
            throw new NullPointerException("compressed bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gzip.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip decompress error", e);
        }
    }

    @Override
    public byte getId() {
        return 0;
    }

    @Override
    public byte[] encode() {
        return new byte[] {getId()};
    }
}
