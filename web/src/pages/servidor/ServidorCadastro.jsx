import {useEffect, useState} from "react";
import api from "../../api/api.js";
import {useNavigate} from "react-router-dom";
import { useForm } from "react-hook-form";
import {useAuth} from "../../context/AuthContext.jsx";
import * as Promisse from "axios";
import {perfilLabel} from "../../utils/utils.js";

function ServidorCadastro() {
    const {
        reset,
        register,
        handleSubmit,
        watch,
    } = useForm({
        defaultValues: {
            nome: "",
            email: "",
            login: "",
            senha: "",
            confirmarSenha: "",
            perfil: "",
            unidadeSaudeId: ""
        }
    });
    const perfil = watch("perfil");
    const [erros, setErros] = useState({});
    const [unidades, setUnidades] = useState([]);
    const [servicos, setServicos] = useState([]);
    const navigate = useNavigate();
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [mostrarSenha, setMostrarSenha] = useState(false);
    const [mostrarConfirmarSenha, setMostrarConfirmarSenha] = useState(false);
    const { servidor } = useAuth();

    useEffect(() => {
        async function carregarUnidades() {
            try {
                const [ubsReponse, servicosResponse] = await Promisse.all( [
                    api.get("/unidades-saude/ubs"),
                    api.get("/unidades-saude/outros")
                ]);
                setUnidades(ubsReponse.data);
                setServicos(servicosResponse.data);
            } catch {
                setMensagem("Erro ao carregar serviços");
                setMensagemSucesso("")
            }
        }

        void carregarUnidades();
    }, []);



    async function salvar(dados) {
        setMensagem("");
        setMensagemSucesso("")
        try {
            const payload = {
                ...dados,

                perfil: dados.perfil || null,

                unidadeSaudeId: dados.unidadeSaudeId
                    ? Number(dados.unidadeSaudeId)
                    : null
            };
            await api.post("/servidores", payload);
            setMensagemSucesso("Servidor cadastrado com sucesso!");
            reset();
            setErros({});
        }catch (error) {
            if (error.response?.data?.errors) {
                const errors = error.response.data.errors;
                setErros(errors);
                setMensagem(error.response.data.message || "Dados inválidos");
            } else {
                setMensagem(error.response.data.message);
            }
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Novo servidor</h1>
                        <p>
                            Cadastre servidores para acesso ao sistema
                        </p>
                    </div>
                    <div className="perfil-badge">
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.unidadeSaude}
                    </div>
                </div>
                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <span onClick={() => setMensagemSucesso("")}>✕</span>
                    </div>
                )}
                <form className="cadastro-card" onSubmit={handleSubmit(salvar)}>
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>
                                Nome <span>*</span>
                            </label>
                            <input
                                className="input-field"
                                {...register("nome")}
                            />
                            {erros.nome && (
                                <small>{erros.nome}</small>
                            )}
                        </div>
                    </div>
                    <div className="form-grid two">
                        <div className="form-group">
                            <label>
                                Email <span>*</span>
                            </label>
                            <input
                                className="input-field"
                                {...register("email")}
                                placeholder="name@example.com"
                            />
                            {erros.email && (
                                <small>{erros.email}</small>
                            )}
                        </div>
                        <div className="form-group">
                            <label>
                                Login <span>*</span>
                            </label>
                            <input
                                className="input-field"
                                {...register("login")}
                            />
                            {erros.login && (
                                <small>{erros.login}</small>
                            )}
                        </div>
                    </div>
                    <div className="form-grid two">
                        <div className="form-group">
                            <label>
                                Senha <span>*</span>
                            </label>
                            <div className="input-wrapper">
                                <input
                                    className="input-field senha-input"
                                    type={mostrarSenha ? "text" : "password"}
                                    {...register("senha")}
                                />

                                <span
                                    className="eye"
                                    onClick={() =>
                                        setMostrarSenha(!mostrarSenha)
                                    }
                                >
                                    <img
                                        src={
                                            mostrarSenha
                                                ? "/eye2.svg"
                                                : "/eye.svg"
                                        }
                                        alt="mostrar senha"
                                        width={20}
                                    />
                                </span>
                            </div>
                            {erros.senha && (
                                <small>{erros.senha}</small>
                            )}
                        </div>
                        <div className="form-group">
                            <label>
                                Confirmar senha <span>*</span>
                            </label>
                            <div className="input-wrapper">
                                <input
                                    className="input-field senha-input"
                                    type={mostrarConfirmarSenha ? "text" : "password"}
                                    {...register("confirmarSenha")}
                                />

                                <span
                                    className="eye"
                                    onClick={() =>
                                        setMostrarConfirmarSenha(!mostrarConfirmarSenha)
                                    }
                                >
                                    <img
                                        src={
                                            mostrarConfirmarSenha
                                                ? "/eye2.svg"
                                                : "/eye.svg"
                                        }
                                        alt="mostrar senha"
                                        width={20}
                                    />
                                </span>
                            </div>
                            {erros.confirmarSenha && (
                                <small>{erros.confirmarSenha}</small>
                            )}
                        </div>
                    </div>
                    <div className="form-grid two">
                        <div className="form-group">
                            <label>
                                Perfil <span>*</span>
                            </label>
                            <select
                                className="input-field"
                                {...register("perfil")}
                            >
                                <option value="">
                                    Selecionar
                                </option>
                                <option value="GESTAO_MUNICIPAL">
                                    Gestão Municipal
                                </option>
                                <option value="VIGILANCIA">
                                    Vigilância
                                </option>
                                <option value="COORDENADORIA">
                                    Coordenadoria
                                </option>
                                <option value="SOLICITANTE">
                                    Solicitante
                                </option>
                                <option value="SERVIDOR_APS">
                                    Servidor APS
                                </option>
                            </select>
                            {erros.perfil && (
                                <small>{erros.perfil}</small>
                            )}
                        </div>
                        {(perfil === "SOLICITANTE" || perfil === "SERVIDOR_APS") && (
                            <div className="form-group">
                                <label>
                                    Serviço de Saúde <span> *</span>
                                </label>

                                <select
                                    className="input-field"
                                    {...register("unidadeSaudeId")}
                                >
                                    <option value="">Selecione</option>

                                    {(perfil === "SERVIDOR_APS" ? unidades : perfil === "SOLICITANTE" ? servicos : []).map((u) => (
                                        <option key={u.id} value={u.id}>
                                            {u.nome}
                                        </option>
                                    ))}
                                </select>

                                {erros.unidadeSaudeId && (
                                    <small>{erros.unidadeSaudeId}</small>
                                )}
                            </div>
                        )}
                    </div>
                    <div className="form-actions">
                        <span
                            onClick={handleSubmit(salvar)}
                            className="buscar-btn"
                        >
                            Cadastrar
                        </span>
                        <span
                            className="buscar-btn"
                            onClick={() => navigate("/servidores")}
                        >
                            Cancelar
                        </span>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ServidorCadastro;