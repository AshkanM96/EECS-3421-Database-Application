/**
 * @author Ashkan Moatamed <br>
 *         <br>
 *         The <code>Book</code> class encapsulates a single tuple from the <code>yrb_book</code>
 *         table in the <b><i>York River Bookseller's Database</i></b>.
 * 
 * @see #Book(String, short, String, String, short)
 * @see #Book(String, short, String, short)
 * 
 * @see YRBAPP
 */
public final class Book implements Comparable<Book> {
	/**
	 * The maximum length of a book title in the <code>yrb_book</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_book ( ..., title varchar(25) not null, ... );</code>
	 * 
	 * @see #Book(String, short, String, String, short)
	 * @see #Book(String, short, String, short)
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_BOOK_TITLE_LENGTH = 25;

	/**
	 * The title of a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be not <code>null</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_book ( ..., title varchar(25) not null, ... );</code>
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final String title;

	/**
	 * The year of a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b>
	 * <code>create table yrb_book ( ..., year smallint not null, ..., check (year > 0), ... );</code>
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final short year;

	/**
	 * The maximum length of a book language in the <code>yrb_book</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_book ( ..., language varchar(10), ... );</code>
	 * 
	 * @see #Book(String, short, String, String, short)
	 * @see #Book(String, short, String, short)
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_BOOK_LANGUAGE_LENGTH = 10;

	/**
	 * The language of a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Could be <code>null</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_book ( ..., language varchar(10), ... );</code>
	 * 
	 * @see #getLanguage()
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final String language;

	/**
	 * A special keyword used to map <code>this.language</code> to, <br>
	 * when <code>this.language == null</code>.
	 * 
	 * @see #Book(String, short, String, String, short)
	 * 
	 * @see #getLanguage()
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final String UNKNOWN = "Unknown";

	/**
	 * Returns a null-safe representation of <code>this.language</code>
	 * 
	 * @return <code>this.language == null ? Book.UNKNOWN : this.language</code>
	 * 
	 * @see #language
	 * @see #UNKNOWN
	 * 
	 * @see #toString()
	 */
	// Strings are immutable in Java which is why returning a direct reference to this variable,
	// does not pose any issues against encapsulation.
	public String getLanguage() {
		return (this.language == null ? Book.UNKNOWN : this.language);
	}

	/**
	 * The maximum length of a book category in the <code>yrb_book</code> table. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_book ( ..., cat varchar(10) not null, ... );</code>
	 * 
	 * @see #Book(String, short, String, String, short)
	 * @see #Book(String, short, String, short)
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public static final int MAX_BOOK_CATEGORY_LENGTH = 10;

	/**
	 * The category of a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be not <code>null</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b> <code>create table yrb_book ( ..., cat varchar(10) not null, ... );</code>
	 */
	// Strings are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final String category;

	/**
	 * The weight of a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Guaranteed to be <code>non-negative</code>. <br>
	 * <br>
	 * 
	 * <b>SQL :</b>
	 * <code>create table yrb_book ( ..., weight smallint not null, ..., check (weight >= 0), ... );</code>
	 */
	// ints are immutable in Java which is why making this final variable public,
	// does not pose any issues against encapsulation.
	public final short weight;

