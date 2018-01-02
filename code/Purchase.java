import java.sql.Timestamp;

/**
 * @author Ashkan Moatamed <br>
 *         <br>
 *         The <code>Purchase</code> class can encapsulate a single tuple from the
 *         <code>yrb_purchase</code> table in the <b><i>York River Bookseller's Database</i></b>.
 * 
 * @see #Purchase(short, String, String, short, float, short, long)
 * @see #Purchase(short, String, String, short, float, short)
 * @see #Purchase(short, String, String, short, float)
 * 
 * @see YRBAPP
 */
public final class Purchase implements Comparable<Purchase> {
	/**
	 * The customer ID of a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., cid smallint not null, ... );</code>
	 */
	// shorts are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final short cid;

	/**
	 * The maximum length of a club name in the <code>yrb_purchase</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., club varchar(15) not null, ... );</code>
	 * 
	 * @see #Purchase(short, String, String, short, float, short, long)
	 * @see #Purchase(short, String, String, short, float, short)
	 * @see #Purchase(short, String, String, short, float)
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_PURCHASE_CLUB_LENGTH = 15;

	/**
	 * The club name of a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be not <code>null</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., club varchar(15) not null, ... );</code>
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final String club;

	/**
	 * The maximum length of a book title in the <code>yrb_purchase</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., title varchar(25) not null, ... );</code>
	 * 
	 * @see #Purchase(short, String, String, short, float, short, long)
	 * @see #Purchase(short, String, String, short, float, short)
	 * @see #Purchase(short, String, String, short, float)
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_PURCHASE_TITLE_LENGTH = 25;

	/**
	 * The book title of a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be not <code>null</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., title varchar(25) not null, ... );</code>
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final String title;

	/**
	 * The book year of a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., year smallint not null, ... );</code>
	 */
	// shorts are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final short year;

	/**
	 * The book price of a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., price decimal(5,2) not null, ... );</code>
	 */
	// floats are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final float price;

	/**
	 * The book quantity of a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., qnty smallint not null, ... );</code>
	 * 
	 * @see #getQuantity()
	 * @see #setQuantity(short)
	 */
	private short quantity;

	/**
	 * Returns the current book purchase quantity.
	 * 
	 * @return <code>this.quantity</code>
	 * 
	 * @see #quantity
	 */
	// shorts are call-by-value in Java which is why returning the value directly,
	// does not pose any issues against encapsulation.
	public short getQuantity() {
		return this.quantity;
	}

	/**
	 * Set the current book purchase quantity to the given quantity.
	 * 
	 * @param quantity
	 *            the new quantity
	 * 
	 * @return <code>this.quantity</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>quantity &lt; 0</code>
	 * 
	 * @see #quantity
	 */
	// shorts are call-by-value in Java which is why returning the value directly,
	// does not pose any issues against encapsulation.
	public short setQuantity(short quantity) throws IllegalArgumentException {
		if (quantity < 0) {
			throw new IllegalArgumentException("Given book purchase quantity(" + quantity + ") is negative.");
		}
		return (this.quantity = quantity);
	}

	/**
	 * The timestamp of a <code>Purchase</code> object in milliseconds. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>.
	 * 
	 * @see #getWhen()
	 * @see #setWhen(long)
	 * @see #setWhen()
	 */
	private long currentTimeMillis;

	/**
	 * Get the timestamp of this <code>Purchase</code> object as a <code>java.sql.Timestamp</code>
	 * object. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_purchase ( ..., when timestamp not null, ... );</code>
	 * 
	 * @see Timestamp
	 * 
	 * @see #currentTimeMillis
	 * 
	 * @see #setWhen(long)
	 */
	// Returns a new object every time since Timestamp is mutable.
	public Timestamp getWhen() {
		return new Timestamp(this.currentTimeMillis);
	}

	/**
	 * Set the timestamp of this <code>Purchase</code> object and get the old timestamp.
	 * 
	 * @param currentTimeMillis
	 *            the new timestamp in milliseconds
	 * 
	 * @return The old timestamp.
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>currentTimeMillis &lt; 0</code>
	 * 
	 * @see #currentTimeMillis
	 * 
	 * @see #getWhen()
	 * @see #setWhen()
	 * 
	 * @see #Purchase(short, String, String, short, float, short, long)
	 */
	public Timestamp setWhen(long currentTimeMillis) throws IllegalArgumentException {
		if (currentTimeMillis < 0) {
			throw new IllegalArgumentException(
					"Given current time in milliseconds(" + currentTimeMillis + ") is negative.");
		}

		Timestamp result = this.getWhen();
		this.currentTimeMillis = currentTimeMillis;
		return result;
	}

