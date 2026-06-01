import {useState} from "react";

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
                            <p>{item.valor}</p>
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

export default Ranking;