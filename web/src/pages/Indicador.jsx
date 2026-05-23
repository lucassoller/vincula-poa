import { useCallback, useEffect, useState } from "react";
import {
    PieChart,
    Pie,
    Cell,
    ResponsiveContainer,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
} from "recharts";
import api from "../api/api";
import "./indicador.css";
import {useAuth} from "../context/AuthContext.jsx";

const COLORS = ["#2563eb", "#06b6d4", "#22c55e", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899"];

function Indicador() {
    const { usuario } = useAuth();
    const [indicador, setIndicador] = useState(null);
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [inicio, setInicio] = useState("");
    const [fim, setFim] = useState("");
    const [unidadeSaudeId, setUnidadeSaudeId] = useState("");
    const [unidades, setUnidades] = useState([]);

    const carregarIndicador = useCallback(async () => {
        try {
            const temInicio = inicio !== "";
            const temFim = fim !== "";

            if ((temInicio && !temFim) || (!temInicio && temFim)) {
                setErro("Informe início e fim do período.");
                return;
            }

            setCarregando(true);
            setErro("");

            const params = new URLSearchParams();

            if (unidadeSaudeId) {
                params.append("unidadeSaudeId", unidadeSaudeId);
            }

            if (temInicio && temFim) {
                params.append("inicio", `${inicio}T00:00:00`);
                params.append("fim", `${fim}T23:59:59`);
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
        let ativo = true;

        async function carregarUnidades() {
            try {
                const response = await api.get("/unidades-saude/all");

                if (ativo) {
                    setUnidades(response.data);

                    if (usuario?.perfil === "EXECUTOR_APS") {
                        setUnidadeSaudeId(usuario.unidadeSaudeId);
                    }
                }
            } catch {
                if (ativo) {
                    setErro("Erro ao carregar unidades.");
                }
            }
        }

        void carregarUnidades();

        return () => {
            ativo = false;
        };
    }, [usuario]);

    useEffect(() => {
        const executar = async () => {
            await carregarIndicador();
        };

        void executar();
    }, []);

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">Carregando indicadores...</div>
            </div>
        );
    }

    if (erro) {
        return <div className="indicador-error">{erro}</div>;
    }

    const producao = indicador?.producao || [];
    const processo = indicador?.processo || [];
    const prazos = indicador?.prazos || [];
    const resultado = indicador?.resultado || [];
    const motivos = indicador?.principaisMotivosInsucesso || [];

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

            const response = await api.get(
                `/indicadores/exportar?${params.toString()}`,
                { responseType: "blob" }
            );

            const blob = new Blob([response.data], {
                type: "text/csv;charset=utf-8;",
            });

            const agora = new Date();

            const dataHora = agora
                .toLocaleString("pt-BR")
                .replaceAll("/", "-")
                .replaceAll(":", "-")
                .replaceAll(", ", "_");

            const url = window.URL.createObjectURL(blob);

            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", "indicadores-vincula-poa"+dataHora+".csv");

            document.body.appendChild(link);
            link.click();

            link.remove();
            window.URL.revokeObjectURL(url);
        } catch {
            setErro("Erro ao exportar CSV.");
        }
    }

    return (
        <div className="indicador-container">
            <div className="indicador-page">
                <div className="indicador-header">
                    <div>
                        <h1>Indicadores</h1>
                        <p>Indicadores gerais de busca ativa</p>
                    </div>
                    <div className="indicador-filtros">
                        <input
                            type="date"
                            className="input-field"
                            value={inicio}
                            onChange={(e) => setInicio(e.target.value)}
                        />

                        <input
                            type="date"
                            className="input-field"
                            value={fim}
                            onChange={(e) => setFim(e.target.value)}
                        />

                        <select
                            className="input-field"
                            value={unidadeSaudeId}
                            onChange={(e) => setUnidadeSaudeId(e.target.value)}
                            disabled={usuario?.perfil === "EXECUTOR_APS"}
                        >
                            <option value="">Todas as UBS</option>

                            {unidades.map((u) => (
                                <option key={u.id} value={u.id}>
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
                                onClick={exportarCsv}
                            >
                                Exportar CSV
                            </button>
                        </div>
                    </div>
                </div>

                <SecaoCards titulo="Produção" dados={producao} />

                <div className="indicador-grid">
                    <ChartCard titulo="Prazos das demandas abertas ou em andamento">
                        <DonutChart dados={prazos} nomeKey="indicador" valorKey="valor" />
                    </ChartCard>

                    <ChartCard titulo="Resumo dos prazos">
                        <SecaoCardsInterna dados={prazos} />
                    </ChartCard>
                </div>

                <div className="indicador-grid">
                    <ChartCard titulo="Resultados dos desfechos">
                        <DonutChart dados={resultado} nomeKey="indicador" valorKey="valor" />
                    </ChartCard>

                    <ChartCard titulo="Principais motivos de insucesso">
                        <BarChartSimples dados={motivos} nomeKey="motivo" valorKey="quantidade" />
                    </ChartCard>
                </div>

                <SecaoCards titulo="Processo" dados={processo} />

                <div className="ranking-grid">
                    <Ranking titulo="Total de demandas por UBS" dados={indicador?.rankingTotalDemandas} />
                    <Ranking titulo="Percentual de resolução de demandas" dados={indicador?.rankingPercentualResolucao} />
                    <Ranking titulo="Tempo médio de resolução de demandas" dados={indicador?.rankingTempoMedioResolucao} />
                    <Ranking titulo="Tempo médio até primeira tentativa de contato" dados={indicador?.rankingTempoPrimeiraTentativa} />
                </div>
            </div>
        </div>
    );
}

function SecaoCardsInterna({ dados }) {
    return (
        <div className="prazo-card-list">
            {dados?.map((item, index) => (
                <div key={index} className="prazo-card-item">
                    <span>{item.indicador}</span>
                    <strong>{formatarValorIndicador(item)}</strong>
                </div>
            ))}
        </div>
    );
}

function SecaoCards({ titulo, dados }) {
    return (
        <section className="indicador-section">
            <h2>{titulo}</h2>

            <div className="kpi-grid">
                {dados?.map((item, index) => (
                    <div key={index} className="kpi-card">
                        <span>{item.indicador}</span>
                        <strong>{formatarValorIndicador(item)}</strong>
                    </div>
                ))}
            </div>
        </section>
    );
}

function ChartCard({ titulo, children }) {
    return (
        <div className="chart-card">
            <h2>{titulo}</h2>
            {children}
        </div>
    );
}

function DonutChart({ dados, nomeKey, valorKey }) {
    const dadosValidos = dados
        .map((item) => ({
            nome: item[nomeKey],
            valor: Number(item[valorKey]) || 0,
        }))
        .filter((item) => item.valor > 0);

    if (dadosValidos.length === 0) {
        return <div className="empty-state">Sem dados para exibir.</div>;
    }

    return (
        <div className="donut-wrapper">
            <ResponsiveContainer width="100%" height={280}>
                <PieChart>
                    <Pie
                        data={dadosValidos}
                        dataKey="valor"
                        nameKey="nome"
                        innerRadius={70}
                        outerRadius={110}
                        paddingAngle={3}
                    >
                        {dadosValidos.map((_, index) => (
                            <Cell key={index} fill={COLORS[index % COLORS.length]} />
                        ))}
                    </Pie>
                    <Tooltip />
                </PieChart>
            </ResponsiveContainer>

            <div className="chart-legend">
                {dadosValidos.map((item, index) => (
                    <div key={index} className="legend-item">
                        <span style={{ background: COLORS[index % COLORS.length] }}></span>
                        {item.nome}: <strong>{item.valor + " %"}</strong>
                    </div>
                ))}
            </div>
        </div>
    );
}

function BarChartSimples({ dados, nomeKey, valorKey }) {
    const dadosGrafico = dados.map((item) => ({
        nome: item[nomeKey],
        valor: Number(item[valorKey]) || 0,
    }));

    if (dadosGrafico.length === 0) {
        return <div className="empty-state">Sem dados para exibir.</div>;
    }

    return (
        <ResponsiveContainer width="100%" height={320}>
            <BarChart data={dadosGrafico}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="nome" tick={{ fontSize: 12 }} />
                <YAxis />
                <Tooltip />
                <Bar dataKey="valor" radius={[10, 10, 0, 0]} fill="#2563eb" />
            </BarChart>
        </ResponsiveContainer>
    );
}

function Ranking({ titulo, dados }) {

    const [expandido, setExpandido] = useState(false);

    if (!dados || dados.length === 0) {
        return null;
    }

    const quantidadeInicial = 5;

    const dadosExibidos = expandido
        ? dados
        : dados.slice(0, quantidadeInicial);

    return (
        <div className="ranking-card">

            <h2>{titulo}</h2>

            <div className="ranking-list">

                {dadosExibidos.map((item, index) => (

                    <div
                        key={item.unidadeSaudeId ?? index}
                        className="ranking-item"
                    >

                        <span className="ranking-position">
                            {index + 1}
                        </span>

                        <div>
                            <strong>
                                {item.unidadeSaudeNome}
                            </strong>

                            <p>
                                {item.valor}
                            </p>
                        </div>

                    </div>
                ))}

            </div>

            {dados.length > quantidadeInicial && (

                <button
                    type="button"
                    className="ranking-expand-btn"
                    onClick={() => setExpandido(!expandido)}
                >
                    {expandido ? "Ver menos" : "Ver mais"}
                </button>

            )}

        </div>
    );
}

function formatarValorIndicador(item) {
    const nome = item.indicador.toLowerCase();

    const ehPercentual =
        nome.includes("percentual") ||
        nome.includes("(%)") ||
        nome.includes("dentro do prazo") ||
        nome.includes("atrasadas") ||
        nome.includes("finalizadas com atraso");

    if (ehPercentual && typeof item.valor === "number") {
        return `${item.valor} %`;
    }

    return item.valor;
}

export default Indicador;