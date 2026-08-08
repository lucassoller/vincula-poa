import "../../styles/modalFiltrosDemanda.css";
import {useEffect, useRef} from "react";

function ModalFiltrosAuditoria({
                                 aberto,
                                 onFechar,
                                 filtros,
                                 setFiltros,
                                 onAplicar,
                                 onLimpar,
                                 servidores = [],
                                 servicos = [],
                                 buscarServidores,
                                 setServidores,
                             }) {
    const autocompleteRef = useRef(null);

    function toggle(campo, valor) {
        setFiltros(prev => ({
            ...prev,
            [campo]: prev[campo].includes(valor)
                ? prev[campo].filter(v => v !== valor)
                : [...prev[campo], valor]
        }));
    }

    useEffect(() => {
        function handleClickOutside(event) {
            if (autocompleteRef.current && !autocompleteRef.current.contains(event.target)) {
                setServidores([]);
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

                    <h4>Servidor</h4>

                    <input
                        type="text"
                        className="input-field"
                        placeholder="Digite o nome do servidor"
                        value={filtros.nome|| ""}
                        onChange={(e) => {

                            const nome = e.target.value;

                            setFiltros(prev => ({
                                ...prev,
                                nome: nome,
                                servidorId: ""
                            }));

                            buscarServidores(nome);
                        }}
                    />

                    {servidores.length > 0 && (

                        <div className="autocomplete-list modal-autocomplete">

                            {servidores.map(servidor => (

                                <div
                                    key={servidor.id}
                                    className="autocomplete-item"
                                    onClick={() => {

                                        setFiltros(prev => ({
                                            ...prev,
                                            nome: servidor.nome,
                                            servidorId: servidor.id
                                        }));

                                        setServidores([]);

                                    }}
                                >
                                    {servidor.nome}
                                </div>
                            ))}
                        </div>
                    )}
                    {filtros.servidorId && (
                        <div className="usuario-chip">
                            <span>{filtros.nome}</span>

                            <button
                                type="button"
                                onClick={() =>
                                    setFiltros(prev => ({
                                        ...prev,
                                        nome: "",
                                        servidorId: ""
                                    }))
                                }
                            >
                                ✕
                            </button>
                        </div>
                    )}
                </div>

                <div className="grupo-filtro">

                    <h4>Perfil</h4>

                    {[
                        ["GESTAO_MUNICIPAL","Gestão"],
                        ["SERVIDOR_APS","Servidor APS"],
                        ["SOLICITANTE","Solicitante"],
                    ].map(([valor,label]) => (

                        <label key={valor}>

                            <input
                                type="checkbox"
                                checked={filtros.perfil.includes(valor)}
                                onChange={() => toggle("perfil", valor)}
                            />

                            {label}

                        </label>
                    ))}
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
                        <option value="">
                            Todas
                        </option>
                        {servicos.map(servico => (

                            <option
                                key={servico.id}
                                value={servico.id}
                            >
                                {servico.nome}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="grupo-filtro">

                    <h4>Data/hora</h4>

                    <input
                        type="datetime-local"
                        value={filtros.dataInicial}
                        onChange={(e) =>
                            setFiltros(prev => ({
                                ...prev,
                                dataInicial: e.target.value
                            }))
                        }
                    />

                    <input
                        type="datetime-local"
                        value={filtros.dataFinal}
                        onChange={(e) =>
                            setFiltros(prev => ({
                                ...prev,
                                dataFinal: e.target.value
                            }))
                        }
                    />

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

export default ModalFiltrosAuditoria;