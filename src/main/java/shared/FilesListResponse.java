package shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FilesListResponse extends AbstractCommand {

    private List<FileInfo> files;

    public FilesListResponse(Path path) throws IOException {
        files = Files.list(path)
                .map(FileInfo::new)
                .collect(Collectors.toList());
    }

    public List<FileInfo> getFiles() {
        return files;
    }
}
