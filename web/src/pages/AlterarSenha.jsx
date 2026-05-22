import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api.js";

function AlterarSenha() {
    const navigate = useNavigate();
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");

    const [form, setForm] = useState({
        senhaAtual: "",
        novaSenha: "",
        confirmarSenha: "",
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
            await api.put("/usuarios/me/senha", form);

            setMensagem("Senha alterada com sucesso!");

            setForm({
                senhaAtual: "",
                novaSenha: "",
                confirmarSenha: "",
            });
        } catch (error) {
            const data = error.response?.data;
            setErros(data?.errors || {});
            setMensagem(
                typeof data === "string"
                    ? data
                    : data?.message || "Erro ao alterar senha"
            );
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Alterar senha</h1>
                        <p>Atualize sua senha de acesso ao sistema</p>
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
                            <label>Senha atual</label>
                            <input
                                className="input-field"
                                name="senhaAtual"
                                type="password"
                                value={form.senhaAtual}
                                onChange={alterar}
                            />
                            {erros.senhaAtual && (
                                <small>{erros.senhaAtual}</small>
                            )}
                        </div>
                    </div>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Nova senha</label>
                            <input
                                className="input-field"
                                name="novaSenha"
                                type="password"
                                value={form.novaSenha}
                                onChange={alterar}
                            />
                            {erros.novaSenha && (
                                <small>{erros.novaSenha}</small>
                            )}
                        </div>

                        <div className="form-group">
                            <label>Confirmar nova senha</label>
                            <input
                                className="input-field"
                                name="confirmarSenha"
                                type="password"
                                value={form.confirmarSenha}
                                onChange={alterar}
                            />
                            {erros.confirmarSenha && (
                                <small>{erros.confirmarSenha}</small>
                            )}
                        </div>
                    </div>

                    <div className="form-actions">
                        <button type="submit" className="buscar-btn">
                            Alterar senha
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

export default AlterarSenha;