package com.vincula.service;

import com.vincula.entity.Servico;
import com.vincula.repository.ServicoRepository;
import com.vincula.repository.TerritorioUbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TerritorializacaoService {

    private final TerritorioUbsRepository territorioUbsRepository;
    private final ServicoRepository servicoRepository;

    public Servico buscarUbsPorCoordenada(Double latitude, Double longitude) {

        return territorioUbsRepository
                .buscarServicoIdPorCoordenada(latitude, longitude)
                .flatMap(servicoRepository::findById)
                .orElse(null);
    }
}