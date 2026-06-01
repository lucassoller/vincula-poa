import {Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";

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

export default BarChartSimples