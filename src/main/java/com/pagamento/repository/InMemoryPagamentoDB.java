package com.pagamento.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.model.Pagamento;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryPagamentoDB {

    private static final Map<String, String> DATABASE =
            new ConcurrentHashMap<>();

    private final ObjectMapper mapper;

    @SneakyThrows
    public Pagamento save(String chave, Pagamento pagamento) {

        final var pagamentoEmString =
            mapper.writeValueAsString(pagamento);

        Thread.sleep(500);
        DATABASE.put(chave, pagamentoEmString);

        return pagamento;
    }

    @SneakyThrows
    public Optional<Pagamento> get(String chave) {
        final var json = DATABASE.get(chave);

        Thread.sleep(200);
        return Optional.ofNullable(json)
                .map(payload -> transformar(payload, Pagamento.class));

    }

    @SneakyThrows
    public List<Pagamento> findAll() {
        Thread.sleep(500);
        return DATABASE.values()
                .stream()
                .map(payload -> transformar(payload, Pagamento.class))
                .toList();
    }

    private <T> T transformar(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro no mapper");
        }
    }

}
