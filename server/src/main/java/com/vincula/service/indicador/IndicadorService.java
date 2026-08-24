package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.dto.indicador.IndicadorRankingDTO;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import com.vincula.exception.BusinessException;
import com.vincula.export.IndicadorExporter;
import com.vincula.service.ServidorService;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class IndicadorService {

    private final IndicadorProducaoService indicadorProducaoService;
    private final IndicadorProcessoService indicadorProcessoService;
    private final IndicadorResultadoService indicadorResultadoService;
    private final IndicadorMotivoService indicadorMotivoService;
    private final IndicadorComplementoService indicadorComplementoService;
    private final ServidorService servidorService;
    private final IndicadorExporter csvExporter;
    private final IndicadorRankingService indicadorRankingService;
    private final AuditoriaFacade auditoriaFacade;
    private final IndicadorPrazoService indicadorPrazoService;

    public IndicadorService(IndicadorProducaoService indicadorProducaoService,
                            IndicadorProcessoService indicadorProcessoService,
                            IndicadorResultadoService indicadorResultadoService,
                            IndicadorMotivoService indicadorMotivoService,
                            IndicadorComplementoService indicadorComplementoService,
                            IndicadorRankingService indicadorRankingService,
                            IndicadorPrazoService indicadorPrazoService,
                            IndicadorExporter csvExporter,
                            ServidorService servidorService,
                            AuditoriaFacade auditoriaFacade) {
        this.indicadorProducaoService = indicadorProducaoService;
        this.indicadorProcessoService = indicadorProcessoService;
        this.indicadorResultadoService = indicadorResultadoService;
        this.indicadorMotivoService = indicadorMotivoService;
        this.indicadorComplementoService = indicadorComplementoService;
        this.indicadorRankingService = indicadorRankingService;
        this.indicadorPrazoService = indicadorPrazoService;
        this.csvExporter = csvExporter;
        this.servidorService = servidorService;
        this.auditoriaFacade = auditoriaFacade;
    }

    public IndicadorDTO gerarIndicadores(FiltroIndicadorRequestDTO filtro) {
        Servidor servidor = servidorService.buscarServidorAutenticado();

        validarFiltro(filtro);
        aplicarRestricoesPerfil(filtro, servidor);

        List<IndicadorRankingDTO> rankingTotal = List.of();
        List<IndicadorRankingDTO> rankingResolucao = List.of();
        List<IndicadorRankingDTO> rankingTempo = List.of();
        List<IndicadorRankingDTO> rankingPrimeiraTentativa = List.of();

        if (servidor.getPerfil() == PerfilServidor.GESTAO_MUNICIPAL) {
            rankingTotal = indicadorRankingService.gerarRankingPorTotalDemandas();
            rankingResolucao = indicadorRankingService.gerarRankingPorPercentualResolucao();
            rankingTempo = indicadorRankingService.gerarRankingPorTempoMedioResolucao();
            rankingPrimeiraTentativa = indicadorRankingService.gerarRankingPorTempoAtePrimeiraTentativa();
        }

        return new IndicadorDTO(
                indicadorProducaoService.gerarIndicadores(filtro),
                indicadorProcessoService.gerarIndicadores(filtro),
                indicadorResultadoService.gerarIndicadores(filtro),
                indicadorMotivoService.gerarIndicadores(filtro),
                indicadorComplementoService.gerarIndicadores(filtro),
                indicadorPrazoService.gerarIndicadores(filtro),
                rankingTotal,
                rankingResolucao,
                rankingTempo,
                rankingPrimeiraTentativa
        );
    }

    public String exportarIndicadoresCsv(FiltroIndicadorRequestDTO filtro) {

        IndicadorDTO indicador = gerarIndicadores(filtro);

        auditoriaFacade.exportacaoCsvRealizada(
                "Indicador exportado"
        );

        return csvExporter.exportar(indicador);
    }


    private void validarFiltro(FiltroIndicadorRequestDTO filtro) {

        LocalDate inicio = filtro.getDataInicial();
        LocalDate fim = filtro.getDataFinal();

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new BusinessException("A data inicial deve ser anterior à data final.");
        }
    }

    private void aplicarRestricoesPerfil(FiltroIndicadorRequestDTO filtro, Servidor servidor) {

        switch (servidor.getPerfil()) {

            case SERVIDOR_APS ->
                filtro.setServicoResponsavelId(
                        servidor.getServico().getId()
                );


            case SOLICITANTE ->
                filtro.setServicoSolicitanteId(
                        servidor.getServico().getId()
                );

        }
    }
}