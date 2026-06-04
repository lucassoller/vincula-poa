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

    public IndicadorDTO indicadorGeral(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim, Long servidorId) {
        if ((inicio == null && fim != null) || (inicio != null && fim == null)) {
            throw new BusinessException("Informe início e fim do período");
        }

        boolean temPeriodo = inicio != null;
        boolean temUnidade = unidadeSaudeId != null;
        boolean temServidor = servidorId != null;

        if(temServidor){
            if (temPeriodo) {
                return indicadorPorServidorEPeriodo(inicio, fim, servidorId);
            }

            return indicadorPorServidor(servidorId);
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

    public IndicadorDTO indicadorPorServidor(Long servidorId) {
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorServidor(servidorId),
                indicadorProcessoService.montarProcessoPorServidor(servidorId),
                indicadorResultadoService.percentualPorDesfechoPorServidor(servidorId),
                indicadorInsucessoService.principaisMotivosInsucessoPorServidor(servidorId),
                indicadorPrazoService.indicadoresPrazoPorServidor(servidorId),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public IndicadorDTO indicadorPorServidorEPeriodo(LocalDateTime inicio, LocalDateTime fim, Long servidorId) {
        return new IndicadorDTO(
                indicadorProducaoService.indicadoresPorServidorEPeriodo(servidorId, inicio, fim),
                indicadorProcessoService.montarProcessoPorServidorEPeriodo(servidorId, inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorServidorEPeriodo(servidorId, inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorServidorEPeriodo(servidorId, inicio, fim),
                indicadorPrazoService.indicadoresPrazoPorServidorEPeriodo(servidorId, inicio, fim),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public String exportarIndicadorGeralCsv(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim, Long servidorId) {
        if ((inicio == null && fim != null) || (inicio != null && fim == null)) {
            throw new BusinessException("Informe início e fim do período");
        }

        boolean temPeriodo = inicio != null;
        boolean temUnidade = unidadeSaudeId != null;
        boolean temServidor = servidorId != null;

        if(temServidor){
            if (temPeriodo) {
                return exportarIndicadorPorServidorEPeriodoCsv(servidorId, inicio, fim);
            }
            auditoriaFacade.exportacaoCsvRealizada("Indicador geral do servidor criador de ID " + servidorId + "exportado");
            return csvExporter.exportar(indicadorPorServidor(servidorId));
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

    public String exportarIndicadorPorServidorEPeriodoCsv(Long servidorId, LocalDateTime inicio, LocalDateTime fim) {
        auditoriaFacade.exportacaoCsvRealizada("Indicador do servidor criador de ID " +
                servidorId + "de "+ inicio + " até "+ fim +" exportado");
        return csvExporter.exportar(indicadorPorServidorEPeriodo(inicio, fim, servidorId));
    }

    private void validarAcessoIndicadorGeral() {
        Servidor servidor = servidorService.buscarServidorAutenticado();

        if (servidor.getPerfil() != PerfilServidor.GESTAO_MUNICIPAL) {
            throw new BusinessException("Servidor não pode acessar indicadores gerais");
        }
    }

    private void validarAcessoUnidade(Long unidadeSaudeId) {
        Servidor servidor = servidorService.buscarServidorAutenticado();

        if (servidor.getPerfil() == PerfilServidor.GESTAO_MUNICIPAL) {
            return;
        }

        if (servidor.getPerfil() == PerfilServidor.SERVIDOR_APS) {
            if (servidor.getUnidadeSaude() == null || !servidor.getUnidadeSaude().getId().equals(unidadeSaudeId)) {
                throw new BusinessException("Servidor não pode acessar indicadores de outra unidade");
            }
            return;
        }

        throw new BusinessException("Servidor não pode acessar indicadores por unidade");
    }
}