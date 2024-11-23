package org.example.files.Logic.Images;

import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;



public class ScanTest {
    public static void saveGridImage(File output, BufferedImage gridImage, int dpi) throws IOException {
        output.delete();

        final String formatName = "png";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata,dpi);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(gridImage, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
    }

    private static void setDPI(IIOMetadata metadata,int dpi) throws IIOInvalidTreeException {

        double INCH_2_CM = 2.54;

        // for PMG, it's dots per millimeter
        double dotsPerMilli = 1.0 * dpi / 10 / INCH_2_CM;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }


    public static void degrad(String r11,String r22,String r33,String inputPath){
        String outputPath=inputPath;
        Boolean r1 = Boolean.valueOf(r11);
        Boolean r2 = Boolean.valueOf(r22);
        Boolean r3 = Boolean.valueOf(r33);
        if(r3){
            try {
                // Считать исходное изображение
                BufferedImage originalImage = ImageIO.read(new File(inputPath));

                // Создать черно-белое изображение
                BufferedImage bwImage = new BufferedImage(
                        originalImage.getWidth(),
                        originalImage.getHeight(),
                        BufferedImage.TYPE_BYTE_BINARY
                );

                // Преобразование в черно-белое изображение
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    for (int x = 0; x < originalImage.getWidth(); x++) {
                        // Получение RGB значения пикселя
                        int rgb = originalImage.getRGB(x, y);

                        // Получение значений красного, зеленого и синего цветов
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Вычисление яркости пикселя
                        int gray = (red + green + blue) / 3;

                        // Пороговая обработка: если яркость больше 128, то белый, иначе черный
                        int bwColor = gray > 128 ? 0xFFFFFF : 0x000000;

                        // Установка нового цвета пикселя
                        bwImage.setRGB(x, y, bwColor);
                    }
                }

                // Сохранить черно-белое изображение на диск
                ImageIO.write(bwImage, "jpeg", new File(outputPath));


            } catch (IOException e) {
                System.err.println("Ошибка при обработке изображения: " + e.getMessage());
            }
        }else if(r2){
            try {
                // Считать исходное изображение
                BufferedImage originalImage = ImageIO.read(new File(inputPath));

                // Создать черно-белое изображение
                BufferedImage grayscaleImage = new BufferedImage(
                        originalImage.getWidth(),
                        originalImage.getHeight(),
                        BufferedImage.TYPE_BYTE_GRAY
                );

                // Получить графический контекст для черно-белого изображения
                Graphics2D g2d = grayscaleImage.createGraphics();
                // Рисовать исходное изображение в черно-белое изображение
                g2d.drawImage(originalImage, 0, 0, null);
                g2d.dispose();

                // Сохранить черно-белое изображение на диск
                ImageIO.write(grayscaleImage, "jpeg", new File(outputPath));


            } catch (IOException e) {
                System.err.println("Ошибка при обработке изображения: " + e.getMessage());
            }
        }
    }

}
