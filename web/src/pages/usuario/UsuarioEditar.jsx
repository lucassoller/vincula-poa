import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/api.js";
import EnderecoForm from "../../components/EnderecoForm.jsx";
import "../../styles/usuarioCadastro.css";
import { useForm } from "react-hook-form";
import ModalRedirecionarDemandas from "../../components/Modal/ModalRedirecionarDemandas.jsx";
import {useAuth} from "../../context/AuthContext.jsx";
import {perfilLabel} from "../../utils/utils.js";
import ModalVinculacaoManual from "../../components/Modal/ModalVinculacaoManual.jsx";

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
            servicoNome: "",
            unidadeSaudeId: null,
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
    const [servicoOriginalId, setServicoOriginalId] = useState(null);
    const [mostrarConfirmacaoRedirecionamento, setMostrarConfirmacaoRedirecionamento] = useState(false);
    const [usuarioAtualizado, setUsuarioAtualizado] = useState(null);
    const [unidades, setUnidades] = useState([]);
    const [mensagemModal, setMensagemModal] = useState("");
    const [mostrarSelecaoUbs, setMostrarSelecaoUbs] = useState(false);
    const [unidadeSelecionada, setUnidadeSelecionada] = useState("");
    const [dadosCadastroPendente, setDadosCadastroPendente] = useState(null);
    const servicoNome = watch("servicoNome");

    useEffect(() => {
        async function carregarUsuario() {
            try {
                const response = await api.get(`/usuarios/${id}`);
                reset(response.data);
                if(servidor?.perfil === 'SOLICITANTE' && servidor?.servicoId !== response.data.servicoSolicitanteId){
                    navigate("/usuarios");
                    return;
                }else if(servidor?.perfil === 'SERVIDOR_APS' && servidor?.servicoId !== response.data.servicoId){
                    navigate("/usuarios");
                    return;
                }
                setServicoOriginalId(response.data.servicoId);
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

    async function salvar(dados, unidadeSaudeId = null) {
        setMensagem("");
        setMensagemSucesso("")
        setErros({});

        const payload = {
            ...dados,
            sexo: dados.sexo || null,
            unidadeSaudeId: unidadeSaudeId
                ? Number(unidadeSaudeId)
                : null,
        };

        try {
            const response = await api.put(`/usuarios/${id}`, payload);

            setUsuarioAtualizado(response.data);

            const mudouServico =
                Number(servicoOriginalId) !== Number(response.data.servicoId);

            if (mudouServico) {
                setMostrarConfirmacaoRedirecionamento(true);
                setMensagemSucesso("Usuário atualizado. O serviço vinculado mudou.");
                return;
            }

            setMensagemSucesso("Usuário atualizado com sucesso e vinculado no Serviço " + response.data.servicoNome);
            setEtapa(1);

        } catch (error) {

            setMensagemSucesso("");

            const data = error.response?.data;
            const codigo = data?.codigo;

            if (

                (
                    codigo === "GEORREFERENCIAMENTO_NAO_ENCONTRADO" ||
                    codigo === "TERRITORIO_NAO_ENCONTRADO"
                )
            ) {
                if (servidor.perfil === "SOLICITANTE") {
                    setMensagem(
                        `${data?.message} Entre em contato com um servidor do tipo gestão para relatar o erro.`
                    );
                    return;
                }

                setDadosCadastroPendente(payload);
                setUnidadeSelecionada("");
                setMostrarSelecaoUbs(true);
                setMensagemModal(
                    `${data?.message} Selecione manualmente uma unidade de saúde.`
                );

                return;
            }

            if (data?.errors) {
                const errors = data.errors;

                setErros(errors);
                setMensagem(data.message || "Dados inválidos");
                voltarParaEtapaComErro(errors);

                return;
            }

            setMensagem(
                data?.message ||
                "Ocorreu um erro ao realizar o cadastro"
            );
        }
    }

    async function confirmarRedirecionamentoDemandas() {
        try {
            await api.patch(`/usuarios/${id}/redirecionar-abertas`, {
                novaServicoResponsavelId: usuarioAtualizado.servicoId,
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

    useEffect(() => {
        async function carregarUnidades() {
            try {
                const response = await api.get("/servicos/ubs");
                setUnidades(response.data);
            } catch {
                setMensagem("Erro ao carregar unidades.");
                setMensagemSucesso("");
            }
        }

        void carregarUnidades();
    }, []);

    function negarRedirecionamentoDemandas() {
        navigate(`/usuarios/${id}`);
    }

    function fecharModalVinculacao() {
        setMostrarSelecaoUbs(false);
        setUnidadeSelecionada("");
        setDadosCadastroPendente(null);
    }

    async function salvarVinculacaoManual() {
        if (!unidadeSelecionada) {
            setMensagem("Selecione uma unidade de saúde.");
            return;
        }

        await salvar(
            dadosCadastroPendente,
            unidadeSelecionada
        );

        fecharModalVinculacao();
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
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.servico}
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
                                servico={servicoNome}
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

            {mostrarSelecaoUbs && (
                <ModalVinculacaoManual
                    unidades={unidades}
                    unidadeSelecionada={unidadeSelecionada}
                    setUnidadeSelecionada={setUnidadeSelecionada}
                    onSalvar={salvarVinculacaoManual}
                    onFechar={fecharModalVinculacao}
                    mensagem={mensagemModal}
                    setMensagem={setMensagemModal}
                />
            )}
        </div>
    );
}

export default UsuarioEditar;