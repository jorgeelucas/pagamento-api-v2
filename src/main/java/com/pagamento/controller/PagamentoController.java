package com.pagamento.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pagamento.model.Pagamento;
import com.pagamento.service.PagamentoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService service;

    @GetMapping
    public Flux<Pagamento> obterTodos() {
        return Flux.defer(service::getTodos)
                .doOnComplete(() -> log.info("Elementos pendentes buscados com sucesso"));
    }

    @PostMapping
    public Mono<Pagamento> novoPagamento(@RequestBody NovoPagamentoRequest request){
        return Mono.defer(() -> service.save(request))
                .subscribeOn(Schedulers.parallel())
                .doOnError(err -> log.error("Erro no pagamento", err))
                .doOnNext(__ -> log.info("Pagamento salvo com sucesso"));
    }

    @GetMapping("/{id}")
    public Mono<Pagamento> get(@PathVariable("id") String id) {
        return Mono.defer(() -> service.get(id))
                .doOnError(err -> log.error("Erro na busca do pagamento ", err))
                .doOnNext(__ -> log.info("Pagamento encontrado com sucesso"));
    }

    @GetMapping("/pendentes")
    public Flux<Pagamento> obterTodosPendentes() {
        return Flux.defer(service::getTodosPendentes)
                .doOnComplete(() -> log.info("Elementos pendentes buscados com sucesso"));
    }


    @Data
    public static class NovoPagamentoRequest {
        @JsonProperty("usuario_id")
        private String usuarioId;
    }
}
