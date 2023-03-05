package br.com.mscartoes.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.mscartoes.domain.ClienteCartao;
import br.com.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
	
	private final ClienteCartaoRepository repository;
	
	public List<ClienteCartao> listCartoesByCpf(String cpf){
		return repository.findByCpf(cpf);
	}

}
