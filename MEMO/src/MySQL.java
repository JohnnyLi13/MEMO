

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQL {
	
	private Connection connection = null;
    private Statement statement = null;
    //private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
	String url = "jdbc:mysql://localhost:3306/shop";
	String user      = "root";
	String password  = "password";
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	void update_task_nums() {
		try {
			String database_name = null;
			String SELECT_TN_SQL = "SELECT task_num FROM shop";
			connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(SELECT_TN_SQL);
        	ArrayList<Integer> task_nums = new ArrayList<Integer>();
        	int task_num = 0;
        	while(resultSet.next()) {
        		task_num = resultSet.getInt("task_num");
        		task_nums.add(task_num);
        	}
        	System.out.println(task_nums);
        	for (int i=1;i<task_nums.size()+1;i++) {
        		String UPDATE_TN_SQL = "UPDATE shop SET task_num = " + Integer.toString(i) + " WHERE task_num = " + Integer.toString(task_nums.get(i-1));
        		statement.executeUpdate(UPDATE_TN_SQL);
        	}
        	System.out.println("all task_num s are updated");
			resultSet.close();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to update task#");
			e.printStackTrace();
		}
	}
	
	
	void set_coordinates(String place, String latitude, String longitude) {
		try {
			String database_name = null;
			String formatted_coordinate = "'" + latitude + ", " + longitude + "'";
			//System.out.println(formatted_coordinate);
			String UPDATE_COORDINATE_SQL = "UPDATE shop SET coordinate = " 
					+ formatted_coordinate
					+ " WHERE place = " + "'" + place + "'";
			connection = DriverManager.getConnection(url, user, password);
			database_name = connection.getCatalog();
			System.out.println(String.format("connected to database %s successfully", database_name));
			statement = connection.createStatement();
			statement.executeUpdate(UPDATE_COORDINATE_SQL);
			System.out.printf("coordinate at %s updated successfully \n", place);
			resultSet.close();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to update coordinate");
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void nullify_column(String characteristic) {
		try {
			String database_name = null;
			String NULL_COLUMN_SQL = "UPDATE shop SET coordinate = 'NULL' WHERE coordinate is not NULL";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	statement.executeUpdate(NULL_COLUMN_SQL);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
			System.out.println("all records deleted successfully");
			System.out.println("all entries of " + characteristic + "set up NULL");
		}catch(SQLException e) {
			System.out.println("fail to delete column: " + characteristic);
			e.printStackTrace();
		}
	}
	
	
	void delte_all_records() {
		try {
			String database_name = null;
			String DELETE_ALL_RECORDS_SQL = "DELETE FROM shop";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	//
        	statement.executeUpdate(DELETE_ALL_RECORDS_SQL);
        	//
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
			System.out.println("all records deleted successfully");
		}catch(SQLException e) {
			System.out.println("fail to delete all records");
			e.printStackTrace();
		}
	}
	
	
	void delete_record_given_task(String task) {
		try {
			String database_name = null;
			String DELETE_RECORDS_SQL = "DELETE FROM shop " + "WHERE task = " + "'" + task + "'";
			String SELECT_RECORDS_SQL = "SELECT task_num, task, place, coordinate FROM shop"
					+ " WHERE task = " + "'" + task + "'" + " ";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(SELECT_RECORDS_SQL);
        	int task_num = 0; String place = null; String t = null; String coordinate = null;
        	while(resultSet.next()) {
				task_num = resultSet.getInt("task_num");
				t = resultSet.getString("task");
				place = resultSet.getString("place");
				coordinate = resultSet.getString("coordinate");
				System.out.print("this task will be deleted: ");
				System.out.print("task#: " + task_num);
				System.out.print("	task: " + t);
				System.out.print("		place: " + place);
				System.out.println("	coordinate: " + coordinate);
        	}
        	///
        	statement.executeUpdate(DELETE_RECORDS_SQL);
        	System.out.println("tasks: " + task + " are removed");
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
        	///
			update_task_nums();
		}catch(SQLException e) {
			System.out.println("fail to delete records with tasks = " + task);
			e.printStackTrace();
		}
	}
	
	
	void delete_record_given_place(String place) {
		try {
			String database_name = null;
			String DELETE_RECORDS_SQL = "DELETE FROM shop " + "WHERE place = " + "'" + place + "'";
			String SELECT_RECORDS_SQL = "SELECT task_num, task, place, coordinate FROM shop"
					+ " WHERE place = " + "'" + place + "'" + " ";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(SELECT_RECORDS_SQL);
        	int task_num = 0; String p = null; String task = null; String coordinate = null;
        	while(resultSet.next()) {
				task_num = resultSet.getInt("task_num");
				task = resultSet.getString("task");
				p = resultSet.getString("place");
				coordinate = resultSet.getString("coordinate");
				System.out.print("this task will be deleted: ");
				System.out.print("task#: " + task_num);
				System.out.print("	task: " + task);
				System.out.print("		place: " + p);
				System.out.print("		coordinate: " + coordinate);
        	}
        	///
        	statement.executeUpdate(DELETE_RECORDS_SQL);
        	System.out.println("tasks at " + place + " are removed");
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
        	///
			update_task_nums();
		}catch(SQLException e) {
			System.out.println("fail to delete records with place = " + place);
			e.printStackTrace();
		}
	}
	
	
	void delete_record_given_task_num(int task_num) {
		try {
			String database_name = null;
			String DELETE_RECORD_SQL = "DELETE FROM shop " + "WHERE task_num = " + task_num;
			String SELECT_RECORD_SQL = "SELECT task_num, task, place, coordinate FROM shop"
					+ " WHERE task_num = " + task_num + " ";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(SELECT_RECORD_SQL);
        	int tnum = 0; String place = null; String task = null; String coordinate = null;
        	while(resultSet.next()) {
            	tnum = resultSet.getInt("task_num");
            	task = resultSet.getString("task");
            	place = resultSet.getString("place");
            	coordinate = resultSet.getString("coordinate");
        	}
        	///
        	statement.executeUpdate(DELETE_RECORD_SQL);
        	///
        	System.out.printf("task delete: task#: %d	task:%s		place: %s		coordinate: %s\n", tnum, task, place, coordinate);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
			///
			update_task_nums();
		}catch(SQLException e) {
			System.out.println("fail to delete record with task# = " + task_num);
			e.printStackTrace();
		}
	}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	ArrayList<String> select_places_with_coordinates(){
		try {
			String database_name = null;
			String SELECT_RECORDS_SQL = "SELECT place, coordinate FROM shop";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	resultSet = statement.executeQuery(SELECT_RECORDS_SQL);
        	String place = null, coordinate = null; 
        	ArrayList<String> places = new ArrayList<String>();
        	while(resultSet.next()) {
				place = resultSet.getString("place");
				coordinate = resultSet.getString("coordinate");
				//System.out.println("place: " + place);
				places.add(place);
				places.add(coordinate);
        	}
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
			System.out.println(places);
			return places;
		}catch(SQLException e) {
			System.out.println("fail to select all places with coordinates");
			e.printStackTrace();
		}
		return null;
	}
	
	
	ArrayList<String> select_all_places() {
		try {
			String database_name = null;
			String SELECT_RECORDS_SQL = "SELECT place FROM shop";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	resultSet = statement.executeQuery(SELECT_RECORDS_SQL);
        	String place = null;
        	ArrayList<String> places = new ArrayList<String>();
        	while(resultSet.next()) {
				place = resultSet.getString("place");
				//System.out.println("place: " + place);
				places.add(place);
        	}
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
			return places;
		}catch(SQLException e) {
			System.out.println("fail to select all places from SQL");
			e.printStackTrace();
			return null;
		}
	}
	
	
	void select_record_given_place(String place) {
		try {
			String database_name = null;
			String SELECT_RECORDS_SQL = "SELECT task_num, task, place, coordinate FROM shop"
					+ " WHERE place = " + "'" + place + "'" + " ";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(SELECT_RECORDS_SQL);
        	int task_num = 0; String p = null; String task = null; String coordinate = null;
        	while(resultSet.next()) {
				task_num = resultSet.getInt("task_num");
				task = resultSet.getString("task");
				p = resultSet.getString("place");
				coordinate = resultSet.getString("coordinate");
				System.out.print("task#: " + task_num);
				System.out.print("	task: " + task);
				System.out.print("	place: " + place);
				System.out.println("		coordinate: " + coordinate);
        	}
        	///
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
        	///
		}catch(SQLException e) {
			System.out.println("fail to delete records with place = " + place);
			e.printStackTrace();
		}
	}
	
	
	void select_record_given_task_num(int task_num) {
		try {
			String database_name = null;
			String SELECT_RECORD_SQL = "SELECT task_num, task, place, coordinate FROM shop"
					+ " WHERE task_num = " + task_num + " ";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(SELECT_RECORD_SQL);
        	String place = null; String task = null; String coordinate = null;
        	while(resultSet.next()) {
            	task = resultSet.getString("task");
            	place = resultSet.getString("place");
            	coordinate = resultSet.getString("coordinate");
				System.out.print("task#: " + task_num);
				System.out.print("	task: " + task);
				System.out.print("		place: " + place);
				System.out.print("		coordinate: " + coordinate);
        	}
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to delete record with task# = " + task_num);
			e.printStackTrace();
		}
	}
	
	
	void select_all_records() {
		try {
			String database_name = null;
			String SELECT_RECORD_SQL = "SELECT task_num, task, place, coordinate FROM shop";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
			resultSet = statement.executeQuery(SELECT_RECORD_SQL);
			while(resultSet.next()) {
				int task_num = resultSet.getInt("task_num");
				String task = resultSet.getString("task");
				String place = resultSet.getString("place");
				String coordinate = resultSet.getString("coordinate");
				///
				ArrayList<String> record = new ArrayList<String>();
				record.add(Integer.toString(task_num));
				record.add(place);
				record.add(task);
				record.add(coordinate);
				System.out.println(record);
				//System.out.println(String.format("task#: %d	place: %s		task: %s"
				//		+ "	deadline: %s    priority: %d	created on: %s", task_num, place, task, deadline, priority, created));
			}
			resultSet.close();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to select record");
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void insert_record(String place, String task) {
		try {
			int task_num = get_last_task_num();
			String database_name = null; 
			String INSERT_RECORD_SQL = String.format("INSERT INTO shop "
					+ "VALUES (%s, '%s', '%s', 'NULL')", task_num + 1, task, place);
			//
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	statement.executeUpdate(INSERT_RECORD_SQL);
        	System.out.printf("place: %s & task: %s inserted into MySQL \n", place, task);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to insert record");
			e.printStackTrace();
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void connect_to_database() {
    	String database_name = null;
    	try {
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
    	}catch(SQLException e1) {
    		System.out.println("fail to connect to SQL server");
    		e1.printStackTrace();
    	}
    }
    
    
    void create_table(String table_name) {
    	String database_name = null;
    	String CREATE_TABLE_SQL ="CREATE TABLE shop ("
    			+ "task_num INT,"  
    			+ "task VARCHAR(30),"
    			+ "place VARCHAR(30),"
    			+ "coordinate VARCHAR(30)"
    			+ "PRIMARY KEY (task_num))";
    	try {
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	statement.execute(CREATE_TABLE_SQL);
        	System.out.printf("%s created \n", table_name);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
    	}catch(SQLException e) {
    		System.out.printf("fail to create table %s", table_name);
    		e.printStackTrace();
    	}
    }
    
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
	int get_last_task_num() {
		int task_num = 0;
		try {
			String database_name = null;
			String SELECT_RECORD_SQL = "SELECT task_num, task, place, coordinate FROM shop";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
			resultSet = statement.executeQuery(SELECT_RECORD_SQL);
			if(resultSet.last()==true) {
				task_num = resultSet.getInt("task_num");
				System.out.println("last task_num is " + task_num);
			}
			resultSet.close();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
			return task_num;
		}catch(SQLException e) {
			System.out.println("fail to select last row");
			e.printStackTrace();
		}
		return task_num;
	}
    
    
    void drop_table(String table_name) {
    	String database_name = null;
    	String DROP_TABLE_SQL = "DROP TABLE " + table_name;
    	try {
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
    		statement = connection.createStatement();
    		statement.executeUpdate(DROP_TABLE_SQL);
    		System.out.printf("table %s dropped successfully \n", table_name);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
    	}catch(SQLException e) {
    		System.out.printf("fail to drop table %s \n", table_name);
    		e.printStackTrace();
    	}
    }
    
    
    void close_connection() {
		try {
			String database_name = connection.getCatalog();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e2) {
			System.out.println("fail to close connection");
			e2.printStackTrace();
		}
    }
    
    
    ///abandoned///
    /*
	void insert_record(String place, String task, int priority) {
		try {
			int task_num = get_last_task_num();
			String database_name = null;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			LocalDate localDate = LocalDate.now();
			String date = dtf.format(localDate); //2016/11/16
			date = date.replaceAll("/","-");
			String INSERT_RECORD_SQL = String.format("INSERT INTO shop_table "
					+ "VALUES (%s, '%s', '%s', NULL, %d, '%s')", task_num + 1, task, task, priority, date);
					//"INSERT INTO shop_table "
					//+ "VALUES (2, 'GolfTown', 'balls', NULL, 1, '2019-07-26')";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	statement.executeUpdate(INSERT_RECORD_SQL);
        	System.out.printf("place: %s & task: %s inserted into MySQL \n", place, task);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to insert record");
			e.printStackTrace();
		}
	}
	
		void insert_record(String place, String task, String deadline) {
		try {
			int task_num = get_last_task_num();
			String database_name = null;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			LocalDate localDate = LocalDate.now();
			String date = dtf.format(localDate); //2016/11/16
			date = date.replaceAll("/","-");
			String INSERT_RECORD_SQL = String.format("INSERT INTO shop_table "
					+ "VALUES (%s, '%s', '%s', '%s', NULL, '%s')", task_num + 1, place, task, deadline, date);
					//"INSERT INTO shop_table "
					//+ "VALUES (2, 'GolfTown', 'balls', '2019-09-01', 1, '2019-07-26')";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	statement.executeUpdate(INSERT_RECORD_SQL);
        	System.out.printf("place: %s & task: %s inserted into MySQL \n", place, task);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to insert record");
			e.printStackTrace();
		}
	}
	
	
		void insert_record(String place, String task, int priority, String deadline) {
		try {
			int task_num = get_last_task_num();
			String database_name = null;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			LocalDate localDate = LocalDate.now();
			String date = dtf.format(localDate); //2016/11/16
			date = date.replaceAll("/","-");
			String INSERT_RECORD_SQL = String.format("INSERT INTO shop_table "
					+ "VALUES (%s, '%s', '%s', '%s', %d, '%s')", task_num + 1, place, task, deadline, priority, date);
					//"INSERT INTO shop_table "
					//+ "VALUES (2, 'GolfTown', 'balls', '2019-09-01', 1, '2019-07-26')";
    		connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	statement.executeUpdate(INSERT_RECORD_SQL);
        	System.out.printf("place: %s & task: %s inserted into MySQL \n", place, task);
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to insert record");
			e.printStackTrace();
		}
	}
	
	
		void rank_by_deadline() {
		try {
			String database_name = null;
			String RANK_SQL = "SELECT task_num, place, task, deadline, priority, created FROM shop_table"
					+ " ORDER BY deadline DESC";
			connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(RANK_SQL);
        	int task_num = 0; String p = null; String task = null; java.sql.Date deadline = null; int priority = 0; java.sql.Date created = null;
        	while(resultSet.next()) {
				task_num = resultSet.getInt("task_num");
				p = resultSet.getString("place");
				task = resultSet.getString("task");
				deadline = resultSet.getDate("deadline");
				priority = resultSet.getInt("priority");
				created = resultSet.getDate("created");
				System.out.print("task#: " + task_num);
				System.out.print("	place: " + p);
				System.out.print("		task: " + task);
				System.out.print("		deadline: " + deadline);
				System.out.print("    priority: " + priority);
				System.out.println("	created on: " + created);
        	}
			resultSet.close();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to to rank by priority");
			e.printStackTrace();
		}
	}
	
	
	void rank_by_priority() {
		try {
			String database_name = null;
			String RANK_SQL = "SELECT task_num, place, task, deadline, priority, created FROM shop_table"
					+ " ORDER BY priority DESC";
			connection = DriverManager.getConnection(url, user, password);
    		database_name = connection.getCatalog();
    		System.out.println(String.format("connected to database %s successfully", database_name));
        	statement = connection.createStatement();
        	///
        	resultSet = statement.executeQuery(RANK_SQL);
        	int task_num = 0; String p = null; String task = null; java.sql.Date deadline = null; int priority = 0; java.sql.Date created = null;
        	while(resultSet.next()) {
				task_num = resultSet.getInt("task_num");
				p = resultSet.getString("place");
				task = resultSet.getString("task");
				deadline = resultSet.getDate("deadline");
				priority = resultSet.getInt("priority");
				created = resultSet.getDate("created");
				System.out.print("task#: " + task_num);
				System.out.print("	place: " + p);
				System.out.print("		task: " + task);
				System.out.print("		deadline: " + deadline);
				System.out.print("    priority: " + priority);
				System.out.println("	created on: " + created);
        	}
			resultSet.close();
			connection.close();
			System.out.printf("connection to database %s closed \n", database_name);
		}catch(SQLException e) {
			System.out.println("fail to to rank by priority");
			e.printStackTrace();
		}
	}
	*/
    
}
