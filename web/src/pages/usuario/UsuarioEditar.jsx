import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/api.js";
import EnderecoForm from "../../components/EnderecoForm.jsx";
import "../../styles/usuarioCadastro.css";
import { useForm } from "react-hook-form";

import ModalRedirecionarDemandas from "../../components/ModalRedirecionarDemandas.jsx";
import {useAuth} from "../../context/AuthContext.jsx";
import {perfilLabel} from "../../utils/utils.js";

const camposEtapa1 = ["nomeCompleto", "telefone", "documento", "dataNascimento", "sexo"];

function UsuarioEditar() {
    const {
        register,
        handleSubmit,
        reset,
        watch,
    } = useForm({
        defaultValues: {
            nomeCompleto: "",
            telefone: "",
            documento: "",
            dataNascimento: "",
            sexo: "",
            unidadeSaudeNome: "",
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
    const { servidor } = useAuth();
    const [etapa, setEtapa] = useState(1);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [unidadeOriginalId, setUnidadeOriginalId] = useState(null);
    const [mostrarConfirmacaoRedirecionamento, setMostrarConfirmacaoRedirecionamento] = useState(false);
    const [usuarioAtualizado, setUsuarioAtualizado] = useState(null);
    const unidadeSaudeNome = watch("unidadeSaudeNome");

    useEffect(() => {
        async function carregarUsuario() {
            try {
                const response = await api.get(`/usuarios/${id}`);
                reset(response.data);
                if(servidor?.perfil === 'SOLICITANTE' && servidor?.unidadeSaudeId !== response.data.unidadeSolicitanteId){
                    navigate("/usuarios");
                    return;
                }else if(servidor?.perfil === 'SERVIDOR_APS' && servidor?.unidadeSaudeId !== response.data.unidadeSaudeId){
                    navigate("/usuarios");
                    return;
                }
                setUnidadeOriginalId(response.data.unidadeSaudeId);
            } catch {
                setMensagemSucesso("")
                setMensagem("Erro ao carregar usuário.");
            } finally {
                setCarregando(false);
            }
        }

        void carregarUsuario();
    }, [id, reset, servidor]);

    function voltarParaEtapaComErro(errors) {
        const temErroEtapa1 = camposEtapa1.some((campo) => errors[campo]);
        if (temErroEtapa1) {
            setEtapa(1);
        } else{
            setEtapa(2);
        }
        setMensagemSucesso("")
        setMensagem("Dados inválidos.");
    }

    async function salvar(dados) {
        setMensagem("");
        setMensagemSucesso("")
        setErros({});

        try {
            const payload = {
                ...dados,
                sexo: dados.sexo || null
            };

            const response = await api.put(`/usuarios/${id}`, payload);

            setUsuarioAtualizado(response.data);

            const mudouUnidade =
                Number(unidadeOriginalId) !== Number(response.data.unidadeSaudeId);

            if (mudouUnidade) {
                setMostrarConfirmacaoRedirecionamento(true);
                setMensagemSucesso("Usuário atualizado. A UBS vinculada mudou.");
                return;
            }

            setMensagemSucesso("Usuário atualizado com sucesso e vinculado na Unidade " + response.data.unidadeSaudeNome);
            setEtapa(1);

        } catch (error) {
            setMensagemSucesso("")
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
            await api.patch(`/usuarios/${id}/redirecionar-abertas`, {
                novaUnidadeResponsavelId: usuarioAtualizado.unidadeSaudeId,
                motivoRedirecionamento: "Atualização de endereço/território",
            });

            setMensagemSucesso("Usuário atualizado e demandas abertas redirecionadas com sucesso!");
            navigate(`/usuarios/${id}`);
        } catch (error) {
            setMensagemSucesso("")
            setMensagem(
                error.response?.data?.message ||
                "Erro ao redirecionar demandas abertas."
            );
        }
    }

    function negarRedirecionamentoDemandas() {
        navigate(`/usuarios/${id}`);
    }

    if (carregando) {
        return (<div className="loading-container">
                    <div className="loading-card">
                        <p>Carregando usuário...</p>
                    </div>
                </div>
        );
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Editar usuário</h1>
                        <p>Atualize os dados cadastrais do usuário</p>
                    </div>
                    <div className="perfil-badge">
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.unidadeSaude}
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <span onClick={() => setMensagemSucesso("")}>✕</span>
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
                                <span className="buscar-btn" onClick={() => setEtapa(2)}>
                                    Próximo
                                </span>

                                <span className="buscar-btn" onClick={() => navigate("/usuarios")}>
                                    Cancelar
                                </span>
                            </div>
                        </>
                    )}

                    {etapa === 2 && (
                        <>
                            <EnderecoForm
                                register={register}
                                erros={erros}
                                unidadeSaude={unidadeSaudeNome}
                            />

                            <div className="form-actions">
                                <span onClick={handleSubmit(salvar)} className="buscar-btn">
                                    Salvar alterações
                                </span>

                                <span className="buscar-btn" onClick={() => setEtapa(1)}>
                                    Voltar
                                </span>
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

export default UsuarioEditar;