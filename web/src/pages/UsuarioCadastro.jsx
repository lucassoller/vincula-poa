import { useState } from "react";
import api from "../api/api";

function UsuarioCadastro() {
    const [form, setForm] = useState({
        nome: "",
        email: "",
        login: "",
        senha: "",
        perfil: "SOLICITANTE",
        unidadeSaudeId: "",
        observacao: "",
    });

    const [mensagem, setMensagem] = useState("");

    function alterar(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    async function salvar(e) {
        e.preventDefault();
        setMensagem("");

        const payload = {
            ...form,
            unidadeSaudeId: form.unidadeSaudeId ? Number(form.unidadeSaudeId) : null,
        };

        try {
            await api.post("/usuarios", payload);
            setMensagem("Usuário cadastrado com sucesso!");
        } catch {
            setMensagem("Erro ao cadastrar usuário.");
        }
    }

    return (
        <div>
            <h1>Cadastrar Usuário</h1>

            <form onSubmit={salvar} style={styles.form}>
                <input name="nome" placeholder="Nome" onChange={alterar} />
                <input name="email" placeholder="Email" onChange={alterar} />
                <input name="login" placeholder="Login" onChange={alterar} />
                <input name="senha" type="password" placeholder="Senha" onChange={alterar} />

                <select name="perfil" value={form.perfil} onChange={alterar}>
                    <option value="SOLICITANTE">Solicitante</option>
                    <option value="EXECUTOR_APS">Executor APS</option>
                    <option value="GESTAO_MUNICIPAL">Gestão Municipal</option>
                </select>

                <input name="unidadeSaudeId" placeholder="ID da unidade de saúde" onChange={alterar} />

                <textarea name="observacao" placeholder="Observação" onChange={alterar} />

                <button type="submit">Cadastrar</button>
            </form>

            {mensagem && <p>{mensagem}</p>}
        </div>
    );
}

const styles = {
    form: {
        display: "flex",
        flexDirection: "column",
        gap: "12px",
        maxWidth: "500px",
    },
};

export default UsuarioCadastro;