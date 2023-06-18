package View;

import ViewModel.ScrabbleViewModel;
import javafx.animation.TranslateTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
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
    ListView<Cell> playerTiles; // Specify the type of ListView items
    @FXML
    ListView<String> score; // Specify the type of ListView items
    @FXML
    GridPane board;

    @FXML
    Button submit;
    @FXML
    Button skip;
    @FXML
    Button shuffle;
    @FXML
    Button reset;
    @FXML
    HBox hbox;


    ObjectProperty<Character[][]> bindingBoard;

    ListProperty<Character> tiles;

    ListProperty<String> bindingScore;

    private Tile selectedTile;

    private Label draggedTile; // Reference to the dragged tile label

    private BooleanProperty myTurn;

    public BoardController(){}

    public void setViewModel(ScrabbleViewModel vm) {
        this.vm = vm;
    }

    public void initWindow(){
        initBinding();
        initBoard();
        initPlayersTiles();
        initButtons();
        addListeners();
    }

    private void initBoard(){
        // Draw empty board and bind it
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
                        cell.setDraggable(false);
                        board.add(cell, col, row);
                        m[row][col] = cell.getTile().getCharacter();
                    } else {
                        Tile tile = new Tile(cellValue);
                        Cell cell = new Cell();
                        cell.setTile(tile); // Set the tile content in the Cell
                        cell.setDraggable(false);
                        board.add(cell, col, row);

                    }
                }
            }
        }
        bindingBoard.set(m);
    }

    private void initPlayersTiles(){
        for (int i = 0; i < 7 ; i++) {
            Tile tile;
            if (!tiles.isEmpty()) {
                tile = new Tile(tiles.get(i));
            } else {
                tile = new Tile('A');
            }
            Cell c = new Cell();
            c.setDraggable(true);
            playerTiles.getItems().add(c);
            playerTiles.getItems().get(i).setTile(tile);
            playerTiles.getItems().get(i).setStyle("-fx-background-color: white; -fx-border-color: black;"); //-fx-border-color: black;
        }
    }

    private void initBinding(){
        score.getItems().clear();
        score.itemsProperty().bind(vm.getScores());
        tiles = new SimpleListProperty<>(FXCollections.observableArrayList());
        tiles.bindBidirectional(vm.getTiles());
        bindingBoard = new SimpleObjectProperty<>();
        bindingBoard.bindBidirectional(vm.getBoard());
        myTurn = new SimpleBooleanProperty();
        myTurn.bindBidirectional(vm.myTurn);
    }

    private void initButtons(){
        if(!myTurn.get()){
            submit.setDisable(true);
            skip.setDisable(true);
            shuffle.setDisable(true);
            reset.setDisable(true);
            submit.setOpacity(0.5);
            skip.setOpacity(0.5);
            shuffle.setOpacity(0.5);
            reset.setOpacity(0.5);
        }else{
            submit.setDisable(false);
            skip.setDisable(false);
            shuffle.setDisable(false);
            reset.setDisable(false);
            submit.setOpacity(1);
            skip.setOpacity(1);
            shuffle.setOpacity(1);
            reset.setOpacity(1);
        }

    }

    private void addListeners(){
        // Change visibility so the player will know if it is his turn or not
        System.out.println(myTurn.get());
        myTurn.addListener((observable, oldValue, newValue)->{
            initButtons();
        });

        // Add a ChangeListener to the bindingBoard property
        bindingBoard.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Clear the board and re-populate it with the new values
                board.getChildren().clear();
                int numRows1 = board.getRowCount();
                int numCols1 = board.getColumnCount();
                for (int row = 0; row < numRows1; row++) {
                    for (int col = 0; col < numCols1; col++) {
                        char cellValue = newValue[row][col];
                        Cell cell = new Cell();
                        if (cellValue == '\u0000') {
                            String cellType = getCellType(row, col);
                            cell.setStyle(getCellStyle(cellType));
                        } else {
                            Tile tile = new Tile(cellValue);
                            cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                            cell.setTile(tile);
                        }
                        cell.setDraggable(false);
                        board.add(cell, col, row);
                    }
                }
            }
        });
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
        if(this.vm.myTurn.get()&& vm.submitWord())
        {
            for (int i = 0; i < 7 ; i++) {
                Tile tile = new Tile(tiles.get(i));
                playerTiles.getItems().get(i).setTile(tile);
            }
            this.myTurn.set(false);
        }
        this.resetButton();
    }

    public void skipTurn() {
        System.out.println("Skip Turn Clicked!");
        if(this.vm.myTurn.get()){
            vm.skipTurn();
            this.myTurn.set(false);
        }
    }

    public void shuffleTiles() {
        Collections.shuffle(playerTiles.getItems());
    }

    public void resetButton(){
        if(this.vm.myTurn.get()){
            // Reset the playerTiles ListView
            //playerBoardTiles.clear();
            playerTiles.getItems().clear();
            // Add the original tiles back to the playerTiles ListView
            for (int i = 0; i < tiles.size(); i++) {
                Tile tile = new Tile(tiles.get(i));
                playerTiles.getItems().add(new Cell());
                playerTiles.getItems().get(i).setTile(tile);
                playerTiles.getItems().get(i).setStyle("-fx-background-color: white; -fx-border-color: black;");
            }
            board.getChildren().clear();
            this.bindingBoard.set(vm.getPrevBoard());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Character[][] prevBoard = this.vm.getPrevBoard();
            // Reset the tiles on the board to empty cells
            for (int row = 0; row < board.getRowCount(); row++) {
                for (int col = 0; col < board.getColumnCount(); col++) {
                    Cell cell = new Cell();
                    if (prevBoard[row][col]==null||prevBoard[row][col] == '\u0000') {
                        String cellType = getCellType(row, col);
                        cell.setStyle(getCellStyle(cellType));
                        cell.setDraggable(false);
                    } else {
                        Tile tile = new Tile(prevBoard[row][col]);
                        cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                        cell.setTile(tile);
                        cell.setDraggable(false);
                    }
                    board.add(cell, col, row);
                }
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private class Cell extends StackPane {
        private Tile tile;

        private boolean draggable;

        public Cell() {
            this.tile = new Tile();
            tile.setStyle("-fx-background-color: transparent; -fx-font-size: 14px;");
            this.getChildren().add(tile);
            this.draggable = true;
            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            initEvents();
        }

        private void initEvents(){
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
                if (event.getGestureSource() != this && event.getDragboard().hasString() && this.tile.character == '\u0000') {
                    // Indicate that the drag operation was successful
                    event.setDropCompleted(true);

                    Tile sourceTile = (Tile) event.getGestureSource();
                    Cell sourceCell = (Cell) sourceTile.getParent();
                    sourceCell.setDraggable(true);
                    // Check if the dragged Tile belongs to the playerTiles or tiles
                    if (playerTiles.getItems().contains(sourceCell) || tiles.contains(sourceCell.tile.getCharacter())) {
                        if (this == sourceCell) {
                            // The tile is being dragged onto the same cell, no action needed
                            return;
                        }
                        // Update the UI to reflect the change
                        System.out.println(sourceCell.tile.character);
                        //playerBoardTiles.add(this);
                        if(this.tile.character!='\u0000') {
                            System.out.println(this.tile.character);
                            return;
                        }
                        Tile newTile = new Tile(sourceTile.getCharacter());

                        this.getChildren().clear();
                        this.tile.setTile(newTile);
                        this.getChildren().add(this.tile);
                        sourceCell.getChildren().clear();
                        sourceTile.setTile(new Tile());
                        this.setDraggable(true);
                        // Update the bindingBoard property
                        int row = GridPane.getRowIndex(this);
                        int col = GridPane.getColumnIndex(this);
                        Character[][] currentBoard = bindingBoard.get();
                        currentBoard[row][col] = newTile.getCharacter();
                        bindingBoard.set(currentBoard);
                    }
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });


        }

        public void setTile(Tile tile) {
            this.tile = tile;
            getChildren().clear();
            getChildren().add(this.tile);
        }

        public void setDraggable(Boolean val){
            tile.setDraggable(val);
            this.draggable = val;
        }

        public Tile getTile() {
            return tile;
        }

        public boolean isDraggable(){
            return draggable;
        }
    }

    private class Tile extends Label {

        private char character;
        private int value;
        private final DropShadow shadow = new DropShadow();
        private Cell targetCell;

        private boolean draggable;

        public Tile(){
            super();
            this.character = '\u0000';
            draggable = true;
            // Determine the value based on the Scrabble rules
            initValue();

            setText(getTileText());

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            setAlignment(Pos.CENTER);

            this.initEvents();
        }

        public Tile(char character) {
            super();
            this.character = character;
            draggable = true;
            // Determine the value based on the Scrabble rules
            initValue();

            setText(getTileText());

            setPrefSize(TILE_SIZE, TILE_SIZE);
            setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            setAlignment(Pos.CENTER);

            this.initEvents();
        }

        private void initValue(){
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
        }

        public void setTile(Tile tile) {
            if (tile != null) {
                this.character = tile.getCharacter();
                this.value=tile.value;
                this.setStyle("-fx-background-color: lightblue; -fx-font-size: 14px;");
            } else {
                this.character = '\u0000';
            }
            setText(getTileText());
        }

        private void initEvents(){
            setOnDragDetected(event -> {
                System.out.println(draggable);
                if(!draggable)
                    return;
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
                event.setDragDetect(true);
                event.consume();
            });

            setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE && targetCell != null) {
                    selectedTile = null;
                    // Remove the tile from the source cell
                    Cell sourceCell = (Cell) getParent();
                    sourceCell.setTile(new Tile());
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

        private String getTileText() {
            if (value == 0) {
                return String.valueOf(character);
            } else {
                return character + " (" + value + ")";
            }
        }

        public Character getCharacter() {
            return this.character;
        }

        public void setDraggable(Boolean val) {
            draggable = val;
        }
    }
}