package org.example.files.Logic.Images;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;

public class Compressor {
    public static void compress(String path,float power) throws IOException {
        File inputFile = new File(path);

        BufferedImage inputImage = ImageIO.read(inputFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = writers.next();

        File outputFile = new File(path);
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile);
        writer.setOutput(outputStream);

        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(power);

        writer.write(null, new IIOImage(inputImage, null, null), params);

        outputStream.close();
        writer.dispose();
    }
    public static void convertToTiff(File inputFile, File outputFile,String compress) throws IOException {
        if(compress.contains("G4")||compress.contains("G3")){
            compressTiffCCITT(inputFile,outputFile,compress);
        }else{
            // Загрузка исходного TIFF файла
            BufferedImage image = ImageIO.read(inputFile);

            // Получение TIFF ImageWriter
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");
            if (!writers.hasNext()) {
                throw new RuntimeException("No TIFF ImageWriter found!");
            }
            ImageWriter writer = writers.next();

            // Установка параметров сжатия
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType("LZW"); // Пример типа сжатия

            // Сохранение сжатого TIFF в ByteArrayOutputStream
            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(imageOutputStream);
                writer.write(null, new IIOImage(image, null, null), writeParam);
            }

            writer.dispose();
        }

    }

    public static void compressTiffCCITT(File inputFile, File outputFile,String compress) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);

        // Преобразование изображения в черно-белое с глубиной 1 бит на пиксель
        BufferedImage bwImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int gray = (int) (0.3 * ((rgb >> 16) & 0xFF) + 0.59 * ((rgb >> 8) & 0xFF) + 0.11 * (rgb & 0xFF));
                int binary = (gray > 127) ? 0xFFFFFF : 0x000000;
                bwImage.setRGB(x, y, binary);
            }
        }

        // Получение TIFF ImageWriter
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");
        if (!writers.hasNext()) {
            throw new RuntimeException("No TIFF ImageWriter found!");
        }
        ImageWriter writer = writers.next();

        // Установка параметров сжатия
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        System.out.println(Arrays.asList(writeParam.getCompressionTypes()));
        if(compress.contains("G4")){
            writeParam.setCompressionType("CCITT T.6"); // Пример типа сжатия
        }else{
            writeParam.setCompressionType("CCITT T.4"); // Пример типа сжатия
        }

        // Сохранение сжатого TIFF
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(bwImage, null, null), writeParam);
        }

        writer.dispose();
    }


    public static String convertTiffToJpegBase64(File inputFile) throws IOException {
        // Загрузка исходного TIFF файла
        BufferedImage image = ImageIO.read(inputFile);

        // Конвертация изображения в PNG и кодирование в Base64
        ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
        convertToJpeg(image, pngStream);

        // Преобразование байтового массива в Base64
        byte[] pngBytes = pngStream.toByteArray();
        return Base64.getEncoder().encodeToString(pngBytes);
    }

    private static void convertToJpeg(BufferedImage image, ByteArrayOutputStream outputStream) throws IOException {
        // Получение PNG ImageWriter
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("JPEG");
        if (!writers.hasNext()) {
            throw new RuntimeException("No PNG ImageWriter found!");
        }
        ImageWriter writer = writers.next();

        // Сохранение PNG в ByteArrayOutputStream
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(image, null, null), null);
        }

        writer.dispose();
    }
}