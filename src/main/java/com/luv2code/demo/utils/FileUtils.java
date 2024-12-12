package com.luv2code.demo.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileUtils {

    public static byte[] compressFile(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int size = deflater.deflate(buffer);
            outputStream.write(buffer, 0, size);
        }

        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] outputData = outputStream.toByteArray();
        log.info(data.length / 1024 + " KB");
        log.info(outputData.length / 1024 + " KB");

        return outputData;
    }

    public static byte[] decompressFile(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        try {
            while (!inflater.finished()) {
                int size = inflater.inflate(buffer);
                stream.write(buffer, 0, size);
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }

}
