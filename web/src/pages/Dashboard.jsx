import { useEffect, useState } from "react";
import api from "../api/api";

function Dashboard() {
    const [dashboard, setDashboard] = useState(null);
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        carregarDashboard();
    }, []);

    async function carregarDashboard() {
        try {
            setCarregando(true);
            setErro("");

            const response = await api.get("/indicadores/dashboard/geral");
            setDashboard(response.data);
        } catch (error) {
            setErro("Erro ao carregar dashboard");
        } finally {
            setCarregando(false);
        }
    }

    if (carregando) {
        return <p>Carregando dashboard...</p>;
    }

    if (erro) {
        return <p style={{ color: "red" }}>{erro}</p>;
    }

    return (
        <div>
            <h1>Dashboard</h1>

            <Secao titulo="Produção" dados={dashboard?.producao} />
            <Secao titulo="Processo" dados={dashboard?.processo} />
            <Secao titulo="Resultado" dados={dashboard?.resultado} />

            <h2>Principais motivos de insucesso</h2>
            <div style={styles.grid}>
                {dashboard?.principaisMotivosInsucesso?.map((item, index) => (
                    <div key={index} style={styles.card}>
                        <strong>{item.motivo}</strong>
                        <p>{item.quantidade}</p>
                    </div>
                ))}
            </div>

            <Ranking titulo="Ranking por total de demandas" dados={dashboard?.rankingTotalDemandas} />
            <Ranking titulo="Ranking por percentual de resolução" dados={dashboard?.rankingPercentualResolucao} />
            <Ranking titulo="Ranking por tempo médio de resolução" dados={dashboard?.rankingTempoMedioResolucao} />
            <Ranking titulo="Ranking por tempo até primeira tentativa" dados={dashboard?.rankingTempoPrimeiraTentativa} />
        </div>
    );
}

function Secao({ titulo, dados }) {
    return (
        <>
            <h2>{titulo}</h2>
            <div style={styles.grid}>
                {dados?.map((item, index) => (
                    <div key={index} style={styles.card}>
                        <strong>{item.indicador}</strong>
                        <p>{item.valor}</p>
                    </div>
                ))}
            </div>
        </>
    );
}

function Ranking({ titulo, dados }) {
    if (!dados || dados.length === 0) {
        return null;
    }

    return (
        <>
            <h2>{titulo}</h2>
            <table style={styles.table}>
                <thead>
                <tr>
                    <th style={styles.th}>#</th>
                    <th style={styles.th}>Unidade</th>
                    <th style={styles.th}>Valor</th>
                </tr>
                </thead>
                <tbody>
                {dados.map((item, index) => (
                    <tr key={item.unidadeSaudeId ?? index}>
                        <td style={styles.td}>{index + 1}</td>
                        <td style={styles.td}>{item.unidadeSaudeNome}</td>
                        <td style={styles.td}>{item.valor}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
}

const styles = {
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
        gap: "16px",
        marginBottom: "28px",
    },
    card: {
        background: "#fff",
        padding: "18px",
        borderRadius: "10px",
        boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
        background: "#fff",
        marginBottom: "28px",
    },
    th: {
        textAlign: "left",
        padding: "12px",
        borderBottom: "1px solid #ddd",
        background: "#f3f4f6",
    },
    td: {
        padding: "12px",
        borderBottom: "1px solid #eee",
    },
};

export default Dashboard;