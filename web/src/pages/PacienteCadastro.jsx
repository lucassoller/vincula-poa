import { useState } from "react";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";
import "./pacienteCadastro.css";

function PacienteCadastro() {
    const camposEtapa1 = ["nomeCompleto", "telefone", "email", "cpf", "cns", "dataNascimento", "sexo"];

    const [etapa, setEtapa] = useState(1);

    const [erros, setErros] = useState({});

    const [form, setForm] = useState({
        nomeCompleto: "",
        telefone: "",
        email: "",
        cpf: "",
        cns: "",
        dataNascimento: "",
        sexo: undefined,
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

    function voltarParaEtapaComErro(errors) {
        const temErroEtapa1 = camposEtapa1.some((campo) => errors[campo]);
        if (temErroEtapa1) {
            setEtapa(1);
        } else{
            setEtapa(2);
        }
            setMensagem("Dados inválidos.");
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
        setErros({});

        try {
            await api.post("/pacientes", form);
            setMensagem("Paciente cadastrado com sucesso!");
        }catch (error) {
            if (error.response?.data?.errors) {
                const errors = error.response.data.errors;

                setErros(errors);
                setMensagem(error.response.data.message || "Dados inválidos");
                voltarParaEtapaComErro(errors);
            } else {
                setMensagem(error.response.data.message);
            }
        }
    }

    return (
        <div className="p-container">
            <div className="p-body">
                <h1>Cadastrar Paciente</h1>
                {mensagem &&
                    <div className="alert alert-warning alert-dismissible fade show" role="alert">
                        <p>{mensagem}</p>
                        <button type="button" className="close" data-dismiss="alert" aria-label="Close" onClick={() => setMensagem("")}>
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>}

                <div className="p-form">
                    <form className={"p-form-child"} onSubmit={salvar}>
                        {etapa === 1 && (
                            <>
                                <label className="label">Nome completo <span className="p-required">*</span> </label>
                                <input
                                    className="form-control"
                                    name="nomeCompleto"
                                    value={form.nomeCompleto}
                                    onChange={alterar}
                                />
                                {erros.nomeCompleto && <span className="campo-erro">{erros.nomeCompleto}</span>}

                                <label className="label">Telefone</label>
                                <input
                                    className="form-control"
                                    name="telefone"
                                    placeholder="(xx)xxxxx-xxxx"
                                    value={form.telefone}
                                    onChange={alterar}
                                    type={"number"}
                                />
                                {erros.telefone && <span className="campo-erro">{erros.telefone}</span>}

                                <label className="label">Email</label>
                                <input
                                    className="form-control"
                                    name="email"
                                    placeholder="name@example.com"
                                    value={form.email}
                                    onChange={alterar}
                                />
                                {erros.email && <span className="campo-erro">{erros.email}</span>}

                                <label className="label">CPF</label>
                                <input
                                    className="form-control"
                                    name="cpf"
                                    value={form.cpf}
                                    onChange={alterar}
                                    type={"number"}
                                />
                                {erros.cpf && <span className="campo-erro">{erros.cpf}</span>}

                                <label className="label">CNS</label>
                                <input
                                    className="form-control"
                                    name="cns"
                                    value={form.cns}
                                    onChange={alterar}
                                    type={"number"}
                                />
                                {erros.cns && <span className="campo-erro">{erros.cns}</span>}

                                <label className="label">Data de nascimento</label>
                                <input
                                    className="form-control"
                                    name="dataNascimento"
                                    type="date"
                                    value={form.dataNascimento}
                                    onChange={alterar}
                                />
                                {erros.dataNascimento && <span className="campo-erro">{erros.nomeCompleto}</span>}

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
                                {erros.sexo && <span className="campo-erro">{erros.sexo}</span>}

                                <div className={"div-button"}>
                                    <button type="button" className="btn btn-primary"
                                            onClick={() => setEtapa(2)}>Próximo
                                    </button>
                                    <button type="button" className="btn btn-danger"
                                            onClick={() => setEtapa(2)}>Cancelar
                                    </button>
                                </div>
                            </>
                        )}

                        {etapa === 2 && (
                            <>
                                <EnderecoForm
                                    endereco={form.endereco}
                                    erros={erros}
                                    onChange={alterarEndereco}
                                    onBuscarCep={alterarCep}
                                />

                                <div className={"div-button"}>
                                    <button type="submit" className="btn btn-primary">Cadastrar</button>
                                    <button type="button" className="btn btn-secondary"
                                            onClick={() => setEtapa(1)}>Voltar
                                    </button>
                                </div>
                            </>
                        )}
                    </form>

                </div>
            </div>
        </div>
    );
}

export default PacienteCadastro;