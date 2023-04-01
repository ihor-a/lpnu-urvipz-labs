module com.example.lpnuurvipzlabs {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.lpnuurvipzlabs to javafx.fxml;
    opens com.example.lpnuurvipzlabs.service to javafx.base;
    exports com.example.lpnuurvipzlabs;
}