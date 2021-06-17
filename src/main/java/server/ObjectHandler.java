package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import shared.FilesListRequest;
import shared.AbstractCommand;
import shared.FileMessage;
import shared.FileRequest;
import shared.FilesListResponse;

public class ObjectHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private static final String serverRootDir = "serverDir";
    private Path serverPath;

    public ObjectHandler() {
        serverPath = Paths.get(serverRootDir);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand message) throws Exception {
        System.out.println("received: " + message);
        if (message instanceof FilesListRequest) {
            ctx.writeAndFlush(getFiles());
            System.out.println(" in block FilesListRequest");
        }
        if (message instanceof FileRequest) {
            ctx.writeAndFlush(getFileMessage((FileRequest) message));
        }
        if (message instanceof FileMessage) {
            saveFile((FileMessage) message);
        }
    }

    private void saveFile(FileMessage fileMessage) throws IOException {
        Files.write(
                serverPath.resolve(fileMessage.getName()),
                fileMessage.getContent(),
                StandardOpenOption.CREATE
        );
    }

    private FileMessage getFileMessage(FileRequest request) throws IOException {
        return new FileMessage(serverPath.resolve(request.getName()));
    }

    private FilesListResponse getFiles() throws IOException {
        return new FilesListResponse(serverPath);
    }

}
