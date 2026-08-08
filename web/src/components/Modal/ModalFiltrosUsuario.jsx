import "../../styles/modalFiltrosDemanda.css";
import {useEffect, useRef} from "react";

function ModalFiltrosUsuario({
                                 aberto,
                                 onFechar,
                                 filtros,
                                 setFiltros,
                                 onAplicar,
                                 onLimpar,
                                 usuarios = [],
                                 servicos = [],
                                 buscarUsuarios,
                                 setUsuarios,
                                 servidor
                             }) {
    const autocompleteRef = useRef(null);

    useEffect(() => {
        function handleClickOutside(event) {
            if (autocompleteRef.current && !autocompleteRef.current.contains(event.target)) {
                setUsuarios([]);
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

                    <h4>Usuário</h4>

                    <input
                        type="text"
                        className="input-field"
                        placeholder="Digite o nome do usuário"
                        value={filtros.nomeCompleto || ""}
                        onChange={(e) => {

                            const nome = e.target.value;

                            setFiltros(prev => ({
                                ...prev,
                                nomeCompleto: nome,
                                usuarioId: ""
                            }));

                            buscarUsuarios(nome);
                        }}
                    />

                    {usuarios.length > 0 && (

                        <div className="autocomplete-list modal-autocomplete">

                            {usuarios.map(usuario => (

                                <div
                                    key={usuario.id}
                                    className="autocomplete-item"
                                    onClick={() => {

                                        setFiltros(prev => ({
                                            ...prev,
                                            nomeCompleto: usuario.nomeCompleto,
                                            usuarioId: usuario.id
                                        }));

                                        setUsuarios([]);

                                    }}
                                >
                                    {usuario.nomeCompleto}
                                </div>
                            ))}
                        </div>
                    )}
                    {filtros.usuarioId && (
                        <div className="usuario-chip">
                            <span>{filtros.nomeCompleto}</span>

                            <button
                                type="button"
                                onClick={() =>
                                    setFiltros(prev => ({
                                        ...prev,
                                        nomeCompleto: "",
                                        usuarioId: ""
                                    }))
                                }
                            >
                                ✕
                            </button>
                        </div>
                    )}
                </div>


                {servidor.perfil !== 'SERVIDOR_APS' && (

                    <div className="grupo-filtro">

                        <h4>Serviço responsável</h4>

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
                )}

                <div className="grupo-filtro">

                    <h4>Faixa etária</h4>

                    {[
                        ["CRIANCA", "Criança (0–11 anos)"],
                        ["ADOLESCENTE", "Adolescente (12–17 anos)"],
                        ["ADULTO", "Adulto (18–59 anos)"],
                        ["IDOSO", "Idoso (60+ anos)"]
                    ].map(([valor, label]) => (

                        <label key={valor}>

                            <input
                                type="checkbox"
                                checked={filtros.faixaEtaria.includes(valor)}
                                onChange={() => toggle("faixaEtaria", valor)}
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

export default ModalFiltrosUsuario;