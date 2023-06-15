package BancoPostgreSql;

import java.sql.*;
import javax.swing.JOptionPane;

public class Conexao {

	Connection con = null;
	public Statement stmt; 
	public ResultSet rs; 
	private String endereco;
	private String usuario;
	private String senha; 

	public void Conectar(String strEnd, String strUsuario, String strSenha) {
		endereco = strEnd; 
		usuario = strUsuario;
		senha = strSenha;
		JOptionPane.showMessageDialog(null, "Tentando realizar conexão só apresentando...");
		try {
			Class.forName("org.postgresql.Driver");

			con = DriverManager.getConnection(endereco, usuario, strSenha);

			stmt = con.createStatement();
			JOptionPane.showMessageDialog(null, "Banco conectado com sucesso");
		} catch (ClassNotFoundException cnfe) {
			JOptionPane.showMessageDialog(null, "Erro ao conectar o driver");
			cnfe.printStackTrace();

		} catch (SQLException sqlex) {
			JOptionPane.showMessageDialog(null, "erro na query");
			sqlex.printStackTrace();

		}
	}

	public void desconectar() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro ao desconectar o banco");
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String query) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = con.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return preparedStatement;
	}
}


