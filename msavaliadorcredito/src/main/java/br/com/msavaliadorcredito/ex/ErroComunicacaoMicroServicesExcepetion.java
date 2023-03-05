package br.com.msavaliadorcredito.ex;

import lombok.Getter;

public class ErroComunicacaoMicroServicesExcepetion extends Exception {
	
	@Getter
	private Integer status;
	
	public ErroComunicacaoMicroServicesExcepetion(String msg, Integer status) {
		super(msg);
		this.status = status;
	}

}
