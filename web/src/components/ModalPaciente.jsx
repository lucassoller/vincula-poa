import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import CampoDetalhe from "./CampoDetalhe.jsx";
import { mascaraDocumento, mascaraTelefone } from "../utils/mascaras";
import { sexoLabel } from "../utils/utils.js";

function ModalPaciente({ pacienteSelecionado, setPacienteSelecionado }) {
    const navigate = useNavigate();
    const { usuario } = useAuth();

    return (
        <div className="modal-overlay">
            <div className="demanda-detalhe-card">

                <div className="modal-header">
                    <div>
                        <h2>{pacienteSelecionado.nomeCompleto}</h2>
                        <p>Informações do paciente</p>
                    </div>

                    <button
                        type="button"
                        className="modal-close"
                        onClick={() => setPacienteSelecionado(null)}
                    >
                        ✕
                    </button>
                </div>

                <div className="detalhe-section">
                    <h3>Dados pessoais</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe
                            label="CPF/CNS"
                            valor={mascaraDocumento(
                                pacienteSelecionado.documento
                            )}
                        />

                        <CampoDetalhe
                            label="Telefone"
                            valor={mascaraTelefone(
                                pacienteSelecionado.telefone
                            )}
                        />

                        <CampoDetalhe
                            label="Sexo"
                            valor={
                                sexoLabel[
                                    pacienteSelecionado.sexo
                                    ]
                            }
                        />

                        <CampoDetalhe
                            label="Data de nascimento"
                            valor={pacienteSelecionado.dataNascimento
                                ?.split("-")
                                .reverse()
                                .join("/")}
                        />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Unidade vinculada</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe
                            label="UBS"
                            valor={
                                pacienteSelecionado.unidadeSaudeNome
                            }
                        />
                    </div>
                </div>

                <div className="detalhe-section">
                    <h3>Endereço</h3>

                    <div className="detalhe-grid">
                        <CampoDetalhe
                            label="Rua"
                            valor={
                                pacienteSelecionado.endereco?.rua
                            }
                        />

                        <CampoDetalhe
                            label="Número"
                            valor={
                                pacienteSelecionado.endereco?.numero
                            }
                        />

                        <CampoDetalhe
                            label="Bairro"
                            valor={
                                pacienteSelecionado.endereco?.bairro
                            }
                        />

                        <CampoDetalhe
                            label="Cidade"
                            valor={
                                pacienteSelecionado.endereco?.cidade
                            }
                        />

                        <CampoDetalhe
                            label="Estado"
                            valor={
                                pacienteSelecionado.endereco?.estado
                            }
                        />
                    </div>
                </div>

                {usuario?.perfil !== "SOLICITANTE" && (
                    <div className="ubs-actions">
                        <button
                            type="button"
                            className="btn-editar"
                            onClick={() =>
                                navigate(
                                    `/pacientes/${pacienteSelecionado.id}/editar`
                                )
                            }
                        >
                            Editar paciente
                        </button>
                    </div>
                )}

            </div>
        </div>
    );
}

export default ModalPaciente;