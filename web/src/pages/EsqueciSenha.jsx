import { useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import "./esqueciSenha.css"

function EsqueciSenha() {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(false);

    async function recuperarSenha(e) {
        e.preventDefault();
        setMensagem("");

        try {
            setCarregando(true);
            /*await api.post("/auth/esqueci-senha", {
                email
            });*/

            await api.get("/auth/teste-smtp");

            setMensagem("Email de recuperação enviado com sucesso.");

        } catch (error){
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

                        <button
                            type="button"
                            onClick={() => setMensagem("")}
                        >
                            ✕
                        </button>

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

export default EsqueciSenha;