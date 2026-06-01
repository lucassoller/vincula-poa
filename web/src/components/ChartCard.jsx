function ChartCard({ titulo, children }) {
    return (
        <div className="chart-card">
            <h2>{titulo}</h2>
            {children}
        </div>
    );
}

export default ChartCard