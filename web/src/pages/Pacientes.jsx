import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./pacientes.css"
import {useNavigate} from "react-router-dom";
import {
    mascaraDocumento,
    mascaraTelefone
} from "../utils/mascaras.js";

function Pacientes() {
    const { usuario } = useAuth();
    const [pacientes, setPacientes] = useState([]);
    const navigate = useNavigate();
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);

    useEffect(() => {
        let ativo = true;

        async function carregar() {
            try {
                setCarregando(true);

                let pacientesResponse;
                
                if (usuario?.perfil === "GESTAO_MUNICIPAL") {
                    pacientesResponse = await api.get("/pacientes");
                } else if (usuario?.perfil === "EXECUTOR_APS") {
                    pacientesResponse = await api.get(
                        `/pacientes/unidadeSaude/${usuario.unidadeSaudeId}`
                    );
                } else {
                    setMensagem("Seu perfil não possui acesso à lista de pacientes.");
                    setPacientes([]);
                    return;
                }

                if (ativo) {
                    setPacientes(pacientesResponse.data);
                }
            } catch {
                if (ativo) {
                    setMensagem("Erro ao carregar pacientes.");
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        void carregar();

        return () => {
            ativo = false;
        };
    }, [usuario]);

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando pacientes...
                </div>
            </div>
        );
    }

    async function abrirCardUbs(unidadeSaudeId) {
        try {
            setCarregandoUbs(true);

            const response = await api.get(`/unidades-saude/${unidadeSaudeId}`);

            setUbsSelecionada(response.data);
        } catch {
            setMensagem("Erro ao carregar dados da UBS.");
        } finally {
            setCarregandoUbs(false);
        }
    }

    return (
        <div className="pacientes-container">
            <div className="pacientes-page">
                <div className="pacientes-header">
                    <div>
                        <h1 className="pacientes-title">
                            Pacientes
                        </h1>
                        <p className="pacientes-subtitle">
                            Visualize e gerencie os pacientes cadastrados
                        </p>
                    </div>
                    <div className="perfil-badge">
                        {usuario?.perfil}
                    </div>
                </div>
                {mensagem && (
                    <div className="alerta-geral">
                        {mensagem}
                    </div>
                )}
                <div className="table-card">
                    <div className="table-topbar">
                        <input
                            className="paciente-search"
                            placeholder="Buscar paciente..."
                        />
                        <button className="novo-paciente-btn" onClick={() => navigate("/pacientes/cadastro")}>
                            + Novo paciente
                        </button>
                    </div>
                    <table className="pacientes-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Documento</th>
                            <th>Telefone</th>
                            <th>UBS</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        {pacientes.map((paciente) => (
                            <tr key={paciente.id}>
                                <td>
                                    <div className="paciente-nome">
                                        {paciente.nomeCompleto}
                                    </div>
                                </td>
                                <td>
                                    {mascaraDocumento(paciente.documento)}
                                </td>
                                <td>
                                    {mascaraTelefone(paciente.telefone)}
                                </td>
                                <td>
                                    <button
                                        type="button"
                                        className="ubs-badge ubs-clickable"
                                        onClick={() => abrirCardUbs(paciente.unidadeSaudeId)}
                                    >
                                        {paciente.unidadeSaudeNome}
                                    </button>
                                </td>
                                <td>

                                    <div className="acoes-container">
                                        <button className="btn-visualizar" onClick={() => navigate(`/pacientes/${paciente.id}`)}>
                                            Visualizar
                                        </button>
                                        <button className="btn-editar" onClick={() => navigate(`/pacientes/${paciente.id}/editar`)}>
                                            Editar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    {pacientes.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum paciente encontrado.
                        </div>
                    )}
                </div>
            </div>
            {ubsSelecionada && (
                <div className="ubs-overlay">
                    <div className="ubs-card">

                        <div className="ubs-card-header">
                            <div>
                                <h2>{ubsSelecionada.nome}</h2>
                                <p>Informações da Unidade Básica de Saúde</p>
                            </div>

                            <button
                                type="button"
                                className="ubs-close"
                                onClick={() => setUbsSelecionada(null)}
                            >
                                ✕
                            </button>
                        </div>

                        <div className="ubs-info-grid">
                            <CampoUbs label="CNES" valor={ubsSelecionada.cnes} />
                            <CampoUbs label="Telefone" valor={ubsSelecionada.telefone} />
                            <CampoUbs label="Email" valor={ubsSelecionada.email} />
                        </div>

                        <div className="ubs-section">
                            <h3>Endereço</h3>

                            <div className="ubs-info-grid">
                                <CampoUbs label="CEP" valor={ubsSelecionada.endereco?.cep} />
                                <CampoUbs label="Rua" valor={ubsSelecionada.endereco?.rua} />
                                <CampoUbs label="Número" valor={ubsSelecionada.endereco?.numero} />
                                <CampoUbs label="Bairro" valor={ubsSelecionada.endereco?.bairro} />
                                <CampoUbs label="Cidade" valor={ubsSelecionada.endereco?.cidade} />
                                <CampoUbs label="Estado" valor={ubsSelecionada.endereco?.estado} />
                            </div>
                        </div>

                        <div className="ubs-actions">
                            <button type="button" className="btn-editar" onClick={() => navigate(`/unidades-saude/${ubsSelecionada.id}/editar`)}>
                                Editar UBS
                            </button>
                        </div>

                    </div>
                </div>
            )}

            {carregandoUbs && (
                <div className="ubs-overlay">
                    <div className="ubs-card">
                        <p>Carregando dados da UBS...</p>
                    </div>
                </div>
            )}
        </div>


    );
}

function CampoUbs({ label, valor }) {
    return (
        <div className="ubs-campo">
            <span>{label}</span>
            <strong>{valor || "-"}</strong>
        </div>
    );
}

export default Pacientes;