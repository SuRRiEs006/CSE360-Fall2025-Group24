module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	requires org.controlsfx.controls;
	
	opens application to javafx.base, javafx.graphics, javafx.fxml;
}
