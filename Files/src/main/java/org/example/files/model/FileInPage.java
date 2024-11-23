package org.example.files.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.File;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;


@Getter
@Setter
public class FileInPage {
    private String fileName;
    private String fileSize;
    private String Date;
    private String fileIcon;
    private Long size;
    private ArrayList<String> Acctions=new ArrayList<>();

    public ArrayList<String> getAcctions() {
        return Acctions;
    }

    public void setAcctions(ArrayList<String> acctions) {
        Acctions = acctions;
    }

    public FileInPage(String fileName, String fileSize, String date, String fileIcon, Long size, ArrayList<String> acctions) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        Date = date;
        this.fileIcon = fileIcon;
        this.size = size;
        Acctions = acctions;
    }

    public FileInPage(File file) {
        this.fileName = file.getName();
        this.fileSize = file.isDirectory() ? "Папка" : humanReadableByteCountSI(file.length());
        long lastModified = file.lastModified();
        Date date = new Date(lastModified);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date =  formatter.format(date);
        if(file.isDirectory()) {
            Acctions.add("trash-can");
            Acctions.add("pen-to-square");
        }else{
            Acctions.add("trash-can");
            Acctions.add("pen-to-square");
            Acctions.add("download");
        }
        if(file.isDirectory()) {
            fileIcon = "folder_old";
        }else{
            fileIcon = "file_old";
        }
    }

    @Override
    public String toString() {
        return "FileInPage{" +
                "fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", Date='" + Date + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", size=" + size +
                ", Acctions=" + Acctions +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("fileName", fileName);
        json.put("fileSize", fileSize);
        json.put("Date", Date);
        json.put("fileIcon", fileIcon);
        return json;
    }
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
