import { useState } from "react";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";
import "./pacienteCadastro.css";

function PacienteCadastro() {
    const [etapa, setEtapa] = useState(1);

    const [form, setForm] = useState({
        nomeCompleto: "",
        telefone: "",
        email: "",
        cpf: "",
        cns: "",
        dataNascimento: "",
        sexo: "",
        endereco: {
            rua: "",
            numero: "",
            bairro: "",
            cidade: "Porto Alegre",
            estado: "RS",
            cep: "",
        },
        unidadeSaudeId: 1
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
        } catch {
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
        <div className="p-container">
            <div className="p-body">
                <h1>Cadastrar Paciente</h1>

                <div className="p-form">
                    <form className={"p-form-child"} onSubmit={salvar}>
                        {etapa === 1 && (
                            <>
                                <label className="label">Nome completo</label>
                                <input
                                    className="form-control"
                                    name="nomeCompleto"
                                    value={form.nomeCompleto}
                                    onChange={alterar}
                                />

                                <label className="label">Telefone</label>
                                <input
                                    className="form-control"
                                    name="telefone"
                                    placeholder="(xx)xxxxx-xxxx"
                                    value={form.telefone}
                                    onChange={alterar}
                                />

                                <label className="label">Email</label>
                                <input
                                    type="email"
                                    className="form-control"
                                    name="email"
                                    placeholder="name@example.com"
                                    value={form.email}
                                    onChange={alterar}
                                />

                                <label className="label">CPF</label>
                                <input
                                    className="form-control"
                                    name="cpf"
                                    value={form.cpf}
                                    onChange={alterar}
                                />

                                <label className="label">CNS</label>
                                <input
                                    className="form-control"
                                    name="cns"
                                    value={form.cns}
                                    onChange={alterar}
                                />

                                <label className="label">Data de nascimento</label>
                                <input
                                    className="form-control"
                                    name="dataNascimento"
                                    type="date"
                                    value={form.dataNascimento}
                                    onChange={alterar}
                                />

                                <label className="label">Sexo</label>
                                <select
                                    className="form-control"
                                    name="sexo"
                                    value={form.sexo}
                                    onChange={alterar}
                                >
                                    <option value="">Selecione</option>
                                    <option value="FEMININO">Feminino</option>
                                    <option value="MASCULINO">Masculino</option>
                                    <option value="NAO_INFORMADO">Não informar</option>
                                </select>

                                <div>
                                <button type="button" onClick={() => setEtapa(2)}>
                                    Próximo
                                </button>
                                <button type="button" onClick={() => setEtapa(2)}>
                                    Cancelar
                                </button>
                                </div>
                            </>
                        )}

                        {etapa === 2 && (
                            <>
                                <EnderecoForm
                                    endereco={form.endereco}
                                    onChange={alterarEndereco}
                                    onBuscarCep={alterarCep}
                                />

                                <div style={{ display: "flex", gap: "12px", marginTop: "16px" }}>
                                    <button type="button" onClick={() => setEtapa(1)}>
                                        Voltar
                                    </button>

                                    <button type="submit">Cadastrar</button>
                                </div>
                            </>
                        )}
                    </form>

                    {mensagem && <p>{mensagem}</p>}
                </div>
            </div>
        </div>
    );
}

export default PacienteCadastro;