#!/bin/sh

# Check if a Java source code has a package or not

# Returns -1 if number of given arguments is not 1 or if the given argument is empty
# Returns -2 if no file with the given argument name can be found
# Returns -3 if the file has no extension
# Returns -4 if the file extension is not java 

# Returns 0 for false and 1 for true
hasPackage() {
	if [ "$#" -ne 1 -o -z "$1" ]; then
		return -1;
	elif [ ! -f "$1" ]; then
		return -2;
	fi

	ext="${1##*.}"
	if [ -z "$ext" ]; then
		return -3;
	elif [ "$ext" != "java" ]; then
		return -4;
	fi

	first=$(head -1 "$1" | cut -d ' ' -f 1)
	if [ -z "$first" ]; then
		return 0;
	elif [ "$first" = "package" ]; then
		return 1;
	fi
	return 0;
}

# working directory
wdir=$(pwd)
echo -e "Working Directory is: "$wdir"\n"

usageError=0
if [ "$#" -ne 1 -o -z "$1" ]; then
	usageError=1
elif [ "$1" != "-create" -a "$1" != "-drop" -a "$1" != "-compile" -a "$1" != "-install" ]; then
	usageError=2
fi
if [ "$usageError" -ne 0 ]; then
	echo -e "Usage:\n"$0" -create: Create the YRB database\nOR\n"$0" -drop: Drop the YRB database"
	echo -e "OR\n"$0" -compile: Compile the Java Application\nOR\n"$0" -install: Install the DB2 Driver"
	exit "$usageError";
fi

if [ "$1" = "-compile" ]; then
	miss=0
	if [ ! -f "Book.java" ]; then
		echo "Missing: Book.java"
		miss=`expr "$miss" + 1`
	fi
	if [ ! -f "Purchase.java" ]; then
		echo "Missing: Purchase.java"
		miss=`expr "$miss" + 1`
	fi
	if [ ! -f "YRBAPPUtility.java" ]; then
		echo "Missing: YRBAPPUtility.java"
		miss=`expr "$miss" + 1`
	fi
	if [ ! -f "YRBAPP.java" ]; then
		echo "Missing: YRBAPP.java"
		miss=`expr "$miss" + 1`
	fi
	[ "$miss" -ne 0 ] && exit `expr "$miss" + 2`;

	pack=0
	temp=$(hasPackage "Book.java") ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "Book.java has a package. Please remove it."
		pack=`expr "$pack" + 1`
	fi
	temp=$(hasPackage "Purchase.java") ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "Purchase.java has a package. Please remove it."
		pack=`expr "$pack" + 1`
	fi
	temp=$(hasPackage "YRBAPPUtility.java") ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "YRBAPPUtility.java has a package. Please remove it."
		pack=`expr "$pack" + 1`
	fi
	temp=$(hasPackage "YRBAPP.java") ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "YRBAPP.java has a package. Please remove it."
		pack=`expr "$pack" + 1`
	fi
	[ "$pack" -ne 0 ] && exit `expr "$pack" + 6`;

	javac Book.java ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "javac error "$ret" on Book.java"
		exit 11;
	fi
	javac Purchase.java ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "javac error "$ret" on Purchase.java"
		exit 12;
	fi
	javac YRBAPPUtility.java ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "javac error "$ret" on YRBAPPUtility.java"
		exit 13;
	fi
	javac YRBAPP.java ; ret="$?"
	if [ "$ret" -ne 0 ]; then
		echo "javac error "$ret" YRBAPP.java"
		exit 14;
	fi

	# make all class files executable
	chmod +x *.class

	echo "compile success"

	echo -e "\nUse the following command to run the application: java YRBAPP"
elif [ "$1" = "-install" ]; then
	driver="source ~db2leduc/cshrc.runtime"

	if [ -f ""$HOME"/.cshrc" ]; then
		rc=""$HOME"/.cshrc"
		lastLine=$(tail -1 "$rc")
		if [ -n "$lastLine" ]; then
			if [ "$lastLine" != "$driver" ]; then
				echo "" >> "$rc"
				echo "$driver" >> "$rc"
			fi
		fi

		echo -e "source success\n"

		echo "Please close your current terminal and open it up again so that the DB2 Driver can be loaded up."
	else
		echo "Please use '"$driver"' to install the DB2 Driver onto your prism machine so that the Java Application can connect to IBM DB2 Server."
	fi
else
	# at this point sqlScript can only be 'yrb-create' or 'yrb-drop'
	sqlScript="yrb"$1""
	if [ ! -f "$sqlScript" ]; then
		echo "Missing: SQL script with name "$sqlScript""
		exit 15;
	fi

	# msg is either 'create' or 'drop'
	msg=$(echo "$1" | cut -c 2-)

	connectLog=".connection-log.txt"
	[ -f "$connectLog" ] && yes | rm -f "$connectLog" &> /dev/null

	db2 connect to c3421a &> "$connectLog" ; ret="$?"
	if [ "$ret" -eq 0 ]; then
		[ -f "$connectLog" ] && yes | rm -f "$connectLog" &> /dev/null

		scriptLog="."$msg"-log.txt"
		[ -f "$scriptLog" ] && yes | rm -f "$scriptLog" &> /dev/null

		db2 -tf "$sqlScript" &> "$scriptLog" ; ret="$?"
		if [ "$ret" -eq 0 ]; then
			[ -f "$scriptLog" ] && yes | rm -f "$scriptLog" &> /dev/null

			echo ""$msg" success"
		else
			echo ""$msg" fail"
			echo -e "\ndb2 error "$ret""
			echo -e "\ndb2 error "$ret"" >> "$scriptLog"

			echo -e "\ndb2 "$msg" log written to "$scriptLog""
		fi
	else
		echo "db2 could not successfully connect to c3421a"
		echo -e "\ndb2 error "$ret""
		echo -e "\ndb2 error "$ret"" >> "$connectLog"

		echo -e "\ndb2 connection log written to "$connectLog""
	fi

	db2 connect reset &> /dev/null
	db2 terminate &> /dev/null
fi

exit 0;
