package br.com.mscartoes.infra.mqueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.mscartoes.domain.Cartao;
import br.com.mscartoes.domain.ClienteCartao;
import br.com.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import br.com.mscartoes.infra.repository.CartaoRepository;
import br.com.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EmissaoCartaoSubscriber {
	
	private final CartaoRepository cartaoRepository;
	private final ClienteCartaoRepository clienteCartaoRepository;
	
	@RabbitListener(queues = "${mq.queues.emissao-cartoes}")
	public void receberSolicitacaoEmissao(@Payload String payload) {
		try {
			var mapper = new ObjectMapper();
			DadosSolicitacaoEmissaoCartao dados = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
			
			Cartao cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();
			
			ClienteCartao clienteCartao = new ClienteCartao();
			clienteCartao.setCartao(cartao);
			clienteCartao.setCpf(dados.getCpf());
			clienteCartao.setLimite(dados.getLimiteLiberado());
			
			clienteCartaoRepository.save(clienteCartao);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
