import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ashkan Moatamed <br>
 *         <br>
 *         The <code>YRBAPP</code> class represents a <b>Search and Purchase</b> application for the
 *         <b><i>York River Bookseller's Database</i></b>.
 *
 * @see #YRBAPP()
 * 
 * @see YRBAPPUtility
 * @see Book
 * @see Purchase
 */
public final class YRBAPP {
	/**
	 * The url for the <code>prism db2 client</code>.
	 * 
	 * @see #YRBAPP()
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String DEFAULT_URL = "jdbc:db2:c3421a";

	/**
	 * The URL of the current database connection.
	 * 
	 * @see #getURL()
	 * @see #setURL(String, boolean)
	 * @see #setURL(String)
	 */
	private String url;

	/**
	 * Returns the current database connection url.
	 * 
	 * @return <code>this.url</code>.
	 * 
	 * @see #url
	 */
	// Strings are immutable in Java which is why returning a direct reference to this variable,
	// does not pose any issues against encapsulation.
	public String getURL() {
		return this.url;
	}

	/**
	 * <code>If commit then</code> commit any/all of the changes made <code>else</code> rollback any/all
	 * of the changes made, to the current database and then close the connection. <br>
	 * <br>
	 * 
	 * At this point set the new <code>url</code> and the new <code>database connection</code>.
	 * 
	 * @param url
	 *            the new database url
	 * 
	 * @param commit
	 *            <code>commit ? this.commitChanges() : this.rollbackChanges()</code>
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @throws NullPointerException
	 *             If <code>url == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>url.isEmpty()</code>
	 * 
	 * @see #url
	 * @see #db_connect
	 * 
	 * @see #commitChanges()
	 * @see #rollbackChanges()
	 * @see #closeConnection()
	 * 
	 * @see #setURL(String)
	 */
	public boolean setURL(String url, boolean commit) throws NullPointerException, IllegalArgumentException {
		if (url == null) {
			throw new NullPointerException("Given URL string is null.");
		} else if (url.isEmpty()) {
			throw new IllegalArgumentException("Given URL string is empty.");
		}

		// Initialize the connection.
		Connection db_connect;
		try {
			// Connect with a fall-thru ID & password.
			db_connect = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			YRBAPP.logError("Failed to connect to database.\n" + ex.toString());
			return false;
		}

		// Save the current url and set it to the new url
		String savedURL = this.getURL();
		this.url = url;

		if (commit) {
			// Commit any/all of the changes made, to the database.
			if (!this.commitChanges()) {
				// Upon failure, restore the saved url.
				this.url = savedURL;
				return false;
			}
		} else {
			// Rollback any/all of the changes made, to the current database.
			if (!this.rollbackChanges()) {
				// Upon failure, restore the saved url.
				this.url = savedURL;
				return false;
			}
		}

		// Close the database connection.
		if (!this.closeConnection()) {
			// Upon failure, restore the saved url.
			this.url = savedURL;
			return false;
		}

		// Set the database connection.
		this.db_connect = db_connect;
		return true;
	}

	/**
	 * Commit any/all of the changes made, to the current database and then close the connection. <br>
	 * <br>
	 * 
	 * At this point set the new <code>url</code> and the new <code>database connection</code>. <br>
	 * <br>
	 * 
	 * Same as calling <code>setURL(String url, boolean commit)</code> with arguments
	 * <code>(url, true)</code>.
	 * 
	 * @param url
	 *            the new database url
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @throws NullPointerException
	 *             If <code>url == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>url.isEmpty()</code>
	 * 
	 * @see #url
	 * @see #db_connect
	 * @see #setURL(String, boolean)
	 * 
	 * @see #YRBAPP()
	 */
	public boolean setURL(String url) throws NullPointerException, IllegalArgumentException {
		return this.setURL(url, true);
	}

	/**
	 * The current connection to the database system.
	 * 
	 * @see #setURL(String, boolean)
	 * @see #setURL(String)
	 */
	private Connection db_connect;

	/**
	 * Commit any/all of the changes made, to the current database.
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @see #setURL(String)
	 * 
	 * @see #exit(boolean, int)
	 */
	private boolean commitChanges() {
		if (this.db_connect != null) {
			// Commit to the database.
			try {
				this.db_connect.commit();
			} catch (SQLException ex) {
				// YRBAPP.printStackTrace(ex);
				YRBAPP.logError("Failed to commit to database.\nURL = " + this.getURL() + "\n" + ex.toString());
				return false;
			}
		}

		// No connection implies success.
		return true;
	}

	/**
	 * Rollback any/all of the changes made, to the current database.
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @see #exit(boolean, int)
	 */
	private boolean rollbackChanges() {
		if (this.db_connect != null) {
			// Rollback the database.
			try {
				this.db_connect.rollback();
			} catch (SQLException ex) {
				// YRBAPP.printStackTrace(ex);
				YRBAPP.logError("Failed to rollback database.\nURL = " + this.getURL() + "\n" + ex.toString());
				return false;
			}
		}

		// No connection implies success.
		return true;
	}

	/**
	 * Close the current database connection.
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @see #setURL(String)
	 * 
	 * @see #exit(boolean, int)
	 */
	private boolean closeConnection() {
		if (this.db_connect != null) {
			// Close the database connection.
			try {
				this.db_connect.close();
			} catch (SQLException ex) {
				// YRBAPP.printStackTrace(ex);
				YRBAPP.logError("Failed to close database connection.\nURL = " + this.getURL() + "\n" + ex.toString());
				return false;
			}
		}

		// No connection to close implies success.
		return true;
	}

	/**
	 * The confirmation indicator used during application run to prompt user for confirmation or not.
	 * <br>
	 * <br>
	 * 
	 * <code>this.confirm ? "Prompt user for confirmation after data entry." : "Do NOT prompt user for confirmation after data entry."</code>
	 * 
	 * @see #getConfirm()
	 * @see #setConfirm(boolean)
	 */
	private boolean confirm;

	/**
	 * Returns the current value of the confirmation indicator.
	 * 
	 * @return <code>this.confirm</code>.
	 * 
	 * @see #confirm
	 */
	// booleans are call-by-value in Java which is why returning the value directly,
	// does not pose any issues against encapsulation.
	public boolean getConfirm() {
		return this.confirm;
	}

	/**
	 * Set the current value of the confirmation indicator to the new given value.
	 * 
	 * @param confirm
	 *            the new confirmation indicator
	 * 
	 * @return <code>this.confirm</code>.
	 * 
	 * @see #confirm
	 * 
	 * @see #appStartup(boolean)
	 */
	// booleans are call-by-value in Java which is why returning the value directly,
	// does not pose any issues against encapsulation.
	public boolean setConfirm(boolean confirm) {
		return (this.confirm = confirm);
	}

	/**
	 * The commit indicator used when End Of Input has been reached during an application run and
	 * <code>this.EOI()</code> is called. <br>
	 * <br>
	 * 
	 * <code>this.EOICommit ? "this.EOI() commits any/all changes." : "this.EOI() rolls back any/all changes."</code>
	 * 
	 * @see #getEOICommit()
	 * @see #setEOICommit(boolean)
	 * 
	 * @see #EOI()
	 */
	private boolean EOICommit;

	/**
	 * Returns the current value of the commit indicator.
	 * 
	 * @return <code>this.EOICommit</code>.
	 * 
	 * @see #EOICommit
	 */
	// booleans are call-by-value in Java which is why returning the value directly,
	// does not pose any issues against encapsulation.
	public boolean getEOICommit() {
		return this.EOICommit;
	}

	/**
	 * Set the current value of the commit indicator to the new given value.
	 * 
	 * @param EOICommit
	 *            the new commit indicator
	 * 
	 * @return <code>this.EOICommit</code>.
	 * 
	 * @see #EOICommit
	 */
	public boolean setEOICommit(boolean EOICommit) {
		return (this.EOICommit = EOICommit);
	}

	/**
	 * A <code>StringBuilder</code> of all errors that have occurred thus far.
	 * 
	 * @see #getErrors()
	 * @see #logError(CharSequence)
	 * @see #clearErrors()
	 */
	private static StringBuilder errors;

	/**
	 * Returns all of the errors that have occurred thus far.
	 * 
	 * @return <code>YRBAPP.errors.toString()</code>.
	 * 
	 * @see #errors
	 * @see #clearErrors()
	 */
	public static String getErrors() {
		return YRBAPP.errors.toString();
	}

	/**
	 * Log the most recent error to <code>errors</code>.
	 * 
	 * @param s
	 *            the most recent error
	 * 
	 * @see #errors
	 */
	private static void logError(CharSequence s) {
		if (s != null && s.length() != 0) { // Only append if valid input.
			YRBAPP.errors.append(s);
			YRBAPP.errors.append("\n");
		}
	}