	/**
	 * Set the timestamp of this <code>Purchase</code> object and get the old timestamp. <br>
	 * <br>
	 * 
	 * Same as calling <code>setWhen(long currentTimeMillis)</code> with argument
	 * <code>(System.currentTimeMillis())</code>.
	 * 
	 * @return The old timestamp.
	 * 
	 * @see #currentTimeMillis
	 * 
	 * @see #setWhen(long)
	 * 
	 * @see System#currentTimeMillis()
	 */
	public Timestamp setWhen() {
		return this.setWhen(System.currentTimeMillis());
	}

	/**
	 * General constructor for a <code>Purchase</code> object.
	 * 
	 * @param cid
	 *            the new customer ID
	 * 
	 * @param club
	 *            the new club name
	 * 
	 * @param title
	 *            the new book title
	 * 
	 * @param year
	 *            the new book year
	 * 
	 * @param price
	 *            the new book price
	 * 
	 * @param quantity
	 *            the new book quantity
	 * 
	 * @param currentTimeMillis
	 *            the new timestamp in milliseconds
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>club == null</code> <br>
	 *             If <code>title == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             <br>
	 *             If <code>club.length() &gt; Purchase.MAX_PURCHASE_CLUB_LENGTH</code> <br>
	 *             If <code>title.length() &gt; Purchase.MAX_PURCHASE_TITLE_LENGTH</code> <br>
	 *             If <code>year &lt; 0</code> <br>
	 *             If <code>price &lt; 0</code> <br>
	 *             If <code>quantity &lt; 0</code> <br>
	 *             If <code>currentTimeMillis &lt; 0</code>
	 * 
	 * @see #MAX_PURCHASE_CLUB_LENGTH
	 * @see #MAX_PURCHASE_TITLE_LENGTH
	 * 
	 * @see #setQuantity(short)
	 * 
	 * @see #setWhen(long)
	 * 
	 * @see #Purchase(short, String, String, short, float, short)
	 */
	public Purchase(short cid, String club, String title, short year, float price, short quantity,
			long currentTimeMillis) throws NullPointerException, IllegalArgumentException {
		if (club == null) {
			throw new NullPointerException("Given club name is null.");
		} else if (club.length() > Purchase.MAX_PURCHASE_CLUB_LENGTH) {
			throw new IllegalArgumentException("Given club name has length " + club.length()
					+ " which is not in the following range: [0, " + Purchase.MAX_PURCHASE_CLUB_LENGTH + "]");
		} else if (title == null) {
			throw new NullPointerException("Given book title is null.");
		} else if (title.length() > Purchase.MAX_PURCHASE_TITLE_LENGTH) {
			throw new IllegalArgumentException("Given book title has length " + title.length()
					+ " which is not in the following range: [0, " + Purchase.MAX_PURCHASE_TITLE_LENGTH + "]");
		} else if (year < 0) {
			throw new IllegalArgumentException("Given book year(" + year + ") is negative.");
		} else if (price < 0) {
			throw new IllegalArgumentException("Given book price(" + price + ") is negative.");
		}
		// setQuantity handles the IllegalArgumentException when quantity is invalid.
		// setWhen handles the IllegalArgumentException when currentTimeMillis is invalid.

		this.cid = cid;
		this.club = club;
		this.title = title;
		this.year = year;
		this.price = price;
		// Does not matter if the above assignments come after the following two mutator
		// calls since if an Exception is thrown, the constructor will not finish completely
		// and as such the caller will not get a reference to the half completed object.
		this.setQuantity(quantity);
		this.setWhen(currentTimeMillis);
	}

