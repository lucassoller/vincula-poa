function ModalEncerrarDemanda({ demanda, encerramento, setEncerramento, erros, onSalvar, onFechar, mensagem, setMensagem }) {
    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h2>Encerrar demanda</h2>
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
                        <label>Desfecho <span>*</span></label>

                        <select
                            className="input-field"
                            value={encerramento.desfechoDemanda}
                            onChange={(e) =>
                                setEncerramento({
                                    ...encerramento,
                                    desfechoDemanda: e.target.value,
                                })
                            }
                        >
                            <option value="">Selecione</option>
                            <option value="ENCONTRADO_VINCULADO">Encontrado e vinculado</option>
                            <option value="ENCONTRADO_RECUSOU">Encontrado e recusou</option>
                            <option value="NAO_LOCALIZADO">Não localizado</option>
                            <option value="ENDERECO_INCORRETO">Endereço incorreto</option>
                            <option value="MUDOU_TERRITORIO">Mudou de território</option>
                            <option value="OBITO">Óbito</option>
                            <option value="OUTRO">Outro</option>
                        </select>

                        {erros.desfechoDemanda && <small>{erros.desfechoDemanda}</small>}
                    </div>

                    <div className="form-group">
                        <label>Descrição do desfecho <span>*</span></label>

                        <textarea
                            className="input-field textarea-field"
                            value={encerramento.descricaoDesfecho}
                            maxLength={500}
                            onChange={(e) =>
                                setEncerramento({
                                    ...encerramento,
                                    descricaoDesfecho: e.target.value,
                                })
                            }
                        />
                        {erros.descricaoDesfecho && <small>{erros.descricaoDesfecho}</small>}
                    </div>

                    <div className="modal-actions">
                        <span onClick={onSalvar} className="buscar-btn">Encerrar demanda</span>
                        <span className="buscar-btn" onClick={onFechar}>Cancelar</span>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ModalEncerrarDemanda;