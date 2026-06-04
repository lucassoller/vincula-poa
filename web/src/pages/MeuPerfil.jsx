import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import api from "../api/api.js";
import {useForm} from "react-hook-form";

function MeuPerfil() {
    const {servidor, setServidor} = useAuth();
    const {
        register,
        handleSubmit,
    } = useForm({
        defaultValues: {
            nome: servidor?.nome,
            email: servidor?.email,
            login: servidor?.login,
        }
    });
    const navigate = useNavigate();
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");


    async function salvar(dados) {
        try {
            const response = await api.put("/servidores/me", dados);
            setServidor(response.data);
            localStorage.setItem("servidor", JSON.stringify(response.data));
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

                <form className="cadastro-card" onSubmit={handleSubmit(salvar)}>
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>Nome</label>
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
                            <label>Email</label>
                            <input
                                className="input-field"
                                type="email"
                                {...register("email")}
                            />
                            {erros.email && (
                                <small>{erros.email}</small>
                            )}
                        </div>

                        <div className="form-group">
                            <label>Login</label>
                            <input
                                className="input-field"
                                {...register("login")}
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

export default MeuPerfil;