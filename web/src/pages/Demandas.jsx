import { useEffect, useState, useCallback } from "react";
import api from "../api/api";
import { useAuth } from "../context/AuthContext.jsx";
import "./demandas.css";
import {prazoLabel, formatarDataHora, statusLabel, motivoBuscaLabel } from "../utils/utils.js";
import ModalTentativaContato from "../components/ModalTentativaContato";
import ModalRedirecionarDemanda from "../components/ModalRedirecionarDemanda";
import ModalEncerrarDemanda from "../components/ModalEncerrarDemanda";
import ModalDetalhesDemanda from "../components/ModalDetalhesDemanda";
import { useNavigate } from "react-router-dom";
import Pagination from "../components/Paginations.jsx";

function Demandas() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [demandas, setDemandas] = useState([]);
    const [unidades, setUnidades] = useState([]);
    const [demandaSelecionada, setDemandaSelecionada] = useState(null);
    const [acao, setAcao] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [erros, setErros] = useState({});
    const [demandaDetalhada, setDemandaDetalhada] = useState(null);
    const [tentativasContato, setTentativasContato] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [filtro, setFiltro] = useState("");
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [modoFiltrado, setModoFiltrado] = useState(false);
    const tamanhoPagina = 10;

    const [tentativa, setTentativa] = useState({
        tipo: "",
        descricao: "",
    });

    const [redirecionamento, setRedirecionamento] = useState({
        novaUnidadeResponsavelId: "",
        motivoRedirecionamento: "",
    });

    const [encerramento, setEncerramento] = useState({
        desfechoDemanda: "",
        descricaoDesfecho: "",
    });

    const carregarDados = useCallback(async (paginaAtual = pagina) => {

        try {
            setCarregando(true);
            let demandasResponse;
            if (servidor?.perfil === "GESTAO_MUNICIPAL") {
                demandasResponse = await api.get(
                    `/demandas?page=${paginaAtual}&size=${tamanhoPagina}`
                );

            } else if (servidor?.perfil === "SERVIDOR_APS") {
                demandasResponse = await api.get(
                    `/demandas/unidade/${servidor.unidadeSaudeId}?page=${paginaAtual}&size=${tamanhoPagina}`
                );
            } else if (servidor?.perfil === "SOLICITANTE") {
                demandasResponse = await api.get(
                    `/demandas/servidor/${servidor.id}?page=${paginaAtual}&size=${tamanhoPagina}`
                );
            }else {
                demandasResponse = { data: { content: [], page: { totalPages: 0 } } };
            }

            setDemandas(demandasResponse.data.content);
            setTotalPaginas(demandasResponse.data.page.totalPages);
        } catch {
            setMensagemSucesso("Erro ao carregar demandas.");
        } finally {
            setCarregando(false);
        }

    }, [servidor, pagina]);

    const buscarDemandas = useCallback(async (paginaAtual = pagina) => {

        if (!filtro.trim()) {
            return;
        }
        try {
            setCarregando(true);
            let demandasResponse;

            if (servidor?.perfil === "GESTAO_MUNICIPAL") {
                demandasResponse = await api.get(
                    `/demandas/filtradas/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`
                );

            } else if (servidor?.perfil === "SERVIDOR_APS") {
                demandasResponse = await api.get(
                    `/demandas/filtradas/unidade/${servidor.unidadeSaudeId}/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`
                );

            }  else if (servidor?.perfil === "SOLICITANTE") {
                demandasResponse = await api.get(
                    `/demandas/filtradas/servidor/${servidor.id}/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`
                );

            } else {
                demandasResponse = { data: { content: [], page: { totalPages: 0 } } };

            }
            setDemandas(demandasResponse.data.content);
            setTotalPaginas(demandasResponse.data.page.totalPages);

        } catch {
            setMensagemSucesso("Erro ao buscar demandas.");
        } finally {
            setCarregando(false);
        }
    }, [servidor, pagina, filtro]);

    useEffect(() => {

        const executar = async () => {
            if (modoFiltrado) {
                await buscarDemandas(pagina);
            } else {
                await carregarDados(pagina);
            }
        };
        void executar();

    }, [pagina, modoFiltrado]);

    useEffect(() => {
        async function carregarUnidades() {
            try {
                if(servidor?.perfil !== 'SOLICITANTE'){
                    const response = await api.get("/unidades-saude/all");
                    setUnidades(response.data);
                }
            } catch {
                setMensagemSucesso("Erro ao carregar unidades.");
            }
        }

        void carregarUnidades();
    }, []);

    async function executarBusca() {
        if (!filtro.trim()) {
            return;
        }
        setModoFiltrado(true);

        if (pagina !== 0) {
            setPagina(0);
        } else {
            await buscarDemandas(0);
        }
    }

    async function limparFiltro() {
        setFiltro("");
        setModoFiltrado(false);

        if (pagina !== 0) {
            setPagina(0);
        } else {
            await carregarDados(0);
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
            novaUnidadeResponsavelId: "",
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
            setMensagemSucesso("Tentativa registrada com sucesso!");
            fecharModal();

            if (modoFiltrado) {
                await buscarDemandas(pagina);
            } else {
                await carregarDados(pagina);
            }
        } catch (error) {
            tratarErro(error);
        }
    }

    async function salvarRedirecionamento(e) {

        e.preventDefault();

        const payload = {
            novaUnidadeResponsavelId: redirecionamento.novaUnidadeResponsavelId
                ? Number(redirecionamento.novaUnidadeResponsavelId)
                : null,
            motivoRedirecionamento: redirecionamento.motivoRedirecionamento,
        };

        try {
            await api.patch(
                `/demandas/${demandaSelecionada.id}/redirecionar`,
                payload
            );
            setMensagemSucesso("Demanda redirecionada com sucesso!");
            fecharModal();

            if (modoFiltrado) {
                await buscarDemandas(pagina);
            } else {
                await carregarDados(pagina);
            }
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
            fecharModal();
            if (modoFiltrado) {
                await buscarDemandas(pagina);
            } else {
                await carregarDados(pagina);
            }
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
            setMensagemSucesso("Erro ao carregar detalhes da demanda.");
        }
    }

    function tratarErro(error) {
        if (error.response?.data?.errors) {
            setErros(error.response.data.errors);
            setMensagem(error.response.data.message || "Dados inválidos.");
        } else {
            setMensagem(error.response?.data?.message || "Erro ao realizar ação.");
        }
    }

    async function exportarCsv() {
        try {

            let response;
            if (modoFiltrado && filtro.trim()) {
                if (servidor?.perfil === "GESTAO_MUNICIPAL") {
                    response = await api.get(
                        `/demandas/exportar/filtradas/${filtro}`,
                        {
                            responseType: "blob",
                        }
                    );
                } else if (servidor?.perfil === "SERVIDOR_APS") {
                    response = await api.get(
                        `/demandas/exportar/filtradas/unidade/${servidor.unidadeSaudeId}/${filtro}`,
                        {
                            responseType: "blob",
                        }
                    );
                }else if (servidor?.perfil === "SOLICITANTE") {
                    response = await api.get(
                        `/demandas/exportar/filtradas/servidor/${servidor.id}/${filtro}`,
                        {
                            responseType: "blob",
                        }
                    );
                }
            } else {
                if (servidor?.perfil === "GESTAO_MUNICIPAL") {
                    response = await api.get(
                        "/demandas/exportar",
                        {
                            responseType: "blob",
                        }
                    );
                } else if (servidor?.perfil === "SERVIDOR_APS") {

                    response = await api.get(
                        `/demandas/exportar/unidade/${servidor.unidadeSaudeId}`,
                        {
                            responseType: "blob",
                        }
                    );
                } else if (servidor?.perfil === "SOLICITANTE") {

                    response = await api.get(
                        `/demandas/exportar/servidor/${servidor.id}`,
                        {
                            responseType: "blob",
                        }
                    );
                }
            }
            const blob = new Blob(
                [response.data],
                {
                    type: "text/csv;charset=utf-8;",
                }
            );

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
            setMensagem("Erro ao exportar CSV.");
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

                    <span className="perfil-badge">{servidor?.perfil}</span>
                </div>

                {mensagemSucesso && (
                    <div className="alert-card">
                        <span>{mensagemSucesso}</span>
                        <button
                            type="button"
                            onClick={() => setMensagemSucesso("")}
                        >
                            ✕
                        </button>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">
                            <input
                                className="usuario-search"
                                placeholder="Buscar demanda..."
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                onKeyDown={(e) => {

                                    if (e.key === "Enter") {
                                        executarBusca();
                                    }
                                }}
                            />

                            <button
                                type="button"
                                className="buscar-btn"
                                onClick={executarBusca}
                            >
                                Buscar
                            </button>

                            <button
                                type="button"
                                className="buscar-btn"
                                onClick={limparFiltro}
                            >
                                Limpar filtro
                            </button>

                            <button
                                type="button"
                                className="buscar-btn"
                                onClick={exportarCsv}
                            >
                                Exportar CSV
                            </button>
                        </div>
                        <button
                            className="buscar-btn"
                            onClick={() => navigate("/demandas/cadastro")}
                        >
                            + Nova demanda
                        </button>
                    </div>

                    <table className="demandas-table">
                        <thead>
                        <tr>
                            <th>Usuário</th>
                            <th>Motivo</th>
                            <th>Criador</th>
                            <th>Data de abertura</th>
                            <th>Data de encerramento</th>
                            <th>Status</th>
                            <th>Prazo</th>
                            <th>Unidade</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>

                        {demandas.map((d) => (

                            <tr key={d.id}>

                                <td><b>{d.usuarioNome || d.usuarioId}</b></td>
                                <td>{motivoBuscaLabel[d.motivoBuscaAtiva]}</td>
                                <td>{d.servidorCriadorNome || d.servidorCriadorId}</td>
                                <td>{formatarDataHora(d.dataHoraCriacao)}</td>
                                <td>{formatarDataHora(d.dataHoraFinalizacao) || "-"}</td>

                                <td>
                                    <span className={`status-badge status-${d.status}`}>
                                        {statusLabel[d.status]}
                                    </span>
                                </td>

                                <td>{prazoLabel[d.prazoDemanda] || "-"}</td>
                                <td>{d.unidadeResponsavelNome || d.unidadeResponsavelId}</td>
                                <td>
                                    <div className="acoes-container">
                                        <button
                                            className="btn-visualizar"
                                            onClick={() => abrirDetalhes(d)}
                                        >
                                            Ver mais
                                        </button>

                                        {d.status !== "FINALIZADA" && servidor?.perfil !== "SOLICITANTE" && (
                                            <>
                                                <button
                                                    className="btn-tentativa"
                                                    onClick={() => abrirAcao(d, "TENTATIVA")}
                                                >
                                                    Tentativa contato
                                                </button>

                                                <button
                                                    className="btn-editar"
                                                    onClick={() => abrirAcao(d, "REDIRECIONAR")}
                                                >
                                                    Redirecionar
                                                </button>

                                                <button
                                                    className="btn-encerrar"
                                                    onClick={() => abrirAcao(d, "ENCERRAR")}
                                                >
                                                    Encerrar
                                                </button>

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
                    unidades={unidades}
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
        </div>
    );
}

export default Demandas;