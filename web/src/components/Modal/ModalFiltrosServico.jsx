import "../../styles/modalFiltrosDemanda.css";

function ModalFiltrosServico({
                                 aberto,
                                 onFechar,
                                 filtros,
                                 setFiltros,
                                 onAplicar,
                                 onLimpar,
                                 servicos = []
                             }) {
    if (!aberto) return null;

    function toggle(campo, valor) {
        setFiltros(prev => ({
            ...prev,
            [campo]: prev[campo].includes(valor)
                ? prev[campo].filter(v => v !== valor)
                : [...prev[campo], valor]
        }));
    }

    return (
        <div className="overlay-filtros">

            <div className="modal-filtros">

                <div className="header-filtros">

                    <h2>Filtros</h2>

                    <button onClick={onFechar}>
                        ✕
                    </button>
                </div>

                <div className="grupo-filtro">

                    <h4>Serviço</h4>

                    <select
                        value={filtros.servico}
                        onChange={(e) =>
                            setFiltros(prev => ({
                                ...prev,
                                servico: e.target.value
                            }))
                        }
                    >
                        <option value="">Todos</option>

                        {servicos.map(servico => (
                            <option key={servico.id} value={servico.id}>
                                {servico.nome}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="grupo-filtro">

                    <h4>Tipo de serviço</h4>

                    {[
                        ["UBS","UBS"],
                        ["SERVICO_ESPECIALIZADO","Serviço especializado"],
                        ["OUTRO","Outro"]
                    ].map(([valor,label]) => (

                        <label key={valor}>

                            <input
                                type="checkbox"
                                checked={filtros.tipoServico.includes(valor)}
                                onChange={() => toggle("tipoServico", valor)}
                            />

                            {label}

                        </label>

                    ))}

                </div>
                <div className="footer-filtros">
                    <button
                        className="btn-secundario"
                        onClick={onLimpar}
                    >
                        Limpar
                    </button>
                    <button
                        className="btn-primario"
                        onClick={onAplicar}
                    >
                        Aplicar filtros
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ModalFiltrosServico;