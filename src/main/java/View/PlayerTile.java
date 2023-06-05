package View;

import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;

public class PlayerTile extends Label {
    public PlayerTile() {
        setPrefSize(40, 40);
        setText("A"); // for demonstration, all tiles are 'A'
        setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #000000; -fx-border-radius: 10; -fx-font-size: 20; -fx-gap-start-and-end: 5");

        // You would probably want to add some kind of drag event handler here.
        // Below is a very basic example of a drag event handler.
        setOnDragDetected(event -> {
            /* allow any transfer mode */
            startDragAndDrop(TransferMode.MOVE);

            /* put a string on dragboard */
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(getText());
            //getDragboard().setContent(content);

            event.consume();
        });
    }
}