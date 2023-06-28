package View;

import ViewModel.ScrabbleViewModel;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.io.File;
import java.io.IOException;
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
    @FXML
    BorderPane mainContainer;
    BooleanProperty disconnect;
    ObjectProperty<Character[][]> bindingBoard;
    ListProperty<Character> tiles;

    ListProperty<String> bindingScore;

    private Tile selectedTile;

    private Label draggedTile; // Reference to the dragged tile label

    private BooleanProperty myTurn;

    private BooleanProperty gameOver;

    public BoardController(){}

    /**
     * The setViewModel function is used to set the view model of the class.
     *
     * @param vm Set the view model for this class
     */
    public void setViewModel(ScrabbleViewModel vm) {
        this.vm = vm;
    }

    /**
     * The initWindow function initializes the window by calling the initBinding,
     * initBoard, and addListeners functions.
     */
    public void initWindow(){
        initBinding();
        initBoard();
        initPlayersTiles();
        initButtons();
        addListeners();
    }

    /**
     * The gameOver function is called when the game ends. It loads the GameOverPageController and sets its stage, vm,
     * playersList and scene. The scene is then set to be full screen.
     */
    public void gameOver() {
        try {
            FXMLLoader fxmlLoader = null;
            String fxmlPath = "src/main/resources/ui/fxml/game-over.fxml";
            fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("/ui/css/game-over.css").toExternalForm());
            GameOverPageController go = fxmlLoader.getController();
            go.setVm(this.vm);
            go.setPlayersList(score.getItems());
            go.setStage(stage);
            stage.setScene(scene);
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * The initBoard function is used to draw the board and bind it.
     * It takes in no parameters, but uses the global variables numRows and numCols to determine how many rows and columns there are.
     * It then creates a 2D array of characters called m that will be used as a placeholder for bindingBoard's value.
     * The function then iterates through each row, column pair on the board using two nested for loops (one looping through rows, one looping through columns).
     * For each cell in this iteration: if bindingBoard's value is not null (i.e., if we have already set
     */
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

    /**
     * The initPlayersTiles function initializes the player's tiles.
     * It does this by creating a new tile for each of the 7 cells in the playerTiles list, and then adding that cell to the list.
     */
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

    /**
     * The initBinding function initializes the bindings between the view model and the view.
     * It clears any previous scores, binds score to vm.getScores(), creates a new SimpleListProperty&lt;&gt;() for tiles,
     * binds tiles to vm.getTiles(), creates a new SimpleObjectProperty&lt;&gt;() for bindingBoard,
     * binds bindingBoard to vm.getBoard(), creates a new SimpleBooleanProperty() for myTurn and gameOver,
     * then finally it sets up bidirectional bindings between myTurn and gameOver with their respective properties in VMGame2048LogicImpl().
     *
     * @return A simplelistproperty&lt;tile&gt;
     */
    private void initBinding(){
        score.getItems().clear();
        score.itemsProperty().bind(vm.getScores());
        tiles = new SimpleListProperty<>(FXCollections.observableArrayList());
        tiles.bindBidirectional(vm.getTiles());
        bindingBoard = new SimpleObjectProperty<>();
        bindingBoard.bindBidirectional(vm.getBoard());
        myTurn = new SimpleBooleanProperty();
        myTurn.bindBidirectional(vm.myTurn);
        gameOver = new SimpleBooleanProperty();
        gameOver.bind(vm.getGameOver());
        disconnect = new SimpleBooleanProperty();
        disconnect.bindBidirectional(vm.getDisconnect());
    }

    /**
     * The initButtons function is used to initialize the buttons on the board.
     * It sets all of them to be disabled and transparent if it is not your turn,
     * or enabled and opaque if it is your turn. This function also sets up a listener for each button that calls its respective function when clicked.
     */
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

    /**
     * The addListeners function adds listeners to the myTurn, gameOver, and disconnect properties.
     * The listener for myTurn changes the visibility of buttons depending on whether it is this player's turn or not.
     * The listener for gameOver closes the stage if a player wins or loses.
     * The listener for disconnect closes the stage if a player disconnects from their opponent.
     */
    private void addListeners(){
        // Change visibility so the player will know if it is his turn or not
        System.out.println(myTurn.get());
        myTurn.addListener((observable, oldValue, newValue)->{
            initButtons();
        });

        gameOver.addListener((observable, oldValue, newValue)->{
            if(newValue){
                Platform.runLater(()->{
                    gameOver();
                });
            }
        });

        disconnect.addListener((observable, oldValue, newValue)->{
            if(newValue){
                Platform.runLater(()->stage.close());
            }
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


    /**
     * The getCellType function returns the type of cell at a given row and column.
     * The function takes two parameters: an integer representing the row, and an
     * integer representing the column. It returns a string that represents what kind
     * of cell is at that location on the board. The possible return values are: &quot;dl&quot; for double letter,
     * &quot;dw&quot; for double word, &quot;tl&quot; for triple letter, or &quot;tw&quot; for triple word. If there is no special tile
     * at that location on the board (i.e., it's just a normal square), then this function will return an
     *
     * @param row Determine the row of the cell

     * @param col Determine the column of the cell
     *
     * @return A string that is used to determine the type of tile
     */
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

    /**
     * The getCellStyle function takes in a String cellType and returns the appropriate style for that cell.
     *
     * @param cellType Determine the style of the cell
     *
     * @return A string that is used to set the style of a cell
     */
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

    /**
     * The submitWord function is called when the submit button is clicked.
     * It checks if it's the player's turn, and if so, calls vm.submitWord() to check whether or not a word has been submitted.
     * If a word has been submitted, then it resets all of the tiles in playerTiles to be blank tiles (i.e., no letter).
     *
     */
    public void submitWord() {
        System.out.println("Submit Clicked!");
        try {
            if(this.vm.myTurn.get()&& vm.submitWord())
            {
                for (int i = 0; i < 7 ; i++) {
                    Tile tile = new Tile(tiles.get(i));
                    playerTiles.getItems().get(i).setTile(tile);
                }
                this.myTurn.set(false);
            }
        } catch (NullPointerException|IOException e) {
            if(disconnect.get()) {
                this.stage.close();
            }
            return;
        }
        this.resetButton();
    }

    /**
     * The skipTurn function is called when the user clicks on the &quot;Skip Turn&quot; button.
     * It sends a message to the server that it is skipping its turn, and then sets
     * myTurn to false so that no more moves can be made until it becomes this player's turn again.
     */
    public void skipTurn() throws IOException, InterruptedException {
        System.out.println("Skip Turn Clicked!");
        if(this.vm.myTurn.get()){
            vm.skipTurn();
            this.myTurn.set(false);
        }
    }

    /**
     * The shuffleTiles function shuffles the tiles in the player's hand.
     */
    public void shuffleTiles() {
        Collections.shuffle(playerTiles.getItems());
    }

    /**
     * The resetButton function resets the board to its previous state.
     * It does this by clearing the playerTiles ListView and adding back all of the original tiles.
     * Then it clears the board GridPane and sets it equal to vm's prevBoard, which is a copy of what was on the board before resetButton was called.
     */
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

    /**
     * The setStage function is used to set the stage of the application.
     *
     * @param stage Set the stage
     *
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private class Cell extends StackPane {
        private Tile tile;

        private boolean draggable;

        /**
         * The Cell function creates a new cell object that extends StackPane.
         *
         * @return A cell with a tile
         */
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

        /**
         * The setTile function sets the tile to be displayed in this cell.
         *
         * @param tile Set the tile to a new one
         */
        public void setTile(Tile tile) {
            this.tile = tile;
            getChildren().clear();
            getChildren().add(this.tile);
        }

        /**
         * The setDraggable function sets the draggable property of a tile to true or false.
         *
         * @param val Set the draggable property of the tile
         */
        public void setDraggable(Boolean val){
            tile.setDraggable(val);
            this.draggable = val;
        }

        /**
         * The getTile function returns the tile that is stored in this object.
         *
         * @return The tile
         */
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

        /**
         * The Tile function is a constructor for the Tile class. It sets the character
         * of the tile to be blank, and it also sets whether or not it can be dragged.
         * The function then determines what value each tile should have based on
         * Scrabble rules, and then initializes events for when a mouse button is pressed,
         * released or clicked on this particular tile. Finally, it sets its text to be
         * whatever getTileText() returns (which will either return an empty string if there's no character in this Tile object yet), or else return that character as a String. It also gives
         *
         * @return A tile object
         *
         */
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

        /**
         * The Tile function is a constructor for the Tile class. It takes in a character and sets it as the tile's character,
         * then initializes its value based on Scrabble rules, sets its text to be that of getTileText(), and gives it some
         * styling. Finally, it calls initEvents() to initialize all events associated with this tile.
         *
         * @param character Determine the value of the tile
         *
         * @return A tile object
         */
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

        /**
         * The initValue function assigns a value to the tile based on its character.
         * The values are as follows:
         * E, A, I, O, N, R, T and L = 1 point; D and G = 2 points; B C M P = 3 points; F H V W Y = 4 points; K 5 points J X 8 Points Q Z 10 Points.
         *
         * @return The value of the tile
         *
         */
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

        /**
         * The setTile function sets the tile to a new tile.
         *
         * @param tile Set the character and value of a tile
         */
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

        /**
         * The initEvents function is used to initialize the events for each tile.
         * The first event is a drag detected event, which occurs when the user clicks on a tile and drags it.
         * This function sets up the Dragboard object that will be used to transfer data between cells during dragging.
         * It also sets up some visual effects for dragging, such as adding a shadow effect and bringing the dragged tile to front of all other tiles in its cell.
         */
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

        /**
         * The getTileText function returns the text that should be displayed on a tile.
         * If the value of the tile is 0, then only its character should be displayed.
         * Otherwise, both its character and value should be displayed in parentheses.
         *
         * @return A string that is the character and value of a tile
         */
        private String getTileText() {
            if (value == 0) {
                return String.valueOf(character);
            } else {
                return character + " (" + value + ")";
            }
        }

        /**
         * The getCharacter function returns the character that is stored in this object.
         *
         * @return The character object
         */
        public Character getCharacter() {
            return this.character;
        }

        /**
         * The setDraggable function sets the draggable property of a marker.
         *
         *
         * @param val Determine whether the marker is draggable or not
         */
        public void setDraggable(Boolean val) {
            draggable = val;
        }
    }
}