import CampoDetalhe from "./CampoDetalhe";
import { formatarDataHora, prazoLabel } from "../utils/demandaUtils";

function ModalDetalhesDemanda({ demanda, tentativasContato, onFechar }) {
    return (
        <div className="modal-overlay">
            <div className="demanda-detalhe-card">

                <div className="modal-header">
                    <div>
                        <h2>Detalhes da demanda</h2>
                        <p>Demanda #{demanda.id}</p>
                    </div>

                    <button className="modal-close" onClick={onFechar}>✕</button>
                </div>

                <div className="detalhe-section">
                    <h3>Informações gerais</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe label="Paciente" valor={demanda.pacienteNome} />
                        <CampoDetalhe label="Motivo da busca" valor={demanda.motivoBuscaAtiva} />
                        <CampoDetalhe label="Status" valor={demanda.status} />
                        <CampoDetalhe label="Prazo" valor={prazoLabel[demanda.prazoDemanda]} />
                        <CampoDetalhe label="Criada em" valor={formatarDataHora(demanda.dataHoraCriacao)} />
                        <CampoDetalhe label="Data limite" valor={formatarDataHora(demanda.dataHoraLimite)} />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Unidades</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe
                            label="Unidade solicitante"
                            valor={demanda.unidadeSolicitanteNome || demanda.unidadeSolicitanteId}
                        />

                        <CampoDetalhe
                            label="Unidade responsável"
                            valor={demanda.unidadeResponsavelNome || demanda.unidadeResponsavelId}
                        />

                        <CampoDetalhe
                            label="Unidade anterior"
                            valor={demanda.unidadeResponsavelAnteriorNome || demanda.unidadeResponsavelAnteriorId}
                        />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Usuários</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe label="Criada por" valor={demanda.usuarioCriadorNome} />
                        <CampoDetalhe label="Redirecionada por" valor={demanda.usuarioRedirecionamentoNome} />
                        <CampoDetalhe label="Finalizada por" valor={demanda.usuarioEncerramentoNome} />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Desfecho</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe label="Desfecho" valor={demanda.desfecho} />
                        <CampoDetalhe label="Descrição do desfecho" valor={demanda.descricaoDesfecho} />
                        <CampoDetalhe label="Data finalização" valor={formatarDataHora(demanda.dataHoraFinalizacao)} />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Tentativas de contato</h3>

                    {tentativasContato.length === 0 ? (
                        <div className="empty-state">
                            Nenhuma tentativa registrada.
                        </div>
                    ) : (
                        <div className="tentativas-lista">
                            {tentativasContato.map((t) => (
                                <div className="tentativa-card" key={t.id}>
                                    <div className="tentativa-top">
                                        <strong>{t.tipo}</strong>
                                        <span>{formatarDataHora(t.dataHora)}</span>
                                    </div>

                                    <p>{t.descricao}</p>

                                    <small>
                                        Registrado por: {t.usuarioNome}
                                    </small>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

            </div>
        </div>
    );
}

export default ModalDetalhesDemanda;