package shared;

public class FileRequest extends AbstractCommand {
    private String name;

    public FileRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
