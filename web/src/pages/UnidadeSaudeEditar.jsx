import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";

const formInicial = {
    nome: "",
    cnes: "",
    telefone: "",
    telefone2: "",
    endereco: {
        cep: "",
        rua: "",
        numero: "",
        bairro: "",
        cidade: "Porto Alegre",
        estado: "RS",
    },
};

const camposEtapa1 = ["nome", "cnes", "telefone", "telefone2"];

function UnidadeSaudeEditar() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [etapa, setEtapa] = useState(1);
    const [form, setForm] = useState(formInicial);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        let ativo = true;

        async function carregarUnidade() {
            try {
                const response = await api.get(`/unidades-saude/${id}`);

                if (ativo) {
                    setForm({
                        nome: response.data.nome || "",
                        cnes: response.data.cnes || "",
                        telefone: response.data.telefone || "",
                        endereco: {
                            cep: response.data.endereco?.cep || "",
                            rua: response.data.endereco?.rua || "",
                            numero: response.data.endereco?.numero || "",
                            bairro: response.data.endereco?.bairro || "",
                            cidade: response.data.endereco?.cidade || "Porto Alegre",
                            estado: response.data.endereco?.estado || "RS",
                        },
                    });
                }
            } catch {
                if (ativo) {
                    setMensagem("Erro ao carregar unidade de saúde.");
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        void carregarUnidade();

        return () => {
            ativo = false;
        };
    }, [id]);

    function alterar(e) {
        const { name, value } = e.target;

        setForm({
            ...form,
            [name]: value,
        });

        setErros((prev) => {
            const novos = { ...prev };
            delete novos[name];
            return novos;
        });
    }

    function alterarEndereco(e) {
        const { name, value } = e.target;

        setForm({
            ...form,
            endereco: {
                ...form.endereco,
                [name]: value,
            },
        });

        setErros((prev) => {
            const novos = { ...prev };
            delete novos[`endereco.${name}`];
            return novos;
        });
    }

    function alterarCep(e) {
        const cep = e.target.value.replace(/\D/g, "");

        setForm({
            ...form,
            endereco: {
                ...form.endereco,
                cep,
            },
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
            await api.put(`/unidades-saude/${id}`, form);

            navigate("/pacientes");
        } catch (error) {
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

    if (carregando) {
        return (
            <div className="cadastro-container">
                <div className="cadastro-page">
                    <p>Carregando unidade de saúde...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Editar Unidade Básica de Saúde</h1>
                        <p>
                            Atualize os dados cadastrais da unidade
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
                                    className="primary-btn"
                                    onClick={() => setEtapa(2)}
                                >
                                    Próximo
                                </button>
                                <button
                                    type="button"
                                    className="danger-btn"
                                    onClick={() => navigate("/pacientes")}
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
                                    className="primary-btn"
                                >
                                    Salvar alterações
                                </button>
                                <button
                                    type="button"
                                    className="secondary-btn"
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

export default UnidadeSaudeEditar;