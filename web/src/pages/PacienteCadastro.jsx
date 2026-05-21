import { useState } from "react";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";
import "./pacienteCadastro.css";
import {useNavigate} from "react-router-dom";

const camposEtapa1 = ["nomeCompleto", "telefone", "documento", "dataNascimento", "sexo"];

const formInicial = {
    nomeCompleto: "",
    telefone: "",
    documento: "",
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
};

function PacienteCadastro() {
    const [etapa, setEtapa] = useState(1);

    const [erros, setErros] = useState({});

    const navigate = useNavigate();

    const [form, setForm] = useState(formInicial);

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
            const payload = {
                ...form,
                sexo: form.sexo || null,
                unidadeSaudeId: form.unidadeSaudeId
                    ? Number(form.unidadeSaudeId)
                    : null,
            };

            const response = await api.post("/pacientes", payload);
            setMensagem("Paciente cadastrado com sucesso e vinculado na Unidade " + response.data.unidadeSaudeNome);
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
                        <h1>Novo paciente</h1>
                        <p>Preencha os dados do paciente para iniciar o acompanhamento</p>
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button type="button" onClick={() => setMensagem("")}>✕</button>
                    </div>
                )}

                <div className="stepper">
                    <div className={`step ${etapa === 1 ? "active" : ""}`}>
                        <span>1</span>
                        Dados pessoais
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
                                    <label>Nome completo <span>*</span></label>
                                    <input
                                        className="input-field"
                                        name="nomeCompleto"
                                        value={form.nomeCompleto}
                                        onChange={alterar}
                                    />
                                    {erros.nomeCompleto && <small>{erros.nomeCompleto}</small>}
                                </div>
                            </div>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>CPF/CNS <span>*</span></label>

                                    <input
                                        className="input-field"
                                        name="documento"
                                        value={form.documento}
                                        onChange={alterar}
                                        type="number"
                                        placeholder="Digite CPF ou CNS"
                                    />

                                    {erros.documento && <small>{erros.documento}</small>}
                                </div>
                                <div className="form-group">
                                    <label>Telefone</label>
                                    <input
                                        className="input-field"
                                        name="telefone"
                                        placeholder="(xx)xxxxx-xxxx"
                                        value={form.telefone}
                                        onChange={alterar}
                                        type="text"
                                    />
                                    {erros.telefone && <small>{erros.telefone}</small>}
                                </div>
                            </div>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Data de nascimento</label>
                                    <input
                                        className="input-field"
                                        name="dataNascimento"
                                        type="date"
                                        value={form.dataNascimento}
                                        onChange={alterar}
                                    />
                                    {erros.dataNascimento && <small>{erros.dataNascimento}</small>}
                                </div>

                                <div className="form-group">
                                    <label>Sexo</label>
                                    <select
                                        className="input-field"
                                        name="sexo"
                                        value={form.sexo}
                                        onChange={alterar}
                                    >
                                        <option value="">Selecione</option>
                                        <option value="FEMININO">Feminino</option>
                                        <option value="MASCULINO">Masculino</option>
                                        <option value="OUTRO">Outro</option>
                                        <option value="NAO_INFORMADO">Não informar</option>
                                    </select>
                                    {erros.sexo && <small>{erros.sexo}</small>}
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="button" className="primary-btn" onClick={() => setEtapa(2)}>
                                    Próximo
                                </button>

                                <button type="button" className="danger-btn" onClick={() => navigate("/indicadores")}>
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
                                <button type="submit" className="primary-btn">
                                    Cadastrar
                                </button>

                                <button type="button" className="secondary-btn" onClick={() => setEtapa(1)}>
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

export default PacienteCadastro;