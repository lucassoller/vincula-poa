function ModalVinculacaoManual({
                                   unidades,
                                   unidadeSelecionada,
                                   setUnidadeSelecionada,
                                   onSalvar,
                                   onFechar,
                                   mensagem,
                                   setMensagem,
                               }) {
    return (
        <div className="modal-overlay">
            <div className="modal-card">

                <div className="modal-header">
                    <div>
                        <h2>Vinculação manual</h2>
                        <p>Selecione a unidade de saúde responsável pelo usuário</p>
                    </div>

                    <span
                        className="modal-close"
                        onClick={onFechar}
                    >
                        ✕
                    </span>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                <div className="form-group">
                    <label>
                        Unidade de saúde <span>*</span>
                    </label>

                    <select
                        className="input-field"
                        value={unidadeSelecionada}
                        onChange={(e) =>
                            setUnidadeSelecionada(e.target.value)
                        }
                    >
                        <option value="">
                            Selecione uma unidade
                        </option>

                        {unidades.map((unidade) => (
                            <option
                                key={unidade.id}
                                value={unidade.id}
                            >
                                {unidade.nome}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="modal-actions">
                    <span
                        onClick={onSalvar}
                        className="buscar-btn"
                    >
                        Vincular e cadastrar
                    </span>

                    <span
                        className="buscar-btn"
                        onClick={onFechar}
                    >
                        Cancelar
                    </span>
                </div>

            </div>
        </div>
    );
}

export default ModalVinculacaoManual;