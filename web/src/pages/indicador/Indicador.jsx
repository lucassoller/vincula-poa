import { useEffect, useState } from "react";
import api from "../../api/api.js";
import "../../styles/indicador.css";
import { useAuth } from "../../context/AuthContext.jsx";
import Ranking from "../../components/Ranking.jsx";
import BarChartSimples from "../../components/BarChartSimples.jsx";
import DonutChart from "../../components/DonutChart.jsx";
import SecaoCardsInterna from "../../components/SecaoCardsInterna.jsx";
import ChartCard from "../../components/ChartCard.jsx";
import SecaoCards from "../../components/SecaoCards.jsx";
import {perfilLabel} from "../../utils/utils.js";
import ModalFiltrosIndicador from "../../components/Modal/ModalFiltrosIndicador.jsx";

function Indicador() {
    const { servidor } = useAuth();
    const [indicador, setIndicador] = useState(null);
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [unidades, setUnidades] = useState([]);
    const [servicos, setServicos] = useState([]);
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    const [filtros, setFiltros] = useState({
        unidade: "",
        servico: "",
        dataInicial: "",
        dataFinal: "",
    });

    useEffect(() => {
        async function carregarServicos() {
            try {
                const response = await api.get("/servicos/all");
                setUnidades(response.data.ubs);
                setServicos(response.data.servicos);

            } catch {
                setErro("Erro ao carregar serviços.");
            }
        }

        void carregarServicos();
    }, []);


    async function carregarDados(filtrosAtuais = filtros) {

        try {

            setCarregando(true);

            const payload = {
                usuarioId: filtrosAtuais.usuario || null,
                complemento: filtrosAtuais.complemento || null,
                servicoResponsavelId: filtrosAtuais.servico || null,
                servicoSolicitanteId: filtrosAtuais.servico || null,
                dataInicial: filtrosAtuais.dataInicial || null,
                dataFinal: filtrosAtuais.dataFinal || null,
            };

            if (servidor?.perfil === "SERVIDOR_APS") {
                payload.servicoResponsavelId = servidor.servicoId;
            }else if (servidor?.perfil === "SOLICITANTE") {
                payload.servicoSolicitanteId = servidor.servicoId;
            }

            const indicadorResponse = await api.post(
                "/indicadores/geral",
                payload
            );

            setIndicador(indicadorResponse.data);

        } catch {
            setErro("Erro ao carregar indicador.");
        } finally {
            setCarregando(false);
        }
    }

    async function executarBusca() {
        await carregarDados(filtros);
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        void carregarDados(filtros);
    }, []);


    async function limparFiltros() {

        const filtrosVazios = {
            unidade: "",
            servico: "",
            dataInicial: "",
            dataFinal: "",
        };

        setFiltros(filtrosVazios);
        setErro("");

        await carregarDados(filtrosVazios);
    }


    async function exportarCsv() {
        try {
            const payload = {
                servicoResponsavelId: filtros.servico || null,
                servicoSolicitanteId: filtros.servico || null,
                dataInicial: filtros.dataInicial || null,
                dataFinal: filtros.dataFinal || null,
            };

            if (servidor?.perfil === "SERVIDOR_APS") {
                payload.servicoResponsavelId = servidor.servicoId;
            }else if (servidor?.perfil === "SOLICITANTE") {
                payload.servicoSolicitanteId = servidor.servicoId;
            }

            const response = await api.post("/indicadores/exportar",
                payload,
                {
                    responseType: "blob",
                }
            );

            const blob = new Blob(
                [response.data],
                {
                    type:
                        "text/csv;charset=utf-8;",
                }
            );

            const agora = new Date();

            const dataHora = agora
                .toLocaleString("pt-BR")
                .replaceAll("/", "-")
                .replaceAll(":", "-")
                .replaceAll(", ", "_");

            const url = window.URL.createObjectURL(blob);

            const link = document.createElement("a");

            link.href = url;

            link.setAttribute(
                "download",
                `indicadores-vincula-poa-${dataHora}.csv`
            );

            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch {
            setErro("Erro ao exportar CSV.");
        }
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando indicadores...
                </div>
            </div>
        );
    }

    if (erro) {
        return (
            <div className="indicador-error">
                {erro}
            </div>
        );
    }

    const producao = indicador?.producao || [];
    const processo = indicador?.processo || [];
    const prazos = indicador?.prazos || [];
    const resultado = indicador?.resultado || [];
    const motivos = indicador?.principaisMotivos || [];
    const complementos = indicador?.principaisComplementos || [];

    return (
        <div className="indicador-container">
            <div className="indicador-page">
                <div className="indicador-header">
                    <div className="indicador-header-top">
                        <div>
                            <h1>Indicadores</h1>
                            <p>Indicadores gerais de busca ativa</p>
                        </div>

                        <div className="perfil-badge">
                            {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.servico}
                        </div>
                    </div>

                    <div className="search-container">

                           <span
                               className="buscar-btn"
                               onClick={() => setMostrarFiltros(true)}
                           >
                                Filtrar indicadores
                            </span>

                            <span
                                className="buscar-btn"
                                onClick={() => limparFiltros()}
                            >
                                Limpar filtros
                            </span>

                            <span
                                className="buscar-btn"
                                onClick={exportarCsv}
                            >
                                Exportar CSV
                            </span>

                    </div>
                </div>
                <SecaoCards
                    titulo="Produção"
                    dados={producao}
                />

                <div className="indicador-grid">
                    <ChartCard titulo="Prazos das demandas abertas ou em andamento">
                        <DonutChart
                            dados={prazos}
                            nomeKey="indicador"
                            valorKey="valor"
                        />
                    </ChartCard>
                    <ChartCard titulo="Resumo dos prazos">
                        <SecaoCardsInterna
                            dados={prazos}
                        />
                    </ChartCard>
                </div>

                <div className="indicador-grid">
                    <ChartCard titulo="Resultados dos desfechos">
                        <DonutChart
                            dados={resultado}
                            nomeKey="indicador"
                            valorKey="valor"
                        />
                    </ChartCard>
                    <ChartCard titulo="Motivos de busca ativa">
                        <BarChartSimples
                            dados={motivos}
                            nomeKey="motivo"
                            valorKey="quantidade"
                        />
                    </ChartCard>
                </div>

                <div className="indicador-grid-all">
                    <ChartCard titulo="Detalhamentos dos motivos de busca ativa">
                        <BarChartSimples
                            dados={complementos}
                            nomeKey="motivo"
                            valorKey="quantidade"
                        />
                    </ChartCard>
                </div>


                <SecaoCards
                    titulo="Processo"
                    dados={processo}
                />
                <div className="ranking-grid">
                    <Ranking
                        titulo="Total de demandas por UBS"
                        dados={indicador?.rankingTotalDemandas}
                    />

                    <Ranking
                        titulo="Percentual de resolução de demandas"
                        dados={indicador?.rankingPercentualResolucao}
                    />

                    <Ranking
                        titulo="Tempo médio de resolução de demandas"
                        dados={indicador?.rankingTempoMedioResolucao}
                    />

                    <Ranking
                        titulo="Tempo médio até primeira tentativa de contato"
                        dados={indicador?.rankingTempoPrimeiraTentativa}
                    />
                </div>
            </div>


            <ModalFiltrosIndicador
                aberto={mostrarFiltros}
                onFechar={() => setMostrarFiltros(false)}
                filtros={filtros}
                setFiltros={setFiltros}
                unidades={unidades}
                servicos={servicos}
                onAplicar={() => {
                    setMostrarFiltros(false);
                    void executarBusca();
                }}
                servidor={servidor}
                onLimpar={limparFiltros}
            />

        </div>
    );
}

export default Indicador;