package br.com.msavaliadorcredito.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.msavaliadorcredito.domain.model.DadosAvaliacao;
import br.com.msavaliadorcredito.domain.model.DadosSolicitacaoEmissaoCartao;
import br.com.msavaliadorcredito.domain.model.ProtocoloSolicitacaoCartao;
import br.com.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import br.com.msavaliadorcredito.domain.model.SituacaoCliente;
import br.com.msavaliadorcredito.ex.DadosClienteNotFoundException;
import br.com.msavaliadorcredito.ex.ErroComunicacaoMicroServicesExcepetion;
import br.com.msavaliadorcredito.ex.ErroSolicitacaoCartaoException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/avaliador-credito")
public class AvaliadorCreditoController {
	
	private final AvaliadorCreditoService avaliadorCreditoService;
	
	@GetMapping(value = "situacao-cliente", params = "cpf")
	public ResponseEntity consultarSituacaoCliente(@RequestParam("cpf") String cpf){
		try {
			SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
			return ResponseEntity.ok(situacaoCliente);
		} catch (DadosClienteNotFoundException e) {
			return ResponseEntity.notFound().build();
		}catch (ErroComunicacaoMicroServicesExcepetion e) {
			return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados) {
		try {
			RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.realizarAvaliacao(dados.getCpf(), dados.getRenda());
			return ResponseEntity.ok(retornoAvaliacaoCliente);
		} catch (DadosClienteNotFoundException e) {
			return ResponseEntity.notFound().build();
		}catch (ErroComunicacaoMicroServicesExcepetion e) {
			return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
		}
	}
	
	@PostMapping("solicitacoes-cartao")
	public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados) {
		try {
			ProtocoloSolicitacaoCartao solicitarEmissaoCartao = avaliadorCreditoService.solicitarEmissaoCartao(dados);
			return ResponseEntity.ok(solicitarEmissaoCartao);
		} catch (ErroSolicitacaoCartaoException e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

}
