import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import CampoDetalhe from "./CampoDetalhe.jsx";
import { mascaraDocumento, mascaraTelefone } from "../utils/mascaras";
import { sexoLabel } from "../utils/utils.js";

function ModalUsuario({ usuarioSelecionado, setUsuarioSelecionado }) {
    const navigate = useNavigate();
    const { servidor } = useAuth();

    return (
        <div className="modal-overlay">
            <div className="demanda-detalhe-card">

                <div className="modal-header">
                    <div>
                        <h2>{usuarioSelecionado.nomeCompleto}</h2>
                        <p>Informações do usuário</p>
                    </div>

                    <button
                        type="button"
                        className="modal-close"
                        onClick={() => setUsuarioSelecionado(null)}
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
                                usuarioSelecionado.documento
                            )}
                        />

                        <CampoDetalhe
                            label="Telefone"
                            valor={mascaraTelefone(
                                usuarioSelecionado.telefone
                            )}
                        />

                        <CampoDetalhe
                            label="Sexo"
                            valor={
                                sexoLabel[
                                    usuarioSelecionado.sexo
                                    ]
                            }
                        />

                        <CampoDetalhe
                            label="Data de nascimento"
                            valor={usuarioSelecionado.dataNascimento
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
                                usuarioSelecionado.unidadeSaudeNome
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
                                usuarioSelecionado.endereco?.rua
                            }
                        />

                        <CampoDetalhe
                            label="Número"
                            valor={
                                usuarioSelecionado.endereco?.numero
                            }
                        />

                        <CampoDetalhe
                            label="Bairro"
                            valor={
                                usuarioSelecionado.endereco?.bairro
                            }
                        />

                        <CampoDetalhe
                            label="Complemento"
                            valor={
                                usuarioSelecionado.endereco?.complemento
                            }
                        />

                        <CampoDetalhe
                            label="Cidade"
                            valor={
                                usuarioSelecionado.endereco?.cidade
                            }
                        />

                        <CampoDetalhe
                            label="Estado"
                            valor={
                                usuarioSelecionado.endereco?.estado
                            }
                        />
                    </div>
                </div>

                <div className="ubs-actions">
                    <button
                        type="button"
                        className="btn-editar"
                        onClick={() =>
                            navigate(
                                `/usuarios/${usuarioSelecionado.id}/editar`
                            )
                        }
                    >
                        Editar usuario
                    </button>
                </div>

            </div>
        </div>
    );
}

export default ModalUsuario;