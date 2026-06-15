import { useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import "./esqueciSenha.css"

function EsqueciSenha() {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [carregando, setCarregando] = useState(false);

    async function recuperarSenha(e) {
        e.preventDefault();
        setMensagem("");

        try {
            setCarregando(true);
            await api.post("/auth/esqueci-senha", {
                email
            });

            setMensagemSucesso("Email de recuperação enviado com sucesso.");
            setMensagem("")
            setEmail("");

        } catch (error){
            setMensagemSucesso("")
            if (error.response?.data?.errors) {
                setMensagem(error.response.data.errors.email || "Dados inválidos");
            } else {
                setMensagem(error.response?.data?.message || "Erro ao redefinir senha");
            }
        } finally {
            setCarregando(false);
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Recuperar senha</h1>
                        <p>
                            Digite seu email para receber um link e recuperar sua senha
                        </p>
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
                <form
                    className="cadastro-card"
                    onSubmit={recuperarSenha}
                >
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>
                                Email <span>*</span>
                            </label>

                            <input
                                className="input-field"
                                placeholder="email@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="form-actions">

                        <button
                            type="submit"
                            className="buscar-btn"
                            disabled={carregando}
                        >
                            {carregando
                                ? "Carregando..."
                                : "Enviar email de recuperação"}
                        </button>

                        <span
                            className="buscar-btn"
                            onClick={() => navigate("/")}
                        >
                            Voltar
                        </span>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default EsqueciSenha;