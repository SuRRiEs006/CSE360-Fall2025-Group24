package passwordEvaluationTestbed;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
//////////////////////////////////////////////////////////////
// ADDED
import javafx.scene.control.Button;
//////////////////////////////////////////////////////////////
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
//////////////////////////////////////////////////////////////
// ADDED
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//////////////////////////////////////////////////////////////

/**
 * <p> Title: UserInterface Class. </p>
 * 
 * <p> Description: The Java/FX-based user interface testbed to develop and test UI ideas.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2022-02-21 The JavaFX-based GUI for the implementation of a testbed
 *  
 */

public class UserInterface {
	
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are the application values required by the user interface
	private Label label_ApplicationTitle = new Label("Sign Up:");
	private Label label_Password = new Label("Enter the password here");
	private TextField text_Password = new TextField();
	private Label label_errPassword = new Label("");	
    private Label noInputFound = new Label("");
	private TextFlow errPassword;
    private Text errPasswordPart1 = new Text();
    private Text errPasswordPart2 = new Text();
    private Label errPasswordPart3 = new Label("");
    private Label validPassword = new Label("");
    private Label label_Requirements = new Label("A valid password must satisfy the following requirements:");
    private Label label_UpperCase = new Label("At least one upper case letter");
    private Label label_LowerCase = new Label("At least one lower case letter");
    private Label label_NumericDigit = new Label("At least one numeric digit");
    private Label label_SpecialChar = new Label("At least one special character");
    private Label label_LongEnough = new Label("At least eight characters");

    //////////////////////////////////////////////////////////////
    // ADDED: New single-line inputs in requested order:
    // Name -> Address -> Email (Password is last)
    //////////////////////////////////////////////////////////////
    private Label label_Name = new Label("Enter your name");
    private TextField text_Name = new TextField();

    private Label label_Address = new Label("Enter your address");
    private TextField text_Address = new TextField();

    private Label label_Email = new Label("Enter your email");
    private TextField text_Email = new TextField();
    //////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////
    // ADDED: Submit button + save status message
    //////////////////////////////////////////////////////////////
    private Button button_Submit = new Button("Submit");
    private Label label_SaveStatus = new Label("");
    //////////////////////////////////////////////////////////////
	
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	/**********
	 * This method initializes all of the elements of the graphical user interface. These assignments
	 * determine the location, size, font, color, and change and event handlers for each GUI object.
	 */
	public UserInterface(Pane theRoot) {
		
		// Title
		setupLabelUI(label_ApplicationTitle, "Arial", 24, PasswordEvaluationGUITestbed.WINDOW_WIDTH, 
				Pos.CENTER, 0, 10);

        // ORDER: Name
        setupLabelUI(label_Name, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
                Pos.BASELINE_LEFT, 10, 50);
        setupTextUI(text_Name, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
                Pos.BASELINE_LEFT, 10, 70, true);

        // ORDER: Address
        setupLabelUI(label_Address, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
                Pos.BASELINE_LEFT, 10, 110);
        setupTextUI(text_Address, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
                Pos.BASELINE_LEFT, 10, 130, true);

        // ORDER: Email
        setupLabelUI(label_Email, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
                Pos.BASELINE_LEFT, 10, 170);
        setupTextUI(text_Email, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
                Pos.BASELINE_LEFT, 10, 190, true);

        // ORDER: Password (LAST)
		setupLabelUI(label_Password, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 230);
		setupTextUI(text_Password, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 250, true);
		text_Password.textProperty().addListener((observable, oldValue, newValue) 
				-> { setPassword(); });
		
		// "no input" message
		noInputFound.setTextFill(Color.RED);
		noInputFound.setAlignment(Pos.BASELINE_LEFT);
		setupLabelUI(noInputFound, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 285);		
		
		// Error line
		label_errPassword.setTextFill(Color.RED);
		label_errPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errPassword, "Arial", 14,  
						PasswordEvaluationGUITestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 311);		
		
