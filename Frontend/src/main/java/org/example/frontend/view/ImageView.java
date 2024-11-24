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


    private int atte=1;
    private static String cache="C:/Users/nazar/OneDrive/Desktop/Projects/.cache";
    private String FS="C:/Users/nazar/OneDrive/Desktop/Projects/";
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


    private File inCache(BufferedImage image,String name,Integer tileCount, Integer tileNumber) throws IOException {
        File[] files = new File(cache).listFiles();
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().startsWith(String.valueOf(atte))){
                File file = new File(cache,atte+"_"+tileCount+"_"+tileNumber+"_"+name);
                ImageIO.write(image,name.substring(0,name.lastIndexOf(".") ),file);
                atte+=1;
                return file;
            }
        }
        File file = new File(cache,atte+"_"+tileCount+"_"+tileNumber+"_"+name);
        ImageIO.write(image,name.substring(name.lastIndexOf(".")+1 ),file);
        atte+=1;
        if(atte==11){
            atte=1;
        }
        return file;
    }


    private String checkinCache(Integer tileCount, Integer tileNumber,String name) throws IOException {
        File[] files = new File(cache).listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name_file = file.getName();
            String[] dates = name_file.split("_");
            if(dates[1].contains(String.valueOf(tileCount))
            && dates[2].contains(String.valueOf(tileNumber))
            && name_file.endsWith(name)){
                return file.getAbsolutePath();
            }

        }
        return null;
    }

    @GetMapping("/image_tile/Uploads/{nickname}")
    public ResponseEntity<Resource> serveHome(@PathVariable String nickname, @RequestParam("fileName") String fileName,
                                              @RequestParam("tileCount") Integer tileCount,
                                              @RequestParam("tileNumber") Integer tileNumber) throws IOException {
        Path filePath = Paths.get(FS+nickname).resolve(fileName);
        File dir = new File(FS+nickname+"/"+fileName.replace(".","_"));
        String file_format = fileName.substring(fileName.lastIndexOf(".")+1);
        Resource resource = null;
        File file = filePath.toFile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int x = 0,y=0;
        String pathMb=checkinCache(tileCount,tileNumber,fileName);
        Path path;
        if(pathMb!=null){
            path=Paths.get(pathMb);

        }else{
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
                path = Paths.get(inCache(tile,fileName,tileCount,tileNumber).getAbsolutePath());
                System.out.println(path.toUri());
                System.out.println("Разбиение завершено.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        System.out.println(path.toAbsolutePath());;
        String path_n = path.toAbsolutePath().toString();
        if(path_n.contains(".tif")
                ||path_n.contains(".tif")){
            BufferedImage tiffImage = ImageIO.read(path.toFile());

            // Конвертируем TIFF в PNG
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(tiffImage, "png", outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            System.out.println("bytes");
            resource = new ByteArrayResource(imageBytes);
        }else{
            resource=new UrlResource(path.toUri());
        }
        if(resource==null){
            byte[] imageBytes = outputStream.toByteArray();
            System.out.println("bytes");
            resource = new ByteArrayResource(imageBytes);
        }
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
