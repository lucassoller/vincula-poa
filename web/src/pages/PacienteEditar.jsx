import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import EnderecoForm from "../components/EnderecoForm";
import "./pacienteCadastro.css";
import { useForm } from "react-hook-form";
import ModalRedirecionarDemandas from "../components/ModalRedirecionarDemandas.jsx";

const camposEtapa1 = ["nomeCompleto", "telefone", "documento", "dataNascimento", "sexo"];

function PacienteEditar() {
    const {
        register,
        handleSubmit,
        reset,
    } = useForm({
        defaultValues: {
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
                cep: "00000000",
            }
        }
    });
    const { id } = useParams();
    const navigate = useNavigate();
    const [etapa, setEtapa] = useState(1);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [unidadeOriginalId, setUnidadeOriginalId] = useState(null);
    const [mostrarConfirmacaoRedirecionamento, setMostrarConfirmacaoRedirecionamento] = useState(false);
    const [pacienteAtualizado, setPacienteAtualizado] = useState(null);

    useEffect(() => {
        async function carregarPaciente() {
            try {
                const response = await api.get(`/pacientes/${id}`);
                reset(response.data);
                setUnidadeOriginalId(response.data.unidadeSaudeId);
            } catch {
                setMensagem("Erro ao carregar paciente.");
            } finally {
                setCarregando(false);
            }
        }

        void carregarPaciente();
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
            const payload = {
                ...dados,
                sexo: dados.sexo || null
            };

            const response = await api.put(`/pacientes/${id}`, payload);

            setPacienteAtualizado(response.data);

            const mudouUnidade =
                Number(unidadeOriginalId) !== Number(response.data.unidadeSaudeId);

            if (mudouUnidade) {
                setMostrarConfirmacaoRedirecionamento(true);
                setMensagem("Paciente atualizado. A UBS vinculada mudou.");
                return;
            }

            setMensagem("Paciente atualizado com sucesso e vinculado na Unidade " + response.data.unidadeSaudeNome);
            navigate(`/pacientes/${id}`);

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

    async function confirmarRedirecionamentoDemandas() {
        try {
            await api.patch(`/pacientes/${id}/redirecionar-abertas`, {
                novaUnidadeResponsavelId: pacienteAtualizado.unidadeSaudeId,
                motivoRedirecionamento: "Atualização de endereço/território",
            });

            setMensagem("Paciente atualizado e demandas abertas redirecionadas com sucesso!");
            navigate(`/pacientes/${id}`);
        } catch (error) {
            setMensagem(
                error.response?.data?.message ||
                "Erro ao redirecionar demandas abertas."
            );
        }
    }

    function negarRedirecionamentoDemandas() {
        navigate(`/pacientes/${id}`);
    }

    if (carregando) {
        return <p>Carregando paciente...</p>;
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Editar paciente</h1>
                        <p>Atualize os dados cadastrais do paciente</p>
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
                                    <input className="input-field" {...register("nomeCompleto")} />
                                    {erros.nomeCompleto && <small>{erros.nomeCompleto}</small>}
                                </div>
                            </div>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>CPF/CNS <span>*</span></label>
                                    <input placeholder="Digite CPF ou CNS" type={"number"} className="input-field" {...register("documento")} />
                                    {erros.documento && <small>{erros.documento}</small>}
                                </div>
                                <div className="form-group">
                                    <label>Telefone</label>
                                    <input placeholder="(xx)xxxxx-xxxx" type={"number"} className="input-field" {...register("telefone")} />
                                    {erros.telefone && <small>{erros.telefone}</small>}
                                </div>
                            </div>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Data de nascimento</label>
                                    <input className="input-field" type="date" {...register("dataNascimento")} />
                                    {erros.dataNascimento && <small>{erros.dataNascimento}</small>}
                                </div>

                                <div className="form-group">
                                    <label>Sexo</label>
                                    <select className="input-field" {...register("sexo")}>
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
                                    Salvar alterações
                                </button>

                                <button type="button" className="buscar-btn" onClick={() => setEtapa(1)}>
                                    Voltar
                                </button>
                            </div>
                        </>
                    )}
                </form>
            </div>
            {mostrarConfirmacaoRedirecionamento && (
                <ModalRedirecionarDemandas
                    onConfirmar={confirmarRedirecionamentoDemandas}
                    onCancelar={negarRedirecionamentoDemandas}
                />
            )}
        </div>
    );
}

export default PacienteEditar;