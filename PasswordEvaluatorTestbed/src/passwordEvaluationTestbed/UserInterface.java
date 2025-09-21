package passwordEvaluationTestbed;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {

	private final Pane theRoot;

	// login screen
	private final Pane paneLogin = new Pane();
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

    // login fields in order: name -> address -> email -> password
    private Label label_Name = new Label("Enter your name");
    private TextField text_Name = new TextField();
    private Label label_Address = new Label("Enter your address");
    private TextField text_Address = new TextField();
    private Label label_Email = new Label("Enter your email");
    private TextField text_Email = new TextField();
    private Button button_Login = new Button("Login");
    private Label label_LoginStatus = new Label("");

	// update screen
	private final Pane paneUpdate = new Pane();
	private Label label_UpdateTitle = new Label("Update Account");
	private Label label_UpdateName = new Label("Name");
	private TextField text_UpdateName = new TextField();
	private Label label_UpdateAddress = new Label("Address");
	private TextField text_UpdateAddress = new TextField();
	private Label label_UpdateEmail = new Label("Email");
	private TextField text_UpdateEmail = new TextField();
	private Label label_UpdatePassword = new Label("Current Password");
	private TextField text_UpdatePassword = new TextField();
	private Label label_NewPassword = new Label("New Password");
	private TextField text_NewPassword = new TextField();
	private Label label_ConfirmPassword = new Label("Confirm New Password");
	private TextField text_ConfirmPassword = new TextField();
	private Button button_SaveChanges = new Button("Save Changes");
	private Button button_Logout = new Button("Log Out");
	private Label label_UpdateStatus = new Label("");
	private Label label_UpdateNote = new Label(
		"(email is locked; to change your password, enter your current password," +
		" a valid new password, and a matching confirmation.)"
	);

	// simple csv storage in current directory
	private static final String CSV_FILE_NAME = "password_testbed_users.csv";
	private static final String CSV_HEADER   = "timestamp,name,address,email,password\n";

	private static class UserRecord {
		String name, address, email, password;
		UserRecord(String n, String a, String e, String p){ name=n; address=a; email=e; password=p; }
	}

	public UserInterface(Pane _root) {
		this.theRoot = _root;
		buildLoginScreen();
		theRoot.getChildren().setAll(paneLogin);
	}

	private void buildLoginScreen() {
		paneLogin.getChildren().clear();

		setupLabelUI(label_ApplicationTitle, "Arial", 24, PasswordEvaluationGUITestbed.WINDOW_WIDTH, Pos.CENTER, 0, 10);

        setupLabelUI(label_Name, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 50);
        setupTextUI(text_Name, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 70, true);

        setupLabelUI(label_Address, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 110);
        setupTextUI(text_Address, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 130, true);

        setupLabelUI(label_Email, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 170);
        setupTextUI(text_Email, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 190, true);

		setupLabelUI(label_Password, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 230);
		setupTextUI(text_Password, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 250, true);
		text_Password.textProperty().addListener((obs, o, n) -> setPassword());

		noInputFound.setTextFill(Color.RED);
		noInputFound.setAlignment(Pos.BASELINE_LEFT);
		setupLabelUI(noInputFound, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 285);

		label_errPassword.setTextFill(Color.RED);
		label_errPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errPassword, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 311);

		errPasswordPart1.setFill(Color.BLACK);
	    errPasswordPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
	    errPasswordPart2.setFill(Color.RED);
	    errPasswordPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
	    errPassword = new TextFlow(errPasswordPart1, errPasswordPart2);
		errPassword.setMinWidth(PasswordEvaluationGUITestbed.WINDOW_WIDTH-10);
		errPassword.setLayoutX(22);
		errPassword.setLayoutY(285);
		setupLabelUI(errPasswordPart3, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 310);

        button_Login.setFont(Font.font("Arial", 16));
        button_Login.setPrefWidth(160);
        button_Login.setLayoutX(10);
        button_Login.setLayoutY(340);
        button_Login.setOnAction(e -> handleLogin());

        label_LoginStatus.setTextFill(Color.GREEN);
        setupLabelUI(label_LoginStatus, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 180, 343);

	    setupLabelUI(label_Requirements, "Arial", 16, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 380);
	    setupLabelUI(label_UpperCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 410);
	    label_UpperCase.setTextFill(Color.RED);
	    setupLabelUI(label_LowerCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 435);
	    label_LowerCase.setTextFill(Color.RED);
	    setupLabelUI(label_NumericDigit, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 460);
	    label_NumericDigit.setTextFill(Color.RED);
	    setupLabelUI(label_SpecialChar, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 485);
	    label_SpecialChar.setTextFill(Color.RED);
	    setupLabelUI(label_LongEnough, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 510);
	    label_LongEnough.setTextFill(Color.RED);
		resetAssessments();

		validPassword.setTextFill(Color.GREEN);
		validPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(validPassword, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 10, 545);

		paneLogin.getChildren().addAll(
			label_ApplicationTitle,
            label_Name, text_Name,
            label_Address, text_Address,
            label_Email, text_Email,
			label_Password, text_Password,
            button_Login, label_LoginStatus,
			noInputFound, label_errPassword, errPassword, errPasswordPart3, validPassword,
			label_Requirements, label_UpperCase, label_LowerCase, label_NumericDigit, label_SpecialChar, label_LongEnough
        );
	}

	private void buildUpdateScreen(String name, String address, String email) {
		paneUpdate.getChildren().clear();

		setupLabelUI(label_UpdateTitle, "Arial", 24, PasswordEvaluationGUITestbed.WINDOW_WIDTH, Pos.CENTER, 0, 20);

		// prefill fields and lock email
		text_UpdateName.setText(name);
		text_UpdateAddress.setText(address);
		text_UpdateEmail.setText(email);
		text_UpdateEmail.setEditable(false);
		text_UpdatePassword.setText("");

		setupLabelUI(label_UpdateName, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 80);
		setupTextUI(text_UpdateName, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 100, true);

		setupLabelUI(label_UpdateAddress, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 140);
		setupTextUI(text_UpdateAddress, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 160, true);

		setupLabelUI(label_UpdateEmail, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 200);
		setupTextUI(text_UpdateEmail, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 220, false);

		setupLabelUI(label_UpdatePassword, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 260);
		setupTextUI(text_UpdatePassword, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 280, true);

		setupLabelUI(label_NewPassword, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 320);
		setupTextUI(text_NewPassword, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 340, true);

		setupLabelUI(label_ConfirmPassword, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 380);
		setupTextUI(text_ConfirmPassword, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 10, 400, true);

		label_UpdateNote.setTextFill(Color.GRAY);
		setupLabelUI(label_UpdateNote, "Arial", 12, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 430);

		label_UpdateStatus.setTextFill(Color.RED);
		setupLabelUI(label_UpdateStatus, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 455);

		button_SaveChanges.setFont(Font.font("Arial", 16));
		button_SaveChanges.setLayoutX(10);
		button_SaveChanges.setLayoutY(485);
		button_SaveChanges.setOnAction(e -> handleSaveChanges());

		button_Logout.setFont(Font.font("Arial", 16));
		button_Logout.setLayoutX(190);
		button_Logout.setLayoutY(485);
		button_Logout.setOnAction(e -> showGoodbyeScreen());

		paneUpdate.getChildren().addAll(
			label_UpdateTitle,
			label_UpdateName, text_UpdateName,
			label_UpdateAddress, text_UpdateAddress,
			label_UpdateEmail, text_UpdateEmail,
			label_UpdatePassword, text_UpdatePassword,
			label_NewPassword, text_NewPassword,
			label_ConfirmPassword, text_ConfirmPassword,
			label_UpdateNote, label_UpdateStatus, button_SaveChanges, button_Logout
		);
	}

	private void goToUpdateScreen(String name, String address, String email) {
		buildUpdateScreen(name, address, email);
		theRoot.getChildren().setAll(paneUpdate);
	}

	private void showGoodbyeScreen() {
		Pane bye = new Pane();
		Label thanks = new Label("Thanks for using our program,\nwe'll see you later!");
		setupLabelUI(thanks, "Arial", 20, PasswordEvaluationGUITestbed.WINDOW_WIDTH, Pos.CENTER, 0, 200);
		bye.getChildren().add(thanks);
		theRoot.getChildren().setAll(bye);
	}

	// small ui helpers
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}

	// live password validation on login
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
		if (inputText.isEmpty()) {
		    noInputFound.setText("No input text found!");
		} else {
			String errMessage = PasswordEvaluator.evaluatePassword(inputText);
			updateFlags();
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
	    setupLabelUI(label_UpperCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 410);
	    label_UpperCase.setTextFill(Color.RED);

	    label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
	    setupLabelUI(label_LowerCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 435);
	    label_LowerCase.setTextFill(Color.RED);

	    label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
	    setupLabelUI(label_NumericDigit, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 460);
	    label_NumericDigit.setTextFill(Color.RED);

	    label_SpecialChar.setText("At least one special character - Not yet satisfied");
	    setupLabelUI(label_SpecialChar, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 485);
	    label_SpecialChar.setTextFill(Color.RED);

	    label_LongEnough.setText("At least eight characters - Not yet satisfied");
	    setupLabelUI(label_LongEnough, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 30, 510);
	    label_LongEnough.setTextFill(Color.RED);

	    errPasswordPart3.setText("");
	}

	private void updateFlags() {
		if (PasswordEvaluator.foundUpperCase) { label_UpperCase.setText("At least one upper case letter - Satisfied"); label_UpperCase.setTextFill(Color.GREEN); }
		if (PasswordEvaluator.foundLowerCase) { label_LowerCase.setText("At least one lower case letter - Satisfied"); label_LowerCase.setTextFill(Color.GREEN); }
		if (PasswordEvaluator.foundNumericDigit) { label_NumericDigit.setText("At least one numeric digit - Satisfied"); label_NumericDigit.setTextFill(Color.GREEN); }
		if (PasswordEvaluator.foundSpecialChar) { label_SpecialChar.setText("At least one special character - Satisfied"); label_SpecialChar.setTextFill(Color.GREEN); }
		if (PasswordEvaluator.foundLongEnough) { label_LongEnough.setText("At least eight characters - Satisfied"); label_LongEnough.setTextFill(Color.GREEN); }
	}

	// csv helpers
	private Path csvPath() { return Path.of(CSV_FILE_NAME); }
	private void ensureCsvHeader(Path p) {
		try {
			if (!Files.exists(p)) Files.writeString(p, CSV_HEADER, StandardOpenOption.CREATE);
		} catch (Exception ignored) {}
	}
	private static String escapeCsv(String s) { return s.replace("\"", "\"\""); }
	private static String unquote(String s) {
		if (s == null) return "";
		s = s.trim();
		if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
			return s.substring(1, s.length()-1).replace("\"\"", "\"");
		}
		return s;
	}
	private static String[] parseCsvLine(String line) {
		return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
	}
	private UserRecord findByEmail(String email) {
		try {
			Path p = csvPath();
			if (!Files.exists(p)) return null;
			for (String line : Files.readAllLines(p)) {
				if (line.startsWith("timestamp,")) continue;
				String[] cols = parseCsvLine(line);
				if (cols.length < 5) continue;
				String e = unquote(cols[3]);
				if (email.equalsIgnoreCase(e)) {
					String n = unquote(cols[1]);
					String a = unquote(cols[2]);
					String pw = unquote(cols[4]);
					return new UserRecord(n, a, e, pw);
				}
			}
		} catch (Exception ignored) {}
		return null;
	}
	private void upsert(UserRecord rec) throws Exception {
		Path p = csvPath();
		ensureCsvHeader(p);
		List<String> lines = new ArrayList<>(Files.readAllLines(p));
		if (lines.isEmpty() || !lines.get(0).startsWith("timestamp,")) {
			if (!lines.isEmpty()) lines.add(0, CSV_HEADER.trim()); else lines.add(CSV_HEADER.trim());
		}
		List<String> kept = new ArrayList<>();
		kept.add(lines.get(0));
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] cols = parseCsvLine(line);
			if (cols.length < 5) continue;
			String e = unquote(cols[3]);
			if (!rec.email.equalsIgnoreCase(e)) kept.add(line);
		}
		String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String newline = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
				escapeCsv(ts), escapeCsv(rec.name), escapeCsv(rec.address),
				escapeCsv(rec.email), escapeCsv(rec.password));
		kept.add(newline);
		Files.write(p, String.join("\n", kept).concat("\n").getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

	// login handler: creates or authenticates then navigates
	private void handleLogin() {
		label_LoginStatus.setTextFill(Color.RED);
		label_LoginStatus.setText("");

		String name = text_Name.getText().trim();
		String address = text_Address.getText().trim();
		String email = text_Email.getText().trim();
		String password = text_Password.getText();

		if (name.isEmpty()) { label_LoginStatus.setText("Enter your name."); return; }
		if (address.isEmpty()) { label_LoginStatus.setText("Enter your address."); return; }
		if (email.isEmpty() || !email.contains("@")) { label_LoginStatus.setText("Enter a valid email."); return; }

		String err = PasswordEvaluator.evaluatePassword(password);
		updateFlags();
		if (!err.isEmpty()) { label_LoginStatus.setText("Password is not valid."); return; }

		UserRecord existing = findByEmail(email);
		try {
			if (existing == null) {
				upsert(new UserRecord(name, address, email, password));
				label_LoginStatus.setTextFill(Color.GREEN);
				label_LoginStatus.setText("Account created.");
			} else {
				if (!existing.password.equals(password)) {
					label_LoginStatus.setText("Wrong password for this email.");
					return;
				}
				name = existing.name;
				address = existing.address;
			}
		} catch (Exception ex) {
			label_LoginStatus.setText("Data error: " + ex.getMessage());
			return;
		}

		goToUpdateScreen(name, address, email);
	}

	// update handler: requires current password and valid matching new password
	private void handleSaveChanges() {
		label_UpdateStatus.setTextFill(Color.RED);
		label_UpdateStatus.setText("");

		String name = text_UpdateName.getText().trim();
		String address = text_UpdateAddress.getText().trim();
		String email = text_UpdateEmail.getText().trim();
		String currentPwdInput = text_UpdatePassword.getText();
		String newPwd = text_NewPassword.getText();
		String confirm = text_ConfirmPassword.getText();

		if (name.isEmpty()) { label_UpdateStatus.setText("Name required."); return; }
		if (address.isEmpty()) { label_UpdateStatus.setText("Address required."); return; }
		if (email.isEmpty() || !email.contains("@")) { label_UpdateStatus.setText("Email looks invalid."); return; }

		UserRecord existing = findByEmail(email);
		if (existing == null) { label_UpdateStatus.setText("Account not found for this email."); return; }
		if (currentPwdInput == null || currentPwdInput.isEmpty() || !currentPwdInput.equals(existing.password)) {
			label_UpdateStatus.setText("Current password is incorrect. Please try again.");
			return;
		}

		if (newPwd.isEmpty()) { label_UpdateStatus.setText("Enter a new password."); return; }
		if (!newPwd.equals(confirm)) { label_UpdateStatus.setText("New password and confirmation do not match. Please try again."); return; }

		String err = PasswordEvaluator.evaluatePassword(newPwd);
		if (!err.isEmpty()) { label_UpdateStatus.setText("New password is not valid."); return; }

		try {
			upsert(new UserRecord(name, address, email, newPwd));
			label_UpdateStatus.setTextFill(Color.GREEN);
			label_UpdateStatus.setText("Changes saved.");
			text_UpdatePassword.clear();
			text_NewPassword.clear();
			text_ConfirmPassword.clear();
		} catch (Exception ex) {
			label_UpdateStatus.setText("Save failed: " + ex.getMessage());
		}
	}
}
