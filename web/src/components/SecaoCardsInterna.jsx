import {formatarValorIndicador} from "../utils/utils.js";

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
export default SecaoCardsInterna