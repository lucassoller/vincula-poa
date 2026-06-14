import {useNavigate, useSearchParams} from "react-router-dom";
import { useState } from "react";
import api from "../api/api";
import {useAuth} from "../context/AuthContext.jsx";

function RedefinirSenha() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const [novaSenha, setNovaSenha] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [mostrarSenha, setMostrarSenha] = useState(false);
    const { servidor } = useAuth();

    async function redefinirSenha(e) {
        e.preventDefault();
        setMensagem("");

        try {

            await api.post("/auth/redefinir-senha", {
                token,
                novaSenha
            });

            setMensagemSucesso("Senha redefinida com sucesso.");
            setMensagem("")
            setNovaSenha("");

        } catch (error){
            setMensagemSucesso("")
            if (error.response?.data?.errors) {
                setMensagem(error.response.data.errors.novaSenha || "Dados inválidos");
            } else {
                setMensagem(error.response?.data?.message || "Erro ao redefinir senha.");
            }
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Redefinir senha</h1>
                        <p>
                            Digite sua nova senha
                        </p>
                    </div>
                    <div className="perfil-badge">
                        {servidor?.perfil === 'GESTAO_MUNICIPAL' ? servidor.perfil : servidor.unidadeSaude}
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

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <button type="button" onClick={() => setMensagemSucesso("")}>✕</button>
                    </div>
                )}
                <form
                    className="cadastro-card"
                    onSubmit={redefinirSenha}
                >
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>
                                Nova senha <span>*</span>
                            </label>

                            <div className="input-wrapper">
                                <input
                                    className="input-field senha-input"
                                    type={mostrarSenha ? "text" : "password"}
                                    value={novaSenha}
                                    onChange={(e) => setNovaSenha(e.target.value)}
                                />

                                <span
                                    className="eye"
                                    onClick={() => setMostrarSenha(!mostrarSenha)}
                                >
                                    <img
                                        src={mostrarSenha ? "/eye2.svg" : "/eye.svg"}
                                        alt="mostrar senha"
                                        width={20}
                                    />
                                </span>
                            </div>
                        </div>
                    </div>
                    <div className="form-actions">

                        <button
                            type="submit"
                            className="buscar-btn"
                        >
                            Redefinir senha
                        </button>

                        <button
                            type="button"
                            className="buscar-btn"
                            onClick={() => navigate("/")}
                        >
                            Voltar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default RedefinirSenha;