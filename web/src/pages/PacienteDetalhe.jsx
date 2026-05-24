import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./pacienteDetalhe.css";
import {mascaraDocumento, mascaraTelefone, mascaraCEP} from "../utils/mascaras";
import {sexoLabel} from "../utils/utils.js";

function PacienteDetalhe() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [paciente, setPaciente] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    useEffect(() => {
        let ativo = true;

        async function carregarPaciente() {
            try {
                setCarregando(true);

                const response = await api.get(`/pacientes/${id}`);

                if (ativo) {
                    setPaciente(response.data);
                }
            } catch {
                if (ativo) {
                    setErro("Erro ao carregar paciente.");
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        void carregarPaciente();

        return () => {
            ativo = false;
        };
    }, [id]);

    if (carregando) {
        return (
            <div className="paciente-detalhe-container">
                <div className="paciente-detalhe-page">
                    <p>Carregando paciente...</p>
                </div>
            </div>
        );
    }

    if (erro) {
        return (
            <div className="paciente-detalhe-container">
                <div className="paciente-detalhe-page">
                    <div className="alerta-geral">{erro}</div>
                    <button className="btn-voltar" onClick={() => navigate("/pacientes")}>
                        Voltar
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="paciente-detalhe-container">
            <div className="paciente-detalhe-page">
                <div className="detalhe-header">
                    <div>
                        <h1>{paciente.nomeCompleto}</h1>
                        <p>Detalhes do paciente cadastrado</p>
                    </div>

                    <button className="buscar-btn" onClick={() => navigate("/pacientes")}>
                        Voltar
                    </button>
                </div>

                <div className="detalhe-card">
                    <h2>Dados pessoais</h2>

                    <div className="detalhe-grid">
                        <Campo label="CPF/CNS" valor={mascaraDocumento(paciente.documento)} />
                        <Campo label="Telefone" valor={mascaraTelefone(paciente.telefone)} />
                        <Campo label="Sexo" valor={sexoLabel[paciente.sexo]} />
                        <Campo label="Data de nascimento" valor={paciente.dataNascimento
                            ?.split("-")
                            .reverse()
                            .join("/")} />
                    </div>
                </div>

                <div className="detalhe-card">
                    <h2>Unidade vinculada</h2>

                    <div className="detalhe-grid">
                        <Campo label="UBS" valor={paciente.unidadeSaudeNome || paciente.unidadeSaudeId} />
                    </div>
                </div>

                <div className="detalhe-card">
                    <h2>Endereço</h2>

                    <div className="detalhe-grid">
                        <Campo label="CEP" valor={mascaraCEP(paciente.endereco?.cep)} />
                        <Campo label="Rua" valor={paciente.endereco?.rua} />
                        <Campo label="Número" valor={paciente.endereco?.numero} />
                        <Campo label="Bairro" valor={paciente.endereco?.bairro} />
                        <Campo label="Cidade" valor={paciente.endereco?.cidade} />
                        <Campo label="Estado" valor={paciente.endereco?.estado} />
                    </div>
                </div>
            </div>
        </div>
    );
}

function Campo({ label, valor }) {
    return (
        <div className="campo-detalhe">
            <span>{label}</span>
            <strong>{valor || "-"}</strong>
        </div>
    );
}

export default PacienteDetalhe;