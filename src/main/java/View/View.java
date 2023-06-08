package View;

import Model.GuestModel;
import Model.HostModel;
import ViewModel.ScrabbleViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class View {
    boolean host;
    ScrabbleViewModel vm;

    Scene sceneShowed;
    Scene mainPage;
    Scene boardPageScene;

    Stage stage;
    int selectedIndex = -1;
    int BOARD_SIZE = 15;

    @FXML
    TextField nickname;
    @FXML
    Button editButton;
    @FXML
    Button join;
    @FXML
    TextField ip;
    @FXML
    TextField port;
    @FXML
    DialogPane dialog;

//    public View(){}

    public View(Stage stage) throws IOException {
        this.stage = stage;
        this.buildScenes();
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(this.mainPage);
        stage.show();
    }

    private void buildScenes() throws IOException {
        buildBoardScene();
        buildMainPageScene();
    }

    private void buildBoardScene() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/board-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        fxmlLoader.setController(this);
      
        this.boardPageScene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        boardPageScene.getStylesheets().add(getClass().getResource("/ui/css/board-page.css").toExternalForm());

        // Game Board
        GridPane board = (GridPane) fxmlLoader.getNamespace().get("grid");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Label tile = new Label();
                tile.setMinWidth(40);
                tile.setMinHeight(40);
                board.add(tile,i,j);
                GridPane.setHalignment(tile, HPos.CENTER);
                GridPane.setValignment(tile, VPos.CENTER);
            }
        }

        // Player Tiles
        ListView<Character> playerTiles = (ListView) fxmlLoader.getNamespace().get("playerTiles");
        ObservableList<Character> tiles = FXCollections.observableArrayList();
        // Create a ListProperty and bind it to the ObservableList
        ListProperty<Character> listProperty = new SimpleListProperty<>(tiles);
        playerTiles.itemsProperty().bindBidirectional(listProperty);
        tiles.add('a');
        tiles.add('b');
        tiles.add('c');
        System.out.println(listProperty);
        System.out.println(listProperty);
        System.out.println(tiles);
        playerTiles.setPrefHeight(tiles.size() * playerTiles.getFixedCellSize());

        playerTiles.setOnDragDetected(event -> {
            selectedIndex = playerTiles.getSelectionModel().getSelectedIndex();
            if (playerTiles.getSelectionModel().getSelectedItem() != null) {
                System.out.println(selectedIndex);
                Dragboard dragboard = playerTiles.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(playerTiles.getSelectionModel().getSelectedItem()));
                dragboard.setContent(content);
                System.out.println("#1");
            }
            event.consume();
        });

        board.setOnDragOver(event -> {
            if (event.getGestureSource() != board && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                System.out.println("#2");
            }
            event.consume();
        });

        board.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                String draggedItem = dragboard.getString();
                System.out.println(draggedItem);
                // Retrieve the target node
                Node targetNode = (Node) event.getTarget();
                System.out.println(targetNode.getClass());
                // Check if the target node is a Label
                if (targetNode instanceof Label && ((Label) targetNode).getText().equals("")) {
                    try {
                        // Use reflection to access the setText() method of LabeledText
                        Method setTextMethod = targetNode.getClass().getMethod("setText", String.class);
                        setTextMethod.invoke(targetNode, draggedItem);

                        success = true;
                        tiles.remove(selectedIndex);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        ListView<Object> scoreboard = (ListView) fxmlLoader.getNamespace().get("score");
        ObservableList<Object> scores = FXCollections.observableArrayList();
        scores.add("Arik: 1");
        scores.add("Dan: 15");
        scores.add("Daniel: 20");
        scoreboard.setItems(scores);
    }

    private void buildMainPageScene() throws IOException {
        String fxmlPath = "src/main/resources/ui/fxml/main-page.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        fxmlLoader.setController(this);
        this.mainPage = new Scene(fxmlLoader.load());
        this.mainPage.getStylesheets().add(getClass().getResource("/ui/css/main-page.css").toExternalForm());
    }

    public void hostGame() throws IOException {
        System.out.println("HOST");
        this.vm = new ScrabbleViewModel(new HostModel(nickname.toString(),"localhost",Integer.parseInt(port.toString())));
        this.stage.setScene(this.boardPageScene);
    }

    public void joinGame() throws IOException {
        System.out.println("Guest");
        this.vm = new ScrabbleViewModel(new GuestModel(nickname.toString(),"localhost",Integer.parseInt(port.toString())));
        this.stage.setScene(this.boardPageScene);
    }

    public void editParameters() {
        System.out.println("Edit nickname clicked! "+ nickname.getText());
        dialog.setContentText("");
        if(editButton.getText().equals("EDIT")){
            nickname.setEditable(true);
            ip.setEditable(true);
            port.setEditable(true);
            editButton.setText("SAVE");
        }else {
            nickname.setEditable(false);
            ip.setEditable(false);
            port.setEditable(false);
            editButton.setText("EDIT");
        }
    }
}
