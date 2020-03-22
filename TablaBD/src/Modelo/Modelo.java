package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

import Vista.Vista;

public class Modelo {
	private Vista miVista;

	private String bd;
	private String login;
	private String pwd;
	private String url;
	private String driver;
	private String sqlTabla1;
	private String sqlTabla2;
	private Scanner sc;
	private Connection conexion;

	private DefaultTableModel miTabla;

	public Modelo() {
		try {
			sc = new Scanner (System.in);
			eligeConexion();	
			Class.forName(driver);
			conexion = DriverManager.getConnection(url, login, pwd);
		} catch (SQLException e) {
			System.err.println("Error BD: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.err.println("Error General: " + e.getMessage());
			System.exit(2);
		}
		eligeMetodo();	
	}
	
	private void eligeConexion() {
		String respuesta;
		do {
			System.out.print("Conectar con MySQL (m) o con Oracle (o): ");
			respuesta = sc.next();
			if (respuesta.equalsIgnoreCase("m")) 
				conectaMySQL();
			if (respuesta.equalsIgnoreCase("o")) 
				conectaOracle();
		} while (!respuesta.equalsIgnoreCase("m") && 
				!respuesta.equalsIgnoreCase("o"));	
	}

	private void eligeMetodo() {
		String respuesta;
		do {
			System.out.print("Elige metodo 1 (1) o metodo 2 (2): ");
			respuesta = sc.next();
			if (respuesta.equalsIgnoreCase("1")) 
				cargarTabla1();
			if (respuesta.equalsIgnoreCase("2")) 
				cargarTabla2();
		} while (!respuesta.equalsIgnoreCase("1") && 
				!respuesta.equalsIgnoreCase("2"));	
	}
	
	private void conectaMySQL () {
		bd = "world";
		login = "root";
		pwd = "root";
		url = "jdbc:mysql://localhost/" + bd
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		driver = "com.mysql.cj.jdbc.Driver";
		sqlTabla1 = "Select * from countrylanguage";
		sqlTabla2 = "Select * from city";
	}
	
	private void conectaOracle () {
		bd = "";
		login = "SYSTEM";
		pwd = "root";
		url = "jdbc:oracle:thin:@localhost:1521:XE";
		driver = "com.mysql.cj.jdbc.Driver";
		sqlTabla1 = "Select * from pedro.empleados";
		sqlTabla2 = "Select * from pedro.productos";
	}

	private void cargarTabla1() {
		int numColumnas = getNumColumnas(sqlTabla1);
		int numFilas = getNumFilas(sqlTabla1);

		String[] cabecera = new String[numColumnas];

		Object[][] contenido = new Object[numFilas][numColumnas];
		PreparedStatement pstmt;
		try {
			pstmt = conexion.prepareStatement(sqlTabla1);
			ResultSet rset = pstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			for (int i = 0; i < numColumnas; i++) {
				cabecera[i] = rsmd.getColumnName(i+1);
			}
			int fila = 0;
			while (rset.next()) {
				for (int col = 1; col <= numColumnas; col++) {
					contenido[fila][col - 1] = rset.getString(col);
				}
				fila++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		miTabla = new DefaultTableModel(contenido, cabecera);
	}

	private void cargarTabla2() {
		miTabla = new DefaultTableModel();
		int numColumnas = getNumColumnas(sqlTabla2);
		Object[] contenido = new Object[numColumnas];
		PreparedStatement pstmt;
		try {
			pstmt = conexion.prepareStatement(sqlTabla2);
			ResultSet rset = pstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			for (int i = 0; i < numColumnas; i++) {
				miTabla.addColumn(rsmd.getColumnName(i+1));
			}
			while (rset.next()) {
				for (int col = 1; col <= numColumnas; col++) {
					contenido[col - 1] = rset.getString(col);
				}
				miTabla.addRow(contenido);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private int getNumColumnas(String sql) {
		int num = 0;
		try {
			PreparedStatement pstmt = conexion.prepareStatement(sql);
			ResultSet rset = pstmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			num = rsmd.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}

	private int getNumFilas(String sql) {
		int numFilas = 0;
		try {
			PreparedStatement pstmt = conexion.prepareStatement(sql);
			ResultSet rset = pstmt.executeQuery();
			while (rset.next())
				numFilas++;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numFilas;
	}

	public DefaultTableModel getTabla() {
		return miTabla;
	}

	public void setVista(Vista miVista) {
		this.miVista = miVista;
	}

}
