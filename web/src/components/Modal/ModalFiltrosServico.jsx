import "../../styles/modalFiltrosDemanda.css";
import {useEffect, useRef} from "react";

function ModalFiltrosServico({
                                 aberto,
                                 onFechar,
                                 filtros,
                                 setFiltros,
                                 onAplicar,
                                 onLimpar,
                                 servicos = [],
                                 buscarServicos,
                                 setServicos,
                             }) {
    const autocompleteRef = useRef(null);

    useEffect(() => {
        function handleClickOutside(event) {
            if (autocompleteRef.current && !autocompleteRef.current.contains(event.target)) {
                setServicos([]);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener(
                "mousedown",
                handleClickOutside
            );
        };

    }, []);


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

                <div className="autocomplete-container grupo-filtro" ref={autocompleteRef}>

                    <h4>Serviço</h4>

                    <input
                        type="text"
                        className="input-field"
                        placeholder="Digite o nome do serviço"
                        value={filtros.nome || ""}
                        onChange={(e) => {

                            const nome = e.target.value;

                            setFiltros(prev => ({
                                ...prev,
                                nome: nome,
                                id: ""
                            }));

                            buscarServicos(nome);
                        }}
                    />

                    {servicos.length > 0 && (

                        <div className="autocomplete-list modal-autocomplete">

                            {servicos.map(servico => (

                                <div
                                    key={servico.id}
                                    className="autocomplete-item"
                                    onClick={() => {

                                        setFiltros(prev => ({
                                            ...prev,
                                            nome: servico.nome,
                                            id: servico.id
                                        }));

                                        setServicos([]);

                                    }}
                                >
                                    {servico.nome}
                                </div>
                            ))}
                        </div>
                    )}
                    {filtros.id && (
                        <div className="usuario-chip">
                            <span>{filtros.nome}</span>

                            <button
                                type="button"
                                onClick={() =>
                                    setFiltros(prev => ({
                                        ...prev,
                                        nome: "",
                                        id: ""
                                    }))
                                }
                            >
                                ✕
                            </button>
                        </div>
                    )}
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