	/**
	 * A slightly simpler version of the general constructor for a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Same as calling
	 * <code>Purchase(short cid, String club, String title, short year, float price, short quantity, long currentTimeMillis)</code>
	 * constructor with arguments
	 * <code>(cid, club, title, year, price, quantity, System.currentTimeMillis())</code>.
	 * 
	 * @param cid
	 *            the new customer ID
	 * 
	 * @param club
	 *            the new club name
	 * 
	 * @param title
	 *            the new book title
	 * 
	 * @param year
	 *            the new book year
	 * 
	 * @param price
	 *            the new book price
	 * 
	 * @param quantity
	 *            the new book quantity
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>club == null</code> <br>
	 *             If <code>title == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             <br>
	 *             If <code>club.length() &gt; Purchase.MAX_PURCHASE_CLUB_LENGTH</code> <br>
	 *             If <code>title.length() &gt; Purchase.MAX_PURCHASE_TITLE_LENGTH</code> <br>
	 *             If <code>year &lt; 0</code> <br>
	 *             If <code>price &lt; 0</code> <br>
	 *             If <code>quantity &lt; 0</code>
	 * 
	 * @see #MAX_PURCHASE_CLUB_LENGTH
	 * @see #MAX_PURCHASE_TITLE_LENGTH
	 * 
	 * @see #Purchase(short, String, String, short, float, short, long)
	 * @see #Purchase(short, String, String, short, float)
	 * 
	 * @see System#currentTimeMillis()
	 */
	public Purchase(short cid, String club, String title, short year, float price, short quantity)
			throws NullPointerException, IllegalArgumentException {
		this(cid, club, title, year, price, quantity, System.currentTimeMillis());
	}

	/**
	 * An even simpler version of the general constructor for a <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Same as calling
	 * <code>Purchase(short cid, String club, String title, short year, float price, short quantity)</code>
	 * constructor with arguments <code>(cid, club, title, year, price, (short) 0)</code>.
	 * 
	 * @param cid
	 *            the new customer ID
	 * 
	 * @param club
	 *            the new club name
	 * 
	 * @param title
	 *            the new book title
	 * 
	 * @param year
	 *            the new book year
	 * 
	 * @param price
	 *            the new book price
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>club == null</code> <br>
	 *             If <code>title == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             <br>
	 *             If <code>club.length() &gt; Purchase.MAX_PURCHASE_CLUB_LENGTH</code> <br>
	 *             If <code>title.length() &gt; Purchase.MAX_PURCHASE_TITLE_LENGTH</code> <br>
	 *             If <code>year &lt; 0</code> <br>
	 *             If <code>price &lt; 0</code>
	 * 
	 * @see #MAX_PURCHASE_CLUB_LENGTH
	 * @see #MAX_PURCHASE_TITLE_LENGTH
	 * 
	 * @see #Purchase(short, String, String, short, float, short)
	 */
	public Purchase(short cid, String club, String title, short year, float price)
			throws NullPointerException, IllegalArgumentException {
		this(cid, club, title, year, price, (short) 0);
	}

	/**
	 * Returns a <code>String</code> representation of this <code>Purchase</code>.
	 * 
	 * @return <code>"Customer ID: " + this.cid + <br> ", Club: " + this.club + <br> ", Title: " + this.title +
	 * <br> ", Year: " + this.year + <br> ", Price: " + this.price + <br> ", Quantity: " + this.quantity</code>
	 */
	@Override
	public String toString() {
		return ("Customer ID: " + this.cid + ", Club: " + this.club + ", Title: " + this.title + ", Year: " + this.year
				+ ", Price: " + this.price + ", Quantity: " + this.quantity);
	}

