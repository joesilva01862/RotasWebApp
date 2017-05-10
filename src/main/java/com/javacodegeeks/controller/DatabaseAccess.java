package com.javacodegeeks.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAccess {
	private static final String DB_DRIVER = "org.postgresql.Driver";
	
	
    // to be used for Heroku deployment
	private static Connection getConnection() throws URISyntaxException, SQLException {
	    URI dbUri = new URI(System.getenv("DATABASE_URL"));

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

	    return DriverManager.getConnection(dbUrl, username, password);
	}
	
/*	
    // to be used for local deployment
	private static Connection getConnection() throws SQLException {
		// for local test
		try {
			Class.forName(DB_DRIVER);
		} 
		catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

	    String dbUrl = System.getenv("JDBC_DATABASE_URL");
	    String user = "postgres";
	    String password = "admin";
	    return DriverManager.getConnection(dbUrl, user, password);
	}
*/
	
	public static int read(int year, int month) throws SQLException, URISyntaxException {

		String selectTableSQL = "select count from USAGE "
				+ "where year = ? and month = ?";
		Connection conn = getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(selectTableSQL);
		preparedStatement.setInt(1,  year);
		preparedStatement.setInt(2,  month);
		ResultSet rs = preparedStatement.executeQuery();

		int rowCount = 0;
		
		while (rs.next()) {
			rowCount = rs.getInt("COUNT");
			break;
		}
		
		preparedStatement.close();
		conn.close();
		return rowCount;
	}
	
	public static void insert(int year, int month) throws SQLException, URISyntaxException {

		String insertTableSQL = "INSERT INTO USAGE"
				+ "(year, month, day, count) VALUES"
				+ "(?,?,?,?)";
		Connection conn = getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
		preparedStatement.setInt(1, year);
		preparedStatement.setInt(2, month);
		preparedStatement.setInt(3, 0);
		preparedStatement.setInt(4, 1);
		// execute insert SQL stetement
		preparedStatement.executeUpdate();
		preparedStatement.close();
		conn.close();
	}
	
	public static void update(int year, int month, int count) throws SQLException, URISyntaxException {

		String updateTableSQL = "UPDATE USAGE "
				+ "set count = ? "
				+ "where year = ? and month = ?";
		Connection conn = getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(updateTableSQL);
		preparedStatement.setInt(1, count);
		preparedStatement.setInt(2, year);
		preparedStatement.setInt(3, month);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		conn.close();
	}
	
	
}
