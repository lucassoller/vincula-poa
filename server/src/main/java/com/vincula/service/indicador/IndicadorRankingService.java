package com.vincula.service.indicador;

import com.vincula.dto.projection.RankingQuantidadeProjection;
import com.vincula.dto.IndicadorRankingDTO;
import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.entity.Usuario;
import com.vincula.enums.PerfilUsuario;
import com.vincula.exception.BusinessException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import com.vincula.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class IndicadorRankingService {

    private final DemandaRepository demandaRepository;
    private final UsuarioService usuarioService;
    private final TentativaContatoRepository tentativaContatoRepository;

    public IndicadorRankingService(DemandaRepository demandaRepository,
                                   UsuarioService usuarioService,
                                   TentativaContatoRepository tentativaContatoRepository) {
        this.demandaRepository = demandaRepository;
        this.usuarioService = usuarioService;
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


    private double arredondar(Double valor) {
        if (valor == null) {
            return 0.0;
        }

        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private void validarAcessoGestao() {
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        if (usuario.getPerfil() != PerfilUsuario.GESTAO_MUNICIPAL) {
            throw new BusinessException("Usuário não pode acessar ranking de unidades");
        }
    }

    private String formatarTempo(Double totalSegundos) {
        if (totalSegundos == null || totalSegundos <= 0) {
            return "0h 0m 0s";
        }

        long segundosTotais = Math.round(totalSegundos);

        long horas = segundosTotais / 3600;
        long minutos = (segundosTotais % 3600) / 60;
        long segundos = segundosTotais % 60;

        return horas + "h " + minutos + "m " + segundos + "s";
    }
}