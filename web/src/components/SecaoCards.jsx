import {formatarValorIndicador} from "../utils/utils.js";

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

export default SecaoCards