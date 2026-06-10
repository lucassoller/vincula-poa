import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";
import { useForm } from "react-hook-form";

const camposEtapa1 = ["nome", "cnes", "telefone", "telefone2"];

function UnidadeSaudeEditar() {
    const {
        register,
        handleSubmit,
        reset,
    } = useForm({
        defaultValues: {
            nome: "",
            cnes: "",
            telefone: "",
            telefone2: "",
            endereco: {
                rua: "",
                numero: "",
                bairro: "",
                cidade: "Porto Alegre",
                complemento: "",
                estado: "RS",
            }
        }
    });
    const { id } = useParams();
    const navigate = useNavigate();
    const [etapa, setEtapa] = useState(1);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        async function carregarUnidade() {
            try {
                const response = await api.get(`/unidades-saude/${id}`);
                reset(response.data);
            } catch {
                setMensagem("Erro ao carregar unidade de saúde.");

            } finally {
                setCarregando(false);
            }
        }

        void carregarUnidade();

    }, [id, reset]);

    function voltarParaEtapaComErro(errors) {
        const temErroEtapa1 = camposEtapa1.some((campo) => errors[campo]);
        if (temErroEtapa1) {
            setEtapa(1);
        } else{
            setEtapa(2);
        }
        setMensagem("Dados inválidos.");
    }

    async function salvar(dados) {
        setMensagem("");
        setErros({});

        try {
            await api.put(`/unidades-saude/${id}`, dados);
            setMensagem("Unidade de saúde editada com sucesso!");
            setEtapa(1);
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
        return (<div className="loading-container">
                <div className="loading-card">
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
                <form className="cadastro-card" onSubmit={handleSubmit(salvar)}>
                    {etapa === 1 && (
                        <>
                            <div className="form-grid full">
                                <div className="form-group">
                                    <label>
                                        Nome <span>*</span>
                                    </label>
                                    <input
                                        className="input-field"
                                        {...register("nome")}
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
                                        {...register("cnes")}
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
                                        placeholder="(xx)xxxxx-xxxx"
                                        type="text"
                                        {...register("telefone")}
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
                                        placeholder="(xx)xxxxx-xxxx"
                                        type="text"
                                        {...register("telefone2")}
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
                                    onClick={() => navigate("/unidades-saude")}
                                >
                                    Cancelar
                                </button>
                            </div>
                        </>
                    )}
                    {etapa === 2 && (
                        <>
                            <EnderecoForm
                                register={register}
                                erros={erros}
                            />
                            <div className="form-actions">
                                <button
                                    type="submit"
                                    className="buscar-btn"
                                >
                                    Salvar alterações
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

export default UnidadeSaudeEditar;