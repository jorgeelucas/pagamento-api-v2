package com.pagamento.service;

import com.pagamento.model.Pagamento;
import com.pagamento.repository.InMemoryPagamentoDB;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.pagamento.controller.PagamentoController.NovoPagamentoRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final InMemoryPagamentoDB repository;

    public Mono<Pagamento> save(NovoPagamentoRequest request) {
        log.info("Iniciando pagamento - {}", request);

        // criar novo id
        var novoId = UUID.randomUUID().toString();

        // transformar request na entidade
        var novoPagamento = Pagamento.builder()
                .id(novoId)
                .status(Pagamento.Status.PENDENTE)
                .usuarioId(request.getUsuarioId())
                .dataCriacao(Instant.now())
                .build();

        if (request.getUsuarioId().equals("123")) {
            throw new RuntimeException("Erro no pagamento usuario 123 bloqueado");
        }

        // salvar a entidade no banco de dados
        return Mono.fromCallable(() -> {
                    log.info("Persistindo novo pagamento no banco de dados");
                    return repository.save(novoId, novoPagamento);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }


    public Mono<Pagamento> get(String id) {
        log.info("Iniciando a busca de um pagamento pelo id - {}", id);
        return Mono.defer(() -> {
                    log.info("Buscando Pagamento na base de dados");
                    return Mono.justOrEmpty(repository.get(id));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Pagamento> getTodos() {
        log.info("Iniciando a busca de todos os pagamentos");
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Pagamento> getTodosPendentes() {
        log.info("Iniciando a busca de pagamentos pendentes");
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .filter(pagamento -> pagamento.getStatus().equals(Pagamento.Status.PENDENTE))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Pagamento> concluir(String id) {
        log.info("Inciando atualizacao de pagamento - {}", id);

        return Mono.defer(() -> {
            var pagamento = repository.get(id);
            pagamento.ifPresent(pg -> {
                pg.setStatus(Pagamento.Status.CONCLUIDO);
                pg.setDataModificacao(Instant.now());
                repository.save(id, pg);
            });

            return Mono.justOrEmpty(pagamento);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
