package br.com.api.clientes.application.representation;

import br.com.api.clientes.domain.Cliente;
import lombok.Data;

@Data
public class ClienteSaveRequest {
	
	private String cpf;
	private String nome;
	private Integer idade;
	
	public Cliente toModel() {
		return new Cliente(cpf, nome, idade);
	}

}
