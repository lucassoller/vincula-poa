function ModalRedirecionarDemanda({demanda, unidades, redirecionamento, setRedirecionamento, erros, onSalvar, onFechar, mensagem, setMensagem}) {
    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h2>Redirecionar demanda</h2>
                        <p>Demanda #{demanda.id}</p>
                    </div>

                    <button className="modal-close" onClick={onFechar}>✕</button>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button
                            type="button"
                            onClick={() => setMensagem("")}
                        >
                            ✕
                        </button>
                    </div>
                )}

                <form onSubmit={onSalvar}>
                    <div className="form-group">
                        <label>Nova unidade responsável <span>*</span></label>

                        <select
                            className="input-field"
                            value={redirecionamento.novaUnidadeResponsavelId}
                            onChange={(e) =>
                                setRedirecionamento({
                                    ...redirecionamento,
                                    novaUnidadeResponsavelId: e.target.value,
                                })
                            }
                        >
                            <option value="">Selecione</option>

                            {unidades.map((u) => (
                                <option key={u.id} value={u.id}>
                                    {u.nome}
                                </option>
                            ))}
                        </select>

                        {erros.novaUnidadeResponsavelId && (
                            <small>{erros.novaUnidadeResponsavelId}</small>
                        )}
                    </div>

                    <div className="form-group">
                        <label>Motivo do redirecionamento <span>*</span></label>

                        <textarea
                            className="input-field textarea-field"
                            value={redirecionamento.motivoRedirecionamento}
                            maxLength={500}
                            onChange={(e) =>
                                setRedirecionamento({
                                    ...redirecionamento,
                                    motivoRedirecionamento: e.target.value,
                                })
                            }
                        />

                        {erros.motivoRedirecionamento && (
                            <small>{erros.motivoRedirecionamento}</small>
                        )}
                    </div>

                    <div className="modal-actions">
                        <button type="submit" className="buscar-btn">Redirecionar demanda</button>
                        <button type="button" className="buscar-btn" onClick={onFechar}>Cancelar</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ModalRedirecionarDemanda;