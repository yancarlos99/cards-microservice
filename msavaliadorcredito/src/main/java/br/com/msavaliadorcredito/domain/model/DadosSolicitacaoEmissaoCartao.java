package br.com.msavaliadorcredito.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosSolicitacaoEmissaoCartao {
	
	private Long idCartao;
	private String cpf;
	private String endereco;
	private BigDecimal limiteLiberado;
	

}
