import { useState } from "react";
import api from "../api/api";

function PacienteCadastro() {
    const [form, setForm] = useState({
        nomeCompleto: "",
        telefone: "",
        email: "",
        cpf: "",
        cns: "",
        dataNascimento: "",
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

    function alterarCep(e) {
        const cep = e.target.value.replace(/\D/g, "");

        setForm({
            ...form,
            endereco: { ...form.endereco, cep },
        });

        if (cep.length === 8) {
            buscarCep(cep);
        }
    }

    async function buscarCep(cep) {
        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();

            if (data.erro) {
                setMensagem("CEP não encontrado.");
                return;
            }

            setForm((prev) => ({
                ...prev,
                endereco: {
                    ...prev.endereco,
                    rua: data.logradouro || "",
                    bairro: data.bairro || "",
                    cidade: data.localidade || "",
                    estado: data.uf || "",
                    cep,
                },
            }));
        } catch (error) {
            setMensagem("Erro ao buscar CEP.");
        }
    }

    async function salvar(e) {
        e.preventDefault();
        setMensagem("");

        try {
            await api.post("/pacientes", form);
            setMensagem("Paciente cadastrado com sucesso!");
        } catch {
            setMensagem("Erro ao cadastrar paciente.");
        }
    }

    return (
        <div>
            <h1>Cadastrar Paciente</h1>

            <form onSubmit={salvar} style={styles.form}>
                <input name="nomeCompleto" placeholder="Nome completo" value={form.nomeCompleto} onChange={alterar} />
                <input name="telefone" placeholder="Telefone" value={form.telefone} onChange={alterar} />
                <input name="email" placeholder="Email" value={form.email} onChange={alterar} />
                <input name="cpf" placeholder="CPF" value={form.cpf} onChange={alterar} />
                <input name="cns" placeholder="CNS" value={form.cns} onChange={alterar} />
                <input name="dataNascimento" type="date" value={form.dataNascimento} onChange={alterar} />

                <h3>Endereço</h3>

                <input name="cep" placeholder="CEP" value={form.endereco.cep} onChange={alterarCep} />
                <input name="rua" placeholder="Rua" value={form.endereco.rua} onChange={alterarEndereco} />
                <input name="numero" placeholder="Número" value={form.endereco.numero} onChange={alterarEndereco} />
                <input name="bairro" placeholder="Bairro" value={form.endereco.bairro} onChange={alterarEndereco} />
                <input name="cidade" placeholder="Cidade" value={form.endereco.cidade} onChange={alterarEndereco} />
                <input name="estado" placeholder="Estado" value={form.endereco.estado} onChange={alterarEndereco} />

                <textarea name="observacao" placeholder="Observação" value={form.observacao} onChange={alterar} />

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

export default PacienteCadastro;