import { useState } from "react";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm.jsx";

function UnidadeSaudeCadastro() {
    const [etapa, setEtapa] = useState(1);

    const camposEtapa1 = ["nome", "cnes", "telefone", "email"];

    const [erros, setErros] = useState({});

    const [form, setForm] = useState({
        nome: "",
        cnes: "",
        telefone: "",
        email: "",
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
        setForm({...form, [e.target.name]: e.target.value});
    }

    function alterarEndereco(e) {
        setForm({
            ...form,
            endereco: {...form.endereco, [e.target.name]: e.target.value},
        });
    }

    function alterarCep(e) {
        const cep = e.target.value.replace(/\D/g, "");

        setForm({
            ...form,
            endereco: {...form.endereco, cep},
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

        try {
            await api.post("/unidades-saude", form);
            setMensagem("Unidade de saúde cadastrada com sucesso!");
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
                <h1>Cadastrar Unidade Básica de Saúde</h1>
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
                                <label className="label">Nome <span className="p-required">*</span></label>
                                <input
                                    className="form-control"
                                    name="nome"
                                    value={form.nome}
                                    onChange={alterar}
                                />
                                {erros.nome && <span className="campo-erro">{erros.nome}</span>}

                                <label className="label">CNES <span className="p-required">*</span></label>
                                <input
                                    className="form-control"
                                    name="cnes"
                                    type={"number"}
                                    value={form.cnes}
                                    onChange={alterar}
                                />
                                {erros.cnes && <span className="campo-erro">{erros.cnes}</span>}

                                <label className="label">Telefone</label>
                                <input
                                    className="form-control"
                                    name="telefone"
                                    placeholder="(xx)xxxxx-xxxx"
                                    value={form.telefone}
                                    type={"number"}
                                    onChange={alterar}
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

export default UnidadeSaudeCadastro;