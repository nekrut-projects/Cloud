package client;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import shared.*;

public class Controller implements Initializable {

    private static final String clientRootDir = "clientDir";
    public TableView<FileInfo> serverView;
    public TableView<FileInfo> clientView;
    private Path clientPath;
    private Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientPath = Paths.get(clientRootDir);
        constructTableView(clientView);
        constructTableView(serverView);

        showClientFiles();
        network = new Network(
                msg -> {
                    if (msg instanceof FilesListResponse) {
                        updateServerSide((FilesListResponse) msg);
                    }
                    if (msg instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) msg;
                        handleFileMessage(fileMessage);
                    }
                }
        );
    }

    private void handleFileMessage(FileMessage fileMessage) throws IOException {
        Files.write(
                clientPath.resolve(Paths.get(fileMessage.getName())),
                fileMessage.getContent(),
                StandardOpenOption.CREATE
        );
        Platform.runLater(this::showClientFiles);
    }

    public void toServer(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem().getFileName();
        FileMessage fileMessage = new FileMessage(clientPath.resolve(fileName));
        network.sendMessage(fileMessage);
        network.sendMessage(new FilesListRequest());
    }

    public void fromServer(ActionEvent actionEvent) {
        String fileName = serverView.getSelectionModel().getSelectedItem().getFileName();
        FileRequest fileRequest = new FileRequest(fileName);
        network.sendMessage(fileRequest);
    }

    private void updateServerSide(FilesListResponse response) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(response.getFiles());
        });
    }

    private void showClientFiles() {
        clientView.getItems().clear();
        try {
            clientView.getItems().addAll(Files.list(clientPath)
                                                .map(FileInfo:: new)
                                                .collect(Collectors.toList()));
            clientView.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не загружен список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void constructTableView(TableView<FileInfo> tableView) {
        // Name
        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(250);
        // Type file
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Тип");
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTypeFile().toString()));
        fileTypeColumn.setPrefWidth(50);
        // Size file
        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(150);
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = "";
                        if (item >= 1024) {
                            text = String.format("%,d Кбайт", item / 1024);
                        } else {
                            text = String.format("%,d байт", item);
                        }
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        // Modification date
        DateTimeFormatter dataTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileUpdateDateColumn = new TableColumn<>("Дата изменения");
        fileUpdateDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModify().format(dataTFormatter)));
        fileUpdateDateColumn.setPrefWidth(150);

        tableView.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileUpdateDateColumn);
    }
}
