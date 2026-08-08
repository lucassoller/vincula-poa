package com.vincula.service.indicador;

import com.vincula.dto.projection.RankingQuantidadeProjection;
import com.vincula.dto.indicador.IndicadorRankingDTO;
import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.vincula.util.IndicadorUtil.arredondar;
import static com.vincula.util.IndicadorUtil.formatarTempo;

@Service
public class IndicadorRankingService {

    private final DemandaRepository demandaRepository;
    private final TentativaContatoRepository tentativaContatoRepository;

    public IndicadorRankingService(DemandaRepository demandaRepository,
                                   TentativaContatoRepository tentativaContatoRepository) {
        this.demandaRepository = demandaRepository;
        this.tentativaContatoRepository = tentativaContatoRepository;
    }

    public List<IndicadorRankingDTO> gerarRankingPorTotalDemandas() {
        return demandaRepository.rankingServicosPorTotalDemandas()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<IndicadorRankingDTO> gerarRankingPorPercentualResolucao() {
        return demandaRepository.rankingServicosPorPercentualResolucao()
                .stream()
                .map(this::toDTONumerico)
                .toList();
    }

    public List<IndicadorRankingDTO> gerarRankingPorTempoMedioResolucao() {
        return demandaRepository.rankingServicosPorTempoMedioResolucao()
                .stream()
                .map(this::toDTOTempo)
                .toList();
    }

    public List<IndicadorRankingDTO> gerarRankingPorTempoAtePrimeiraTentativa() {
        return tentativaContatoRepository.rankingServicosPorTempoAtePrimeiraTentativa()
                .stream()
                .map(this::toDTOTempo)
                .toList();
    }

    private IndicadorRankingDTO toDTO(RankingQuantidadeProjection item) {
        return new IndicadorRankingDTO(
                item.getServicoId(),
                item.getServicoNome(),
                item.getValor() == null ? 0.0 : item.getValor().doubleValue()
        );
    }

    private IndicadorRankingDTO toDTONumerico(RankingValorProjection item) {
        return new IndicadorRankingDTO(
                item.getServicoId(),
                item.getServicoNome(),
                arredondar(item.getValor())
        );
    }

    private IndicadorRankingDTO toDTOTempo(RankingValorProjection item) {
        return new IndicadorRankingDTO(
                item.getServicoId(),
                item.getServicoNome(),
                formatarTempo(item.getValor())
        );
    }
}