package com.puma.service.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Utils {

	public byte[] loadFileFromLocal(String path, String filename) throws IOException {
		return Files.readAllBytes(Paths.get(path + filename));
	}

	public Connection createDbConnection() throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl",
				"pci", "Pcicol01");
	}
	
	
}
