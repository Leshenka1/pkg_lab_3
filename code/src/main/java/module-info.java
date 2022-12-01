module com.example.appl1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires java.desktop;


    opens com.example.appl1 to javafx.fxml;
    exports com.example.appl1;
}