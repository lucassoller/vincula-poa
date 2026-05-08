import { useState } from "react";
import api from "../api/api";

function UsuarioCadastro() {
    const [erros, setErros] = useState({});

    const [form, setForm] = useState({
        nome: "",
        email: "",
        login: "",
        senha: "",
        confirmarSenha: "",
        perfil: undefined,
        unidadeSaudeId: 1
    });

    const [mensagem, setMensagem] = useState("");

    function alterar(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    async function salvar(e) {
        e.preventDefault();
        setMensagem("");

        try {
            await api.post("/usuarios", form);
            setMensagem("Usuário cadastrado com sucesso!");
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
        <div className="p-container">
            <div className="p-body">
                <h1>Cadastrar Usuário</h1>
                {mensagem &&
                    <div className="alert alert-warning alert-dismissible fade show" role="alert">
                        <p>{mensagem}</p>
                        <button type="button" className="close" data-dismiss="alert" aria-label="Close" onClick={() => setMensagem("")}>
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>}

                <div className="p-form">
                    <form className={"p-form-child"} onSubmit={salvar}>
                                <label className="label">Nome <span className="p-required">*</span></label>
                                <input
                                    className="form-control"
                                    name="nome"
                                    value={form.nome}
                                    onChange={alterar}
                                />
                                {erros.nome && <span className="campo-erro">{erros.nome}</span>}

                                <label className="label">Email <span className="p-required">*</span></label>
                                <input
                                    className="form-control"
                                    name="email"
                                    value={form.email}
                                    onChange={alterar}
                                />
                                {erros.email && <span className="campo-erro">{erros.email}</span>}

                                <label className="label">Login <span className="p-required">*</span></label>
                                <input
                                    className="form-control"
                                    name="login"
                                    value={form.login}
                                    onChange={alterar}
                                />
                                {erros.login && <span className="campo-erro">{erros.login}</span>}

                                <label className="label">Senha <span className="p-required">*</span></label>
                                <input
                                    type="password"
                                    className="form-control"
                                    name="senha"
                                    value={form.senha}
                                    onChange={alterar}
                                />
                                {erros.senha && <span className="campo-erro">{erros.senha}</span>}

                                <label className="label">Confirmar senha <span className="p-required">*</span></label>
                                <input
                                    type="password"
                                    className="form-control"
                                    name="confirmarSenha"
                                    value={form.confirmarSenha}
                                    onChange={alterar}
                                />
                                {erros.confirmarSenha && <span className="campo-erro">{erros.confirmarSenha}</span>}

                                <label className="label">Perfil <span className="p-required">*</span></label>
                                <select
                                    className="form-control"
                                    name="perfil"
                                    value={form.perfil}
                                    onChange={alterar}
                                >
                                    <option value={""}>Selecionar</option>
                                    <option value="SOLICITANTE">Solicitante</option>
                                    <option value="EXECUTOR_APS">Executor APS</option>
                                    <option value="GESTAO_MUNICIPAL">Gestão Municipal</option>
                                </select>
                                {erros.perfil && <span className="campo-erro">{erros.perfil}</span>}
                                <label className="label">Unidade Básica de Saúde <span className="p-required">*</span></label>

                                <div className={"div-button"}>
                                    <button type="submit" className="btn btn-primary">Cadastrar </button>
                                    <button type="button" className="btn btn-danger">Cancelar </button>
                                </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default UsuarioCadastro;