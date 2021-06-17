package shared;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo implements Serializable {
    public enum TypeFile{
        FILE("FILE"), DIRECTORY("DIR");
        String name;

        TypeFile(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String name;
    private long size;
    private TypeFile typeFile;
    private LocalDateTime lastModify;

    public FileInfo(Path path) {
        try {
            this.name = path.getFileName().toString();
            this.lastModify = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
            if (Files.isDirectory(path)) {
                this.typeFile = TypeFile.DIRECTORY;
                this.size = -1L;
            } else {
                this.typeFile = TypeFile.FILE;
                this.size = Files.size(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setLastModify(LocalDateTime lastModify) {
        this.lastModify = lastModify;
    }

    public String getFileName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public TypeFile getTypeFile() {
        return typeFile;
    }

    public LocalDateTime getLastModify() {
        return lastModify;
    }
}
