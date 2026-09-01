import {useCallback, useEffect, useState} from "react";
import api from "../../api/api.js";
import { useAuth } from "../../context/AuthContext.jsx";
import "../../styles/demandas.css";
import {
    diasRestantes,
    prioridadeLabel,
    formatarDataHora,
    statusLabel,
    motivoBuscaLabel,
    perfilLabel
} from "../../utils/utils.js";
import ModalTentativaContato from "../../components/Modal/ModalTentativaContato.jsx";
import ModalRedirecionarDemanda from "../../components/Modal/ModalRedirecionarDemanda.jsx";
import ModalEncerrarDemanda from "../../components/Modal/ModalEncerrarDemanda.jsx";
import ModalDetalhesDemanda from "../../components/Modal/ModalDetalhesDemanda.jsx";
import { useNavigate, useLocation } from "react-router-dom";
import Pagination from "../../components/Paginations.jsx";
import ModalUbs from "../../components/Modal/ModalUbs.jsx";
import ModalFiltrosDemanda from "../../components/Modal/ModalFiltrosDemanda.jsx";

function Demandas() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [demandas, setDemandas] = useState([]);
    const [unidades, setUnidades] = useState([]);
    const [servicos, setServicos] = useState([]);
    const [usuariosBusca, setUsuariosBusca] = useState([]);
    const [demandaSelecionada, setDemandaSelecionada] = useState(null);
    const [acao, setAcao] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [mensagemErro, setMensagemErro] = useState("");
    const [erros, setErros] = useState({});
    const [demandaDetalhada, setDemandaDetalhada] = useState(null);
    const [tentativasContato, setTentativasContato] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);
    const tamanhoPagina = 10;
    const [mostrarFiltros, setMostrarFiltros] = useState(false);
    const [motivos, setMotivos] = useState([]);
    const location = useLocation();

    const [filtros, setFiltros] = useState({
        status: [],
        prioridade: [],
        tempo: [],
        unidade: "",
        servico: "",
        motivo: "",
        usuarioId:  location.state?.usuarioId ?? "",
        nomeCompleto:  location.state?.nomeCompleto ?? "",
        complemento: "",
        dataAbInicial: "",
        dataAbFinal: "",
        dataEnInicial: "",
        dataEnFinal: ""
    });

    const [tentativa, setTentativa] = useState({
        tipo: "",
        descricao: "",
    });

    const [redirecionamento, setRedirecionamento] = useState({
        novaServicoResponsavelId: "",
        motivoRedirecionamento: "",
    });

    const [encerramento, setEncerramento] = useState({
        desfechoDemanda: "",
        descricaoDesfecho: "",
    });

    async function carregarDados(paginaAtual = pagina, filtrosAtuais = filtros) {

        try {

            setCarregando(true);

            const payload = {
                status: filtrosAtuais.status,
                prioridade: filtrosAtuais.prioridade,
                tempo: filtrosAtuais.tempo,
                motivo: filtrosAtuais.motivo || null,
                usuarioId: filtrosAtuais.usuarioId || null,
                nomeCompleto: filtrosAtuais.nomeCompleto || null,
                complemento: filtrosAtuais.complemento || null,
                servicoResponsavelId: filtrosAtuais.unidade || null,
                servicoSolicitanteId: filtrosAtuais.servico || null,
                dataAbInicial: filtrosAtuais.dataAbInicial || null,
                dataAbFinal: filtrosAtuais.dataAbFinal || null,
                dataEnInicial: filtrosAtuais.dataEnInicial || null,
                dataEnFinal: filtrosAtuais.dataEnFinal || null
            };

            if (servidor?.perfil === "SERVIDOR_APS") {

                payload.servicoResponsavelId = servidor.servicoId;

            } else if (servidor?.perfil === "SOLICITANTE") {

                payload.servicoSolicitanteId = servidor.servicoId;

            }

            const demandasResponse = await api.post(
                `/demandas/filtradas?page=${paginaAtual}&size=${tamanhoPagina}`,
                payload
            );

            setDemandas(demandasResponse.data.content);
            setTotalPaginas(demandasResponse.data.page.totalPages);

        } catch {

            setMensagemErro("Erro ao carregar demandas.");
            setMensagemSucesso("");

        } finally {

            setCarregando(false);

        }
    }

    const buscarUsuariosAutocomplete = useCallback(async (nome) => {

        if (!nome || nome.trim().length < 3) {
            setUsuariosBusca([]);
            return;
        }

        try {

            const payload = {
                nomeCompleto: nome,
                servicoId: null,
                servicoSolicitanteId: null,
            };

            if(servidor.perfil === 'SOLICITANTE'){
                payload.servicoSolicitanteId = servidor.servicoId;
            }else if(servidor.perfil === 'SERVIDOR_APS'){
                payload.servicoId = servidor.servicoId;
            }

            const response = await api.post(
                "/usuarios/filtrados/nome-completo",
                payload
            );

            setUsuariosBusca(response.data);

        } catch {
            setMensagem("Erro ao buscar usuários");
        }

    }, []);


    useEffect(() => {
        async function carregarMotivos() {
            const response = await api.get("/demandas/motivos");
            setMotivos(response.data);
        }

        carregarMotivos();
    }, []);

    async function executarBusca() {
        setPagina(0);
        await carregarDados(0, filtros);
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        void carregarDados(pagina, filtros);
    }, [pagina]);

    async function limparFiltros() {
        const filtrosVazios = {
            status: [],
            prioridade: [],
            tempo: [],
            unidade: "",
            servico: "",
            motivo: "",
            usuarioId: "",
            nomeCompleto: "",
            complemento: "",
            dataAbInicial: "",
            dataAbFinal: "",
            dataEnInicial: "",
            dataEnFinal: ""
        };

        setFiltros(filtrosVazios);
        setPagina(0);
        await carregarDados(0, filtrosVazios);
    }

    useEffect(() => {
        async function carregarServicos() {
            try {
                const response = await api.get("/servicos/all");
                setUnidades(response.data.ubs);
                setServicos(response.data.servicos);
            } catch {
                setMensagemErro("Erro ao carregar serviços.");
                setMensagemSucesso("");
            }
        }

        void carregarServicos();
    }, []);

    async function abrirCardUbs(servicoId) {
        if(servicoId === null){
            return
        }
        try {
            setCarregandoUbs(true);
            const response = await api.get(`/servicos/${servicoId}`);
            setUbsSelecionada(response.data);
        } catch {
            setMensagem("Erro ao carregar dados do serviço.");
        } finally {
            setCarregandoUbs(false);
        }
    }

    function abrirAcao(demanda, tipoAcao) {
        setDemandaSelecionada(demanda);
        setAcao(tipoAcao);
        setMensagem("");
        setErros({});
    }

    function fecharModal() {
        setDemandaSelecionada(null);
        setAcao("");
        setErros({});
        setTentativa({
            tipo: "",
            descricao: "",
        });

        setRedirecionamento({
            novaServicoResponsavelId: "",
            motivoRedirecionamento: "",
        });

        setEncerramento({
            desfechoDemanda: "",
            descricaoDesfecho: "",
        });

    }

    async function salvarTentativa(e) {
        e.preventDefault();

        const payload = {
            demandaId: demandaSelecionada.id,
            tipo: tentativa.tipo || null,
            descricao: tentativa.descricao,
        };

        try {
            await api.post("/tentativas-contato", payload);
            setMensagemSucesso("Tentativa de contato registrada com sucesso!");
            setMensagemErro("");
            fecharModal();

            await carregarDados(pagina);

        } catch (error) {
            tratarErro(error);
        }
    }

    async function salvarRedirecionamento(e) {

        e.preventDefault();

        const payload = {
            novaServicoResponsavelId: redirecionamento.novaServicoResponsavelId
                ? Number(redirecionamento.novaServicoResponsavelId)
                : null,
            motivoRedirecionamento: redirecionamento.motivoRedirecionamento,
        };

        try {
            await api.patch(
                `/demandas/${demandaSelecionada.id}/redirecionar`,
                payload
            );
            setMensagemSucesso("Demanda redirecionada com sucesso!");
            setMensagemErro("")
            fecharModal();

            await carregarDados(pagina);

        } catch (error) {
            tratarErro(error);
        }
    }

    async function salvarEncerramento(e) {

        e.preventDefault();

        const payload = {
            desfechoDemanda: encerramento.desfechoDemanda || null,
            descricaoDesfecho: encerramento.descricaoDesfecho,
        };

        try {
            await api.patch(
                `/demandas/${demandaSelecionada.id}/encerrar`,
                payload
            );
            setMensagemSucesso("Demanda encerrada com sucesso!");
            setMensagemErro("")
            fecharModal();
            await carregarDados(pagina);
        } catch (error) {
            tratarErro(error);
        }
    }

    async function abrirDetalhes(d) {
        try {
            setDemandaDetalhada(d);
            const tentativasResponse = await api.get(
                `/tentativas-contato/demanda/${d.id}`
            );
            setTentativasContato(tentativasResponse.data);
        } catch {
            setMensagemErro("Erro ao carregar detalhes da demanda.");
            setMensagemSucesso("");
        }
    }

    function tratarErro(error) {
        setMensagemSucesso("");
        if (error.response?.data?.errors) {
            setErros(error.response.data.errors);
            setMensagem(error.response.data.message || "Dados inválidos.");
        } else {
            setMensagem(error.response?.data?.message || "Erro ao realizar ação.");
        }
    }

    async function exportarCsv() {
        try {

            const payload = {
                status: filtros.status,
                prioridade: filtros.prioridade,
                tempo: filtros.tempo,
                motivo: filtros.motivo || null,
                usuarioId: filtros.usuario || null,
                complemento: filtros.complemento || null,
                servicoResponsavelId: filtros.servico || null,
                servicoSolicitanteId: filtros.servico || null,
                dataAbInicial: filtros.dataAbInicial || null,
                dataAbFinal: filtros.dataAbFinal || null,
                dataEnInicial: filtros.dataEnInicial || null,
                dataEnFinal: filtros.dataEnFinal || null
            };

            if (servidor?.perfil === "SERVIDOR_APS") {

                payload.servicoResponsavelId = servidor.servicoId;

            } else if (servidor?.perfil === "SOLICITANTE") {

                payload.servicoSolicitanteId = servidor.servicoId;

            }

            const response = await api.post("/demandas/exportar",
                payload,
                {
                    responseType: "blob",
                }
            );


            const blob = new Blob([response.data], {
                type: "text/csv;charset=utf-8;",
            });

            const agora = new Date();
            const dataHora = agora
                .toLocaleString("pt-BR")
                .replaceAll("/", "-")
                .replaceAll(":", "-")
                .replaceAll(", ", "_");

            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");

            link.href = url;
            link.setAttribute(
                "download",
                `demandas-vincula-poa-${dataHora}.csv`
            );

            document.body.appendChild(link);
            link.click();
            link.remove();

            window.URL.revokeObjectURL(url);

        } catch {
            setMensagemErro("Erro ao exportar CSV.");
            setMensagemSucesso("");
        }
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando demandas...
                </div>
            </div>
        );
    }

    return (
        <div className="demandas-container">
            <div className="demandas-page">
                <div className="demandas-header">
                    <div>
                        <h1>Demandas</h1>

                        <p>
                            Gerencie buscas ativas, tentativas,
                            redirecionamentos e encerramentos
                        </p>

                    </div>

                    <span className="perfil-badge">{['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.servico}</span>
                </div>

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <span onClick={() => setMensagemSucesso("")}>✕</span>
                    </div>
                )}

                {mensagemErro && (
                    <div className="alert-card">
                        <span>{mensagemErro}</span>
                        <span onClick={() => setMensagemErro("")}>✕</span>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">


                            <span
                                className="buscar-btn"
                                onClick={() => setMostrarFiltros(true)}
                            >
                                Filtrar demandas
                            </span>

                            <span
                                className="buscar-btn"
                                onClick={() => limparFiltros()}
                            >
                                Limpar filtros
                            </span>

                            <span
                                className="buscar-btn"
                                onClick={exportarCsv}
                            >
                                Exportar CSV
                            </span>
                        </div>
                        <span
                            className="buscar-btn"
                            onClick={() => navigate("/demandas/cadastro")}
                        >
                            + Nova demanda
                        </span>
                    </div>

                    <table className="demandas-table">
                        <thead>
                        <tr>
                            <th>Usuário</th>
                            <th>Motivo da busca</th>
                            <th>Serviço solicitante</th>
                            <th>Data de abertura</th>
                            <th>Tempo restante</th>
                            <th>Status</th>
                            <th>Prioridade</th>
                            <th>Serviço responsável</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>

                        {demandas.map((d) => (

                            <tr key={d.id}>

                                <td><b>{d.usuarioNome || d.usuarioId}</b></td>
                                <td>{motivoBuscaLabel[d.motivoBuscaAtiva]}</td>
                                <td>
                                    <span
                                            className="ubs-badge ubs-clickable"
                                            onClick={() => abrirCardUbs(d.servicoSolicitanteId)}>
                                        {d.servicoSolicitanteNome || "Solicitado pela Gestão Municipal"}
                                    </span>
                                </td>
                                <td>{formatarDataHora(d.dataHoraCriacao)}</td>
                                <td>{diasRestantes(d.dataHoraCriacao, d.dataHoraLimite)}</td>
                                <td>
                                    <span className={`status-badge status-${d.status}`}>
                                        {statusLabel[d.status]}
                                    </span>
                                </td>
                                <td>
                                    <span className={`status-badge prioridade-${d.prioridade}`}>
                                        {prioridadeLabel[d.prioridade]}
                                    </span>
                                </td>
                               
                                <td>
                                    <span
                                            className="ubs-badge ubs-clickable"
                                            onClick={() => abrirCardUbs(d.servicoResponsavelId)}>
                                        {d.servicoResponsavelNome || d.servicoResponsavelId || "-"}
                                    </span>
                                </td>
                                <td>
                                    <div className="acoes-container">
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => abrirDetalhes(d)}
                                        >
                                            Ver mais
                                        </span>

                                        {d.status !== "FINALIZADA" && servidor?.perfil !== "SOLICITANTE" && (
                                            <>
                                                <span
                                                    className="btn-visualizar"
                                                    onClick={() => abrirAcao(d, "TENTATIVA")}
                                                >
                                                    Tentativa contato
                                                </span>

                                                <span
                                                    className="btn-visualizar"
                                                    onClick={() => abrirAcao(d, "REDIRECIONAR")}
                                                >
                                                    Redirecionar
                                                </span>

                                                <span
                                                    className="btn-encerrar"
                                                    onClick={() => abrirAcao(d, "ENCERRAR")}
                                                >
                                                    Encerrar
                                                </span>

                                            </>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {demandas.length === 0 && (
                        <div className="empty-state">
                            Nenhuma demanda encontrada.
                        </div>
                    )}

                    <Pagination
                        pagina={pagina}
                        totalPaginas={totalPaginas}
                        onChangePagina={setPagina}
                    />
                </div>
            </div>

            {demandaSelecionada && acao === "TENTATIVA" && (
                <ModalTentativaContato
                    demanda={demandaSelecionada}
                    tentativa={tentativa}
                    setTentativa={setTentativa}
                    erros={erros}
                    onSalvar={salvarTentativa}
                    onFechar={fecharModal}
                    mensagem={mensagem}
                    setMensagem={setMensagem}
                />
            )}

            {demandaSelecionada && acao === "REDIRECIONAR" && (
                <ModalRedirecionarDemanda
                    demanda={demandaSelecionada}
                    servicos={unidades}
                    redirecionamento={redirecionamento}
                    setRedirecionamento={setRedirecionamento}
                    erros={erros}
                    onSalvar={salvarRedirecionamento}
                    onFechar={fecharModal}
                    mensagem={mensagem}
                    setMensagem={setMensagem}
                />
            )}

            {demandaSelecionada && acao === "ENCERRAR" && (
                <ModalEncerrarDemanda
                    demanda={demandaSelecionada}
                    encerramento={encerramento}
                    setEncerramento={setEncerramento}
                    erros={erros}
                    onSalvar={salvarEncerramento}
                    onFechar={fecharModal}
                    mensagem={mensagem}
                    setMensagem={setMensagem}
                />
            )}

            {demandaDetalhada && (
                <ModalDetalhesDemanda
                    demanda={demandaDetalhada}
                    tentativasContato={tentativasContato}
                    onFechar={() => {
                        setDemandaDetalhada(null);
                        setTentativasContato([]);
                    }}
                />
            )}

            {ubsSelecionada && (
                <ModalUbs
                    ubsSelecionada={ubsSelecionada}
                    setUbsSelecionada={setUbsSelecionada}
                />
            )}

            {carregandoUbs && (
                <div className="ubs-overlay">
                    <div className="loading-card">
                        <p>Carregando dados do serviço...</p>
                    </div>
                </div>
            )}

            <ModalFiltrosDemanda
                aberto={mostrarFiltros}
                onFechar={() => setMostrarFiltros(false)}
                filtros={filtros}
                setFiltros={setFiltros}
                unidades={unidades}
                servicos={servicos}
                motivos={motivos}
                onAplicar={() => {
                    setMostrarFiltros(false);
                    void executarBusca();
                }}
                servidor={servidor}
                onLimpar={limparFiltros}
                buscarUsuarios={buscarUsuariosAutocomplete}
                setUsuarios={setUsuariosBusca}
                usuarios={usuariosBusca}
            />
        </div>
    );
}

export default Demandas;