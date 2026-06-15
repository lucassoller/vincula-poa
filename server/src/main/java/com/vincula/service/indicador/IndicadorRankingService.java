package com.vincula.service.indicador;

import com.vincula.dto.projection.RankingQuantidadeProjection;
import com.vincula.dto.indicador.IndicadorRankingDTO;
import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import com.vincula.exception.BusinessException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import com.vincula.service.ServidorService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.vincula.util.IndicadorUtil.arredondar;
import static com.vincula.util.IndicadorUtil.formatarTempo;

@Service
public class IndicadorRankingService {

    private final DemandaRepository demandaRepository;
    private final ServidorService servidorService;
    private final TentativaContatoRepository tentativaContatoRepository;

    public IndicadorRankingService(DemandaRepository demandaRepository,
                                   ServidorService servidorService,
                                   TentativaContatoRepository tentativaContatoRepository) {
        this.demandaRepository = demandaRepository;
        this.servidorService = servidorService;
        this.tentativaContatoRepository = tentativaContatoRepository;
    }

    public List<IndicadorRankingDTO> rankingPorTotalDemandas() {
        validarAcessoGestao();

        return demandaRepository.rankingUnidadesPorTotalDemandas()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<IndicadorRankingDTO> rankingPorPercentualResolucao() {
        validarAcessoGestao();

        return demandaRepository.rankingUnidadesPorPercentualResolucao()
                .stream()
                .map(this::toDTONumerico)
                .toList();
    }

    public List<IndicadorRankingDTO> rankingPorTempoMedioResolucao() {
        validarAcessoGestao();

        return demandaRepository.rankingUnidadesPorTempoMedioResolucao()
                .stream()
                .map(this::toDTOTempo)
                .toList();
    }

    public List<IndicadorRankingDTO> rankingPorTempoAtePrimeiraTentativa() {
        validarAcessoGestao();

        return tentativaContatoRepository.rankingUnidadesPorTempoAtePrimeiraTentativa()
                .stream()
                .map(this::toDTOTempo)
                .toList();
    }

    private IndicadorRankingDTO toDTONumerico(RankingValorProjection item) {
        return new IndicadorRankingDTO(
                item.getUnidadeSaudeId(),
                item.getUnidadeSaudeNome(),
                arredondar(item.getValor())
        );
    }

    private IndicadorRankingDTO toDTOTempo(RankingValorProjection item) {
        return new IndicadorRankingDTO(
                item.getUnidadeSaudeId(),
                item.getUnidadeSaudeNome(),
                formatarTempo(item.getValor())
        );
    }

    private IndicadorRankingDTO toDTO(RankingQuantidadeProjection item) {
        return new IndicadorRankingDTO(
                item.getUnidadeSaudeId(),
                item.getUnidadeSaudeNome(),
                item.getValor() == null ? 0.0 : item.getValor().doubleValue()
        );
    }


    private void validarAcessoGestao() {
        Servidor servidor = servidorService.buscarServidorAutenticado();

        if (servidor.getPerfil() != PerfilServidor.GESTAO_MUNICIPAL) {
            throw new BusinessException("Servidor não pode acessar ranking de unidades");
        }
    }
}