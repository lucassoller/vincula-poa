import {useEffect, useState} from "react";
import api from "../api/api";
import {useNavigate} from "react-router-dom";

function UsuarioCadastro() {
    const formInicial = {
        nome: "",
        email: "",
        login: "",
        senha: "",
        confirmarSenha: "",
        perfil: undefined,
        unidadeSaudeId: ""
    };
    
    const [erros, setErros] = useState({});

    const [unidades, setUnidades] = useState([]);

    const [form, setForm] = useState(formInicial);

    const navigate = useNavigate();

    const [mensagem, setMensagem] = useState("");

    function alterar(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

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

    async function salvar(e) {
        e.preventDefault();
        setMensagem("");

        try {
            const payload = {
                ...form,

                perfil: form.perfil || null,

                unidadeSaudeId: form.unidadeSaudeId
                    ? Number(form.unidadeSaudeId)
                    : null
            };
            await api.post("/usuarios", payload);
            setMensagem("Usuário cadastrado com sucesso!");
            setForm(formInicial);
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
                <form className="cadastro-card" onSubmit={salvar}>
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>
                                Nome <span>*</span>
                            </label>
                            <input
                                className="input-field"
                                name="nome"
                                value={form.nome}
                                onChange={alterar}
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
                                name="email"
                                value={form.email}
                                onChange={alterar}
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
                                name="login"
                                value={form.login}
                                onChange={alterar}
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
                                name="senha"
                                value={form.senha}
                                onChange={alterar}
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
                                name="confirmarSenha"
                                value={form.confirmarSenha}
                                onChange={alterar}
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
                                name="perfil"
                                value={form.perfil}
                                onChange={alterar}
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
                        {form.perfil === "EXECUTOR_APS" && (
                            <div className="form-group">
                                <label>
                                    Unidade Básica de Saúde <span>*</span>
                                </label>
                                <select
                                    className="input-field"
                                    name="unidadeSaudeId"
                                    value={form.unidadeSaudeId}
                                    onChange={alterar}
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