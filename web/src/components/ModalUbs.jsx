import CampoDetalhe from "./CampoDetalhe.jsx";
import { useNavigate } from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";
import {tipoServico} from "../utils/utils.js";

function ModalUbs({ ubsSelecionada, setUbsSelecionada }) {
    const navigate = useNavigate();
    const { servidor } = useAuth();

    return (
        <div className="modal-overlay">
            <div className="demanda-detalhe-card">

                <div className="modal-header">
                    <div>
                        <h2>{ubsSelecionada.nome}</h2>
                        <p>Informações do Serviço de Saúde</p>
                    </div>

                    <span className="modal-close" onClick={() => setUbsSelecionada(null)}>✕</span>
                </div>

                <div className="detalhe-grid">
                    <CampoDetalhe
                        label="CNES"
                        valor={ubsSelecionada.cnes}
                    />

                    <CampoDetalhe
                        label="Tipo de serviço"
                        valor={tipoServico[ubsSelecionada.tipoServico]}
                    />

                    <CampoDetalhe
                        label="Telefone"
                        valor={ubsSelecionada.telefone}
                    />

                    <CampoDetalhe
                        label="Telefone adicional"
                        valor={ubsSelecionada.telefone2}
                    />
                </div>

                <div className="detalhe-section">
                    <h3>Endereço</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe
                            label="Rua"
                            valor={ubsSelecionada.endereco?.rua}
                        />

                        <CampoDetalhe
                            label="Número"
                            valor={ubsSelecionada.endereco?.numero}
                        />

                        <CampoDetalhe
                            label="Bairro"
                            valor={ubsSelecionada.endereco?.bairro}
                        />

                        <CampoDetalhe
                            label="Complemento"
                            valor={ubsSelecionada.endereco?.complemento}
                        />

                        <CampoDetalhe
                            label="Cidade"
                            valor={ubsSelecionada.endereco?.cidade}
                        />

                        <CampoDetalhe
                            label="Estado"
                            valor={ubsSelecionada.endereco?.estado}
                        />
                    </div>
                </div>

                {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) && (
                    <div className="ubs-actions">
                        <span
                            className="btn-editar"
                            onClick={() =>
                                navigate(`/unidades-saude/${ubsSelecionada.id}/editar`)
                            }
                        >
                            Editar Serviço
                        </span>
                    </div>
                )}

            </div>
        </div>
    );
}

export default ModalUbs;