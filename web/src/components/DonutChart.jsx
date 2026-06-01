import {Cell, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
const COLORS = ["#2563eb", "#06b6d4", "#22c55e","#f59e0b", "#ef4444", "#8b5cf6", "#ec4899",];

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

export default DonutChart