module FoundationCode {
	requires javafx.controls;
	requires java.sql;
	
	opens application to javafx.base, javafx.graphics, javafx.fxml;
}