	/**
	 * General constructor for a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Note that the language is set by the following: <br>
	 * <code>this.language = Book.UNKNOWN.equals(language) ? null : language;</code>
	 * 
	 * @param title
	 *            the new book title
	 * 
	 * @param year
	 *            the new book year
	 * 
	 * @param language
	 *            the new book language
	 * 
	 * @param category
	 *            the new book category
	 * 
	 * @param weight
	 *            the new book weight
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>title == null</code> <br>
	 *             If <code>category == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             <br>
	 *             If <code>title.length() &gt; Book.MAX_BOOK_TITLE_LENGTH</code> <br>
	 *             If <code>year &lt; 0</code> <br>
	 *             If
	 *             <code>(language != null && language.length() &gt; Book.MAX_BOOK_LANGUAGE_LENGTH)</code>
	 *             <br>
	 *             If <code>category.length() &gt; Book.MAX_BOOK_CATEGORY_LENGTH</code> <br>
	 *             If <code>weight &lt; 0</code>
	 * 
	 * @see #MAX_BOOK_TITLE_LENGTH
	 * @see #MAX_BOOK_LANGUAGE_LENGTH
	 * @see #MAX_BOOK_CATEGORY_LENGTH
	 * 
	 * @see #UNKNOWN
	 * 
	 * @see #Book(String, short, String, short)
	 */
	public Book(String title, short year, String language, String category, short weight)
			throws NullPointerException, IllegalArgumentException {
		if (title == null) {
			throw new NullPointerException("Given book title is null.");
		} else if (title.length() > Book.MAX_BOOK_TITLE_LENGTH) {
			throw new IllegalArgumentException("Given book title has length " + title.length()
					+ " which is not in the following range: [0, " + Book.MAX_BOOK_TITLE_LENGTH + "]");
		} else if (year < 0) {
			throw new IllegalArgumentException("Given book year(" + year + ") is negative.");
		} else if (language != null && language.length() > Book.MAX_BOOK_LANGUAGE_LENGTH) {
			throw new IllegalArgumentException("Given book language has length " + language.length()
					+ " which is not in the following range: [0, " + Book.MAX_BOOK_LANGUAGE_LENGTH + "]");
		} else if (category == null) {
			throw new NullPointerException("Given book category is null.");
		} else if (category.length() > Book.MAX_BOOK_CATEGORY_LENGTH) {
			throw new IllegalArgumentException("Given book category has length " + category.length()
					+ " which is not in the following range: [0, " + Book.MAX_BOOK_CATEGORY_LENGTH + "]");
		} else if (weight < 0) {
			throw new IllegalArgumentException("Given book weight(" + weight + ") is negative.");
		}

		this.title = title;
		this.year = year;
		this.language = Book.UNKNOWN.equals(language) ? null : language;
		this.category = category;
		this.weight = weight;
	}

	/**
	 * A simpler version of the general constructor for a <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Same as calling
	 * <code>Book(String title, int year, String language, String category, int weight)</code>
	 * constructor with arguments <code>(title, year, null, category, weight)</code>.
	 * 
	 * @param title
	 *            the new book title
	 * 
	 * @param year
	 *            the new book year
	 * 
	 * @param category
	 *            the new book category
	 * 
	 * @param weight
	 *            the new book weight
	 * 
	 * @throws NullPointerException
	 *             <br>
	 *             If <code>title == null</code> <br>
	 *             If <code>category == null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             <br>
	 *             If <code>title.length() &gt; Book.MAX_BOOK_TITLE_LENGTH</code> <br>
	 *             If <code>year &lt; 0</code> <br>
	 *             If <code>category.length() &gt; Book.MAX_BOOK_CATEGORY_LENGTH</code> <br>
	 *             If <code>weight &lt; 0</code>
	 * 
	 * @see #MAX_BOOK_TITLE_LENGTH
	 * @see #MAX_BOOK_CATEGORY_LENGTH
	 * 
	 * @see #Book(String, short, String, String, short)
	 */
	public Book(String title, short year, String category, short weight)
			throws NullPointerException, IllegalArgumentException {
		this(title, year, null, category, weight);
	}

	/**
	 * Returns a <code>String</code> representation of this <code>Book</code>.
	 * 
	 * @return <code>"Title: " + this.title + <br> ", Year: " + this.year + <br> ", Language: " + this.getLanguage() +
	 * <br> ", Category: " + this.category + <br> ", Weight: " + this.weight</code>
	 * 
	 * @see #getLanguage()
	 */
	@Override
	public String toString() {
		return ("Title: " + this.title + ", Year: " + this.year + ", Language: " + this.getLanguage() + ", Category: "
				+ this.category + ", Weight: " + this.weight);
	}

