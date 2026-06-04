import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import api from "../api/api";
import { useAuth } from "../context/AuthContext.jsx";
import "./demandaCadastro.css";
import {useForm, useWatch} from "react-hook-form";

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
            usuarioId: ""
        }
    });
    const location = useLocation();
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [ubsUsuario, setUbsUsuario] = useState("");

    useEffect(() => {
        const usuario = usuarios.find(
            p => String(p.id) === String(location.state?.usuarioId)
        );

        if (usuario) {
            setValue("usuarioId", String(usuario.id));
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setUbsUsuario(usuario.unidadeSaudeNome || "");
        }
    }, [location.state, usuarios, setValue]);

    useEffect(() => {
        async function carregarDados() {
            try {
                const response = await api.get("/usuarios/all");
                setUsuarios(response.data);
            } catch {
                setMensagem("Erro ao carregar dados.");
            }
        }

        void carregarDados();
    }, [servidor]);

    async function salvar(dados) {
        setMensagem("");
        setErros({});

        try {
            const payload = {
                ...dados,
                motivoBuscaAtiva: dados.motivoBuscaAtiva || null,
                prazoDemanda: dados.prazoDemanda || null,
                usuarioId: dados.usuarioId ? Number(dados.usuarioId) : null,
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

    function handleUsuarioChange(event) {
        const usuarioId = Number(event.target.value);

        const usuarioSelecionado = usuarios.find(
            (p) => p.id === usuarioId
        );

        if (!usuarioSelecionado) {
            setUbsUsuario("");
            return;
        }

        setUbsUsuario(
            usuarioSelecionado.unidadeSaudeNome || ""
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
                            <label>Usuário <span>*</span></label>
                            <select
                                className="input-field"
                                {...register("usuarioId")}
                                onChange={handleUsuarioChange}
                            >
                                <option value="">Selecione</option>
                                {usuarios.map((p) => (
                                    <option key={p.id} value={p.id}>
                                        {p.nomeCompleto} - {p.documento}
                                    </option>
                                ))}
                            </select>
                            {erros.usuarioId && <small>{erros.usuarioId}</small>}
                        </div>
                        <div className="form-group">
                            <label>UBS do usuário</label>
                            <input
                                type="text"
                                className="input-field"
                                value={ubsUsuario}
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