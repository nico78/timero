package localdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Example {

	static String database = System.getProperty("user.dir")+"/database/employeeTestDb";
	static Connection connection;

	public static void main(String[] args) throws Exception {
		System.out.println("TestDemobase\n");

		Class.forName("org.hsqldb.jdbcDriver");
		//
		connection = DriverManager.getConnection("jdbc:hsqldb:" + database,
				"sa", "");

		Statement statement = null;
		ResultSet resultSet = null;

		statement = connection.createStatement();

		statement
				.executeUpdate("CREATE TABLE Bookmarks (title VARCHAR(50), url VARCHAR(255));");
		statement
				.executeUpdate("INSERT INTO Bookmarks (title, url) VALUES ('Java Technology', 'http://java.sun.com/');");
		statement
				.executeUpdate("INSERT INTO Bookmarks (title, url) VALUES ('HSQLDB 100% Java Database', 'http://hsqldb.sourceforge.net/');");
		statement
				.executeUpdate("INSERT INTO Bookmarks (title, url) VALUES ('Apache Jakarta Tomcat', 'http://jakarta.apache.org/tomcat/');");
		resultSet = statement
				.executeQuery("SELECT title, url FROM Bookmarks ORDER BY title");

		while (resultSet.next()) {
			System.out.println(resultSet.getString("title") + " ("
					+ resultSet.getString("url") + ")");

		}

		resultSet.close();
		statement.close();
		connection.close();
	}
}
