function ModalTentativaContato({ demanda, tentativa, setTentativa, erros, onSalvar, onFechar, mensagem, setMensagem }) {
    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h2>Registrar tentativa de contato</h2>
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
                        <label>Tipo de tentativa <span>*</span></label>
                        <select
                            className="input-field"
                            value={tentativa.tipo}
                            onChange={(e) => setTentativa({ ...tentativa, tipo: e.target.value })}
                        >
                            <option value="">Selecione</option>
                            <option value="LIGACAO">Ligação</option>
                            <option value="WHATSAPP">WhatsApp</option>
                            <option value="VISITA">Visita domiciliar</option>
                            <option value="OUTRO">Outro</option>
                        </select>
                        {erros.tipo && <small>{erros.tipo}</small>}
                    </div>

                    <div className="form-group">
                        <label>Descrição <span>*</span></label>
                        <textarea
                            className="input-field textarea-field"
                            value={tentativa.descricao}
                            onChange={(e) => setTentativa({ ...tentativa, descricao: e.target.value })}
                            maxLength={500}
                        />
                        {erros.descricao && <small>{erros.descricao}</small>}
                    </div>

                    <div className="modal-actions">
                        <span onClick={onSalvar} className="buscar-btn">Registrar tentativa</span>
                        <span className="buscar-btn" onClick={onFechar}>Cancelar</span>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ModalTentativaContato;