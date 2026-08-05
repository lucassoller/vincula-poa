function ModalTransferirServidor({servidor, unidades, servicos, especializados, transferencia, setTransferencia, erros, onSalvar, onFechar, mensagem, setMensagem}) {
    let opcoes =
        transferencia.perfil === "SERVIDOR_APS"
            ? unidades
            : [];

    if (transferencia.perfil === "SOLICITANTE") {
        if (transferencia.tipoServico === "SERVICO_ESPECIALIZADO") {
            opcoes = especializados;
        } else if (transferencia.tipoServico === "OUTRO") {
            opcoes = servicos;
        } else {
            opcoes = [];
        }
    }

    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h2>Transferir servidor</h2>
                        <p>Servidor #{servidor.id}</p>
                    </div>

                    <span className="modal-close" onClick={onFechar}>
                        ✕
                    </span>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                <form onSubmit={onSalvar}>
                    <div className="form-group">
                        <label>
                            Perfil <span>*</span>
                        </label>

                        <select
                            className="input-field"
                            value={transferencia.perfil}
                            onChange={(e) =>
                                setTransferencia({
                                    ...transferencia,
                                    perfil: e.target.value,
                                    unidadeSaudeId: ""
                                })
                            }
                        >
                            <option value="SERVIDOR_APS">
                                Servidor APS
                            </option>

                            <option value="SOLICITANTE">
                                Solicitante
                            </option>
                        </select>

                        {erros.perfil && (
                            <small>{erros.perfil}</small>
                        )}
                    </div>

                    {transferencia.perfil === "SOLICITANTE" && (
                        <div className="form-group">
                            <label>
                                Tipo do serviço <span>*</span>
                            </label>

                            <select
                                className="input-field"
                                value={transferencia.tipoServico}
                                onChange={(e) =>
                                    setTransferencia({
                                        ...transferencia,
                                        tipoServico: e.target.value,
                                        unidadeSaudeId: ""
                                    })
                                }
                            >
                                <option value="">Selecione</option>
                                <option value="SERVICO_ESPECIALIZADO">Serviço especializado</option>
                                <option value="OUTRO">Outro</option>
                            </select>
                        </div>
                    )}

                    <div className="form-group">
                        <label>
                            {transferencia.perfil === "SERVIDOR_APS"
                                ? "Nova UBS "
                                : "Novo serviço "}
                            <span>*</span>
                        </label>

                        <select
                            className="input-field"
                            value={transferencia.unidadeSaudeId}
                            onChange={(e) =>
                                setTransferencia({
                                    ...transferencia,
                                    unidadeSaudeId: e.target.value
                                })
                            }
                        >
                            <option value="">
                                Selecione
                            </option>

                            {opcoes.map((u) => (
                                <option
                                    key={u.id}
                                    value={u.id}
                                >
                                    {u.nome}
                                </option>
                            ))}
                        </select>

                        {erros.unidadeSaudeId && (
                            <small>{erros.unidadeSaudeId}</small>
                        )}
                    </div>

                    <div className="modal-actions">
                        <button
                            type="submit"
                            className="buscar-btn"
                        >
                            Transferir servidor
                        </button>

                        <button
                            type="button"
                            className="buscar-btn"
                            onClick={onFechar}
                        >
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ModalTransferirServidor;