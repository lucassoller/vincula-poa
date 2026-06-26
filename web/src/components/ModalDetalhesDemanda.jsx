import CampoDetalhe from "./CampoDetalhe";
import {
    desfechoLabel,
    formatarDataHora,
    motivoBuscaLabel,
    motivoComplementoLabel,
    prazoLabel,
    statusLabel,
    prioridadeLabel,
    tentativaContatoLabel,
    diasRestantes
} from "../utils/utils.js";

function ModalDetalhesDemanda({ demanda, tentativasContato, onFechar }) {
    return (
        <div className="modal-overlay">
            <div className="demanda-detalhe-card">

                <div className="modal-header">
                    <div>
                        <h2>Detalhes da demanda</h2>
                        <p>Demanda #{demanda.id}</p>
                    </div>

                    <span className="modal-close" onClick={onFechar}>✕</span>
                </div>

                <div className="detalhe-section">
                    <h3>Informações gerais</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe label="Usuário" valor={demanda.usuarioNome} />
                        <CampoDetalhe label="Motivo da busca" valor={motivoBuscaLabel[demanda.motivoBuscaAtiva]} />
                        <CampoDetalhe label="Detalhamento do motivo da busca" valor={motivoComplementoLabel[demanda.motivoComplemento]} />
                        <CampoDetalhe label="Prioridade" valor={prioridadeLabel[demanda.prioridade]} />
                        <CampoDetalhe label="Status" valor={statusLabel[demanda.status]} />
                        <CampoDetalhe label="Descrição da busca" valor={demanda.descricaoBusca} />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Prazos</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe label="Prazo" valor={prazoLabel[demanda.prazoDemanda]} />
                        <CampoDetalhe label="Criada em" valor={formatarDataHora(demanda.dataHoraCriacao)} />
                        <CampoDetalhe label="Data limite" valor={formatarDataHora(demanda.dataHoraLimite)} />
                        <CampoDetalhe label="Dias restantes" valor={diasRestantes(demanda.dataHoraCriacao, demanda.dataHoraLimite)} />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Serviços de Saúde</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe
                            label="Serviço solicitante"
                            valor={demanda.unidadeSolicitanteNome}
                        />

                        <CampoDetalhe
                            label="Unidade responsável"
                            valor={demanda.unidadeResponsavelNome}
                        />

                        <CampoDetalhe
                            label="Unidade anterior"
                            valor={demanda.unidadeResponsavelAnteriorNome}
                        />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Servidores</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe label="Criada por" valor={demanda.servidorCriadorNome} />
                        <CampoDetalhe label="Redirecionada por" valor={demanda.servidorRedirecionamentoNome} />
                        <CampoDetalhe label="Finalizada por" valor={demanda.servidorEncerramentoNome} />
                    </div>
                </div>


                {demanda.desfecho !== null && (
                    <div className="detalhe-section">
                        <h3>Desfecho</h3>

                        <div className="detalhe-grid">
                            <CampoDetalhe label="Desfecho" valor={desfechoLabel[demanda.desfecho]} />
                            <CampoDetalhe label="Descrição do desfecho" valor={demanda.descricaoDesfecho} />
                            <CampoDetalhe label="Data finalização" valor={formatarDataHora(demanda.dataHoraFinalizacao)} />
                        </div>
                    </div>
                )}

                {demanda.foiRedirecionada && (
                    <div className="detalhe-section">
                        <h3>Redirecionamento</h3>

                        <div className="detalhe-grid">
                            <CampoDetalhe label="Motivo redirecionamento" valor={demanda.motivoRedirecionamento} />
                            <CampoDetalhe label="Data redirecionamento" valor={formatarDataHora(demanda.dataHoraRedirecionamento)} />
                        </div>
                    </div>
                )}

                {tentativasContato.length > 0 && (

                <div className="detalhe-section">
                    <h3>Tentativas de contato</h3>

                        <div className="tentativas-lista">
                            {tentativasContato.map((t) => (
                                <div className="tentativa-card" key={t.id}>
                                    <div className="tentativa-top">
                                        <strong>{tentativaContatoLabel[t.tipo]}</strong>
                                        <span>{formatarDataHora(t.dataHora)}</span>
                                    </div>

                                    <p>{t.descricao}</p>

                                    <small>
                                        Registrado por: {t.servidorNome}
                                    </small>
                                </div>
                            ))}
                        </div>

                </div>
                )}

            </div>
        </div>
    );
}

export default ModalDetalhesDemanda;