import "../styles/modalFiltrosDemanda.css";

function ModalFiltrosIndicador({
                                 aberto,
                                 onFechar,
                                 filtros,
                                 setFiltros,
                                 onAplicar,
                                 unidades = [],
                                 servicos = [],
                                 onLimpar,
                                 servidor
                             }) {

    if (!aberto) return null;

    return (
        <div className="overlay-filtros">

            <div className="modal-filtros">

                <div className="header-filtros">
                    <h2>Filtros</h2>

                    <button onClick={onFechar}>✕</button>
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

                    <h4>Data de criação</h4>

                    <input
                        type="date"
                        value={filtros.dataInicial}
                        onChange={(e)=>setFiltros(prev=>({
                            ...prev,
                            dataInicial:e.target.value
                        }))}
                    />

                    <input
                        type="date"
                        value={filtros.dataFinal}
                        onChange={(e)=>setFiltros(prev=>({
                            ...prev,
                            dataFinal:e.target.value
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

export default ModalFiltrosIndicador;