	/**
	 * Compares this <code>Purchase</code> to the specified object. The result is <tt>true</tt> if and
	 * only if <code>obj != null</code> and it is a <code>Purchase</code> object that represents the
	 * same object.
	 *
	 * @param obj
	 *            the object to compare this <code>Purchase</code> against
	 *
	 * @return <tt>true</tt> if the given object represents a <code>Purchase</code> equivalent to this
	 *         <code>Purchase</code> and <tt>false</tt> otherwise.
	 * 
	 * @see #isEqual(java.lang.Object)
	 * @see #hashCode()
	 * @see #compareTo(Purchase)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			// Check reference equality.
			return true;
		} else if (!(obj instanceof Purchase)) {
			// Check: obj != null && this.getClass() == obj.getClass()
			return false;
		}

		Purchase other = (Purchase) obj; // Cast obj into a variable of static type Purchase.

		// First simple/quick check(s)
		if (this.cid != other.cid) {
			return false;
		} else if (this.year != other.year) {
			return false;
		} else if (this.price != other.price) {
			return false;
		} else if (this.quantity != other.quantity) {
			return false;
		} else if (this.currentTimeMillis != other.currentTimeMillis) {
			return false;
		} else if (!this.club.equals(other.club)) { // Now more complicated check(s)
			return false;
		} else if (!this.title.equals(other.title)) {
			return false;
		}

		return true;
	}

	/**
	 * Compares this <code>Purchase</code> to the specified object. The result is <tt>true</tt> if and
	 * only if <code>obj != null</code> and it is a <code>Purchase</code> object that represents the
	 * same tuple in the <code>yrb_purchase</code> table from the <b><i>York River Bookseller's
	 * Database</i></b>. <br>
	 * <br>
	 * 
	 * This method satisfies all of the basic properties of <code>equals(...)</code> in
	 * <code>Object</code>.
	 *
	 * @param obj
	 *            the object to compare this <code>Purchase</code> against
	 *
	 * @return <tt>true</tt> if the given object represents a <code>Purchase</code> equivalent to this
	 *         <code>Purchase</code> in the <code>yrb_purchase</code> table from the <b><i>York River
	 *         Bookseller's Database</i></b> and <tt>false</tt> otherwise.
	 * 
	 * @see #equals(java.lang.Object)
	 * @see #hashCode()
	 * @see #compareTo(Purchase)
	 */
	public boolean isEqual(Object obj) {
		if (this == obj) {
			// Check reference equality.
			return true;
		} else if (!(obj instanceof Purchase)) {
			// Check: obj != null && this.getClass() == obj.getClass()
			return false;
		}

		Purchase other = (Purchase) obj; // Cast obj into a variable of static type Purchase.

		// Only need to check cid, club, title, year and when since they are the Primary Key
		// for a tuple in the yrb_purchase table.

		// First simple/quick check(s)
		if (this.cid != other.cid) {
			return false;
		} else if (this.year != other.year) {
			return false;
		} else if (this.currentTimeMillis != other.currentTimeMillis) {
			return false;
		} else if (!this.club.equals(other.club)) { // Now more complicated check(s)
			return false;
		} else if (!this.title.equals(other.title)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns a hash code value for this <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Note: Equal objects should have the same hash code.
	 *
	 * @return A hash code value for this <code>Purchase</code> object.
	 * 
	 * @see #equals(java.lang.Object)
	 * @see #isEqual(java.lang.Object)
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + this.cid;
		result = prime * result + this.club.hashCode();
		result = prime * result + Long.hashCode(this.currentTimeMillis);
		result = prime * result + Float.hashCode(this.price);
		result = prime * result + Short.hashCode(this.quantity);
		result = prime * result + this.title.hashCode();
		result = prime * result + this.year;

		return result;
	}

	/**
	 * Returns a ternary comparison after comparing this <code>Purchase</code> to the other given
	 * <code>Purchase</code> object. <br>
	 * <br>
	 * 
	 * Note: This class has a natural ordering that is not necessarily consistent with equals.
	 * 
	 * @param other
	 *            the other given <code>Purchase</code> object
	 * 
	 * @return <b>SQL
	 *         :</b><code>	<i>ORDER BY</i> yrb_purchase.when, yrb_purchase.club, yrb_purchase.year, yrb_purchase.title</code>
	 * 
	 * @throws NullPointerException
	 *             If <code>other == null</code>
	 * 
	 * @see #equals(java.lang.Object)
	 * @see #isEqual(java.lang.Object)
	 */
	@Override
	public int compareTo(Purchase other) throws NullPointerException {
		if (other == null) {
			throw new NullPointerException("Given other Purchase is null.");
		}

		// Compare the whens first.
		int result = ((Long) this.currentTimeMillis).compareTo(other.currentTimeMillis);
		// Only compare the rest if the whens were equal.
		if (result != 0) {
			return result;
		}

		result = this.club.compareTo(other.club);
		// Only compare the rest if the clubs were equal.
		if (result != 0) {
			return result;
		}

		result = ((Short) this.year).compareTo(other.year);
		// Only compare the titles if the years were equal.
		return (result != 0 ? result : this.title.compareTo(other.title));
	}
}
