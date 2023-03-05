package br.com.msavaliadorcredito.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.msavaliadorcredito.domain.model.Cartao;
import br.com.msavaliadorcredito.domain.model.CartaoAprovado;
import br.com.msavaliadorcredito.domain.model.CartaoCliente;
import br.com.msavaliadorcredito.domain.model.DadosCliente;
import br.com.msavaliadorcredito.domain.model.DadosSolicitacaoEmissaoCartao;
import br.com.msavaliadorcredito.domain.model.ProtocoloSolicitacaoCartao;
import br.com.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import br.com.msavaliadorcredito.domain.model.SituacaoCliente;
import br.com.msavaliadorcredito.ex.DadosClienteNotFoundException;
import br.com.msavaliadorcredito.ex.ErroComunicacaoMicroServicesExcepetion;
import br.com.msavaliadorcredito.ex.ErroSolicitacaoCartaoException;
import br.com.msavaliadorcredito.infra.clients.CartaoResourceClient;
import br.com.msavaliadorcredito.infra.clients.ClienteResourceClient;
import br.com.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {
	
	private final ClienteResourceClient clientesClient;
	private final CartaoResourceClient cartaoClient;
	private final SolicitacaoEmissaoCartaoPublisher solicitacaoEmissaoCartaoPublisher;

	public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroServicesExcepetion {
		try {
			
		ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
		ResponseEntity<List<CartaoCliente>> cartoesByClientes = cartaoClient.getCartoesByClientes(cpf);
		
		return SituacaoCliente.builder()
				.cliente(dadosClienteResponse.getBody())
				.cartoes(cartoesByClientes.getBody())
				.build();
		}catch (FeignException.FeignClientException e) {
			int status = e.status();
			if(HttpStatus.NOT_FOUND.value() == status) {
				throw new DadosClienteNotFoundException();
			}
			throw new ErroComunicacaoMicroServicesExcepetion(e.getMessage(), e.status());
		}
	}
	
	public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroServicesExcepetion {
		
		try {
			
			ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
			ResponseEntity<List<Cartao>> cartoesResponse = cartaoClient.getCartoesRendaAteh(renda);
			
			List<Cartao> cartoes = cartoesResponse.getBody();
			
			var listaCartoesAprovado = cartoes.stream().map(cartao -> {
				DadosCliente dadosCliente = dadosClienteResponse.getBody();
				
				BigDecimal limiteBasico = cartao.getLimiteBasico();
				BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
				var fator = idadeBD.divide(BigDecimal.valueOf(10));
				BigDecimal limiteAprovado = fator.multiply(limiteBasico);
				
				CartaoAprovado aprovado = new CartaoAprovado();
				aprovado.setCartao(cartao.getNome());
				aprovado.setBandeira(cartao.getBandeira());
				aprovado.setLimiteAprovado(limiteAprovado);
				
				return aprovado;
			}).collect(Collectors.toList());
			
			return new RetornoAvaliacaoCliente(listaCartoesAprovado);
			
			}catch (FeignException.FeignClientException e) {
				int status = e.status();
				if(HttpStatus.NOT_FOUND.value() == status) {
					throw new DadosClienteNotFoundException();
				}
				throw new ErroComunicacaoMicroServicesExcepetion(e.getMessage(), e.status());
			}
	}
	
	public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados) {
		try {
			solicitacaoEmissaoCartaoPublisher.solicitarCartao(dados);
			var protocolo = UUID.randomUUID().toString();
			return new ProtocoloSolicitacaoCartao(protocolo);
		}catch (Exception e) {
			throw new ErroSolicitacaoCartaoException(e.getMessage());
		}
	}

}
