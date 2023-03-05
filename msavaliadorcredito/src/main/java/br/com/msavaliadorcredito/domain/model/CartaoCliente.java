package br.com.msavaliadorcredito.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CartaoCliente {
	private String nome;
	private String bandeira;
	private BigDecimal limiteLiberado;

}
