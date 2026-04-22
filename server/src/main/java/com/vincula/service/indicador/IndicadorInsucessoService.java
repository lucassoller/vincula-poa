package com.vincula.service.indicador;

import com.vincula.dto.MotivoQuantidadeDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorInsucessoService {

    private final DemandaRepository demandaRepository;

    public IndicadorInsucessoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucesso() {
        return demandaRepository.listarPrincipaisMotivosInsucesso()
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucessoPorUnidade(Long unidadeSaudeId) {
        return demandaRepository.listarPrincipaisMotivosInsucessoPorUnidade(unidadeSaudeId)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucessoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisMotivosInsucessoPorPeriodo(inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucessoPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisMotivosInsucessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }
}