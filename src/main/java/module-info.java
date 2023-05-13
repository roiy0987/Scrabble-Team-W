module com.scrabble.bookscrabblegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.scrabble.bookscrabblegame to javafx.fxml;
    exports com.scrabble.bookscrabblegame;
}