import { useState } from "react";
import api from "../api/api";

function UnidadeSaudeCadastro() {
    const [form, setForm] = useState({
        nome: "",
        cnes: "",
        telefone: "",
        email: "",
        observacao: "",
        endereco: {
            rua: "",
            numero: "",
            bairro: "",
            cidade: "Porto Alegre",
            estado: "RS",
            cep: "",
        },
    });

    const [mensagem, setMensagem] = useState("");

    function alterar(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    function alterarEndereco(e) {
        setForm({
            ...form,
            endereco: { ...form.endereco, [e.target.name]: e.target.value },
        });
    }

    async function salvar(e) {
        e.preventDefault();
        setMensagem("");

        try {
            await api.post("/unidades-saude", form);
            setMensagem("Unidade de saúde cadastrada com sucesso!");
        } catch {
            setMensagem("Erro ao cadastrar unidade de saúde.");
        }
    }

    return (
        <div>
            <h1>Cadastrar Unidade de Saúde</h1>

            <form onSubmit={salvar} style={styles.form}>
                <input name="nome" placeholder="Nome da unidade" onChange={alterar} />
                <input name="cnes" placeholder="CNES" onChange={alterar} />
                <input name="telefone" placeholder="Telefone" onChange={alterar} />
                <input name="email" placeholder="Email" onChange={alterar} />

                <h3>Endereço</h3>
                <input name="rua" placeholder="Rua" onChange={alterarEndereco} />
                <input name="numero" placeholder="Número" onChange={alterarEndereco} />
                <input name="bairro" placeholder="Bairro" onChange={alterarEndereco} />
                <input name="cidade" placeholder="Cidade" value={form.endereco.cidade} onChange={alterarEndereco} />
                <input name="estado" placeholder="Estado" value={form.endereco.estado} onChange={alterarEndereco} />
                <input name="cep" placeholder="CEP" onChange={alterarEndereco} />

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

export default UnidadeSaudeCadastro;