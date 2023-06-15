package BancoPostgreSql;

public class Cliente {
	
	private int id;
	private String nome;
	private String telefone;
	private String placaVeiculo;

	public Cliente(int id, String nome, String telefone, String placaVeiculo) {
		this.id = id;
		this.nome = nome;
		this.telefone = telefone;
		this.placaVeiculo = placaVeiculo;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getPlacaVeiculo() {
		return placaVeiculo;
	}
}
