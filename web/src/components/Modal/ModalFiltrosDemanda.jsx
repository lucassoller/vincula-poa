import "../../styles/modalFiltrosDemanda.css";
import {useEffect, useRef} from "react";

function ModalFiltrosDemanda({
                                 aberto,
                                 onFechar,
                                 filtros,
                                 setFiltros,
                                 onAplicar,
                                 unidades = [],
                                 servicos = [],
                                 motivos = [],
                                 usuarios = [],
                                 setUsuarios,
                                 buscarUsuarios,
                                 onLimpar,
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

                    <button onClick={onFechar}>✕</button>
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

                        <h4>Unidade responsável</h4>

                        <select
                            value={filtros.unidade}
                            onChange={(e) =>
                                setFiltros(prev => ({
                                    ...prev,
                                    unidade: e.target.value
                                }))
                            }
                        >
                            <option value="">Todas</option>

                            {unidades.map(u => (
                                <option key={u.id} value={u.id}>
                                    {u.nome}
                                </option>
                            ))}

                        </select>

                    </div>
                )}

                {servidor.perfil !== 'SOLICITANTE' && (
                    <div className="grupo-filtro">

                        <h4>Serviço solicitante</h4>

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
                            <option value="-1">Solicitado pela gestão</option>

                            {servicos.map(servico => (
                                <option key={servico.id} value={servico.id}>
                                    {servico.nome}
                                </option>
                            ))}

                        </select>

                    </div>
                )}

                <div className="grupo-filtro">

                    <h4>Status</h4>

                    {[
                        ["ABERTA","Aberto"],
                        ["EM_ANDAMENTO","Em andamento"],
                        ["FINALIZADA","Finalizado"]
                    ].map(([valor,label]) => (

                        <label key={valor}>

                            <input
                                type="checkbox"
                                checked={filtros.status.includes(valor)}
                                onChange={() => toggle("status", valor)}
                            />

                            {label}

                        </label>

                    ))}

                </div>

                <div className="grupo-filtro">

                    <h4>Prioridade</h4>

                    {[
                        ["BAIXA","Baixa"],
                        ["MEDIA","Média"],
                        ["ALTA","Alta"],
                        ["IMEDIATA","Imediata"]
                    ].map(([valor,label]) => (

                        <label key={valor}>

                            <input
                                type="checkbox"
                                checked={filtros.prioridade.includes(valor)}
                                onChange={() => toggle("prioridade", valor)}
                            />

                            {label}

                        </label>

                    ))}

                </div>

                <div className="grupo-filtro">

                    <h4>Tempo</h4>

                    {[
                        ["NO_PRAZO","No prazo"],
                        ["HOJE","Vence hoje"],
                        ["ATE_3","Até 3 dias"],
                        ["ATE_7","Até 7 dias"],
                        ["ATE_15","Até 15 dias"],
                        ["ATE_30","Até 30 dias"],
                        ["ATRASADA","Atrasadas"]
                    ].map(([valor,label]) => (

                        <label key={valor}>

                            <input
                                type="checkbox"
                                checked={filtros.tempo.includes(valor)}
                                onChange={() => toggle("tempo", valor)}
                            />

                            {label}

                        </label>

                    ))}

                </div>

                <div className="grupo-filtro">

                    <h4>Motivo da busca</h4>

                    <select
                        value={filtros.motivo}
                        onChange={(e) => {

                            setFiltros(prev => ({
                                ...prev,
                                motivo: e.target.value,
                                complemento: ""
                            }));

                        }}
                    >
                        <option value="">Todos</option>

                        {motivos.map(motivo => (
                            <option
                                key={motivo.valor}
                                value={motivo.valor}
                            >
                                {motivo.descricao}
                            </option>
                        ))}

                    </select>

                </div>

                <div className="grupo-filtro">

                    <h4>Detalhamento</h4>

                    <select
                        value={filtros.complemento}
                        disabled={!filtros.motivo}
                        onChange={(e) =>
                            setFiltros(prev => ({
                                ...prev,
                                complemento: e.target.value
                            }))
                        }
                    >

                        <option value="">
                            {!filtros.motivo
                                ? "Selecione um motivo"
                                : "Todos"}
                        </option>

                        {motivos
                            .find(m => m.valor === filtros.motivo)
                            ?.complementos
                            ?.map(complemento => (

                                <option
                                    key={complemento.valor}
                                    value={complemento.valor}
                                >
                                    {complemento.descricao}
                                </option>

                            ))}

                    </select>

                </div>

                <div className="grupo-filtro">

                    <h4>Data abertura</h4>

                    <input
                        type="date"
                        value={filtros.dataAbInicial}
                        onChange={(e)=>setFiltros(prev=>({
                            ...prev,
                            dataAbInicial:e.target.value
                        }))}
                    />

                    <input
                        type="date"
                        value={filtros.dataAbFinal}
                        onChange={(e)=>setFiltros(prev=>({
                            ...prev,
                            dataAbFinal:e.target.value
                        }))}
                    />

                </div>

                <div className="grupo-filtro">

                    <h4>Data encerramento</h4>

                    <input
                        type="date"
                        value={filtros.dataEnInicial}
                        onChange={(e)=>setFiltros(prev=>({
                            ...prev,
                            dataEnInicial:e.target.value
                        }))}
                    />

                    <input
                        type="date"
                        value={filtros.dataEnFinal}
                        onChange={(e)=>setFiltros(prev=>({
                            ...prev,
                            dataEnFinal:e.target.value
                        }))}
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

export default ModalFiltrosDemanda;