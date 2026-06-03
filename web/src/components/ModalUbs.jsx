import CampoDetalhe from "./CampoDetalhe.jsx";
import { useNavigate } from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

function ModalUbs({ ubsSelecionada, setUbsSelecionada }) {
    const navigate = useNavigate();
    const { usuario } = useAuth();

    return (
        <div className="modal-overlay">
            <div className="demanda-detalhe-card">

                <div className="modal-header">
                    <div>
                        <h2>{ubsSelecionada.nome}</h2>
                        <p>Informações da Unidade Básica de Saúde</p>
                    </div>

                    <button
                        type="button"
                        className="modal-close"
                        onClick={() => setUbsSelecionada(null)}
                    >
                        ✕
                    </button>
                </div>

                <div className="detalhe-grid">
                    <CampoDetalhe
                        label="CNES"
                        valor={ubsSelecionada.cnes}
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
                            label="Cidade"
                            valor={ubsSelecionada.endereco?.cidade}
                        />

                        <CampoDetalhe
                            label="Estado"
                            valor={ubsSelecionada.endereco?.estado}
                        />
                    </div>
                </div>

                {usuario?.perfil === 'GESTAO_MUNICIPAL' && (
                    <div className="ubs-actions">
                        <button
                            type="button"
                            className="btn-editar"
                            onClick={() =>
                                navigate(`/unidades-saude/${ubsSelecionada.id}/editar`)
                            }
                        >
                            Editar UBS
                        </button>
                    </div>
                )}

            </div>
        </div>
    );
}

export default ModalUbs;