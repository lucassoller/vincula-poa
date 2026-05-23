import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api.js";

function AlterarSenha() {

    const navigate = useNavigate();

    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");

    const [mostrarSenhaAtual, setMostrarSenhaAtual] = useState(false);
    const [mostrarNovaSenha, setMostrarNovaSenha] = useState(false);
    const [mostrarConfirmarSenha, setMostrarConfirmarSenha] = useState(false);

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

                            <label>Senha atual</label>

                            <div className="input-wrapper">

                                <input
                                    className="input-field senha-input"
                                    name="senhaAtual"
                                    type={mostrarSenhaAtual ? "text" : "password"}
                                    value={form.senhaAtual}
                                    onChange={alterar}
                                />

                                <span
                                    className="eye"
                                    onClick={() =>
                                        setMostrarSenhaAtual(!mostrarSenhaAtual)
                                    }
                                >
                                    <img
                                        src={
                                            mostrarSenhaAtual
                                                ? "/eye2.svg"
                                                : "/eye.svg"
                                        }
                                        alt="mostrar senha"
                                        width={20}
                                    />
                                </span>

                            </div>

                            {erros.senhaAtual && (
                                <small>{erros.senhaAtual}</small>
                            )}

                        </div>

                    </div>

                    <div className="form-grid two">

                        <div className="form-group">

                            <label>Nova senha</label>

                            <div className="input-wrapper">

                                <input
                                    className="input-field senha-input"
                                    name="novaSenha"
                                    type={mostrarNovaSenha ? "text" : "password"}
                                    value={form.novaSenha}
                                    onChange={alterar}
                                />

                                <span
                                    className="eye"
                                    onClick={() =>
                                        setMostrarNovaSenha(!mostrarNovaSenha)
                                    }
                                >
                                    <img
                                        src={
                                            mostrarNovaSenha
                                                ? "/eye2.svg"
                                                : "/eye.svg"
                                        }
                                        alt="mostrar senha"
                                        width={20}
                                    />
                                </span>

                            </div>

                            {erros.novaSenha && (
                                <small>{erros.novaSenha}</small>
                            )}

                        </div>

                        <div className="form-group">

                            <label>Confirmar nova senha</label>

                            <div className="input-wrapper">

                                <input
                                    className="input-field senha-input"
                                    name="confirmarSenha"
                                    type={mostrarConfirmarSenha ? "text" : "password"}
                                    value={form.confirmarSenha}
                                    onChange={alterar}
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

                    <div className="form-actions">

                        <button
                            type="submit"
                            className="buscar-btn"
                        >
                            Alterar senha
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

export default AlterarSenha;