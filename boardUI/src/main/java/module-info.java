//module com.example.board {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires org.controlsfx.controls;
//    requires com.google.gson;
//
//    opens com.example.board to javafx.fxml;
//    opens com.example.board.controllers to javafx.fxml;
//    opens com.example.board.controllers.auth to javafx.fxml; // 🔥 add this
//
//    exports com.example.board;
//    exports com.example.board.controllers;
//    exports com.example.board.controllers.auth; // optional if other modules need it
//
//}
open module com.example.board {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.google.gson;
}

