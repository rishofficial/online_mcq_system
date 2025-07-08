module com.example.demo1 {
    // This module requires these two JavaFX modules to work
    requires javafx.controls;
    requires javafx.fxml;

    // This opens up your package to the fxml module so it can access your controller
    opens com.example.demo1 to javafx.fxml;

    // This makes your main application class available to be launched
    exports com.example.demo1;
    exports com.example.demo1.pages;
    opens com.example.demo1.pages to javafx.fxml;
    exports com.example.demo1.threads;
    opens com.example.demo1.threads to javafx.fxml;
    exports com.example.demo1.datatypes;
    opens com.example.demo1.datatypes to javafx.fxml;
}