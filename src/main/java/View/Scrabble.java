package View;

import javafx.application.Application;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class Scrabble extends Application {

    public static final int BOARD_SIZE = 15;

    int selectedIndex;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/board-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        scene.getStylesheets().add(getClass().getResource("/ui/css/board-page.css").toExternalForm());

        // Game Board
        GridPane board = (GridPane) fxmlLoader.getNamespace().get("grid");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // TileView is a custom class representing a tile on the game board.
                // It would need to be implemented to include the necessary dragging logic.
//                TileView tile = new TileView(' ', 0);
                Label tile = new Label();
                tile.setMinWidth(40);
                tile.setMinHeight(40);

                board.add(tile,i,j);
                GridPane.setHalignment(tile, HPos.CENTER);
                GridPane.setValignment(tile, VPos.CENTER);
            }
        }

        for (Node childNode : board.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(childNode);
            Integer rowIndex = GridPane.getRowIndex(childNode);

            if (columnIndex != null && rowIndex != null) {
                System.out.println("Node: " + childNode + ", Column: " + columnIndex + ", Row: " + rowIndex);
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
                if (targetNode instanceof Label) {
                    try {
                        // Use reflection to access the setText() method of LabeledText
                        Method setTextMethod = targetNode.getClass().getMethod("setText", String.class);
                        setTextMethod.invoke(targetNode, draggedItem);

                        success = true;
                        listProperty.remove(selectedIndex);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    }
                }
            }
            event.setDropCompleted(success);
//            event.consume();
        });

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


}