package View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Scrabble extends Application {

    public static final int BOARD_SIZE = 15;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Scrabble Game");

        BorderPane root = new BorderPane();

        // Game Board
        GridPane board = new GridPane();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // TileView is a custom class representing a tile on the game board.
                // It would need to be implemented to include the necessary dragging logic.
                TileView tile = new TileView(' ',0);
                board.add(tile, i, j);
            }
        }
        root.setCenter(board);

        // Player Tiles
        GridPane playerTiles = new GridPane();
        for (int i = 0; i < 7; i++) {
            // PlayerTile is a custom class representing a tile held by the player.
            PlayerTile tile = new PlayerTile();
            playerTiles.add(tile, i, 0);
        }
        root.setBottom(playerTiles);

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

        // Submit and Skip buttons
        Button submitButton = new Button("Submit");
        Button skipButton = new Button("Skip");
        sidebar.getChildren().addAll(submitButton, skipButton);

        root.setRight(sidebar);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}