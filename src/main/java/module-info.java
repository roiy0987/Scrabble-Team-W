module com.scrabble.bookscrabblegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens View to javafx.fxml;
    exports View;
}