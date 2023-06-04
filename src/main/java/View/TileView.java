package View;

import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TileView extends Label {
    private final char letter;
    private final int points;

    public TileView(char letter, int points) {
        this.letter = letter;
        this.points = points;
        setText(Character.toString(letter));
        setPrefSize(40, 40);
        setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #000000; -fx-border-radius: 10; -fx-font-size: 20;");
        setFont(Font.font("Arial", 16));
        setTextFill(Color.BLACK);

        setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
    }

    public char getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }
}