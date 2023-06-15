package View;

import ViewModel.ScrabbleViewModel;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Collections;

public class BoardController {

    private static final int TILE_SIZE = 50;

    ScrabbleViewModel vm;
    Stage stage;

    @FXML
    ListView<Tile> playerTiles; // Specify the type of ListView items
    @FXML
    ListView<String> score; // Specify the type of ListView items
    @FXML
    GridPane board;

    ObjectProperty<Character[][]> bindingBoard;

    ListProperty<Character> tiles;

    ListProperty<String> bindingScore;

    private Tile selectedTile;

    private Label draggedTile; // Reference to the dragged tile label

    public void setViewModel(ScrabbleViewModel vm) {
        this.vm = vm;
    }

    public void initWindow(){
        score.getItems().clear();
        score.itemsProperty().bind(vm.getScores());
        tiles = new SimpleListProperty<>(FXCollections.observableArrayList());
        tiles.bindBidirectional(vm.getTiles());
        bindingBoard = new SimpleObjectProperty<>();
        bindingBoard.bindBidirectional(vm.getBoard());

        int numRows = board.getRowCount();
        int numCols = board.getColumnCount();
        Character[][] m = new Character[15][15];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                char cellValue;
                if (bindingBoard.get() != null) {
                    cellValue = bindingBoard.getValue()[row][col];
                    if (cellValue == '\u0000') {
                        Cell cell = new Cell();
                        String cellType = getCellType(row, col);
                        cell.setStyle(getCellStyle(cellType));
                        board.add(cell, col, row);
//                        cell.setTile(new Tile(' '));
                        m[row][col] = cell.getTile().getCharacter();
                    } else {
                        Tile tile = new Tile(cellValue);
                        Cell cell = new Cell();
                        cell.setTileContent(tile); // Set the tile content in the Cell
                        board.add(cell, col, row);
                    }
                }
            }
        }
        bindingBoard.set(m);

        for (int i = 0; i < 7 ; i++) {
            Tile tile;
            if (!tiles.isEmpty()) {
                tile = new Tile(tiles.get(i));
            } else {
                tile = new Tile('A');
            }
            playerTiles.getItems().add(tile);
        }
    }

    private String getCellType(int row, int col) {
        switch (row) {
            case 0: case 14:
                if (col == 3 || col == 11)
                    return "dl";
                if (col == 7 || col == 0 || col == 14)
                    return "tw";
                break;
            case 1: case 13:
                if (col == 1 || col == 13)
                    return "dw";
                if (col == 5 || col == 9)
                    return "tl";
                break;
            case 2: case 12:
                if (col == 2 || col == 12)
                    return "dw";
                if (col == 6 || col == 8)
                    return "dl";
                break;
            case 3: case 11:
                if (col == 0 || col == 14 || col == 7)
                    return "dl";
                if (col == 3 || col == 11)
                    return "dw";
                break;
            case 4: case 10:
                if (col == 4 || col == 10)
                    return "dw";
                break;
            case 5: case 9:
                if (col == 5 || col == 9 || col == 1 || col == 13)
                    return "tl";
                break;
            case 6: case 8:
                if (col == 2 || col == 12 || col == 6 || col == 8)
                    return "dl";
                break;
            case 7:
                if (col == 0 || col == 14)
                    return "tw";
                if (col == 3 || col == 11)
                    return "dl";
                if (col == 7)
                    return "dw";
                break;
            default:
                break;
        }
        return ""; // Default cell type
    }

    private String getCellStyle(String cellType) {
        switch (cellType) {
            case "dl":
                return "-fx-background-color: #EAEDED; -fx-border-color: black;";
            case "dw":
                return "-fx-background-color: #FFA500; -fx-border-color: black;";
            case "tl":
                return "-fx-background-color: #00FFFF; -fx-border-color: black;";
            case "tw":
                return "-fx-background-color: #FF0000; -fx-border-color: black;";
            default:
                return "-fx-background-color: white; -fx-border-color: black;";
        }
    }

    public void submitWord() {
        System.out.println("Submit Clicked!");
        vm.submitWord();
     }

    public void skipTurn() {
        System.out.println("Skip Turn Clicked!");
        vm.skipTurn();
    }

    public void shuffleTiles() {
        Collections.shuffle(playerTiles.getItems());
    }

    public void setVm(ScrabbleViewModel vm) {
        this.vm = vm;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    private class Cell extends StackPane {
        private Tile tile;

        public Cell() {
            this.tile = new Tile();
            tile.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
            this.getChildren().add(tile);

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: yellow; -fx-border-color: black;");

            // Set the drag and drop event handlers for the cell
            setOnDragOver(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    // Allow for moving
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            setOnDragEntered(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    setOpacity(0.7);
                }
            });

            setOnDragExited(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    setOpacity(1.0);
                }
            });

            setOnDragDropped(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    // Indicate that the drag operation was successful
                    event.setDropCompleted(true);

                    // Update the UI to reflect the change
                    Tile draggedTile = new Tile(event.getDragboard().getString().charAt(0));
                    setTile(draggedTile);

                    // Update the bindingBoard property
                    int row = GridPane.getRowIndex(this);
                    int col = GridPane.getColumnIndex(this);
                    Character[][] currentBoard = bindingBoard.get();
                    currentBoard[row][col] = draggedTile.getCharacter();
                    bindingBoard.set(currentBoard);
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });
        }

        public void setTile(Tile tile) {
            this.tile.character = tile.character;
            System.out.println(this.tile.character);
            getChildren().clear();
            if (tile != null) {
                getChildren().add(tile);
            }
        }

        public void setTileContent(Tile tile) {
            getChildren().clear();
            getChildren().add(tile);
        }

        public Tile getTile() {
            return tile;
        }
    }

    private class Tile extends Label {

        private char character;
        private int value;
        private final DropShadow shadow = new DropShadow();
        private Cell targetCell;

        public Tile(){
            super();
            this.character = '\u0000';

            // Determine the value based on the Scrabble rules
            switch (Character.toUpperCase(character)) {
                case 'E', 'A', 'I', 'O', 'N', 'R', 'T', 'L', 'S', 'U' -> value = 1;
                case 'D', 'G' -> value = 2;
                case 'B', 'C', 'M', 'P' -> value = 3;
                case 'F', 'H', 'V', 'W', 'Y' -> value = 4;
                case 'K' -> value = 5;
                case 'J', 'X' -> value = 8;
                case 'Q', 'Z' -> value = 10;
                default -> value = 0; // Blank tiles or unsupported characters
            }

            setText(getTileText());

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            setAlignment(Pos.CENTER);

            setOnDragDetected(event -> {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getText());
                db.setContent(content);
                selectedTile = this;

                // Add dragging effect
                setEffect(shadow);
                toFront();

                // Set the drag view
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                Image snapshot = snapshot(parameters, null);
                db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);

                // Set the drag position relative to the tile
                double offsetX = event.getX();
                double offsetY = event.getY();
                event.setDragDetect(true);
                event.consume();

                // Set the mouse event handlers to move the tile with the mouse
                setOnMouseDragged(e -> {
                    setTranslateX(e.getSceneX() - offsetX - getLayoutX());
                    setTranslateY(e.getSceneY() - offsetY - getLayoutY());
                    e.consume();
                });

                setOnMouseReleased(e -> {
                    setTranslateX(0);
                    setTranslateY(0);
                    e.consume();
                });
            });

            setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE && targetCell != null) {
                    selectedTile = null;
                    // Remove the tile from the source cell
                    Cell sourceCell = (Cell) getParent();
                    sourceCell.setTile(null);
                    // Add the tile to the target cell
                    targetCell.setTile(this);

                    // Center the tile in the target cell
                    double cellCenterX = targetCell.getLayoutX() + targetCell.getWidth() / 2;
                    double cellCenterY = targetCell.getLayoutY() + targetCell.getHeight() / 2;
                    double tileCenterX = cellCenterX - getLayoutX() - TILE_SIZE / 2;
                    double tileCenterY = cellCenterY - getLayoutY() - TILE_SIZE / 2;

                    // Animate the tile movement to the center of the cell
                    TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
                    transition.setToX(tileCenterX);
                    transition.setToY(tileCenterY);
                    transition.play();
                }

                // Remove dragging effect
                setEffect(null);
                event.consume();
            });
        }

        public Tile(char character) {
            super();
            this.character = character;

            // Determine the value based on the Scrabble rules
            switch (Character.toUpperCase(character)) {
                case 'E', 'A', 'I', 'O', 'N', 'R', 'T', 'L', 'S', 'U' -> value = 1;
                case 'D', 'G' -> value = 2;
                case 'B', 'C', 'M', 'P' -> value = 3;
                case 'F', 'H', 'V', 'W', 'Y' -> value = 4;
                case 'K' -> value = 5;
                case 'J', 'X' -> value = 8;
                case 'Q', 'Z' -> value = 10;
                default -> value = 0; // Blank tiles or unsupported characters
            }

            setText(getTileText());

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            setAlignment(Pos.CENTER);

            setOnDragDetected(event -> {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getText());
                db.setContent(content);
                selectedTile = this;

                // Add dragging effect
                setEffect(shadow);
                toFront();

                // Set the drag view
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                Image snapshot = snapshot(parameters, null);
                db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);

                // Set the drag position relative to the tile
                double offsetX = event.getX();
                double offsetY = event.getY();
                event.setDragDetect(true);
                event.consume();

                // Set the mouse event handlers to move the tile with the mouse
                setOnMouseDragged(e -> {
                    setTranslateX(e.getSceneX() - offsetX - getLayoutX());
                    setTranslateY(e.getSceneY() - offsetY - getLayoutY());
                    e.consume();
                });

                setOnMouseReleased(e -> {
                    setTranslateX(0);
                    setTranslateY(0);
                    e.consume();
                });
            });

            setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE && targetCell != null) {
                    selectedTile = null;
                    // Remove the tile from the source cell
                    Cell sourceCell = (Cell) getParent();
                    sourceCell.setTile(null);
                    // Add the tile to the target cell
                    targetCell.setTile(this);

                    // Center the tile in the target cell
                    double cellCenterX = targetCell.getLayoutX() + targetCell.getWidth() / 2;
                    double cellCenterY = targetCell.getLayoutY() + targetCell.getHeight() / 2;
                    double tileCenterX = cellCenterX - getLayoutX() - TILE_SIZE / 2;
                    double tileCenterY = cellCenterY - getLayoutY() - TILE_SIZE / 2;

                    // Animate the tile movement to the center of the cell
                    TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
                    transition.setToX(tileCenterX);
                    transition.setToY(tileCenterY);
                    transition.play();
                }

                // Remove dragging effect
                setEffect(null);
                event.consume();
            });
        }

        public void setTargetCell(Cell cell) {
            this.targetCell = cell;
        }

        private String getTileText() {
            if (value == 0) {
                return String.valueOf(character);
            } else {
                return character + " (" + value + ")";
            }
        }

        public Character toCharacter() {
            return character;
        }

        public Character getCharacter() {
            return this.character;
        }
    }
}
