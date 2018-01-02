/**
 * @author Ashkan Moatamed <br>
 *         <br>
 *         The <code>YRBAPPUtility</code> class encapsulates all information required by a
 *         <code>YRBAPP</code> object. This includes but is not limited to prepared SQL statements
 *         and also existing data constraints in the <b><i>York River Bookseller's Database</i></b>.
 * 
 * @see YRBAPP
 */
public final class YRBAPPUtility {
	/**
	 * Default constructor for a <code>YRBAPPUtility</code> object.
	 */
	// Private constructor so that no object of this type can be instantiated.
	private YRBAPPUtility() {
		// Empty by design.
	}

	/**
	 * Query timeout value in seconds.
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int QUERY_TIMEOUT = 10;

	/**
	 * Query to find the minimum and maximum customer IDs.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String MIN_MAX_CID_QUERY_TEXT = "SELECT MIN(C.cid) AS min_cid, MAX(C.cid) AS max_cid FROM yrb_customer C";

	/**
	 * Query to find the customer with an unknown ID.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String CID_QUERY_TEXT = "SELECT C.cid, C.name, C.city FROM yrb_customer C WHERE C.cid = ? ORDER BY C.cid";

	/**
	 * Query to find all customers.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String CUSTOMER_QUERY_TEXT = "SELECT C.cid, C.name, C.city FROM yrb_customer C ORDER BY C.cid";

	/**
	 * The maximum length of a customer's name in the <code>yrb_customer</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_customer ( ..., name varchar(20), ... );</code>
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_CUSTOMER_NAME_LENGTH = 20;

	/**
	 * The maximum length of a customer's city in the <code>yrb_customer</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_customer ( ..., city varchar(15), ... );</code>
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_CUSTOMER_CITY_LENGTH = 15;

	/**
	 * Query to update the customer's information with an unknown ID.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String CID_UPDATE_TEXT = "UPDATE yrb_customer C SET C.name = ?, C.city = ? WHERE C.cid = ?";

	/**
	 * Query to find all book categories.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String CATEGORY_QUERY_TEXT = "SELECT C.cat FROM yrb_category C ORDER BY C.cat";

	/**
	 * Query to find all books with an unknown category.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String BOOK_QUERY_TEXT = "SELECT B.title, B.year, B.language, B.weight FROM yrb_book B WHERE B.cat = ? ORDER BY B.year, B.title";

	/**
	 * Query to find the minimum price of an unknown book across all clubs that it is offered in.
	 * However only checking such clubs that the customer with the unknown ID is a member of.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String OFFER_QUERY_TEXT = "SELECT O1.club, O1.price FROM yrb_member M1, yrb_offer O1 WHERE M1.cid = ? AND M1.club = O1.club AND O1.title = ? AND O1.year = ? "
			+ "AND O1.price < ALL (SELECT O2.price FROM yrb_member M2, yrb_offer O2 WHERE M2.cid = ? AND M2.club = O2.club AND O2.title = ? AND O2.year = ? AND O2.price <> O1.price)";

	/**
	 * Query to find all purchases made by a customer with an unknown ID.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String PURCHASE_QUERY_TEXT = "SELECT P.club, P.title, P.year, P.when, P.qnty FROM yrb_purchase P WHERE P.cid = ? ORDER BY P.when, P.club, P.year, P.title";

	/**
	 * Insert command to insert an unknown purchase into the <code>yrb_purchase</code> table.
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String PURCHASE_INSERT_TEXT = "INSERT INTO yrb_purchase(cid, club, title, year, when, qnty) VALUES (?, ?, ?, ?, ?, ?)";
}
