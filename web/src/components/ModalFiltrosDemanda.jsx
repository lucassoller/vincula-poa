import "../styles/modalFiltrosDemanda.css";

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
                                onLimpar
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

    function limpar() {
        setFiltros({
            status: [],
            prioridade: [],
            tempo: [],
            unidade: "",
            servico: "",
            motivo: "",
            usuario: "",
            complemento: "",
            dataAbInicial: "",
            dataAbFinal: "",
            dataEnInicial: "",
            dataEnFinal: ""
        });

        onLimpar();
    }

    return (
        <div className="overlay-filtros">

            <div className="modal-filtros">

                <div className="header-filtros">
                    <h2>Filtros</h2>

                    <button onClick={onFechar}>✕</button>
                </div>

                <div className="grupo-filtro">

                    <h4>Usuário</h4>

                    <select
                        value={filtros.usuario}
                        onChange={(e) =>
                            setFiltros(prev => ({
                                ...prev,
                                usuario: e.target.value
                            }))
                        }
                    >
                        <option value="">Todos</option>

                        {usuarios.map(usuario => (
                            <option
                                key={usuario.id}
                                value={usuario.id}
                            >
                                {usuario.nomeCompleto}
                            </option>
                        ))}

                    </select>

                </div>

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

                        {servicos.map(servico => (
                            <option key={servico.id} value={servico.id}>
                                {servico.nome}
                            </option>
                        ))}

                    </select>

                </div>

                <div className="footer-filtros">

                    <button
                        className="btn-secundario"
                        onClick={limpar}
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