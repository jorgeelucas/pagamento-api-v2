package com.pagamento.jobs;

import com.pagamento.model.Pagamento;
import com.pagamento.repository.InMemoryPagamentoDB;
import com.pagamento.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcluirPagamentoJob implements InitializingBean {

    private final PagamentoService service;

    @Override
    public void afterPropertiesSet() throws Exception {
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            Flux.defer(service::getTodosPendentes)
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(Pagamento::getId)
                    .flatMap(service::concluir)
                    .doOnNext(id -> log.info("[JOB] - Concluindo pagamento - {}", id))
                    .doOnComplete(() -> log.info("[JOB] - Todos os pagamentos em PENDENTE foram atualizados com sucesso"))
                    .subscribe();
        }, 1, 45, TimeUnit.SECONDS);
    }

}
