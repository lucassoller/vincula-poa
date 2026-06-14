package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import com.vincula.exception.BusinessException;
import com.vincula.export.IndicadorExporter;
import com.vincula.service.ServidorService;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorService {

    private final IndicadorProducaoService indicadorProducaoService;
    private final IndicadorProcessoService indicadorProcessoService;
    private final IndicadorResultadoService indicadorResultadoService;
    private final IndicadorInsucessoService indicadorInsucessoService;
    private final ServidorService servidorService;
    private final IndicadorExporter csvExporter;
    private final IndicadorRankingService indicadorRankingService;
    private final AuditoriaFacade auditoriaFacade;
    private final IndicadorPrazoService indicadorPrazoService;

    public IndicadorService(IndicadorProducaoService indicadorProducaoService,
                            IndicadorProcessoService indicadorProcessoService,
                            IndicadorResultadoService indicadorResultadoService,
                            IndicadorInsucessoService indicadorInsucessoService,
                            IndicadorRankingService indicadorRankingService,
                            IndicadorPrazoService indicadorPrazoService,
                            IndicadorExporter csvExporter,
                            ServidorService servidorService,
                            AuditoriaFacade auditoriaFacade) {
        this.indicadorProducaoService = indicadorProducaoService;
        this.indicadorProcessoService = indicadorProcessoService;
        this.indicadorResultadoService = indicadorResultadoService;
        this.indicadorInsucessoService = indicadorInsucessoService;
        this.indicadorRankingService = indicadorRankingService;
        this.indicadorPrazoService = indicadorPrazoService;
        this.csvExporter = csvExporter;
        this.servidorService = servidorService;
        this.auditoriaFacade = auditoriaFacade;
    }

    public IndicadorDTO indicadorGeral(){
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresGerais(),
                indicadorProcessoService.montarProcessoGeral(),
                indicadorResultadoService.percentualPorDesfecho(),
                indicadorInsucessoService.principaisMotivosInsucesso(),
                indicadorPrazoService.indicadoresPrazo(),
                indicadorRankingService.rankingPorTotalDemandas(),
                indicadorRankingService.rankingPorPercentualResolucao(),
                indicadorRankingService.rankingPorTempoMedioResolucao(),
                indicadorRankingService.rankingPorTempoAtePrimeiraTentativa()
        );
    }

    public IndicadorDTO indicadorGeral(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim, Long unidadeSolicitanteId) {
        if ((inicio == null && fim != null) || (inicio != null && fim == null)) {
            throw new BusinessException("Informe início e fim do período");
        }

        boolean temPeriodo = inicio != null;
        boolean temUnidade = unidadeSaudeId != null;
        boolean temUnidadeSolicitante = unidadeSolicitanteId != null;

        if(temUnidadeSolicitante){
            if (temPeriodo) {
                return indicadorPorUnidadeSolicitanteEPeriodo(inicio, fim, unidadeSolicitanteId);
            }

            return indicadorPorUnidadeSolicitante(unidadeSolicitanteId);
        }else{
            if (temUnidade && temPeriodo) {
                return indicadorPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);
            }

            if (temUnidade) {
                return indicadorPorUnidade(unidadeSaudeId);
            }

            if (temPeriodo) {
                return indicadorPorPeriodo(inicio, fim);
            }
        }

        validarAcessoIndicadorGeral();
        return indicadorGeral();
    }

    public IndicadorDTO indicadorPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorUnidade(unidadeSaudeId),
                indicadorProcessoService.montarProcessoPorUnidade(unidadeSaudeId),
                indicadorResultadoService.percentualPorDesfechoPorUnidade(unidadeSaudeId),
                indicadorInsucessoService.principaisMotivosInsucessoPorUnidade(unidadeSaudeId),
                indicadorPrazoService.indicadoresPrazoPorUnidade(unidadeSaudeId),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public IndicadorDTO indicadorPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoIndicadorGeral();
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorPeriodo(inicio, fim),
                indicadorProcessoService.montarProcessoPorPeriodo(inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorPeriodo(inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorPeriodo(inicio, fim),
                indicadorPrazoService.indicadoresPrazoPorPeriodo(inicio, fim),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public IndicadorDTO indicadorPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorProcessoService.montarProcessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorPrazoService.indicadoresPrazoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public IndicadorDTO indicadorPorUnidadeSolicitante(Long unidadeSolicitante) {
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorUnidadeSolicitante(unidadeSolicitante),
                indicadorProcessoService.montarProcessoPorServidor(unidadeSolicitante),
                indicadorResultadoService.percentualPorDesfechoPorServidor(unidadeSolicitante),
                indicadorInsucessoService.principaisMotivosInsucessoPorServidor(unidadeSolicitante),
                indicadorPrazoService.indicadoresPrazoPorServidor(unidadeSolicitante),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public IndicadorDTO indicadorPorUnidadeSolicitanteEPeriodo(LocalDateTime inicio, LocalDateTime fim, Long unidadeSolicitanteId) {
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorServidorEPeriodo(unidadeSolicitanteId, inicio, fim),
                indicadorProcessoService.montarProcessoPorServidorEPeriodo(unidadeSolicitanteId, inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorServidorEPeriodo(unidadeSolicitanteId, inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorServidorEPeriodo(unidadeSolicitanteId, inicio, fim),
                indicadorPrazoService.indicadoresPrazoPorServidorEPeriodo(unidadeSolicitanteId, inicio, fim),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public String exportarIndicadorGeralCsv(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim, Long unidadeSolicitanteId) {
        if ((inicio == null && fim != null) || (inicio != null && fim == null)) {
            throw new BusinessException("Informe início e fim do período");
        }

        boolean temPeriodo = inicio != null;
        boolean temUnidade = unidadeSaudeId != null;
        boolean temUnidadeSolicitante = unidadeSolicitanteId != null;

        if(temUnidadeSolicitante){
            if (temPeriodo) {
                return exportarIndicadorPorUnidadeSolicitanteEPeriodoCsv(unidadeSolicitanteId, inicio, fim);
            }
            auditoriaFacade.exportacaoCsvRealizada("Indicador geral do servidor criador de ID " + unidadeSolicitanteId + "exportado");
            return csvExporter.exportar(indicadorPorUnidadeSolicitante(unidadeSolicitanteId));
        }else{
            if (temUnidade && temPeriodo) {
                return exportarIndicadorPorUnidadeEPeriodoCsv(unidadeSaudeId, inicio, fim);
            }

            if (temUnidade) {
                return exportarIndicadorPorUnidadeCsv(unidadeSaudeId);
            }

            if (temPeriodo) {
                return exportarIndicadorPorPeriodoCsv(inicio, fim);
            }
        }

        auditoriaFacade.exportacaoCsvRealizada("Indicador geral exportado");
        return csvExporter.exportar(indicadorGeral());
    }

    public String exportarIndicadorPorUnidadeCsv(Long unidadeSaudeId) {
        auditoriaFacade.exportacaoCsvRealizada("Indicador da unidade ID " + unidadeSaudeId + " exportado");
        return csvExporter.exportar(indicadorPorUnidade(unidadeSaudeId));
    }

    public String exportarIndicadorPorPeriodoCsv(LocalDateTime inicio, LocalDateTime fim) {
        auditoriaFacade.exportacaoCsvRealizada("Indicador de "+ inicio + " até "+ fim +" exportado");
        return csvExporter.exportar(indicadorPorPeriodo(inicio, fim));
    }

    public String exportarIndicadorPorUnidadeEPeriodoCsv(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        auditoriaFacade.exportacaoCsvRealizada("Indicador da unidade ID " +
                unidadeSaudeId + "de "+ inicio + " até "+ fim +" exportado");
        return csvExporter.exportar(indicadorPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim));
    }

    public String exportarIndicadorPorUnidadeSolicitanteEPeriodoCsv(Long unidadeSolicitanteId, LocalDateTime inicio, LocalDateTime fim) {
        auditoriaFacade.exportacaoCsvRealizada("Indicador da unidade ID " +
                unidadeSolicitanteId + "de "+ inicio + " até "+ fim +" exportado");
        return csvExporter.exportar(indicadorPorUnidadeSolicitanteEPeriodo(inicio, fim, unidadeSolicitanteId));
    }

    private void validarAcessoIndicadorGeral() {
        Servidor servidor = servidorService.buscarServidorAutenticado();

        if (servidor.getPerfil() != PerfilServidor.GESTAO_MUNICIPAL) {
            throw new BusinessException("Servidor não pode acessar indicadores gerais");
        }
    }

    private void validarAcessoUnidade(Long unidadeSaudeId) {
        Servidor servidor = servidorService.buscarServidorAutenticado();

        if (servidor.getPerfil() != PerfilServidor.GESTAO_MUNICIPAL) {
            if (servidor.getUnidadeSaude() == null || !servidor.getUnidadeSaude().getId().equals(unidadeSaudeId)) {
                throw new BusinessException("Servidor não pode acessar indicadores de outra unidade");
            }
        }
    }
}