import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuth } from "../context/AuthContext.jsx";
import "./demandaCadastro.css";
import {useForm} from "react-hook-form";

function DemandaCadastro() {
    const {
        reset,
        register,
        handleSubmit,
        setValue,
    } = useForm({
        defaultValues: {
            motivoBuscaAtiva: "",
            descricaoBusca: "",
            prazoDemanda: "",
            pacienteId: "",
            unidadeResponsavelId: "",
        }
    });
    const navigate = useNavigate();
    const { usuario } = useAuth();
    const [pacientes, setPacientes] = useState([]);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [ubsPaciente, setUbsPaciente] = useState("");

    useEffect(() => {
        async function carregarDados() {
            try {
                const response = await api.get("/pacientes/all");
                setPacientes(response.data);
                if (usuario?.perfil === "USUARIO_APS") {
                    setValue(
                        "unidadeResponsavelId",
                        usuario.unidadeSaudeId || ""
                    );
                }
            } catch {
                setMensagem("Erro ao carregar dados.");
            }
        }

        void carregarDados();
    }, [usuario, setValue]);

    async function salvar(dados) {
        setMensagem("");
        setErros({});

        try {
            const payload = {
                ...dados,
                motivoBuscaAtiva: dados.motivoBuscaAtiva || null,
                prazoDemanda: dados.prazoDemanda || null,
                pacienteId: dados.pacienteId ? Number(dados.pacienteId) : null,
                unidadeResponsavelId: dados.unidadeResponsavelId
                    ? Number(dados.unidadeResponsavelId)
                    : null,
            };

            await api.post("/demandas", payload);

            setMensagem("Demanda cadastrada com sucesso!");
            reset();
            setErros({});
        } catch (error) {
            if (error.response?.data?.errors) {
                const errors = error.response.data.errors;
                setErros(errors);
                setMensagem(error.response.data.message || "Dados inválidos");
            } else {
                setMensagem(error.response.data.message);
            }
        }
    }

    function handlePacienteChange(event) {
        const pacienteId = Number(event.target.value);

        const pacienteSelecionado = pacientes.find(
            (p) => p.id === pacienteId
        );

        if (!pacienteSelecionado) {
            setUbsPaciente("");
            setValue("unidadeResponsavelId", "");
            return;
        }

        setUbsPaciente(
            pacienteSelecionado.unidadeSaudeNome || ""
        );

        setValue(
            "unidadeResponsavelId",
            pacienteSelecionado.unidadeSaudeId || ""
        );
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">

                <div className="cadastro-header">
                    <div>
                        <h1>Nova demanda</h1>
                        <p>Cadastre uma solicitação de busca ativa para acompanhamento</p>
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button type="button" onClick={() => setMensagem("")}>✕</button>
                    </div>
                )}

                <form className="cadastro-card" onSubmit={handleSubmit(salvar)}>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Paciente <span>*</span></label>
                            <select
                                className="input-field"
                                {...register("pacienteId")}
                                onChange={handlePacienteChange}
                            >
                                <option value="">Selecione</option>
                                {pacientes.map((p) => (
                                    <option key={p.id} value={p.id}>
                                        {p.nomeCompleto} - {p.documento}
                                    </option>
                                ))}
                            </select>
                            {erros.pacienteId && <small>{erros.pacienteId}</small>}
                        </div>
                        <div className="form-group">
                            <label>UBS do paciente</label>
                            <input
                                type="text"
                                className="input-field"
                                value={ubsPaciente}
                                disabled
                            />
                        </div>
                    </div>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Motivo da busca <span>*</span></label>
                            <select
                                className="input-field"
                                {...register("motivoBuscaAtiva")}
                            >
                                <option value="">Selecione</option>
                                <option value="CONDICAO_SAUDE">Condição de saúde</option>
                                <option value="FALTOSO">Faltoso</option>
                                <option value="ABANDONO">Abandono</option>
                                <option value="OUTRO">Outro</option>
                            </select>
                            {erros.motivoBuscaAtiva && <small>{erros.motivoBuscaAtiva}</small>}
                        </div>

                        <div className="form-group">
                            <label>Prazo <span>*</span></label>
                            <select
                                className="input-field"
                                {...register("prazoDemanda")}
                            >
                                <option value="">Selecione</option>
                                <option value="D1">1 dia</option>
                                <option value="D2">2 dias</option>
                                <option value="D3">3 dias</option>
                                <option value="D7">7 dias</option>
                                <option value="D15">15 dias</option>
                                <option value="D20">20 dias</option>
                                <option value="D30">30 dias</option>
                            </select>
                            {erros.prazoDemanda && <small>{erros.prazoDemanda}</small>}
                        </div>
                    </div>

                    <div className="form-grid full">
                        <div className="form-group">
                            <label>Descrição da busca</label>
                            <textarea
                                className="input-field textarea-field"
                                {...register("descricaoBusca")}
                                placeholder="Descreva informações importantes para a busca ativa"
                                maxLength={500}
                            />
                            {erros.descricaoBusca && <small>{erros.descricaoBusca}</small>}
                        </div>
                    </div>

                    <div className="form-actions">
                        <button type="submit" className="buscar-btn">
                            Cadastrar
                        </button>

                        <button
                            type="button"
                            className="buscar-btn"
                            onClick={() => navigate("/demandas")}
                        >
                            Cancelar
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
}

export default DemandaCadastro;