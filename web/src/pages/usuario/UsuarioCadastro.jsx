import {useEffect, useState} from "react";
import api from "../../api/api.js";
import EnderecoForm from "../../components/EnderecoForm.jsx";
import "../../styles/usuarioCadastro.css";
import {useNavigate} from "react-router-dom";
import { useForm } from "react-hook-form";
import {useAuth} from "../../context/AuthContext.jsx";
import {perfilLabel} from "../../utils/utils.js";
import ModalVinculacaoManual from "../../components/Modal/ModalVinculacaoManual.jsx";


const camposEtapa1 = ["nomeCompleto", "telefone", "documento", "dataNascimento", "sexo"];

function UsuarioCadastro() {
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
    const navigate = useNavigate();
    const [etapa, setEtapa] = useState(1);
    const [unidades, setUnidades] = useState([]);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [mensagemModal, setMensagemModal] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [mostrarSelecaoUbs, setMostrarSelecaoUbs] = useState(false);
    const [unidadeSelecionada, setUnidadeSelecionada] = useState("");
    const [dadosCadastroPendente, setDadosCadastroPendente] = useState(null);

    const { servidor } = useAuth();

    function voltarParaEtapaComErro(errors) {
        setMensagemSucesso("")
        const temErroEtapa1 = camposEtapa1.some((campo) => errors[campo]);
        if (temErroEtapa1) {
            setEtapa(1);
        } else{
            setEtapa(2);
        }
        setMensagem("Dados inválidos.");
    }

    async function salvar(dados, unidadeSaudeId = null) {
        setMensagem("");
        setMensagemSucesso("");
        setErros({});

        const payload = {
            ...dados,
            sexo: dados.sexo || null,
            unidadeSaudeId: unidadeSaudeId
                ? Number(unidadeSaudeId)
                : null,
        };

        try {
            const response = await api.post("/usuarios", payload);

            if (response.data.id !== null) {
                navigate("/demandas/cadastro", {
                    state: {
                        usuarioId: response.data.id
                    }
                });
            }

            setErros({});

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
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Novo usuário</h1>
                        <p>Preencha os dados do usuário para iniciar o acompanhamento</p>
                    </div>
                    <div className="perfil-badge">
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil]: servidor.servico}
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
                            />

                            <div className="form-actions">
                                <span onClick={handleSubmit(salvar)} className="buscar-btn">
                                    Cadastrar
                                </span>

                                <span className="buscar-btn" onClick={() => setEtapa(1)}>
                                    Voltar
                                </span>
                            </div>
                        </>
                    )}
                </form>
            </div>
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

export default UsuarioCadastro;