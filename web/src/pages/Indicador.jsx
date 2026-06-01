import { useCallback, useEffect, useState } from "react";
import api from "../api/api";
import "./indicador.css";
import { useAuth } from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import Ranking from "../components/Ranking.jsx";
import BarChartSimples from "../components/BarChartSimples.jsx";
import DonutChart from "../components/DonutChart.jsx";
import SecaoCardsInterna from "../components/SecaoCardsInterna.jsx";
import ChartCard from "../components/ChartCard.jsx";
import SecaoCards from "../components/SecaoCards.jsx";

function Indicador() {
    const navigate = useNavigate();
    const { usuario } = useAuth();
    const [indicador, setIndicador] = useState(null);
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [inicio, setInicio] = useState("");
    const [fim, setFim] = useState("");
    const [unidadeSelecionada, setUnidadeSelecionada] = useState("");
    const [unidades, setUnidades] = useState([]);
    const unidadeSaudeId =
        usuario?.perfil === "EXECUTOR_APS"
            ? String(usuario.unidadeSaudeId)
            : unidadeSelecionada;

    useEffect(() => {
        if(usuario?.perfil === "SOLICITANTE"){
            navigate("/demandas");
            return;
        }
        
        async function carregarUnidades() {
            try {
                const response = await api.get("/unidades-saude/all");
                setUnidades(response.data);
            } catch {
                setErro("Erro ao carregar unidades.");
            }
        }
        
        void carregarUnidades();

    }, [navigate, usuario]);

    const carregarIndicador = useCallback(async (
        unidade = unidadeSaudeId,
        dataInicio = inicio,
        dataFim = fim
    ) => {

        try {
            const temInicio = dataInicio !== "";
            const temFim = dataFim !== "";
            if ((temInicio && !temFim) || (!temInicio && temFim)) {
                setErro("Informe início e fim do período.");
                return;
            }

            setCarregando(true);
            setErro("");

            const params = new URLSearchParams();

            if (unidade) {
                params.append("unidadeSaudeId", unidadeSaudeId);
            }

            if (temInicio && temFim) {
                params.append("inicio", `${dataInicio}T00:00:00`);
                params.append("fim", `${dataFim}T23:59:59`);
            }

            const response = await api.get(
                `/indicadores/geral?${params.toString()}`
            );
            setIndicador(response.data);

        } catch {
            setErro("Erro ao carregar indicador");
        } finally {
            setCarregando(false);
        }
    }, [unidadeSaudeId, inicio, fim]);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        void carregarIndicador();

    }, []);


    async function exportarCsv() {
        try {
            const params = new URLSearchParams();
            if (unidadeSaudeId) {
                params.append("unidadeSaudeId", unidadeSaudeId);
            }

            if (inicio) {
                params.append("inicio", `${inicio}T00:00:00`);
            }

            if (fim) {
                params.append("fim", `${fim}T23:59:59`);
            }

            const response = await api.get(`/indicadores/exportar?${params.toString()}`,
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

    async function limparFiltros() {

        setInicio("");
        setFim("");

        const unidade =
            usuario?.perfil === "EXECUTOR_APS"
                ? String(usuario.unidadeSaudeId)
                : "";

        if (usuario?.perfil !== "EXECUTOR_APS") {
            setUnidadeSelecionada("");
        }

        setErro("");

        await carregarIndicador(
            unidade,
            "",
            ""
        );
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
    const motivos = indicador?.principaisMotivosInsucesso || [];

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
                            {usuario?.perfil}
                        </div>
                    </div>

                    <div className="indicador-filtros">

                        <input
                            type="date"
                            className="input-field"
                            value={inicio}
                            onChange={(e) =>
                                setInicio(e.target.value)
                            }
                        />

                        <input
                            type="date"
                            className="input-field"
                            value={fim}
                            onChange={(e) =>
                                setFim(e.target.value)
                            }
                        />

                        <select
                            className="input-field"
                            value={unidadeSaudeId}
                            onChange={(e) =>
                                setUnidadeSelecionada(e.target.value)
                            }
                            disabled={usuario?.perfil === "EXECUTOR_APS"}
                        >

                            <option value="">
                                Todas as UBS
                            </option>

                            {unidades.map((u) => (
                                <option
                                    key={u.id}
                                    value={u.id}
                                >
                                    {u.nome}
                                </option>

                            ))}

                        </select>

                        <div className="indicador-actions">
                            <button
                                className="buscar-btn"
                                onClick={carregarIndicador}
                            >
                                Aplicar filtros
                            </button>

                            <button
                                className="buscar-btn"
                                onClick={limparFiltros}
                            >
                                Limpar filtros
                            </button>

                            <button
                                className="buscar-btn"
                                onClick={exportarCsv}
                            >
                                Exportar CSV
                            </button>
                        </div>
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
                    <ChartCard titulo="Principais motivos de insucesso">
                        <BarChartSimples
                            dados={motivos}
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
        </div>
    );
}

export default Indicador;