		// Arrow/caret trace
		errPasswordPart1.setFill(Color.BLACK);
	    errPasswordPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
	    errPasswordPart2.setFill(Color.RED);
	    errPasswordPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
	    errPassword = new TextFlow(errPasswordPart1, errPasswordPart2);
		errPassword.setMinWidth(PasswordEvaluationGUITestbed.WINDOW_WIDTH-10); 
		errPassword.setLayoutX(22);  
		errPassword.setLayoutY(285);
		
		setupLabelUI(errPasswordPart3, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 310);	

        //////////////////////////////////////////////////////////////
        // ADDED: Submit + status
        //////////////////////////////////////////////////////////////
        button_Submit.setFont(Font.font("Arial", 16));
        button_Submit.setPrefWidth(160);
        button_Submit.setLayoutX(10);
        button_Submit.setLayoutY(340);
        button_Submit.setOnAction(e -> saveInfo());

        label_SaveStatus.setTextFill(Color.GREEN);
        setupLabelUI(label_SaveStatus, "Arial", 14,
                PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 180, 343);
        //////////////////////////////////////////////////////////////
		
		// Requirements block
	    setupLabelUI(label_Requirements, "Arial", 16, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 380);
	    setupLabelUI(label_UpperCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 410);
	    label_UpperCase.setTextFill(Color.RED);

	    label_LowerCase.setText("At least one lower case letter");
	    setupLabelUI(label_LowerCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 435);
	    label_LowerCase.setTextFill(Color.RED);
	    
	    setupLabelUI(label_NumericDigit, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 460);
	    label_NumericDigit.setTextFill(Color.RED);
	    
	    setupLabelUI(label_SpecialChar, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 485);
	    label_SpecialChar.setTextFill(Color.RED);
	    
	    setupLabelUI(label_LongEnough, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 510);
	    label_LongEnough.setTextFill(Color.RED);
		resetAssessments();
		
		// Valid/invalid summary line
		validPassword.setTextFill(Color.GREEN);
		validPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(validPassword, "Arial", 18,  
						PasswordEvaluationGUITestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 10, 545);				

