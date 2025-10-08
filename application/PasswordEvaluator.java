package application;

public class PasswordEvaluator {
	/**
	 * <p> Title: Directed Graph-translated Password Assessor. </p>
	 *
	 * <p> Description: A demonstration of the mechanical translation of a Directed Graph
	 * diagram into an executable Java program using the Password Evaluator Directed Graph.
	 * The code detailed design is based on a while loop with a cascade of if statements. </p>
	 *
	 * <p> Copyright: Lynn Robert Carter © 2022 </p>
	 *
	 * @author Lynn Robert Carter
	 *
	 * @version 0.00 2018-02-22 Initial baseline
	 * @version 1.00 2025-01-16 HW1 update:
	 *   - Explicitly rejects whitespace with position.
	 *   - Helpful illegal-character message shows the character and index.
	 *   - Keeps flags the test harness reads (upper/lower/digit/special/length).
	 *   - Preserves console trace and overall style.
	 */

	/**********************************************************************************************
	 * Result attributes for GUI/console: detailed message and pointer to error location.
	 */
	public static String passwordErrorMessage = "";     // The error message text
	public static String passwordInput = "";            // The input being processed
	public static int    passwordIndexofError = -1;     // Index where the error was located

	// Feature flags the harness prints:
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;

	private static String inputLine = "";               // The input line
	private static char   currentChar;                  // The current character in the line
	private static int    currentCharNdx;               // The index of the current character
	private static boolean running;                     // Directed graph "engine" running flag

	// ---- Course baseline knobs (adjust only if your diagram says otherwise) ----
	private static final int MIN_LENGTH = 8; // "At least 8 characters"
	private static final String SPECIALS = "~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/"; // allowed specials
	// ----------------------------------------------------------------------------

	/**********
	 * Display the input line and a caret at the current index (console trace).
	 */
	private static void displayInputState() {
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0, currentCharNdx) + "?");
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " +
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}

	/**********
	 * Directed Graph simulation.
	 *
	 * Contract with the course test harness:
	 *  - Returns "" (empty string) if the password is VALID.
	 *  - Returns a non-empty diagnostic string if INVALID.
	 *  - Updates foundUpperCase / foundLowerCase / foundNumericDigit / foundSpecialChar / foundLongEnough.
	 */
	public static String evaluatePassword(String input) {
		// Initialize
		passwordErrorMessage = "";
		passwordIndexofError = 0;
		inputLine = input == null ? "" : input;
		currentCharNdx = 0;

		if (input == null || input.length() <= 0) return "*** Error *** The password is empty!";

		// Prime first character
		currentChar = input.charAt(0);

		// Reset flags the harness reads
		passwordInput      = input;
		foundUpperCase     = false;
		foundLowerCase     = false;
		foundNumericDigit  = false;
		foundSpecialChar   = false;
		foundLongEnough    = false;
		running            = true;

		// Scan each character (cascade of ifs = "edges" of the directed graph)
		while (running) {
			displayInputState();

			// Set length flag when we've seen at least MIN_LENGTH chars (index is zero-based)
			if (currentCharNdx >= (MIN_LENGTH - 1)) {
				System.out.println("At least " + MIN_LENGTH + " characters found");
				foundLongEnough = true;
			}

			// Category checks (semantic actions)
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;

			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;

			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;

			} else if (SPECIALS.indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;

			} else if (Character.isWhitespace(currentChar)) {
				// Helpful, specific whitespace rejection
				passwordIndexofError = currentCharNdx;
				return "*** Error *** Whitespace is not allowed at position " + currentCharNdx + "!";

			} else {
				// Explicit illegal character message with the character and index
				passwordIndexofError = currentCharNdx;
				return "*** Error *** Illegal character '" + printable(currentChar) +
						"' at position " + currentCharNdx + "!";
			}

			// Advance
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);

			System.out.println();
		}

		// Build any unmet-requirements message
		StringBuilder err = new StringBuilder();
		if (!foundUpperCase)    err.append("Upper case; ");
		if (!foundLowerCase)    err.append("Lower case; ");
		if (!foundNumericDigit) err.append("Numeric digits; ");
		if (!foundSpecialChar)  err.append("Special character; ");
		if (!foundLongEnough)   err.append("Long Enough; ");

		if (err.length() == 0) {
			// Valid password
			return "";
		}

		passwordIndexofError = currentCharNdx;
		return err.toString() + "conditions were not satisfied";
	}

	// Replace non-printables with '?' in diagnostics
	private static char printable(char c) {
		return (c >= 32 && c <= 126) ? c : '?';
	}
}

