package BancoPostgreSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class TestaConexaoBd {

	static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {




		String driver = "org.postgresql.Driver";
		String user   = "postgres";
		String senha = "1234";
		String url      = "jdbc:postgresql://localhost:5432/EstacionamentoIDN";

		try {
			Class.forName(driver);
			Connection con = null;
			con = DriverManager.getConnection(url, user, senha);
			System.out.println("Conexão realizada com sucesso.");
			EstacionamentoController estacionamento = new EstacionamentoController(con);
			menu(estacionamento);

		}catch(Exception e)
		{
			System.err.print(e.getMessage());
		} scanner.close();
	}


	public static void menu(EstacionamentoController estacionamento) {
		int opcao; 
		do {
			System.out.println("1. Cadastrar Cliente");
			System.out.println("2. Cadastrar Veículo");
			System.out.println("3. Registrar Entrada");
			System.out.println("4. Registrar saída");
			System.out.println("5. Listar Clientes");
			System.out.println("6. Verificar Vagas");
			System.out.println("7. Excluir cliente do sistema");
			System.out.println("8. Sair");
			System.out.print("Escolha uma opção: ");
			opcao = scanner.nextInt();
			

			
			switch (opcao) {
			case 1:               	
				estacionamento.cadastrarCliente();
				break;
			case 2:

				estacionamento.cadastrarVeiculo();
				break;
			case 3:
				estacionamento.registrarEntrada();

				break;
			case 4:
				estacionamento.registroSaida(); 

				break;
			case 5:
				estacionamento.listarClientes();

				break;
			case 6:
				estacionamento.verificarEstadoVagas();

				break;
			case 7:
				estacionamento.excluirCliente();
				break;
			case 8:
				System.out.println("Encerrar sistema");
				
				
			default:
				System.out.println("Opção inválida!");
				break;
				
			}
			
		} while (opcao != 8);
		
	}
}