import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import api from "../api/api.js";

function MeuPerfil() {
    const navigate = useNavigate();
    const {usuario, setUsuario} = useAuth();
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");

    const [form, setForm] = useState({
        nome: usuario?.nome,
        email: usuario?.email,
        login: usuario?.login,
    });

    function alterar(e) {
        setForm({
            ...form,
            [e.target.name]: e.target.value,
        });
    }

    async function salvar(e) {
        e.preventDefault();

        try {
            const response = await api.put("/usuarios/me", form);

            setUsuario(response.data);
            localStorage.setItem("usuario", JSON.stringify(response.data));

            setMensagem("Perfil atualizado com sucesso!");
        } catch (error) {
            const errors = error.response.data.errors;
            setErros(errors);
            setMensagem(
                error.response?.data?.message ||
                error.response?.data ||
                "Erro ao atualizar perfil"
            );
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Meu perfil</h1>
                        <p>Atualize seus dados de acesso ao sistema</p>
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button type="button" onClick={() => setMensagem("")}>
                            ✕
                        </button>
                    </div>
                )}

                <form className="cadastro-card" onSubmit={salvar}>
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>Nome</label>
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
                            <label>Email</label>
                            <input
                                className="input-field"
                                name="email"
                                type="email"
                                value={form.email}
                                onChange={alterar}
                            />
                            {erros.email && (
                                <small>{erros.email}</small>
                            )}
                        </div>

                        <div className="form-group">
                            <label>Login</label>
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

                    <div className="form-actions">
                        <button type="submit" className="buscar-btn">
                            Salvar alterações
                        </button>

                        <button
                            type="button"
                            className="limpar-btn"
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

export default MeuPerfil;