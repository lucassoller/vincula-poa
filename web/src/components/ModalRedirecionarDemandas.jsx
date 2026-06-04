function ModalRedirecionarDemandas({
                                       onConfirmar,
                                       onCancelar,
                                   }) {

    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h2>Redirecionar demandas abertas?</h2>

                        <p>
                            A UBS vinculada ao usuário mudou.
                            Deseja redirecionar as demandas abertas
                            e em andamento para a nova UBS?
                        </p>
                    </div>
                </div>

                <div className="modal-actions">
                    <button
                        type="button"
                        className="buscar-btn"
                        onClick={onConfirmar}
                    >
                        Sim, redirecionar
                    </button>

                    <button
                        type="button"
                        className="limpar-btn"
                        onClick={onCancelar}
                    >
                        Não, apenas salvar usuário
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ModalRedirecionarDemandas;