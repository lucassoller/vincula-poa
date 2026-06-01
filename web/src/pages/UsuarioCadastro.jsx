import {useEffect, useState} from "react";
import api from "../api/api";
import {useNavigate} from "react-router-dom";
import { useForm } from "react-hook-form";

function UsuarioCadastro() {
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
    const navigate = useNavigate();
    const [mensagem, setMensagem] = useState("");

    useEffect(() => {
        async function carregarUnidades() {
            try {
                const response = await api.get("/unidades-saude/all");
                setUnidades(response.data);
            } catch {
                setMensagem("Erro ao carregar unidades");
            }
        }

        void carregarUnidades();
    }, []);

    async function salvar(dados) {
        setMensagem("");

        try {
            const payload = {
                ...dados,

                perfil: dados.perfil || null,

                unidadeSaudeId: dados.unidadeSaudeId
                    ? Number(dados.unidadeSaudeId)
                    : null
            };
            await api.post("/usuarios", payload);
            setMensagem("Usuário cadastrado com sucesso!");
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
                        <h1>Novo usuário</h1>
                        <p>
                            Cadastre usuários para acesso ao sistema
                        </p>
                    </div>
                </div>
                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button
                            type="button"
                            onClick={() => setMensagem("")}
                        >
                            ✕
                        </button>
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
                            <input
                                type="password"
                                className="input-field"
                                {...register("senha")}
                            />
                            {erros.senha && (
                                <small>{erros.senha}</small>
                            )}
                        </div>
                        <div className="form-group">
                            <label>
                                Confirmar senha <span>*</span>
                            </label>
                            <input
                                type="password"
                                className="input-field"
                                {...register("confirmarSenha")}
                            />
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
                                <option value="SOLICITANTE">
                                    Solicitante
                                </option>
                                <option value="EXECUTOR_APS">
                                    Executor APS
                                </option>
                                <option value="GESTAO_MUNICIPAL">
                                    Gestão Municipal
                                </option>
                            </select>
                            {erros.perfil && (
                                <small>{erros.perfil}</small>
                            )}
                        </div>
                        {perfil === "EXECUTOR_APS" && (
                            <div className="form-group">
                                <label>
                                    Unidade Básica de Saúde <span>*</span>
                                </label>
                                <select
                                    className="input-field"
                                    {...register("unidadeSaudeId")}
                                >
                                    <option value="">
                                        Selecione
                                    </option>
                                    {unidades.map((u) => (
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
                        <button
                            type="submit"
                            className="buscar-btn"
                        >
                            Cadastrar
                        </button>
                        <button
                            type="button"
                            className="buscar-btn"
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

export default UsuarioCadastro;