import { useState } from "react";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";
import "./pacienteCadastro.css";
import {useNavigate} from "react-router-dom";
import { useForm } from "react-hook-form";
import {useAuth} from "../context/AuthContext.jsx";


const camposEtapa1 = ["nomeCompleto", "telefone", "documento", "dataNascimento", "sexo"];

function PacienteCadastro() {
    const {
        register,
        handleSubmit,
    } = useForm({
        defaultValues: {
            nomeCompleto: "",
            telefone: "",
            documento: "",
            dataNascimento: "",
            sexo: "",
            idUsuarioCadastro: "",
            endereco: {
                rua: "",
                numero: "",
                bairro: "",
                cidade: "Porto Alegre",
                estado: "RS",
            }
        }
    });
    const { usuario } = useAuth();
    const navigate = useNavigate();
    const [etapa, setEtapa] = useState(1);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");

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
            const payload = {
                ...dados,
                sexo: dados.sexo || null,
                idUsuarioCadastro: usuario?.id,
            };

            const response = await api.post("/pacientes", payload);

            if(response.data.id !== null){
                navigate("/demandas/cadastro", {
                    state: {
                        pacienteId: response.data.id
                    }
                });
            }
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

                <form className="cadastro-card" onSubmit={handleSubmit(salvar)}>
                    {etapa === 1 && (
                        <>
                            <div className="form-grid full">
                                <div className="form-group">
                                    <label>Nome completo <span>*</span></label>
                                    <input
                                        className="input-field"
                                        {...register("nomeCompleto")}
                                    />
                                    {erros.nomeCompleto && <small>{erros.nomeCompleto}</small>}
                                </div>
                            </div>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>CPF/CNS <span>*</span></label>

                                    <input
                                        className="input-field"
                                        {...register("documento")}
                                        type="number"
                                        placeholder="Digite CPF ou CNS"
                                    />

                                    {erros.documento && <small>{erros.documento}</small>}
                                </div>
                                <div className="form-group">
                                    <label>Telefone</label>
                                    <input
                                        className="input-field"
                                        {...register("telefone")}
                                        placeholder="(xx)xxxxx-xxxx"
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
                                        {...register("dataNascimento")}
                                        type="date"
                                    />
                                    {erros.dataNascimento && <small>{erros.dataNascimento}</small>}
                                </div>

                                <div className="form-group">
                                    <label>Sexo</label>
                                    <select
                                        className="input-field"
                                        {...register("sexo")}

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
                                <button type="button" className="buscar-btn" onClick={() => setEtapa(2)}>
                                    Próximo
                                </button>

                                <button type="button" className="buscar-btn" onClick={() => navigate("/pacientes")}>
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
                                <button type="submit" className="buscar-btn">
                                    Cadastrar
                                </button>

                                <button type="button" className="buscar-btn" onClick={() => setEtapa(1)}>
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