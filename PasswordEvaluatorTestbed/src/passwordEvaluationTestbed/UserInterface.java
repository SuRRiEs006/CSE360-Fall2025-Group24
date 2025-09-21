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
// ADDED (kept from earlier save-to-CSV utility, now unused)
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
	
	// Root that PasswordEvaluationGUITestbed passes in
	private final Pane theRoot;

	// ========== Screen 1: Login ==========
	private Label label_ApplicationTitle = new Label("Password Evaluation Testbed");
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
    // ADDED: Login fields in requested order
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
    // ADDED: Login button + status message
    //////////////////////////////////////////////////////////////
    private Button button_Login = new Button("Login");
    private Label label_SaveStatus = new Label("");
    //////////////////////////////////////////////////////////////

	// A pane that holds the Login screen widgets (so we can swap screens cleanly)
	private final Pane paneLogin = new Pane();

	// ========== Screen 2: Update Account ==========
	//////////////////////////////////////////////////////////////
	// ADDED: Update Account screen controls
	//////////////////////////////////////////////////////////////
	private final Pane paneUpdate = new Pane(); // second screen container

	private Label label_UpdateTitle = new Label("Update Account");
	private Label label_UpdateName = new Label("Name");
	private TextField text_UpdateName = new TextField();

	private Label label_UpdateAddress = new Label("Address");
	private TextField text_UpdateAddress = new TextField();

	private Label label_UpdateEmail = new Label("Email");
	private TextField text_UpdateEmail = new TextField();

	private Label label_UpdatePassword = new Label("Current Password");
	private TextField text_UpdatePassword = new TextField(); // readonly feel via .setEditable(false)

	private Label label_NewPassword = new Label("New Password");
	private TextField text_NewPassword = new TextField();

	private Button button_SaveChanges = new Button("Save Changes"); // non-functional placeholder
	private Button button_Logout = new Button("Log Out");
	private Label label_UpdateNote = new Label("(Fields are prefilled; changing them is not functional yet.)");
	//////////////////////////////////////////////////////////////

	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	/**********
	 * Initialize the GUI. We build two panes (Login, Update) and swap them on demand.
	 */
	public UserInterface(Pane _root) {
		this.theRoot = _root;

		// Build and show the Login screen first
		buildLoginScreen();
		theRoot.getChildren().setAll(paneLogin);
	}

	/**********
	 * Build Login screen layout (existing look, with Login button)
	 */
	private void buildLoginScreen() {
		paneLogin.getChildren().clear();

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
        // ADDED: Login button + status
        //////////////////////////////////////////////////////////////
        button_Login.setFont(Font.font("Arial", 16));
        button_Login.setPrefWidth(160);
        button_Login.setLayoutX(10);
        button_Login.setLayoutY(340);
        button_Login.setOnAction(e -> goToUpdateScreen()); // <<< navigate

        label_SaveStatus.setTextFill(Color.GREEN);
        setupLabelUI(label_SaveStatus, "Arial", 14,
                PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 180, 343);

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

		// Add everything to the login pane
		paneLogin.getChildren().addAll(
				label_ApplicationTitle, 
                label_Name, text_Name,
                label_Address, text_Address,
                label_Email, text_Email,
				label_Password, text_Password, 
                button_Login, label_SaveStatus,
				noInputFound, label_errPassword, errPassword, errPasswordPart3, validPassword,
				label_Requirements, label_UpperCase, label_LowerCase, label_NumericDigit,
				label_SpecialChar, label_LongEnough
        );
	}

	/**********
	 * Build Update Account screen (prefilled with what the user typed).
	 * No backend work; Save button is a placeholder. Logout swaps to a tiny goodbye panel.
	 */
	private void buildUpdateScreen(String name, String address, String email, String currentPassword) {
		paneUpdate.getChildren().clear();

		// Title
		setupLabelUI(label_UpdateTitle, "Arial", 24, PasswordEvaluationGUITestbed.WINDOW_WIDTH, 
				Pos.CENTER, 0, 20);

		// Prefill fields
		text_UpdateName.setText(name);
		text_UpdateAddress.setText(address);
		text_UpdateEmail.setText(email);
		text_UpdatePassword.setText(currentPassword);
		text_UpdatePassword.setEditable(false); // display-only

		// Layout
		setupLabelUI(label_UpdateName, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
				Pos.BASELINE_LEFT, 10, 80);
		setupTextUI(text_UpdateName, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 100, true);

		setupLabelUI(label_UpdateAddress, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
				Pos.BASELINE_LEFT, 10, 140);
		setupTextUI(text_UpdateAddress, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 160, true);

		setupLabelUI(label_UpdateEmail, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
				Pos.BASELINE_LEFT, 10, 200);
		setupTextUI(text_UpdateEmail, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 220, true);

		setupLabelUI(label_UpdatePassword, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
				Pos.BASELINE_LEFT, 10, 260);
		setupTextUI(text_UpdatePassword, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 280, false);

		setupLabelUI(label_NewPassword, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
				Pos.BASELINE_LEFT, 10, 320);
		setupTextUI(text_NewPassword, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 340, true);

		label_UpdateNote.setTextFill(Color.GRAY);
		setupLabelUI(label_UpdateNote, "Arial", 12, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10,
				Pos.BASELINE_LEFT, 10, 370);

		// Buttons (placeholders)
		button_SaveChanges.setFont(Font.font("Arial", 16));
		button_SaveChanges.setDisable(true); // not functional yet
		button_SaveChanges.setLayoutX(10);
		button_SaveChanges.setLayoutY(400);

		button_Logout.setFont(Font.font("Arial", 16));
		button_Logout.setLayoutX(190);
		button_Logout.setLayoutY(400);
		button_Logout.setOnAction(e -> showGoodbyeScreen());

		// Add to update pane
		paneUpdate.getChildren().addAll(
				label_UpdateTitle,
				label_UpdateName, text_UpdateName,
				label_UpdateAddress, text_UpdateAddress,
				label_UpdateEmail, text_UpdateEmail,
				label_UpdatePassword, text_UpdatePassword,
				label_NewPassword, text_NewPassword,
				label_UpdateNote, button_SaveChanges, button_Logout
		);
	}

	/**********
	 * Swap to Update screen (called when Login is clicked)
	 */
	private void goToUpdateScreen() {
		// Capture current entries
		String name = text_Name.getText();
		String addr = text_Address.getText();
		String email = text_Email.getText();
		String pwd = text_Password.getText();

		// Build the Update pane with these values prefilled
		buildUpdateScreen(name, addr, email, pwd);

		// Swap the whole root content
		theRoot.getChildren().setAll(paneUpdate);
	}

	/**********
	 * Show a tiny goodbye panel after logout
	 */
	private void showGoodbyeScreen() {
		Pane bye = new Pane();
		Label thanks = new Label("Thanks for using our program,\nwe'll see you later!");
		setupLabelUI(thanks, "Arial", 20, PasswordEvaluationGUITestbed.WINDOW_WIDTH,
				Pos.CENTER, 0, 200);
		bye.getChildren().add(thanks);
		theRoot.getChildren().setAll(bye);
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

	User Interface Actions (Login screen)
	
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
	// (Optional) CSV helpers kept from an earlier step. Not used
	// in the new flow, but left here harmlessly if you need them.
	//////////////////////////////////////////////////////////////
	private void saveInfoUnusedExample() {
		Path out = Path.of(System.getProperty("user.home"), "password_testbed_submissions.csv");
		String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n",
				escapeCsv(ts), escapeCsv(text_Name.getText()), escapeCsv(text_Address.getText()), escapeCsv(text_Email.getText()));
		try {
			if (!Files.exists(out)) {
				Files.writeString(out, "timestamp,name,address,email\n", StandardOpenOption.CREATE);
			}
			Files.write(out, line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (Exception ignored) {}
	}
	private String escapeCsv(String s) { return s.replace("\"", "\"\""); }
}