		// Add everything
		theRoot.getChildren().addAll(
				label_ApplicationTitle, 
                //////////////////////////////////////////////////////////////
                // ADDED: include new inputs + button + status
                //////////////////////////////////////////////////////////////
                label_Name, text_Name,
                label_Address, text_Address,
                label_Email, text_Email,
                button_Submit, label_SaveStatus,
                //////////////////////////////////////////////////////////////
				label_Password, text_Password, 
				noInputFound, label_errPassword, errPassword, errPasswordPart3, validPassword,
				label_Requirements, label_UpperCase, label_LowerCase, label_NumericDigit,
				label_SpecialChar, label_LongEnough
        );
	}
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
	
	/**********************************************************************************************

	User Interface Actions
	
	**********************************************************************************************/

	private void setPassword() {
		label_errPassword.setText("");
		noInputFound.setText("");
		errPasswordPart1.setText("");
		errPasswordPart2.setText("");
		validPassword.setText("");
		resetAssessments();
		performEvaluation();			
	}
	
	
	private void performEvaluation() {
		String inputText = text_Password.getText();
		if (inputText.isEmpty())
		    noInputFound.setText("No input text found!");
		else
		{
			String errMessage = PasswordEvaluator.evaluatePassword(inputText);
			updateFlags();

			// Correct Java check (not reference-compare)
			if (!errMessage.isEmpty()) {
				label_errPassword.setText(PasswordEvaluator.passwordErrorMessage);
				if (PasswordEvaluator.passwordIndexofError <= -1) return;
				String input = PasswordEvaluator.passwordInput;
				errPasswordPart1.setText(input.substring(0, PasswordEvaluator.passwordIndexofError));
				errPasswordPart2.setText("\u21EB");
				validPassword.setTextFill(Color.RED);
				errPasswordPart3.setText("The red arrow points at the character causing the error!");
				validPassword.setText("Failure! The password is not valid.");
			}
			else if (PasswordEvaluator.foundUpperCase && PasswordEvaluator.foundLowerCase &&
					PasswordEvaluator.foundNumericDigit && PasswordEvaluator.foundSpecialChar &&
					PasswordEvaluator.foundLongEnough) {
				validPassword.setTextFill(Color.GREEN);
				validPassword.setText("Success! The password satisfies the requirements.");
			} else {
				validPassword.setTextFill(Color.RED);
				validPassword.setText("The password as currently entered is not yet valid.");
			}
		}
	}
	
	protected void resetAssessments() {
	    label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
	    setupLabelUI(label_UpperCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 410);
	    label_UpperCase.setTextFill(Color.RED);
	    
	    label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
	    setupLabelUI(label_LowerCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 435);
	    label_LowerCase.setTextFill(Color.RED);
	    
	    label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
	    setupLabelUI(label_NumericDigit, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 460);
	    label_NumericDigit.setTextFill(Color.RED);
	    
	    label_SpecialChar.setText("At least one special character - Not yet satisfied");
	    setupLabelUI(label_SpecialChar, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 485);
	    label_SpecialChar.setTextFill(Color.RED);
	    
	    label_LongEnough.setText("At least eight characters - Not yet satisfied");
	    setupLabelUI(label_LongEnough, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 510);
	    label_LongEnough.setTextFill(Color.RED);
	    errPasswordPart3.setText("");
	}
	
	private void updateFlags() {
		if (PasswordEvaluator.foundUpperCase) {
			label_UpperCase.setText("At least one upper case letter - Satisfied");
			label_UpperCase.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundLowerCase) {
			label_LowerCase.setText("At least one lower case letter - Satisfied");
			label_LowerCase.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundNumericDigit) {
			label_NumericDigit.setText("At least one numeric digit - Satisfied");
			label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundSpecialChar) {
			label_SpecialChar.setText("At least one special character - Satisfied");
			label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundLongEnough) {
			label_LongEnough.setText("At least eight characters - Satisfied");
			label_LongEnough.setTextFill(Color.GREEN);
		}
	}

	//////////////////////////////////////////////////////////////
	// ADDED: Save name/address/email locally as CSV, then wipe
	// inputs (including password) and reset the readouts.
	//////////////////////////////////////////////////////////////
	private void saveInfo() {
		label_SaveStatus.setTextFill(Color.RED);
		label_SaveStatus.setText("");

		String name = text_Name.getText().trim();
		String address = text_Address.getText().trim();
		String email = text_Email.getText().trim();

		// light validation
		if (name.isEmpty()) { label_SaveStatus.setText("Enter your name."); return; }
		if (address.isEmpty()) { label_SaveStatus.setText("Enter your address."); return; }
		if (email.isEmpty() || !email.contains("@")) { label_SaveStatus.setText("Enter a valid email."); return; }

		Path out = Path.of(System.getProperty("user.home"), "password_testbed_submissions.csv");
		String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n",
				escapeCsv(ts), escapeCsv(name), escapeCsv(address), escapeCsv(email));

		try {
			if (!Files.exists(out)) {
				Files.writeString(out, "timestamp,name,address,email\n", StandardOpenOption.CREATE);
			}
			Files.write(out, line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			label_SaveStatus.setTextFill(Color.GREEN);
			label_SaveStatus.setText("Saved locally to " + out.toString());

			// Wipe inputs and reset UI after successful save
			text_Name.clear();
			text_Address.clear();
			text_Email.clear();
			text_Password.clear();
			label_errPassword.setText("");
			noInputFound.setText("");
			errPasswordPart1.setText("");
			errPasswordPart2.setText("");
			errPasswordPart3.setText("");
			validPassword.setText("");
			resetAssessments();

		} catch (Exception ex) {
			label_SaveStatus.setTextFill(Color.RED);
			label_SaveStatus.setText("Save failed: " + ex.getMessage());
		}
	}

	private String escapeCsv(String s) { return s.replace("\"", "\"\""); }
	//////////////////////////////////////////////////////////////
}
