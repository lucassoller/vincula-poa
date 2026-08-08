function ModalRedirecionarDemanda({demanda, servicos, redirecionamento, setRedirecionamento, erros, onSalvar, onFechar, mensagem, setMensagem}) {
    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h2>Redirecionar demanda</h2>
                        <p>Demanda #{demanda.id}</p>
                    </div>

                    <span className="modal-close" onClick={onFechar}>✕</span>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                <form onSubmit={onSalvar}>
                    <div className="form-group">
                        <label>Novo serviço responsável <span>*</span></label>

                        <select
                            className="input-field"
                            value={redirecionamento.novaServicoResponsavelId}
                            onChange={(e) =>
                                setRedirecionamento({
                                    ...redirecionamento,
                                    novaServicoResponsavelId: e.target.value,
                                })
                            }
                        >
                            <option value="">Selecione</option>

                            {servicos.map((u) => (
                                <option key={u.id} value={u.id}>
                                    {u.nome}
                                </option>
                            ))}
                        </select>

                        {erros.novaServicoResponsavelId && (
                            <small>{erros.novaServicoResponsavelId}</small>
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
                        <span onClick={onSalvar} className="buscar-btn">Redirecionar demanda</span>
                        <span className="buscar-btn" onClick={onFechar}>Cancelar</span>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ModalRedirecionarDemanda;