	/**
	 * Compares this <code>Book</code> to the specified object. The result is <tt>true</tt> if and only
	 * if <code>obj != null</code> and it is a <code>Book</code> object that represents the same object.
	 *
	 * @param obj
	 *            the object to compare this <code>Book</code> against
	 *
	 * @return <tt>true</tt> if the given object represents a <code>Book</code> equivalent to this
	 *         <code>Book</code> and <tt>false</tt> otherwise.
	 * 
	 * @see #isEqual(java.lang.Object)
	 * @see #hashCode()
	 * @see #compareTo(Book)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			// Check reference equality.
			return true;
		} else if (!(obj instanceof Book)) {
			// Check: obj != null && this.getClass() == obj.getClass()
			return false;
		}

		Book other = (Book) obj; // Cast obj into a variable of static type Book.

		// First simple/quick check(s)
		if (this.year != other.year) {
			return false;
		} else if (this.weight != other.weight) {
			return false;
		} else if (!this.title.equals(other.title)) { // Now more complicated check(s)
			return false;
		} else if (!this.category.equals(other.category)) {
			return false;
		}
		// Handle the case where this.language could be null.
		if (this.language == null) {
			if (other.language != null) {
				return false;
			}
		} else if (!this.language.equals(other.language)) {
			return false;
		}

		return true;
	}

	/**
	 * Compares this <code>Book</code> to the specified object. The result is <tt>true</tt> if and only
	 * if <code>obj != null</code> and it is a <code>Book</code> object that represents the same tuple
	 * in the <code>yrb_book</code> table from the <b><i>York River Bookseller's Database</i></b>. <br>
	 * <br>
	 * 
	 * This method satisfies all of the basic properties of <code>equals(...)</code> in
	 * <code>Object</code>.
	 *
	 * @param obj
	 *            the object to compare this <code>Book</code> against
	 *
	 * @return <tt>true</tt> if the given object represents a <code>Book</code> equivalent to this
	 *         <code>Book</code> in the <code>yrb_book</code> table from the <b><i>York River
	 *         Bookseller's Database</i></b> and <tt>false</tt> otherwise.
	 * 
	 * @see #equals(java.lang.Object)
	 * @see #hashCode()
	 * @see #compareTo(Book)
	 */
	public boolean isEqual(Object obj) {
		if (this == obj) {
			// Check reference equality.
			return true;
		} else if (!(obj instanceof Book)) {
			// Check: obj != null && this.getClass() == obj.getClass()
			return false;
		}

		Book other = (Book) obj; // Cast obj into a variable of static type Book.

		// Only need to check title and year since they are the Primary Key
		// for a tuple in the yrb_book table.

		// First simple/quick check(s)
		if (this.year != other.year) {
			return false;
		} else if (!this.title.equals(other.title)) { // Now more complicated check(s)
			return false;
		}

		return true;
	}

	/**
	 * Returns a hash code value for this <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Note: Equal objects should have the same hash code.
	 *
	 * @return A hash code value for this <code>Book</code> object.
	 * 
	 * @see #equals(java.lang.Object)
	 * @see #isEqual(java.lang.Object)
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + this.category.hashCode();
		result = prime * result + ((this.language == null) ? 0 : this.language.hashCode());
		result = prime * result + this.title.hashCode();
		result = prime * result + this.weight;
		result = prime * result + this.year;

		return result;
	}

	/**
	 * Returns a ternary comparison after comparing this <code>Book</code> to the other given
	 * <code>Book</code> object. <br>
	 * <br>
	 * 
	 * Note: This class has a natural ordering that is not necessarily consistent with equals.
	 * 
	 * @param other
	 *            the other given <code>Book</code> object
	 * 
	 * @return <b>SQL :</b><code>	<i>ORDER BY</i> yrb_book.year, yrb_book.title</code>
	 * 
	 * @throws NullPointerException
	 *             If <code>other == null</code>
	 * 
	 * @see #equals(java.lang.Object)
	 * @see #isEqual(java.lang.Object)
	 */
	@Override
	public int compareTo(Book other) throws NullPointerException {
		if (other == null) {
			throw new NullPointerException("Given other Book is null.");
		}

		// Compare the years first.
		int result = ((Short) this.year).compareTo(other.year);
		// Only compare the titles if the years were equal.
		return (result != 0 ? result : this.title.compareTo(other.title));
	}
}
