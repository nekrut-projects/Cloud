package shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractCommand {

    private FileInfo file;
    private byte[] contentFile;

    public FileMessage(Path path) throws IOException {
        this.file = new FileInfo(path);
        contentFile = Files.readAllBytes(path);
    }

    public byte[] getContent() {
        return contentFile;
    }

    public FileInfo getFile() {
        return file;
    }

    public String getName() {
        return file.getFileName();
    }
}
