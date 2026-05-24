import { useState } from "react";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm.jsx";
import {useNavigate} from "react-router-dom";

const camposEtapa1 = ["nome", "cnes", "telefone", "telefone2"];

const formInicial = {
    nome: "",
    cnes: "",
    telefone: "",
    telefone2: "",
    endereco: {
        rua: "",
        numero: "",
        bairro: "",
        cidade: "Porto Alegre",
        estado: "RS",
        cep: "",
    }
};

function UnidadeSaudeCadastro() {
    const [etapa, setEtapa] = useState(1);

    const [erros, setErros] = useState({});

    const navigate = useNavigate();

    const [form, setForm] = useState(formInicial);

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
            setForm(formInicial);
            setErros({});
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
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Nova Unidade Básica de Saúde</h1>
                        <p>
                            Cadastre uma unidade para vinculação de pacientes e equipes
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
                <div className="stepper">
                    <div className={`step ${etapa === 1 ? "active" : ""}`}>
                        <span>1</span>
                        Dados da unidade
                    </div>
                    <div className="step-line"></div>
                    <div className={`step ${etapa === 2 ? "active" : ""}`}>
                        <span>2</span>
                        Endereço
                    </div>
                </div>
                <form className="cadastro-card" onSubmit={salvar}>
                    {etapa === 1 && (
                        <>
                            <div className="form-grid full">
                                <div className="form-group">
                                    <label>
                                        Nome <span>*</span>
                                    </label>
                                    <input
                                        className="input-field"
                                        name="nome"
                                        value={form.nome}
                                        onChange={alterar}
                                    />
                                    {erros.nome && (
                                        <small>{erros.nome}</small>
                                    )}
                                </div>
                            </div>
                            <div className="form-grid full">
                                <div className="form-group">
                                    <label>
                                        CNES <span>*</span>
                                    </label>
                                    <input
                                        className="input-field"
                                        name="cnes"
                                        type="text"
                                        value={form.cnes}
                                        onChange={alterar}
                                    />
                                    {erros.cnes && (
                                        <small>{erros.cnes}</small>
                                    )}
                                </div>
                            </div>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>
                                        Telefone
                                    </label>
                                    <input
                                        className="input-field"
                                        name="telefone"
                                        placeholder="(xx)xxxxx-xxxx"
                                        value={form.telefone}
                                        type="text"
                                        onChange={alterar}
                                    />
                                    {erros.telefone && (
                                        <small>{erros.telefone}</small>
                                    )}
                                </div>
                                <div className="form-group">
                                    <label>
                                        Telefone adicional
                                    </label>
                                    <input
                                        className="input-field"
                                        name="telefone2"
                                        placeholder="(xx)xxxxx-xxxx"
                                        value={form.telefone2}
                                        type="text"
                                        onChange={alterar}
                                    />
                                    {erros.telefone2 && (
                                        <small>{erros.telefone2}</small>
                                    )}
                                </div>

                            </div>
                            <div className="form-actions">
                                <button
                                    type="button"
                                    className="buscar-btn"
                                    onClick={() => setEtapa(2)}
                                >
                                    Próximo
                                </button>
                                <button
                                    type="button"
                                    className="buscar-btn"
                                    onClick={() => navigate("/indicadores")}
                                >
                                    Cancelar
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
                            <div className="form-actions">
                                <button
                                    type="submit"
                                    className="buscar-btn"
                                >
                                    Cadastrar
                                </button>
                                <button
                                    type="button"
                                    className="buscar-btn"
                                    onClick={() => setEtapa(1)}
                                >
                                    Voltar
                                </button>
                            </div>
                        </>
                    )}
                </form>
            </div>
        </div>
    );
}

export default UnidadeSaudeCadastro;