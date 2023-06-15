package BancoPostgreSql;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class EstacionamentoController {

	static Scanner scanner = new Scanner(System.in);

	private Connection conexao;

	public EstacionamentoController(Connection conexao) {
		this.conexao = conexao;
	}

	public void cadastrarCliente() {

		Random random = new Random();
		int clienteId = random.nextInt(Integer.MAX_VALUE);
		System.out.print("Digite o nome do cliente: ");
		String nome = scanner.nextLine();
		
		System.out.print("Digite o telefone do cliente: ");
		String telefone = scanner.nextLine();
		
		System.out.println("Cliente cadastrado com sucesso!");

		

		try {
			String inserir = "INSERT INTO cliente (cliente_id, nome_cliente, telefone_cliente) VALUES (?, ?, ?)";
			PreparedStatement con = conexao.prepareStatement(inserir);
			con.setInt(1, clienteId);
			con.setString(2, nome);
			con.setString(3, telefone);
			con.executeUpdate();
			
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public void cadastrarVeiculo() {
		Random random = new Random();
		int veiculo_id = random.nextInt(Integer.MAX_VALUE);

		try {
			System.out.print("Digite o nome do cliente dono do veiculo: ");
			String nomeCliente = scanner.nextLine();

			System.out.print("Digite o modelo do veiculo: ");
			String modelo = scanner.nextLine();

			System.out.print("Digite a placa do veiculo: ");
			String  placa = scanner.nextLine();

			System.out.println("Tipo de veiculo");
			String tipo = scanner.nextLine();


			
			int cliente_id = buscarClienteIdPorNome(nomeCliente);
			if (cliente_id != -1) {
				String inserir = "INSERT INTO veiculo (nome_cliente, modelo_veiculo, placa_veiculo, tipo_veiculo, veiculo_id ) VALUES (?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conexao.prepareStatement(inserir);
				pstmt.setString(1, nomeCliente);
				pstmt.setString(2, modelo);
				pstmt.setString(3, placa);
				pstmt.setString(4, tipo);
				pstmt.setInt(5, veiculo_id);
				pstmt.executeUpdate();

				System.out.println("Veiculo cadastrado com sucesso! ID: " + veiculo_id);
			} else {
				System.out.println("Cliente nao encontrado.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int buscarClienteIdPorNome(String nomeCliente) {
		try {
			String query = "SELECT cliente_id FROM cliente WHERE nome_cliente = ?";
			PreparedStatement con = conexao.prepareStatement(query);
			con.setString(1, nomeCliente);
			ResultSet retorno = con.executeQuery();

			if (retorno.next()) {
				return retorno.getInt("cliente_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1; 
	}

	public void registrarEntrada() {
		try {
			System.out.print("Digite o nome do cliente: ");
			String nomeCliente = scanner.nextLine();

			int clienteId = buscarClienteIdPorNome(nomeCliente);

			if (clienteId != -1) {
				String placaVeiculo = buscarPlacaPorCliente(nomeCliente);

				if (placaVeiculo != null) {
					System.out.print("Digite o nome da vaga: ");
					String nomeVaga = scanner.nextLine();
					scanner.nextLine();

					boolean vagaDisponivel = verificarDisponibilidadeDaVaga(nomeVaga);

					if (vagaDisponivel) {
						String inserir = "INSERT INTO registro (veiculo_placa, cliente_id, vaga_id, hora_entrada, nome_cliente) VALUES (?, ?, ?, ?, ?)";

						String consultarIdVaga = "SELECT vaga_id FROM vaga WHERE nome_vaga = ?";
						PreparedStatement IdVaga = conexao.prepareStatement(consultarIdVaga);
						IdVaga.setString(1, nomeVaga);
						ResultSet rsIdVaga = IdVaga.executeQuery();

						if (rsIdVaga.next()) {
							int vagaId = rsIdVaga.getInt("vaga_id");

							LocalDateTime horaEntrada = LocalDateTime.now();
							PreparedStatement con = conexao.prepareStatement(inserir);
							con.setString(1, placaVeiculo);
							con.setInt(2, clienteId);
							con.setInt(3, vagaId);
							con.setObject(4, horaEntrada);
							con.setObject(5, nomeCliente);
							con.executeUpdate();

							String atualizarVaga = "UPDATE vaga SET ocupado_vaga = true WHERE vaga_id = ?";
							PreparedStatement AtualizarVaga = conexao.prepareStatement(atualizarVaga);
							AtualizarVaga.setInt(1, vagaId);
							AtualizarVaga.executeUpdate();

							System.out.println("Entrada registrada com sucesso!");
						} else {
							System.out.println("Vaga não encontrada!");
						}
					} else {
						System.out.println("A vaga informada está ocupada ou não existe!");
					}
				} else {
					System.out.println("Cliente não possui veículos registrados.");
				}
			} else {
				System.out.println("Cliente não encontrado!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private boolean verificarDisponibilidadeDaVaga(String nomeVaga) {
		try {
			String consultarDisponibilidade = "SELECT ocupado_vaga FROM vaga WHERE nome_vaga = ?";
			PreparedStatement con = conexao.prepareStatement(consultarDisponibilidade);
			con.setString(1, nomeVaga);
			ResultSet Disponibilidade = con.executeQuery();

			if (Disponibilidade.next()) {
				boolean ocupada = Disponibilidade.getBoolean("ocupado_vaga");
				return !ocupada; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; 
	}

	public void registroSaida() {
		try {
			System.out.print("Digite o nome do cliente: ");
			String nomeCliente = scanner.nextLine();

			String placaVeiculo = buscarPlacaPorCliente(nomeCliente);

			if (placaVeiculo != null) {
				String consultarRegistro = "SELECT * FROM registro WHERE veiculo_placa = ? AND hora_saida IS NULL";
				PreparedStatement con = conexao.prepareStatement(consultarRegistro);
				con.setString(1, placaVeiculo);
				ResultSet ConsultarRegistro = con.executeQuery();

				if (ConsultarRegistro.next()) {
					int registroId = ConsultarRegistro.getInt("registro_id");
					LocalDateTime horaSaida = LocalDateTime.now();

					String atualizarRegistro = "UPDATE registro SET hora_saida = ? WHERE registro_id = ?";
					PreparedStatement con2 = conexao.prepareStatement(atualizarRegistro);
					con2.setObject(1, horaSaida);
					con2.setInt(2, registroId);
					con2.executeUpdate();

					int vagaId = ConsultarRegistro.getInt("vaga_id");

					String atualizarVaga = "UPDATE vaga SET ocupado_vaga = false WHERE vaga_id = ?";
					PreparedStatement con3 = conexao.prepareStatement(atualizarVaga);
					con3.setInt(1, vagaId);
					con3.executeUpdate();

					System.out.println("Saída registrada com sucesso!");
				} else {
					System.out.println("Veículo não encontrado ou já registrou a saída.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public String buscarPlacaPorCliente(String nomeCliente) {
		try {
			String query = "SELECT placa_veiculo FROM veiculo WHERE nome_cliente = ?";
			PreparedStatement con = conexao.prepareStatement(query);
			con.setString(1, nomeCliente);
			ResultSet rs = con.executeQuery();

			List<String> placas = new ArrayList<>();

			while (rs.next()) {
				placas.add(rs.getString("placa_veiculo"));
			}

			if (placas.isEmpty()) {
				System.out.println("Cliente não possui veículos registrados.");
				return null;
			} else if (placas.size() == 1) {
				return placas.get(0);
			} else {
				System.out.println("Cliente possui mais de um veículo registrado. Escolha a placa:");
				for (int i = 0; i < placas.size(); i++) {
					System.out.println((i + 1) + ". " + placas.get(i));
				}

				int opcao;
				do {
					System.out.print("Digite o número correspondente à placa: ");
					opcao = scanner.nextInt();
				} while (opcao < 1 || opcao > placas.size());

				return placas.get(opcao - 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void verificarEstadoVagas() {
		try {
			String query = "SELECT v.nome_vaga, v.ocupado_vaga, r.veiculo_placa, c.nome_cliente FROM vaga v LEFT JOIN registro r ON v.vaga_id = r.vaga_id AND r.hora_saida IS NULL LEFT JOIN veiculo c ON r.veiculo_placa = c.placa_veiculo";
			Statement con = conexao.createStatement();
			ResultSet resultado = con.executeQuery(query);

			System.out.println("Estado das vagas:");

			while (resultado.next()) {
				String nomeVaga = resultado.getString("nome_vaga");
				boolean ocupada = resultado.getBoolean("ocupado_vaga");
				String placaVeiculo = resultado.getString("veiculo_placa");
				String nomeCliente = resultado.getString("nome_cliente");

				System.out.print(nomeVaga + ": ");
				if (ocupada) {
					System.out.println("Ocupada - Placa: " + placaVeiculo + ", Dono: " + nomeCliente);
					System.out.println("____________________________________________");
				} else {
					System.out.println("Livre");
					System.out.println("____________________________________________");
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void excluirCliente() {
	    try {
	        System.out.print("Digite o nome do cliente que deseja excluir: ");
	        String nomeCliente = scanner.nextLine();

	        int clienteId = buscarClienteIdPorNome(nomeCliente);

	        if (clienteId != -1) {
	            
	            excluirVeiculosDoCliente(clienteId);

	           
	            String excluirCliente = "DELETE FROM cliente WHERE cliente_id = ?";
	            PreparedStatement pstmtExcluirCliente = conexao.prepareStatement(excluirCliente);
	            pstmtExcluirCliente.setInt(1, clienteId);
	            pstmtExcluirCliente.executeUpdate();

	            System.out.println("Cliente e seus veículos foram excluídos com sucesso!");
	        } else {
	            System.out.println("Cliente não encontrado!");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private void excluirVeiculosDoCliente(int clienteId) throws SQLException {
	    String excluirVeiculos = "DELETE FROM veiculo WHERE cliente_id = ?";
	    PreparedStatement con = conexao.prepareStatement(excluirVeiculos);
	    con.setInt(1, clienteId);
	    con.executeUpdate();
	}


	public void listarClientes() {
		try {
			String listar = "SELECT * FROM cliente";
			Statement con = conexao.createStatement();
			ResultSet resultado = con.executeQuery(listar);

			System.out.println("Lista de Clientes:");
			while (resultado.next()) {
				String nomeCliente = resultado.getString("nome_cliente");
				String telefoneCliente = resultado.getString("telefone_cliente");

				System.out.println(" [Nome| " + "|"+nomeCliente+"|" + "|Telefone|" + "|"+telefoneCliente+"]");
				System.out.println("_____________________________________________");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