	/**
	 * Clear all of the saved errors.
	 * 
	 * @return All of the errors that have occurred before the call to this method and before
	 *         <code>errors</code> is reset.
	 * 
	 * @see #errors
	 * @see #getErrors()
	 */
	public static String clearErrors() {
		String result = YRBAPP.getErrors();
		YRBAPP.errors = new StringBuilder();
		return result;
	}

	/**
	 * A singleton instance of an object of static type <code>YRBAPP</code>.
	 */
	private static YRBAPP instance;

	/**
	 * Returns a singleton access to a <code>YRBAPP</code> object.
	 * 
	 * @return The singleton reference.
	 */
	public static YRBAPP getInstance() {
		if (YRBAPP.instance == null) {
			YRBAPP.instance = new YRBAPP();
		}
		return YRBAPP.instance;
	}

	/**
	 * <code>RunState</code> encapsulates all of the possible states during a single run of the
	 * application. <br>
	 * <br>
	 * 
	 * <code>CUSTOMER</code>: Find a customer and then update the information if requested. <br>
	 * <br>
	 * 
	 * <code>CATEGORY</code>: Find all book categories and then prompt user for selection. <br>
	 * <br>
	 * 
	 * <code>BOOK</code>: Find all books of the selected category and prompt user for selection. <br>
	 * <br>
	 * 
	 * <code>PURCHASE</code>: Purchase the chosen quantity number of the selected book. <br>
	 * <br>
	 * 
	 * <code>FINALIZE</code>: Finalize the purchase and insert it into the database if requested. <br>
	 * <br>
	 * 
	 * <code>BACK_TRACK</code>: Ask the user if they want to go back to previous states or exit, since
	 * currently there is an error in making the purchase.
	 */
	private static enum RunState {
		CUSTOMER, CATEGORY, BOOK, PURCHASE, FINALIZE, BACK_TRACK
	}

	/**
	 * A special keyword to exit during application run and NOT commit any/all changes.
	 * 
	 * @see #nextLine()
	 * 
	 * @see #handleTermination(String)
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String ABORT = "abort";

	/**
	 * A special keyword to exit during application run and commit any/all changes.
	 * 
	 * @see #nextLine()
	 * 
	 * @see #handleTermination(String)
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String EXIT = "exit";

	/**
	 * A special keyword to stop the application from prompting for more input in the customer
	 * information update stage.
	 * 
	 * @see #update_customer(short, String, String)
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String STOP = "stop";

	/**
	 * A </code>Scanner</code> over <code>System.in</code> used during a single run of the application.
	 * 
	 * @see #nextLine()
	 */
	// It is used so that different methods used during a single run of the application, can all
	// have access to the same <code>InputStream(System.in)</code>.
	private Scanner in;

	/**
	 * Get the next line in the global scanner's buffer and then handle the two following special cases:
	 * <br>
	 * 1. End Of Input: Handled by <code>this.EOI()</code> <br>
	 * 2. Special Keywords(YRBAPP.ABORT and YRBAPP.EXIT): Handled by
	 * <code>this.handleTermination(String)</code>
	 * 
	 * @return <code>this.in.nextLine()</code>.
	 * 
	 * @throws NullPointerException
	 *             If <code>this.in == null</code>
	 * 
	 * @see #in
	 * 
	 * @see #EOI()
	 * 
	 * @see #ABORT
	 * @see #EXIT
	 * @see #handleTermination(String)
	 */
	private String nextLine() throws NullPointerException {
		if (this.in == null) {
			throw new NullPointerException("Global Scanner is null.");
		}

		if (!this.in.hasNextLine()) {
			this.EOI(); // Handle End Of Input.
		}

		String result = this.in.nextLine();

		// Handle the two special reserved exit keywords.
		this.handleTermination(result);

		return result;
	}

	/**
	 * Parse a user's input into a boolean.
	 * 
	 * @param input
	 *            the user's answer to a yes/no question
	 * 
	 * @return <tt>true</tt> if and only if <code>input</code> is one of the following strings: <br>
	 *         "y" or "Y" or "yes" or "Yes" or "YES"
	 */
	public static boolean parseAnswer(String input) {
		if (input == null || input.isEmpty()) { // Invalid inputs.
			return false;
		} else if (Character.toLowerCase(input.charAt(0)) != 'y') {
			// The lowercase first character of all of the following strings is 'y':
			// "y" or "Y" or "yes" or "Yes" or "YES"
			return false;
		} else if (input.length() == 1) {
			// Only matches "y" or "Y".
			return true;
		} else if (input.length() != 3) {
			// "yes", "Yes" and "YES" all have length 3.
			return false;
		}
		return (input.equals("yes") || input.equals("Yes") || input.equals("YES"));
	}

	/**
	 * Read and parse the next line of the input into a boolean.
	 * 
	 * @return <code>YRBAPP.parseAnswer(this.nextLine())</code>.
	 */
	private boolean parseAnswer() {
		return YRBAPP.parseAnswer(this.nextLine());
	}

	/**
	 * Default constructor for a <code>YRBAPP</code> object. Sets up the <code>url</code> and
	 * <code>db_connect</code>(database connection) and turns auto-committing off so that the user can
	 * choose to commit or rollback using <code>this.commitChanges()</code> and
	 * <code>this.rollbackChanges()</code> whenever they want to do so. <br>
	 * <br>
	 * 
	 * Sets the <code>url</code> to <code>YRBAPP.DEFAULT_URL</code>.
	 * 
	 * @see #DEFAULT_URL
	 * 
	 * @see #setURL(String)
	 */
	private YRBAPP() {
		// Construct errors StringBuilder.
		YRBAPP.errors = new StringBuilder();

		try { // Register the driver with DriverManager.
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		} catch (LinkageError | ClassNotFoundException | InstantiationException | IllegalAccessException
				| SecurityException ex) {
			YRBAPP.printStackTrace(ex);
			this.exit();
		}

		// Attempt to set the url and make the database connection.
		if (!this.setURL(YRBAPP.DEFAULT_URL)) {
			this.exit();
		}

		try { // Turn auto commit off.
			this.db_connect.setAutoCommit(false);
		} catch (SQLException ex) {
			YRBAPP.logError("Failed trying to turn autocommit off.\n" + ex.toString());
			this.exit();
		}
	}

	/**
	 * Print the startup messages for a single run of the application to the standard output
	 * stream(<code>System.out</code>). <br>
	 * <br>
	 * 
	 * After initial messages, determine whether user is interested in being prompted for confirmation
	 * or not.
	 * 
	 * @param firstTime
	 *            the indicator representing whether it is the first application run or not
	 * 
	 * @see #setConfirm(boolean)
	 */
	private void appStartup(boolean firstTime) {
		if (!firstTime) {
			// Application run separator.
			System.out.println("\n\n\n\n\n--------------------------------------------------\n\n\n\n\n");
		}

		// Application greeting.
		System.out
				.println("Welcome to the Search and Purchase application for the York River Bookseller's Database.\n");

		// Inform user about yes/no questions and how to answer them.
		System.out.println(
				"Whenever prompted to answer a yes/no question, enter \"y\", \"Y\", \"yes\", \"Yes\" or \"YES\" to indicate a yes. Everything else is processed as a no.\n");

		// Inform user about termination keywords.
		System.out.println(
				"Enter \"" + YRBAPP.ABORT + "\" at any point to abort the application (exit without committing).");
		System.out.println(
				"Enter \"" + YRBAPP.EXIT + "\" at any point to exit the application (exit with committing).\n");

		// Inform user about data input/output.
		System.out.println(
				"Please wait to be prompted for input before you enter data since otherwise the old data will be read which might cause unwanted behavior.");
		System.out.println("Note that input is read line by line so please terminate every input by a single newline.");
		System.out.println("Also note that input is case sensitive so beware of CAPS-LOCK and SHIFT.\n");

		// Inform user about a short integers.
		System.out.println("Short Integers are whole numbers in the following range: [" + Short.MIN_VALUE + ", "
				+ Short.MAX_VALUE + "]\n");

		// Determine the confirmation indicator.
		System.out.println(
				"There are two types of prompts in this application: one requires a yes/no answer while the other requires data entry.");
		System.out
				.print("Do you want to be prompted to confirm your input after validation for the latter type? (y/n) ");
		this.setConfirm(this.parseAnswer());
		System.out.print(firstTime ? "\n" : "");
	}

