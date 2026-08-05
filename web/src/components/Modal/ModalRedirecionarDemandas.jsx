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
                    <span
                        className="buscar-btn"
                        onClick={onConfirmar}
                    >
                        Sim, redirecionar
                    </span>

                    <span
                        className="limpar-btn"
                        onClick={onCancelar}
                    >
                        Não, apenas salvar usuário
                    </span>
                </div>
            </div>
        </div>
    );
}

export default ModalRedirecionarDemandas;