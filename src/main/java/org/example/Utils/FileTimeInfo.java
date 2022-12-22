package org.example.Utils;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class FileTimeInfo {
    public String file_name;//文件名（带路径）
    public Date lastmodfiyTimeDate;//文件修改时间
    public Date CreateTimeDate;//文件创建时间
    public void set_fileInfo() {
        Path path = Paths.get(file_name);
        BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr;
        try {
            attr = basicview.readAttributes();
            this.lastmodfiyTimeDate = new Date(attr.lastModifiedTime().toMillis());
            this.CreateTimeDate = new Date(attr.creationTime().toMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
