import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuth } from "../context/AuthContext.jsx";
import "./demandaCadastro.css";

const formInicial = {
    motivoBuscaAtiva: "",
    descricaoBusca: "",
    prazoDemanda: "",
    pacienteId: "",
    unidadeResponsavelId: "",
};

function DemandaCadastro() {
    const navigate = useNavigate();
    const { usuario } = useAuth();

    const [form, setForm] = useState(formInicial);
    const [pacientes, setPacientes] = useState([]);
    const [unidades, setUnidades] = useState([]);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");

    useEffect(() => {
        async function carregarDados() {
            try {
                const [pacientesRes, unidadesRes] = await Promise.all([
                    api.get("/pacientes"),
                    api.get("/unidades-saude"),
                ]);

                setPacientes(pacientesRes.data);
                setUnidades(unidadesRes.data);

                if (usuario?.perfil === "EXECUTOR_APS") {
                    setForm((prev) => ({
                        ...prev,
                        unidadeResponsavelId: usuario.unidadeSaudeId || "",
                    }));
                }
            } catch {
                setMensagem("Erro ao carregar dados.");
            }
        }

        void carregarDados();
    }, [usuario]);

    function alterar(e) {
        const { name, value } = e.target;

        setForm({
            ...form,
            [name]: value,
        });

        setErros((prev) => {
            const novos = { ...prev };
            delete novos[name];
            return novos;
        });
    }

    async function salvar(e) {
        e.preventDefault();
        setMensagem("");
        setErros({});

        try {
            const payload = {
                ...form,
                motivoBuscaAtiva: form.motivoBuscaAtiva || null,
                prazoDemanda: form.prazoDemanda || null,
                pacienteId: form.pacienteId ? Number(form.pacienteId) : null,
                unidadeResponsavelId: form.unidadeResponsavelId
                    ? Number(form.unidadeResponsavelId)
                    : null,
            };

            await api.post("/demandas", payload);

            setMensagem("Demanda cadastrada com sucesso!");
            limparFormDemanda();
        } catch (error) {
            if (error.response?.data?.errors) {
                setErros(error.response.data.errors);
                setMensagem(error.response.data.message || "Dados inválidos");
            } else {
                setMensagem(error.response?.data?.message || "Erro ao cadastrar demanda.");
            }
        }
    }

    function limparFormDemanda() {
        setForm({
            ...formInicial,
            unidadeResponsavelId:
                usuario?.perfil === "EXECUTOR_APS"
                    ? usuario.unidadeSaudeId
                    : "",
        });

        setErros({});
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

                <form className="cadastro-card" onSubmit={salvar}>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Paciente <span>*</span></label>
                            <select
                                className="input-field"
                                name="pacienteId"
                                value={form.pacienteId}
                                onChange={alterar}
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
                            <label>Unidade responsável <span>*</span></label>
                            <select
                                className="input-field"
                                name="unidadeResponsavelId"
                                value={form.unidadeResponsavelId}
                                onChange={alterar}
                                disabled={usuario?.perfil === "EXECUTOR_APS"}
                            >
                                <option value="">Selecione</option>
                                {unidades.map((u) => (
                                    <option key={u.id} value={u.id}>
                                        {u.nome}
                                    </option>
                                ))}
                            </select>
                            {erros.unidadeResponsavelId && <small>{erros.unidadeResponsavelId}</small>}
                        </div>
                    </div>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Motivo da busca <span>*</span></label>
                            <select
                                className="input-field"
                                name="motivoBuscaAtiva"
                                value={form.motivoBuscaAtiva}
                                onChange={alterar}
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
                                name="prazoDemanda"
                                value={form.prazoDemanda}
                                onChange={alterar}
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
                                name="descricaoBusca"
                                value={form.descricaoBusca}
                                onChange={alterar}
                                placeholder="Descreva informações importantes para a busca ativa"
                                maxLength={500}
                            />
                            {erros.descricaoBusca && <small>{erros.descricaoBusca}</small>}
                        </div>
                    </div>

                    <div className="form-actions">
                        <button type="submit" className="primary-btn">
                            Cadastrar
                        </button>

                        <button
                            type="button"
                            className="danger-btn"
                            onClick={() => navigate("/indicadores")}
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