	/**
	 * Run the <b>Search and Purchase</b> application. <br>
	 * <br>
	 * 
	 * @see #find_customer()
	 * @see #fetch_categories()
	 * @see #find_books(String)
	 * @see #min_price(Short, Book)
	 * @see #insert_purchase(Purchase)
	 * @see #find_purchases(short)
	 */
	public void run() {
		try (Scanner in = new Scanner(System.in)) { // try-with-resource
			this.in = in; // Save the Scanner globally.

			this.appStartup(true);

			// Prompt for the value of this.EOICommit but only do it once.
			// This prompt will only be executed the very first time the app runs.
			System.out.print(
					"Do you want to commit any/all of the changes made, to the database if/when the End Of Input is reached during a prompt? (y/n) ");
			this.setEOICommit(this.parseAnswer());
			System.out.println();

			boolean done = false, exit = false;
			System.out.print("Do you want to start the application? (y/n) ");
			if (!this.parseAnswer()) {
				done = true; // never enter while loop
			}

			RunState state = RunState.CUSTOMER;
			Short cid = null;
			String category = null;
			AtomicBoolean onlyChoice = new AtomicBoolean();
			Book book = null;
			Purchase purchase = null;

			while (!done) {
				switch (state) {
					case CUSTOMER:
						// Find a customer and then update the information if requested.
						System.out.println();
						if ((cid = this.find_customer()) != null) {
							state = RunState.CATEGORY;
						} else {
							System.out.print("Do you want to try again? (y/n) ");
							if (!this.parseAnswer()) {
								done = true; // exit while loop
							}
						}
						break;

					case CATEGORY:
						// Find all book categories and then prompt user for selection.
						category = this.fetch_categories();
						state = RunState.BOOK;
						break;

					case BOOK:
						// Find all books of the selected category and prompt user for selection.
						if ((book = this.find_books(category, onlyChoice)) != null) {
							state = RunState.PURCHASE;
						} else if (onlyChoice.get()) { // Only one book to choose from.
							System.out.print("Do you want to choose a different category? (y/n) ");
							if (this.parseAnswer()) {
								state = RunState.CATEGORY;
							} else {
								System.out.println("\nYou have chosen to not accept the only available book choice, "
										+ "but also not to change the book category which together are undoable!\n");
								done = true; // exit while loop
							}
							onlyChoice.set(false);
						} else {
							System.out.print("Do you want to try again? (y/n) ");
							if (this.parseAnswer()) {
								state = RunState.CATEGORY;
							} else {
								done = true; // exit while loop
							}
						}
						break;

					case PURCHASE:
						// Purchase the chosen quantity number of the selected book.
						System.out.println();
						if ((purchase = this.min_price(cid, book)) != null) {
							String next = null;
							Short qnty = null;

							boolean confirmed = false;
							while (!confirmed) {
								System.out.println("Please enter the purchase quantity.");

								qnty = null; // reset qnty
								while ((qnty == null) && this.in.hasNextLine()) {
									try {
										// Attempt to read a quantity.
										qnty = Short.parseShort(next = this.nextLine());

										// Validate read quantity.
										if (qnty > 0) {
											purchase.setQuantity(qnty.shortValue());
										} else {
											System.out.println("\nGiven quantity(" + next + ") is not positive.");
											qnty = null; // reset qnty
										}
									} catch (NumberFormatException ex) {
										System.out.println(
												"\nGiven string(" + next + ") is not a valid positive short integer.");
									}

									if (qnty == null) { // Prompt for another input.
										System.out.println(
												"\nPlease enter a positive short integer representing the purchase quantity.");
									}
								}

								if (qnty == null) {
									// qnty has not been set which means that the above while loop
									// terminated because this.in.hasNextLine() returned false.

									this.EOI(); // Handle End Of Input.
								} else if (!this.confirm) {
									confirmed = true;
								} else {
									System.out.println(
											"\nYou have entered the following purchase quantity: " + qnty.toString());
									System.out.print("Is this correct? (y/n) ");
									if (!(confirmed = this.parseAnswer())) {
										System.out.println();
									}
								}
							}

							state = RunState.FINALIZE;
						} else {
							if (book == null) {
								// The following will never be executed since state will only be
								// set to PURCHASE in state BOOK when book != null

								// The only reason why it is here is due to the fact that IDEs are
								// not smart enough to detect that book cannot possibly be null
								// by this point.
								return;
							}

							System.out.println("The chosen book with title: " + book.title + " of category: " + category
									+ " is not offered at any price for the customer with ID: " + cid);
							state = RunState.BACK_TRACK;
						}
						break;

					case FINALIZE:
						// Finalize the purchase and insert it into the database if requested.
						if (cid == null) {
							// The following will never be executed since:
							// state -> CUSTOMER -> CATEGORY -> BOOK -> PURCHASE -> FINALIZE
							// But in state CUSTOMER, cid == null is already fully handled.

							// The only reason why it is here is due to the fact that IDEs are
							// not smart enough to detect that purchase cannot possibly be null
							// by this point.
							return;
						}
						if (purchase == null) {
							// The following will never be executed since state will only be
							// set to FINALIZE in state PURCHASE when purchase != null

							// The only reason why it is here is due to the fact that IDEs are
							// not smart enough to detect that purchase cannot possibly be null
							// by this point.
							return;
						}

						// Unlike C/C++, Java String.format uses %f for both float and double.
						System.out.println("\nThe following is your order:");
						System.out.printf("%26s = %26s\n%26s = %26s\n%26s = %26s\n%26s = %26s\n%26s = %26s\n\n",
								"Title", purchase.title, "Year", purchase.year, "Book Price",
								String.format("%.2f", purchase.price), "Quantity",
								((Short) purchase.getQuantity()).toString(), "Total Cost",
								String.format("%.2f", (purchase.getQuantity() * purchase.price)));

						System.out.print("Do you want to make this purchase? (y/n) ");
						if (this.parseAnswer()) { // Purchase requested.
							purchase.setWhen(); // Current purchase time.

							boolean insert = this.insert_purchase(purchase);
							while (!insert) { // insertion failed
								if (!this.rollbackChanges()) {
									System.out.println("\nFailed to rollback changes made by insertion attempt.");
									this.exit(false);
								}

								System.out.print("Do you want to try again? (y/n) ");
								if (this.parseAnswer()) {
									insert = this.insert_purchase(purchase);
								} else {
									insert = true; // exit while loop
								}
							}

							System.out.print("\nDo you want to view all purchases made by the chosen customer? (y/n) ");
							if (this.parseAnswer()) {
								TreeMap<Integer, Purchase> purchases = this.find_purchases(cid.shortValue());
								if (!purchases.isEmpty()) {
									System.out.printf("\n%10s\t %16s %26s %11s %11s %18s %11s %30s\n", "Number",
											"Club Name", "Book Title", "Book Year", "Book Price", "Purchase Quantity",
											"Total Cost", "Purchase Time");
									Purchase p = null; // The current Purchase object.
									for (Map.Entry<Integer, Purchase> e : purchases.entrySet()) {
										p = e.getValue();
										System.out.printf("%10s.\t %16s %26s %11s %11s %18s %11s %30s\n",
												e.getKey().toString(), p.club, p.title, ((Short) p.year).toString(),
												String.format("%.2f", p.price), ((Short) p.getQuantity()).toString(),
												String.format("%.2f", (p.getQuantity() * p.price)),
												p.getWhen().toString());
									}
								}
							}

							done = true; // exit while loop
						} else { // Purchase not requested.
							purchase = null; // Release purchase object memory.
							state = RunState.BACK_TRACK;
						}
						break;

					case BACK_TRACK:
						// Ask the user if they want to go back to previous states or exit,
						// since currently there is an error in making the purchase.
						exit = false;
						System.out.print("\nDo you want to exit the application? (y/n) ");
						if (this.parseAnswer()) {
							done = exit = true; // exit while loop
						}

						if (!exit) {
							boolean answer = false;
							System.out.print("Do you want to choose another book? (y/n) ");
							if (this.parseAnswer()) {
								answer = true;
								// Go back to book choosing state.
								state = RunState.BOOK;
							}

							book = null; // Release book object memory.

							if (!answer) {
								// We know answer is false so need for answer = false;
								System.out.print("Do you want to choose another category? (y/n) ");
								if (this.parseAnswer()) {
									answer = true;
									// Go back to category choosing state.
									state = RunState.CATEGORY;
								}

								category = null; // Release category object memory.
								onlyChoice.set(false); // Reset onlyChoice variable.

								if (!answer) {
									// We know answer is false so need for answer = false;
									System.out.print("Do you want to choose another customer? (y/n) ");
									if (this.parseAnswer()) {
										answer = true;
										// Go back to customer choosing state.
										state = RunState.CUSTOMER;

										System.out.println();
									}

									cid = null; // Release cid object memory.

									if (!answer) {
										System.out.println("\nYou have chosen not to complete your purchase, "
												+ "not to exit the application but also not to make any changes which are contradictory!");
										done = true; // exit while loop
									}
								}
							}
						}
						break;

					default:
						break;
				}

				if (done || exit) { // If an exit is about to happen, prompt for commit/rollback.
					System.out.print("\nDo you want to commit any/all of the changes made, to the database? (y/n) ");
					boolean success = true;
					String command = null;
					if (this.parseAnswer()) {
						success = this.commitChanges();
						command = "commit";
					} else {
						success = this.rollbackChanges();
						command = "rollback";
					}
					if (!success) {
						System.out.println("\nFailed to " + command + " any/all of the changes made, to the database.");
						this.exit(false);
					}
				}

				if (done && !exit) { // Prompt for application restart.
					System.out.print("\nDo you want to restart the application? (y/n) ");
					if (this.parseAnswer()) { // Restart requested.
						this.appStartup(false);

						done = false; // do NOT exit while loop

						// Go back to customer choosing(initial) state.
						state = RunState.CUSTOMER;

						// Reset all purchase information.
						cid = null;
						category = null;
						onlyChoice.set(false);
						book = null;
						purchase = null;
					}
				}
			}

			this.in = null;
			in.close(); // Close the Scanner.
		}
	}

