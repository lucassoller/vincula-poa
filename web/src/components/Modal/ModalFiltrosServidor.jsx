import "../../styles/modalFiltrosDemanda.css";
import {useEffect, useRef} from "react";

function ModalFiltrosServidor({
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
                                 servidor
                             }) {
    const autocompleteRef = useRef(null);

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

                    <h4>Servidor</h4>

                    <input
                        type="text"
                        className="input-field"
                        placeholder="Digite o nome do servidor"
                        value={filtros.nome || ""}
                        onChange={(e) => {

                            const nome = e.target.value;

                            setFiltros(prev => ({
                                ...prev,
                                nome: nome,
                                id: ""
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
                                            id: servidor.id
                                        }));

                                        setServidores([]);

                                    }}
                                >
                                    {servidor.nome}
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


                {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) && (

                    <div className="grupo-filtro">

                        <h4>Serviço vinculado</h4>

                        <select
                            value={filtros.unidade}
                            onChange={(e) =>
                                setFiltros(prev => ({
                                    ...prev,
                                    unidade: e.target.value
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
                )}

                {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) && (
                    <div className="grupo-filtro">

                        <h4>Perfil</h4>

                        {[
                            ["GESTAO_MUNICIPAL", "Gestão Municipal"],
                            ["VIGILANCIA", "Vigilância"],
                            ["COORDENADORIA", "Coordenadoria"],
                            ["SERVIDOR_APS", "Servidor APS"],
                            ["SOLICITANTE", "Solicitante"],
                        ].map(([valor, label]) => (

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
                )}
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

export default ModalFiltrosServidor;