package View;

import ViewModel.ScrabbleViewModel;
import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scrabble extends Application {

    public static final int BOARD_SIZE = 15;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/game-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        scene.getStylesheets().add(getClass().getResource("/ui/css/board-page.css").toExternalForm());

        // Game Board
        GridPane board = (GridPane) fxmlLoader.getNamespace().get("grid");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // TileView is a custom class representing a tile on the game board.
                // It would need to be implemented to include the necessary dragging logic.
                TileView tile = new TileView(' ', 0);
                board.add(tile, i, j);
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
//        listProperty.remove(0);
        System.out.println(listProperty);
        System.out.println(tiles);
        playerTiles.setPrefHeight(tiles.size() * playerTiles.getFixedCellSize());


        ListView<Object> scoreboard = (ListView) fxmlLoader.getNamespace().get("score");
        ObservableList<Object> scores = FXCollections.observableArrayList();
        scores.add("Arik");
        scores.add(1);
        scores.add("Dan");
        scores.add(15);
        scores.add("Arik");
        scores.add(1);
        scoreboard.setItems(scores);

        // Sidebar
        VBox sidebar = new VBox();

        // Players and Scores
        Label playersLabel = new Label("Players and Scores:");
        ListView<String> playersList = new ListView<>();
        sidebar.getChildren().addAll(playersLabel, playersList);

        // Words and Player who played it
        Label wordsLabel = new Label("Words Played:");
        ListView<String> wordsList = new ListView<>();
        sidebar.getChildren().addAll(wordsLabel, wordsList);

        // Timer
        Label timerLabel = new Label("Time Remaining:");
        Label timerDisplay = new Label("00:00"); // Needs to be updated with a Timeline or similar
        sidebar.getChildren().addAll(timerLabel, timerDisplay);

        primaryStage.setTitle("Scrabble Game");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private ListView<String> playerTiles;

    @FXML
    private GridPane grid;

    @FXML
    public void handleDragDetected(MouseEvent event) {
        // Start drag-and-drop operation
        Dragboard dragboard = playerTiles.startDragAndDrop(TransferMode.MOVE);

        // Put the dragged item's data on the dragboard
        ClipboardContent content = new ClipboardContent();
        content.putString(playerTiles.getSelectionModel().getSelectedItem());
        dragboard.setContent(content);

        event.consume();
    }

    @FXML
    public void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != grid && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }

        event.consume();
    }

    @FXML
    public void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasString()) {
            String draggedItem = dragboard.getString();
            int columnIndex = GridPane.getColumnIndex((Button) event.getTarget());
            int rowIndex = GridPane.getRowIndex((Button) event.getTarget());

            // Append the dragged item to the specific cell in the grid
            Button button = new Button(draggedItem);
            grid.add(button, columnIndex, rowIndex);
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }



}