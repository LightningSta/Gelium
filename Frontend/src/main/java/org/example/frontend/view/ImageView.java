package org.example.frontend.view;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Controller
public class ImageView {

    @GetMapping("/imageview/{nickname}")
    public String serveImage(@PathVariable String nickname, @RequestParam("fileName") String fileName, @RequestHeader HttpHeaders headers, Model model) throws IOException {
        model.addAttribute("imagePath", "/images/Uploads/" + nickname + "?fileName=" + fileName);
        return "ImageWork";
    }
    @GetMapping("/imageview_tile/{nickname}")
    public String serveImage2(@PathVariable String nickname, @RequestParam("fileName") String fileName,@RequestParam("tileCount") Integer tileCount,
                             @RequestParam("tileNumber") Integer tileNumber,@RequestHeader HttpHeaders headers, Model model) throws IOException {
        model.addAttribute("imagePath", "/image_tile/Uploads/" + nickname + "?fileName=" + fileName+
                "&tileCount=" + tileCount+"&tileNumber=" + tileNumber);
        return "ImageWork";
    }


    @GetMapping("/image_tile/Uploads/{nickname}")
    public ResponseEntity<Resource> serveHome(@PathVariable String nickname, @RequestParam("fileName") String fileName,
                                              @RequestParam("tileCount") Integer tileCount,
                                              @RequestParam("tileNumber") Integer tileNumber)  {
        Path filePath = Paths.get("C:/Users/nazar/OneDrive/Desktop/Projects/"+nickname).resolve(fileName);
        Resource resource = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int x = 0,y=0;
        File file = filePath.toFile();
        try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new RuntimeException("Формат изображения не поддерживается.");
            }

            ImageReader reader = readers.next();
            reader.setInput(input);
            int imageWidth = reader.getWidth(0);
            int imageHeight = reader.getHeight(0);


            int tileWidth = imageWidth/tileCount;
            int tileHeight = imageHeight/tileCount;
            x=tileWidth*tileNumber;
            y=tileHeight*tileNumber;

            int w = Math.min(tileWidth, imageWidth - x);
            int h = Math.min(tileHeight, imageHeight - y);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle region = new Rectangle(x, y, w, h);
            param.setSourceRegion(region);


            BufferedImage tile = reader.read(0, param);
            ImageIO.write(tile, fileName.split("\\.")[1], outputStream);
            System.out.println("Разбиение завершено.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        byte[] imageBytes = outputStream.toByteArray();
        resource = new ByteArrayResource(imageBytes);
        if(file.getName().endsWith(".png")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        }else if(file.getName().endsWith(".jpeg") || file.getName().endsWith(".jpg")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }else{
            return ResponseEntity.ok()
                    .body(resource);
        }
    }


    @GetMapping("/images/Uploads/{nickname}")
    public ResponseEntity<Resource> serveFile(@PathVariable String nickname, @RequestParam("fileName") String fileName) throws IOException {
        Path filePath = Paths.get("C:/Users/nazar/OneDrive/Desktop/Projects/"+nickname).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