	/**
	 * Attempt to read a valid customer ID and print their information.
	 * 
	 * @return The valid customer ID if successful and <code>null</code> otherwise.
	 * 
	 * @see #run()
	 * 
	 * @see YRBAPPUtility#CID_QUERY_TEXT
	 * 
	 * @see #update_customer(short, String, String)
	 * @see #find_min_max_cid(AtomicInteger, AtomicInteger)
	 * @see #find_all_customers()
	 */
	private Short find_customer() {
		Short cid = null, result = null;
		String next = null;

		boolean confirmed = false;
		while (!confirmed) {
			System.out.println("Please enter a customer identification number(ID).");

			cid = null; // reset cid
			while ((cid == null) && this.in.hasNextLine()) {
				try {
					cid = Short.parseShort(next = this.nextLine());
				} catch (NumberFormatException ex) {
					// Prompt for another input.
					System.out.println("\nGiven string(" + next + ") is not a valid short integer.");
					System.out.println(
							"\nPlease enter a valid short integer representing a customer identification number(ID).");
				}
			}

			if (cid == null) {
				confirmed = true; // exit while loop to handle End Of Input.
			} else if (!this.confirm) {
				confirmed = true; // exit while loop since a valid cid has been read.
			} else {
				System.out.println("\nYou have entered the following customer ID: " + cid.shortValue());
				System.out.print("Is this correct? (y/n) ");
				if (!(confirmed = this.parseAnswer())) {
					System.out.println();
				}
			}
		}
		if (cid == null) {
			// cid has not been set which means that the reading while loop(inner)
			// terminated because this.in.hasNextLine() returned false.

			this.EOI(); // Handle End Of Input.
			// The following will never be executed since this.EOI() will exit the JVM.

			// The only reason why it is here is due to the fact that IDEs are not
			// smart enough to detect that cid cannot possibly be null after this
			// if statement.
			return null;
		}

		// Assume validity of cid(existing cid in database).
		result = cid;

		boolean fail = false;

		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.CID_QUERY_TEXT)) {
			try {
				querySt.setShort(1, cid.shortValue()); // Fix the ? in the query text.
			} catch (SQLException ex) {
				YRBAPP.logError("cidQuery: Failed to remove ? from query statement.\n" + ex.toString());
				fail = true;
			}

			try { // Set the query timeout.
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("cidQuery: Failed to set CID_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						if (answers.next()) { // Are there any answers?
							String name = answers.getString(2), city = answers.getString(3);
							System.out.printf("\n%21s = %21s\n%21s = %21s\n%21s = %21s\n", "CID", cid.toString(),
									"Name", name, "City", city);

							// Update the customer information if requested by the user.
							boolean update = this.update_customer(cid.shortValue(), name, city);
							while (!update) {// updating failed
								if (!this.rollbackChanges()) {
									System.out.println("\nFailed to rollback changes made by updating attempt.");
									this.exit(false);
								}

								System.out.print("Do you want to try again? (y/n) ");
								if (this.parseAnswer()) {
									update = this.update_customer(cid.shortValue(), name, city);
								} else {
									update = true; // exit while loop
								}
							}
						} else {
							System.out.println(
									"\nGiven customer ID(" + cid.toString() + ") does not exist in the database.");
							result = null; // reset result

							AtomicInteger min = new AtomicInteger(), max = new AtomicInteger();
							if (this.find_min_max_cid(min, max)) {
								System.out.println(
										"\nThe minimum and the maximum customer IDs in the database are respectively: "
												+ min.get() + " and " + max.get()
												+ " .\nThis does not however mean that every number between them is a valid ID.");
							}

							System.out.print("\nDo you want to view all customers? (y/n) ");
							if (this.parseAnswer()) {
								boolean find = this.find_all_customers();
								while (!find) {
									System.out.println(
											"\nYou requested to view all customers but unfortunately there was an error.");

									System.out.print("Do you want to try again? (y/n) ");
									if (this.parseAnswer()) {
										find = this.find_all_customers();
									} else {
										find = true; // exit while loop
									}
								}
								System.out.println();
							}
						}
					} catch (SQLException ex) {
						YRBAPP.logError("cidQuery: Failed in answers cursor.\n" + ex.toString());
						fail = true;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("cidQuery: Failed to close answers cursor.\n" + ex.toString());
						fail = true;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("cidQuery: Failed to execute query.\n" + ex.toString());
					fail = true;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("cidQuery: Failed to close query handle.\n" + ex.toString());
				fail = true;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("cidQuery: Failed to prepare query handle.\n" + ex.toString());
			fail = true;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to find the customer with the given ID("
							+ cid.toString() + ").");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		return result;
	}

	/**
	 * Find the minimum and maximum customer ID in the database and save them in <code>min</code> and
	 * <code>max</code> respectively.
	 * 
	 * @param min
	 *            the minimum cid
	 * 
	 * @param max
	 *            the maximum cid
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>min == null</code> <br>
	 *             If <code>max == null</code>
	 * 
	 * @see #find_customer()
	 * 
	 * @see YRBAPPUtility#MIN_MAX_CID_QUERY_TEXT
	 */
	private boolean find_min_max_cid(AtomicInteger min, AtomicInteger max) throws NullPointerException {
		boolean result = true, fail = false;

		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.MIN_MAX_CID_QUERY_TEXT)) {
			try { // Set the query timeout.
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("rangeCidQuery: Failed to set MIN_MAX_CID_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						if (answers.next()) { // Are there any answers?
							min.set(answers.getShort(1)); // minimum cid
							max.set(answers.getShort(2)); // maximum cid
						} else {
							System.out.println(
									"\nThere are no customers in the database since no minimum or maximum customer ID could be found.");
							fail = true;
						}
					} catch (SQLException ex) {
						YRBAPP.logError("rangeCidQuery: Failed in answers cursor.\n" + ex.toString());
						result = false;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("rangeCidQuery: Failed to close answers cursor.\n" + ex.toString());
						result = false;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("rangeCidQuery: Failed to execute query.\n" + ex.toString());
					result = false;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("rangeCidQuery: Failed to close query handle.\n" + ex.toString());
				result = false;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("rangeCidQuery: Failed to prepare query handle.\n" + ex.toString());
			result = false;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to find the minimum and maximum customer IDs.");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		return result;
	}

	/**
	 * Print all customers and their detailed information to the standard output
	 * stream(<code>System.out</code>).
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @see #find_customer()
	 * 
	 * @see YRBAPPUtility#CUSTOMER_QUERY_TEXT
	 */
	private boolean find_all_customers() {
		int i = 0;
		TreeMap<Integer, String> customers = new TreeMap<Integer, String>();

		boolean result = true, fail = false;
		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.CUSTOMER_QUERY_TEXT)) {
			try {
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT); // Set the query timeout.
			} catch (SQLException ex) {
				YRBAPP.logError("customerQuery: Failed to set CUSTOMER_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						while (answers.next()) { // Map i to i^th customer.
							customers.put(++i, String.format("%12s %21s %16s", answers.getString(1),
									answers.getString(2), answers.getString(3)));
						}
					} catch (SQLException ex) {
						YRBAPP.logError("customerQuery: Failed in answers cursor.\n" + ex.toString());
						result = false;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("customerQuery: Failed to close answers cursor.\n" + ex.toString());
						result = false;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("customerQuery: Failed to execute query.\n" + ex.toString());
					result = false;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("customerQuery: Failed to close query handle.\n" + ex.toString());
				result = false;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("customerQuery: Failed to prepare query handle.\n" + ex.toString());
			result = false;
		}

		if (fail) { // Handle possible failures.
			System.out.println("\nUnfortunately a fatal error has occurred when attempting to find all customers.");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		if (!result) { // Handle possible failures(non-fatal).
			return false;
		}

		if (customers.isEmpty()) { // Check if there is at least one customer.
			System.out.println("\nThere are no customers in the database(i.e. yrb_customer is empty).");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		System.out.println("\nThe database contains the following customers:");
		System.out.printf("\n%10s\t %12s %21s %16s\n", "Number", "Customer ID", "Name", "City");
		for (Map.Entry<Integer, String> e : customers.entrySet()) {
			System.out.printf("%10s.\t %s\n", e.getKey().toString(), e.getValue());
		}
		// Failing is only possible if an SQLException is caught.
		return true;
	}

	/**
	 * Update a customer with ID <code>cid</code> if requested by the user.
	 * 
	 * @param cid
	 *            the customer's ID number
	 * 
	 * @param name
	 *            the customer's name
	 * 
	 * @param city
	 *            the customer's city
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>name == null</code> <br>
	 *             If <code>city == null</code>
	 * 
	 * @see #find_customer()
	 * 
	 * @see YRBAPPUtility#MAX_CUSTOMER_NAME_LENGTH
	 * @see YRBAPPUtility#MAX_CUSTOMER_CITY_LENGTH
	 * 
	 * @see #STOP
	 * 
	 * @see #perform_update(short, String, String)
	 */
	private boolean update_customer(short cid, String name, String city) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("Given customer name is null.");
		} else if (city == null) {
			throw new NullPointerException("Given customer city is null.");
		}

		// Suggest all possible ways of updating customer information.
		System.out.println("\nYou can update the name or the city of the current customer(or both).");

		System.out.print("Do you want to update the customer's information? (y/n) ");
		if (this.parseAnswer()) {
			// Updating the customer's name:
			String newName = name;
			System.out.print("\nDo you want to update the customer's name? (y/n) ");
			if (this.parseAnswer()) {
				boolean confirmed = false, stop = false;
				while (!confirmed) {
					System.out.println("Please enter the customer's new name.");

					stop = false; // reset stop
					while (!stop && this.in.hasNextLine()) {
						stop = true;
						if (YRBAPP.STOP.equals(newName = this.nextLine())) {
							// If entered string is equal to YRBAPP.STOP, then restore
							// the original name and stop prompting for a new name.
							newName = name;
						} else if (newName.length() > YRBAPPUtility.MAX_CUSTOMER_NAME_LENGTH || newName.isEmpty()) {
							System.out.println("\nGiven new customer's name(" + newName + ") has length "
									+ newName.length() + " which is not in the following range: [1, "
									+ YRBAPPUtility.MAX_CUSTOMER_NAME_LENGTH + "]");
							System.out.println("\nPlease enter another name or \"" + YRBAPP.STOP
									+ "\" to skip updating the customer's name.");

							newName = name;
							stop = false;
						}
					}

					if (!stop) {
						// stop has not been set to true which means that the above
						// while loop terminated because this.in.hasNextLine() returned false.

						this.EOI(); // Handle End Of Input.
					} else if (!this.confirm) {
						confirmed = true;
					} else {
						System.out.println("\nYou have entered the following name: " + newName);
						System.out.print("Is this correct? (y/n) ");
						if (!(confirmed = this.parseAnswer())) {
							System.out.println();
						}
					}
				}
			}

			// Updating the customer's city:
			String newCity = city;
			boolean updateCity = false;
			System.out.print("\nDo you want to update the customer's city? (y/n) ");
			// Cannot replace this block of code by a simple call to this.nextLine() since a
			// special action(perform_update) is performed when YRBAPP.EXIT has been entered.
			if (this.in.hasNextLine()) {
				String next = null;
				updateCity = YRBAPP.parseAnswer(next = this.in.nextLine());

				if (next.equals(YRBAPP.EXIT)) {
					// YRBAPP.EXIT has been entered but this means that any/all of the changes made,
					// to the database should be committed which includes the name updating.
					this.perform_update(cid, newName, city);
				}

				// Handle the two special reserved exit keywords.
				this.handleTermination(next);
			} else {
				this.EOI(); // Handle End Of Input.
			}
			if (updateCity) {
				boolean confirmed = false, stop = false;
				while (!confirmed) {
					System.out.println("Please enter the customer's new city.");

					stop = false; // reset stop
					while (!stop && this.in.hasNextLine()) {
						stop = true;
						if (YRBAPP.STOP.equals(newCity = this.nextLine())) {
							// If entered string is equal to YRBAPP.STOP, then restore
							// the original city and stop prompting for a new city.
							newCity = city;
						} else if (newCity.length() > YRBAPPUtility.MAX_CUSTOMER_CITY_LENGTH || newCity.isEmpty()) {
							System.out.println("\nGiven new customer's city(" + newCity + ") has length "
									+ newCity.length() + " which is not in the following range: [1, "
									+ YRBAPPUtility.MAX_CUSTOMER_CITY_LENGTH + "]");
							System.out.println("\nPlease enter another city or \"" + YRBAPP.STOP
									+ "\" to skip updating the customer's city.");

							newCity = city;
							stop = false;
						}
					}

					if (!stop) {
						// stop has not been set to true which means that the above
						// while loop terminated because this.in.hasNextLine() returned false.

						this.EOI(); // Handle End Of Input.
					} else if (!this.confirm) {
						confirmed = true;
					} else {
						System.out.println("\nYou have entered the following city: " + newCity);
						System.out.print("Is this correct? (y/n) ");
						if (!(confirmed = this.parseAnswer())) {
							System.out.println();
						}
					}
				}
			}

			// Perform update on database only if new data has been entered.
			if (!newName.equals(name) || !newCity.equals(city)) {
				return this.perform_update(cid, newName, newCity);
			}
		}

		// Update not requested implies success.
		return true;
	}

	/**
	 * Update a customer with ID <code>cid</code>, to have name <code>newName</code>, and city
	 * <code>newCity</code>.
	 * 
	 * @param cid
	 *            the customer ID number
	 * 
	 * @param newName
	 *            the customer's new name
	 * 
	 * @param newCity
	 *            the customer's new city
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>newName == null</code> <br>
	 *             If <code>newCity == null</code>
	 * 
	 * @see #update_customer(short, String, String)
	 * 
	 * @see YRBAPPUtility#CID_UPDATE_TEXT
	 */
	private boolean perform_update(short cid, String newName, String newCity) throws NullPointerException {
		if (newName == null) {
			throw new NullPointerException("Given customer's new name is null.");
		} else if (newCity == null) {
			throw new NullPointerException("Given customer's new city is null.");
		}

		boolean result = true, fail = false;

		// Prepare the update handle.
		try (PreparedStatement updateSt = this.db_connect.prepareStatement(YRBAPPUtility.CID_UPDATE_TEXT)) {
			try {
				updateSt.setString(1, newName); // Fix the first ? in the update text.
				updateSt.setString(2, newCity); // Fix the second ? in the update text.
				updateSt.setShort(3, cid); // Fix the third ? in the update text.
			} catch (SQLException ex) {
				YRBAPP.logError("cidUpdate: Failed to remove ? from update statement.\n" + ex.toString());
				result = false;
			}

			try { // Set the update timeout.
				updateSt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("cidUpdate: Failed to set CID_UPDATE_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (result && !fail) {
				try {
					updateSt.executeUpdate();
				} catch (SQLException ex) {
					YRBAPP.logError("cidUpdate: Failed to execute update.\n" + ex.toString());
					result = false;
				}
			}

			try { // Close the update handle.
				updateSt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("cidUpdate: Failed to close update handle.\n" + ex.toString());
				result = false;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("cidUpdate: Failed to prepare update handle.\n" + ex.toString());
			result = false;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to update the customer's information with the given ID("
							+ cid + ").");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		if (!result) {
			System.out.println("\nAn update was requested but unfortunately it could not be completed.");
		}

		return result;
	}

	/**
	 * Returns a valid book category selected by the user from the <code>yrb_book</code> table in the
	 * <b><i>York River Bookseller's Database</i></b>.
	 * 
	 * @return A <code>category</code> chosen by the user from all possible categories.
	 * 
	 * @see #run()
	 * 
	 * @see YRBAPPUtility#CATEGORY_QUERY_TEXT
	 */
	private String fetch_categories() {
		int i = 0;
		TreeMap<Integer, String> categories = new TreeMap<Integer, String>();

		boolean fail = false;

		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.CATEGORY_QUERY_TEXT)) {
			try { // Set the query timeout.
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("categoryQuery: Failed to set CATEGORY_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						while (answers.next()) { // Map i to i^th category.
							categories.put(++i, answers.getString(1));
						}
					} catch (SQLException ex) {
						YRBAPP.logError("categoryQuery: Failed in answers cursor.\n" + ex.toString());
						fail = true;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("categoryQuery: Failed to close answers cursor.\n" + ex.toString());
						fail = true;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("categoryQuery: Failed to execute query.\n" + ex.toString());
					fail = true;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("categoryQuery: Failed to close query handle.\n" + ex.toString());
				fail = true;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("categoryQuery: Failed to prepare query handle.\n" + ex.toString());
			fail = true;
		}

		if (fail) { // Handle possible failures.
			System.out
					.println("\nUnfortunately a fatal error has occurred when attempting to find all book categories.");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		if (categories.isEmpty()) { // Check if there is at least one category.
			System.out.println("There are no categories in the database(i.e. yrb_category is empty).");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		// Display all categories, so the user can choose one from the list.
		System.out.println("\nThe database contains the following book categories:");
		System.out.printf("\n%10s\t %14s\n", "Number", "Category Name");
		for (Map.Entry<Integer, String> e : categories.entrySet()) {
			System.out.printf("%10s.\t %14s\n", e.getKey().toString(), e.getValue());
		}
		System.out.println();

		String result = null; // The selected category.

		if (categories.size() == 1) { // If there is only one category then just select it.
			System.out.println("There is only one category(" + (result = categories.firstEntry().getValue())
					+ ") to choose and as such it has been automatically selected.");
			return result;
		}

		String next = null, range = "[1, " + i + "]";
		Integer catNum = null;

		boolean confirmed = false;
		while (!confirmed) {
			System.out.println("Please enter a category number or the exact category name itself.");

			result = null; // reset result
			while ((result == null) && this.in.hasNextLine()) {
				try {
					// Attempt to read a category number.
					catNum = Integer.parseInt(next = this.nextLine());

					if (1 <= catNum && catNum <= i) {
						result = categories.get(catNum);

						// Handle the case where a category name is a number.
						if (categories.containsValue(next)) {
							// There is only a conflict if the chosen category
							// is not the same as what was entered.
							if (!result.equals(next)) {
								System.out.println("\nSince " + next
										+ " is both a category name and a category number, your choice is ambiguous.");

								System.out.println("Do you want\n1. " + result + "\nor\n2. " + next
										+ "\n? Please enter 1 or 2.\nNote that anything except 1 and 2 will be assumed to be 1.");

								int num = 1; // Invalid inputs are treated as 1.
								try {
									// Read and parse the inputed string as an integer.
									num = Integer.parseInt(this.nextLine());
								} catch (NumberFormatException ex) {
									// Do nothing since num has been initialized to 1.
								}
								if (num == 2) {
									result = next;
								}
							}
						}
					} else {
						System.out.println("\nGiven category number(" + next
								+ ") is not a valid integer in the following range: " + range);
					}
				} catch (NumberFormatException ex) {
					if (categories.containsValue(next)) {
						result = next;
					} else {
						System.out.println("\nGiven string(" + next + ") is not a valid category name.");
					}
				}

				if (result == null) { // Prompt for another input.
					System.out.println("\nPlease enter a valid integer in the following range: " + range
							+ " or the exact category name itself.");
				}
			}

			if (result == null) {
				// result has not been set which means that the above while loop
				// terminated because this.in.hasNextLine() returned false.

				this.EOI(); // Handle End Of Input.
			} else if (!this.confirm) {
				confirmed = true;
			} else {
				System.out.println("\nYou have entered the following category: " + result);
				System.out.print("Is this correct? (y/n) ");
				if (!(confirmed = this.parseAnswer())) {
					System.out.println();
				}
			}
		}

		return result;
	}

	/**
	 * Returns a <code>Book</code> object encapsulating a book chosen by the user through selecting the
	 * category and the title.
	 * 
	 * @param category
	 *            the book category
	 * 
	 * @param onlyChoice
	 *            the indicator representing whether there was only one book choice
	 * 
	 * @return The <code>Book</code> chosen by the user if successful and <code>null</code> otherwise.
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>category == null</code> <br>
	 *             If <code>onlyChoice == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>onlyChoice.get()</code>
	 * 
	 * @see #run()
	 * 
	 * @see YRBAPPUtility#BOOK_QUERY_TEXT
	 */
	private Book find_books(String category, AtomicBoolean onlyChoice)
			throws NullPointerException, IllegalArgumentException {
		if (category == null) {
			throw new NullPointerException("Given category is null.");
		} else if (onlyChoice == null) {
			throw new NullPointerException("Given atomic boolean is null.");
		} else if (onlyChoice.get()) {
			throw new IllegalArgumentException("Given atomic boolean is invalid since it is already equal to true.");
		}

		int i = 0;
		TreeMap<Integer, Book> books = new TreeMap<Integer, Book>();

		boolean fail = false;

		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.BOOK_QUERY_TEXT)) {
			try {
				querySt.setString(1, category); // Fix the ? in the query text.
			} catch (SQLException ex) {
				YRBAPP.logError("bookQuery: Failed to remove ? from query statement.\n" + ex.toString());
				fail = true;
			}

			try { // Set the query timeout.
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("bookQuery: Failed to set BOOK_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						while (answers.next()) { // Map i to i^th book.
							books.put(++i, new Book(answers.getString(1), answers.getShort(2), answers.getString(3),
									category, answers.getShort(4)));
						}
					} catch (SQLException ex) {
						YRBAPP.logError("bookQuery: Failed in answers cursor.\n" + ex.toString());
						fail = true;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("bookQuery: Failed to close answers cursor.\n" + ex.toString());
						fail = true;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("bookQuery: Failed to execute query.\n" + ex.toString());
					fail = true;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("bookQuery: Failed to close query handle.\n" + ex.toString());
				fail = true;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("bookQuery: Failed to prepare query handle.\n" + ex.toString());
			fail = true;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to find all books of the given category("
							+ category + ").");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		if (books.isEmpty()) { // Check if there is at least one book.
			System.out.println("\nThere are no books in the given category(" + category + ").");
			return null;
		}

		// Display all books, so the user can choose one from the list.
		System.out.println("\nThe database contains the following books with the given category(" + category + "):");
		System.out.printf("\n%10s\t %26s %11s %11s %11s\n", "Number", "Title", "Year", "Language", "Weight");
		Book b = null; // The current Book object.
		for (Map.Entry<Integer, Book> e : books.entrySet()) {
			b = e.getValue();
			System.out.printf("%10s.\t %26s %11s %11s %11s\n", e.getKey().toString(), b.title,
					((Short) b.year).toString(), b.getLanguage(), ((Short) b.weight).toString());
		}
		System.out.println();

		Book result = null; // The selected book.

		if (books.size() == 1) { // If there is only one book then just select it.
			result = books.firstEntry().getValue();
			System.out.println("There is only one book to choose and as such it has been automatically selected.");

			System.out.print("\nDo you want to change the book choice? (y/n) ");
			if (this.parseAnswer()) {
				result = null;
				// Set the boolean indicator representing the fact that there was only one book choice.
				onlyChoice.set(true);
			}

			return result;
		}

		String next = null, range = "[1, " + i + "]";
		Integer bookNum = null;
		boolean isBookNum = false;

		int j = 0;
		TreeMap<Integer, Integer> matches = null;

		boolean confirmed = false;
		while (!confirmed) {
			System.out.println("Please enter a book number or the exact book title itself.");

			result = null; // reset result
			while ((result == null) && this.in.hasNextLine()) {
				// Read the next line of input and handle the special termination case(s).
				next = this.nextLine();

				// reset isBookNum
				isBookNum = false;

				// Construct a map of all books with title equal to next.
				// Mapping the j^th match to its key in books.
				j = 0;
				matches = new TreeMap<Integer, Integer>();
				for (Integer key : books.keySet()) {
					if (books.get(key).title.equals(next)) {
						matches.put(++j, key);
					}
				}

				try {
					// Attempt to parse what was read as a book number.
					bookNum = Integer.parseInt(next);

					// Keep track of the fact that the input was indeed a short integer.
					isBookNum = true;

					if (1 <= bookNum && bookNum <= i) {
						matches.put(0, bookNum);

						if (matches.size() == 1) {
							// Handle the case where there are no books with title bookNum.
							result = books.get(bookNum);
						} else if (matches.size() == 2 && matches.get(1) == bookNum) {
							// Handle the case where there is exactly one book with title bookNum
							// but it's book number is also bookNum and so there is no conflict.
							result = books.get(bookNum);
						} else {
							// There is only a conflict if there are multiple books with title bookNum.
							System.out.println("\nSince " + next
									+ " is both a book name and a book number, your choice is ambiguous.");

							System.out.printf("\n%10s\t %26s %11s %11s %11s\n", "Number", "Title", "Year", "Language",
									"Weight");
							b = null; // The current Book object.
							for (Map.Entry<Integer, Integer> e : matches.entrySet()) {
								b = books.get(e.getValue());
								System.out.printf("%10s.\t %26s %11s %11s %11s\n",
										((Integer) (e.getKey() + 1)).toString(), b.title, ((Short) b.year).toString(),
										b.getLanguage(), ((Short) b.weight).toString());
							}

							System.out.println(
									"\nPlease use one of the book numbers from the table above to choose the desired book.");
							try {
								bookNum = Integer.parseInt(next = this.nextLine());

								if (1 <= bookNum && bookNum <= (j + 1)) {
									result = books.get(matches.get(bookNum - 1));
								} else {
									System.out.println("\nGiven book number(" + next
											+ ") is not a valid integer in the following range: [1, " + (j + 1) + "]");
								}
							} catch (NumberFormatException ex1) {
								System.out.println("\nGiven string(" + next
										+ ") is not valid integer in the following range: [1, " + (j + 1) + "]");
							}
						}
					}
				} catch (NumberFormatException ex) {
					// Book titles are handled in the next block of code.
				}

				if (result == null) {
					// Check if given string is a book title if result is still null.
					if (matches.isEmpty()) {
						if (isBookNum) {
							System.out.println("\nGiven book number(" + next
									+ ") is not a valid integer in the following range: " + range);
						} else {
							System.out.println("\nGiven string(" + next + ") is not a valid book title.");
						}
					} else if (matches.size() == 1) {
						result = books.get(matches.firstEntry().getValue());
					} else {
						System.out.println("\nSince there are multiple books with the given title(" + next
								+ "), your choice is ambiguous.");

						System.out.printf("\n%10s\t %26s %11s %11s %11s\n", "Number", "Title", "Year", "Language",
								"Weight");
						b = null; // The current Book object.
						for (Map.Entry<Integer, Integer> e : matches.entrySet()) {
							b = books.get(e.getValue());
							System.out.printf("%10s.\t %26s %11s %11s %11s\n", e.getKey().toString(), b.title,
									((Short) b.year).toString(), b.getLanguage(), ((Short) b.weight).toString());
						}

						System.out.println(
								"\nPlease use one of the book numbers from the table above to choose the desired book.");
						try {
							bookNum = Integer.parseInt(next = this.nextLine());

							if (1 <= bookNum && bookNum <= j) {
								result = books.get(matches.get(bookNum));
							} else {
								System.out.println("\nGiven book number(" + next
										+ ") is not a valid integer in the following range: [1, " + j + "]");
							}
						} catch (NumberFormatException ex1) {
							System.out.println("\nGiven string(" + next
									+ ") is not valid integer in the following range: [1, " + j + "]");
						}
					}
				}

				if (result == null) { // Prompt for another input.
					System.out.println("\nPlease enter a valid integer in the following range: " + range
							+ " or the exact book title itself.");
				}
			}

			if (result == null) {
				// result has not been set which means that the above while loop
				// terminated because this.in.hasNextLine() returned false.

				this.EOI(); // Handle End Of Input.
			} else if (!this.confirm) {
				confirmed = true;
			} else {
				System.out.println("\nYou have chosen the following book from the given category(" + category + "):");
				System.out.printf("%26s = %26s\n%26s = %26s\n%26s = %26s\n%26s = %26s\n", "Title", result.title, "Year",
						((Short) result.year).toString(), "Language", result.getLanguage(), "Weight",
						((Short) result.weight).toString());
				System.out.print("Is this correct? (y/n) ");
				if (!(confirmed = this.parseAnswer())) {
					System.out.println();
				}
			}
		}

		return result;
	}

	/**
	 * Find the minimum price for the given book across all clubs that offer it. This task is
	 * accomplished by checking all such clubs that the customer is a member of.
	 * 
	 * @param cid
	 *            the customer ID number
	 * 
	 * @param book
	 *            the book chosen for purchase
	 * 
	 * @return The <code>Purchase</code> object representing the current book purchase or
	 *         <code>null</code> if no valid purchase quantity is read.
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>cid == null</code> <br>
	 *             If <code>book == null</code>
	 * 
	 * @see #run()
	 * 
	 * @see #find_purchases(short)
	 * 
	 * @see YRBAPPUtility#OFFER_QUERY_TEXT
	 */
	private Purchase min_price(Short cid, Book book) throws NullPointerException {
		if (cid == null) {
			throw new NullPointerException("Given customer ID is null.");
		} else if (book == null) {
			throw new NullPointerException("Given book is null.");
		}

		// The current purchase.
		Purchase result = null;

		boolean fail = false;

		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.OFFER_QUERY_TEXT)) {
			try {
				querySt.setShort(1, cid.shortValue()); // Fix the first ? in the update text.
				querySt.setString(2, book.title); // Fix the second ? in the update text.
				querySt.setShort(3, book.year); // Fix the third ? in the update text.

				querySt.setShort(4, cid.shortValue()); // Fix the fourth ? in the update text.
				querySt.setString(5, book.title); // Fix the fifth ? in the update text.
				querySt.setShort(6, book.year); // Fix the sixth ? in the update text.
			} catch (SQLException ex) {
				YRBAPP.logError("offerQuery: Failed to remove ? from query statement.\n" + ex.toString());
				fail = true;
			}

			try { // Set the query timeout.
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("offerQuery: Failed to set OFFER_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						if (answers.next()) { // Are there any answers?
							result = new Purchase(cid.shortValue(), answers.getString(1), book.title, book.year,
									answers.getFloat(2));
						}
						// result will remain null if answers is empty.
					} catch (SQLException ex) {
						YRBAPP.logError("offerQuery: Failed in answers cursor.\n" + ex.toString());
						fail = true;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("offerQuery: Failed to close answers cursor.\n" + ex.toString());
						fail = true;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("offerQuery: Failed to execute query.\n" + ex.toString());
					fail = true;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("offerQuery: Failed to close query handle.\n" + ex.toString());
				fail = false;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("offerQuery: Failed to prepare query handle.\n" + ex.toString());
			fail = false;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to find the minimum price for the chosen book with title: "
							+ book.title + " and category: " + book.category + " for the customer with the given ID("
							+ cid.shortValue() + ").");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		return result;
	}

	/**
	 * Find and return all purchases made by the customer with ID <code>cid</code>.
	 * 
	 * @param cid
	 *            the customer's ID
	 * 
	 * @return A <code>TreeMap&lt;Integer, Purchase&gt;</code> mapping
	 *         <code>1 to NUMBER_OF_PURCHASES</code> to <code>Purchase</code> objects representing the
	 *         purchases made by the customer.
	 * 
	 * @see #run()
	 * 
	 * @see #insert_purchase(Purchase)
	 * 
	 * @see YRBAPPUtility#PURCHASE_QUERY_TEXT
	 * 
	 * @see #min_price(Short, Book)
	 */
	private TreeMap<Integer, Purchase> find_purchases(short cid) {
		int i = 0;
		TreeMap<Purchase, Integer> purchases = new TreeMap<Purchase, Integer>();
		Purchase p = null;

		boolean fail = false;

		// Prepare the query handle.
		try (PreparedStatement querySt = this.db_connect.prepareStatement(YRBAPPUtility.PURCHASE_QUERY_TEXT)) {
			try {
				querySt.setShort(1, cid); // Fix the ? in the query text.
			} catch (SQLException ex) {
				YRBAPP.logError("purchaseQuery: Failed to remove ? from query statement.\n" + ex.toString());
				fail = true;
			}

			try { // Set the query timeout.
				querySt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("purchaseQuery: Failed to set PURCHASE_QUERY_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (!fail) {
				// Execute the query and save the answers cursor.
				try (ResultSet answers = querySt.executeQuery()) {
					try { // Process query results.
						while (answers.next()) {
							// Find the minimum price of the given book. Pass dummy values for
							// category
							// and weight since they are not part of the primary key and as such,
							// will not have any effects on the purchase.
							p = this.min_price(cid, new Book(answers.getString(2), answers.getShort(3), "", (short) 0));
							if (p != null) {
								p.setQuantity(answers.getShort(5));
								p.setWhen(answers.getTimestamp(4).getTime());
								// Map i -> purchase so that the map is sorted using
								// the Purchase.compareTo method.
								purchases.put(p, ++i);
							}
						}
					} catch (SQLException ex) {
						YRBAPP.logError("purchaseQuery: Failed in answers cursor.\n" + ex.toString());
						fail = true;
					}

					try { // Close the answers cursor.
						answers.close();
					} catch (SQLException ex) {
						YRBAPP.logError("purchaseQuery: Failed to close answers cursor.\n" + ex.toString());
						fail = true;
					}
				} catch (SQLException ex) {
					YRBAPP.logError("purchaseQuery: Failed to execute query.\n" + ex.toString());
					fail = true;
				}
			}

			try { // Close the query handle.
				querySt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("purchaseQuery: Failed to close query handle.\n" + ex.toString());
				fail = true;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("purchaseQuery: Failed to prepare query handle.\n" + ex.toString());
			fail = true;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to find all purchases made by the customer with the given ID("
							+ cid + ").");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		i = 0;
		TreeMap<Integer, Purchase> result = new TreeMap<Integer, Purchase>();
		for (Purchase key : purchases.keySet()) {
			result.put(++i, key);
		}
		return result;
	}

	/**
	 * Finalize the book purchase represented by the given <code>Purchase</code> object if requested by
	 * the user by inserting it into the <code>yrb_purchase</code> table.
	 * 
	 * @param purchase
	 *            the given <code>Purchase</code> object
	 * 
	 * @return <tt>true</tt> if successful and <tt>false</tt> otherwise.
	 * 
	 * @throws NullPointerException
	 *             If <code>purchase == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>purchase.getQuantity() == 0</code>
	 * 
	 * @see #run()
	 * 
	 * @see YRBAPPUtility#PURCHASE_INSERT_TEXT
	 * 
	 * @see #find_purchases(short)
	 */
	private boolean insert_purchase(Purchase purchase) throws NullPointerException, IllegalArgumentException {
		if (purchase == null) {
			throw new NullPointerException("Given purchase is null.");
		} else if (purchase.getQuantity() == 0) {
			// No need to check for negative since that is already enforced by Purchase.
			throw new IllegalArgumentException("Given purchase quantity(0) is not positive.");
		}

		// Check to make sure the given purchase does not exist the database,
		// and then attempt to insert it.
		TreeMap<Integer, Purchase> purchases = this.find_purchases(purchase.cid);
		for (Map.Entry<Integer, Purchase> e : purchases.entrySet()) {
			if (purchase.isEqual(e.getValue())) {
				// Purchase already exists in the database implies insertion success.
				return true;
			}
		}

		boolean result = true, fail = false;

		// Prepare the insertion handle.
		try (PreparedStatement insertSt = this.db_connect.prepareStatement(YRBAPPUtility.PURCHASE_INSERT_TEXT)) {
			try {
				insertSt.setShort(1, purchase.cid); // Fix the first ? in the insert text.
				insertSt.setString(2, purchase.club); // Fix the second ? in the insert text.
				insertSt.setString(3, purchase.title); // Fix the third ? in the insert text.
				insertSt.setShort(4, purchase.year); // Fix the fourth ? in the insert text.
				// Fix the fifth ? in the insert text.
				insertSt.setTimestamp(5, purchase.getWhen());
				insertSt.setShort(6, purchase.getQuantity()); // Fix the sixth ? in the insert text.
			} catch (SQLException ex) {
				YRBAPP.logError("purchaseInsert: Failed to remove ? from insert statement.\n" + ex.toString());
				result = false;
			}

			try { // Set the query timeout.
				insertSt.setQueryTimeout(YRBAPPUtility.QUERY_TIMEOUT);
			} catch (SQLException ex) {
				YRBAPP.logError("purchaseInsert: Failed to set PURCHASE_INSERT_TEXT timeout.\n" + ex.toString());
				fail = true;
			}

			if (result && !fail) {
				try {
					insertSt.execute();
				} catch (SQLException ex) {
					YRBAPP.logError("purchaseInsert: Failed in answers cursor.\n" + ex.toString());
					result = false;
				}
			}

			try { // Close the insertion handle.
				insertSt.close();
			} catch (SQLException ex) {
				YRBAPP.logError("purchaseInsert: Failed to close insertion handle.\n" + ex.toString());
				result = false;
			}
		} catch (SQLException ex) {
			YRBAPP.logError("purchaseInsert: Failed to prepare insertion handle.\n" + ex.toString());
			result = false;
		}

		if (fail) { // Handle possible failures.
			System.out.println(
					"\nUnfortunately a fatal error has occurred when attempting to insert the customer's most recent purchase.");
			System.out.print("Do you want to commit any/all of the changes made, to the database? (y/n) ");
			this.exit(this.parseAnswer());
		}

		if (!result) {
			System.out.println("\nA purchase was requested but unfortunately it could not be completed.");
		}

		return result;
	}

	/**
	 * The main method for <code>YRBAPP</code>.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		YRBAPP app = YRBAPP.getInstance();
		app.run(); // Run the app.
	}

	/**
	 * <code>If commit then</code> commit any/all of the changes made <code>else</code> rollback any/all
	 * of the changes made, to the database and then close the database connection. <br>
	 * <br>
	 * 
	 * At this point output all of the errors thus far to "stderr.txt" if possible. If the outputting is
	 * not possible then simply output to standard error stream(<code>System.err</code>).
	 * 
	 * @param commit
	 *            <code>commit ? this.commitChanges() : this.rollbackChanges()</code>
	 * 
	 * @param status
	 *            the exit status
	 * 
	 * @see #commitChanges()
	 * @see #rollbackChanges()
	 * @see #closeConnection()
	 * 
	 * @see #handleTermination(String)
	 * 
	 * @see #exit(boolean)
	 */
	private void exit(boolean commit, int status) {
		if (commit) {
			// Commit any/all of the changes made, to the current database.
			this.commitChanges();
		} else {
			// Rollback any/all of the changes made, to the current database.
			this.rollbackChanges();
		}

		// Close the database connection.
		this.closeConnection();

		// Close the global Scanner.
		if (this.in != null) {
			this.in.close();
		}

		// Get all errors.
		String errors = YRBAPP.clearErrors();
		if (!errors.isEmpty()) {
			// Print all errors.
			try (PrintWriter w = new PrintWriter("stderr.txt", "UTF-8")) {
				w.print(errors);
				w.close();

				System.out.println("\n\nThe error log has been written to stderr.txt saved in the same directory.");
			} catch (FileNotFoundException | SecurityException ex) {
				// Print the errors to stderr since there was an error writing to the file.

				// Print two extra lines to separate previous output from the errors
				System.err.println("\nThe error log is as follows:");

				// No need to use println since there is a newline character at the end of errors.
				System.err.print(errors);
				YRBAPP.printStackTrace(ex);
			} catch (UnsupportedEncodingException ex) { // This will never happen.
				System.err.println("\nThe error log is as follows:");
				System.err.print(errors);
				YRBAPP.printStackTrace(ex);
			}
		}

		System.exit(status);
	}

	/**
	 * <code>If commit then</code> commit any/all of the changes made <code>else</code> rollback any/all
	 * of the changes made, to the database and then close the database connection. <br>
	 * <br>
	 * 
	 * At this point output all of the errors thus far to "stderr.txt" if possible. If the outputting is
	 * not possible then simply output to standard error stream(<code>System.err</code>). <br>
	 * <br>
	 * 
	 * Same as calling <code>exit(boolean commit, int status)</code> with arguments
	 * <code>(commit, 0)</code>.
	 * 
	 * @param commit
	 *            <code>commit ? this.commitChanges() : this.rollbackChanges()</code>
	 *
	 * @see #exit(boolean, int)
	 */
	private void exit(boolean commit) {
		this.exit(commit, 0);
	}

	/**
	 * Commit any/all of the changes made, to the database and then close the database connection. <br>
	 * <br>
	 * 
	 * At this point output all of the errors thus far to "stderr.txt" if possible. If the outputting is
	 * not possible then simply output to standard error stream(<code>System.err</code>). <br>
	 * <br>
	 * 
	 * Same as calling <code>exit(boolean commit)</code> with argument <code>(true)</code>.
	 * 
	 * @see #exit(boolean)
	 */
	private void exit() {
		this.exit(true);
	}

	/**
	 * End Of Input has been reached which means that there is nothing else that can be done. Therefore
	 * commit any/all of the changes made, to the database and then close the database connection.
	 * 
	 * @see #EOICommit
	 * 
	 * @see #nextLine()
	 */
	private void EOI() {
		System.out.println("\n\nEnd Of Input has been reached so there is nothing else that can done!");
		this.exit(this.EOICommit);
	}

	/**
	 * Handle the two special reserved keywords: <br>
	 * <br>
	 * 
	 * <code>YRBAPP.ABORT</code> & <code>YRBAPP.EXIT</code>
	 * 
	 * @param input
	 *            the user's next input
	 * 
	 * @see #ABORT
	 * @see #EXIT
	 * 
	 * @see #exit(boolean, int)
	 */
	private void handleTermination(String input) {
		if (input != null && !input.isEmpty()) { // Only process if valid input.
			if (input.equals(YRBAPP.ABORT)) {
				this.exit(false, 0);
			} else if (input.equals(YRBAPP.EXIT)) {
				this.exit(true, 0);
			}
		}
	}

	/**
	 * Print the <code>StackTrace</code> of the given <code>Throwable(t)</code> and then add a newline
	 * to <code>System.err(stderr)</code>.
	 * 
	 * @param t
	 *            the given <code>Throwable</code>
	 * 
	 * @return The given <code>Throwable</code>.
	 * 
	 * @see Throwable#printStackTrace()
	 */
	private static Throwable printStackTrace(Throwable t) {
		if (t != null) {
			t.printStackTrace();
			System.err.print("\n");
		}
		return t;
	}
}
