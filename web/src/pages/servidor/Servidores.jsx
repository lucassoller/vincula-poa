import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.jsx";
import api from "../../api/api.js";
import "../../styles/usuarios.css";
import { useNavigate } from "react-router-dom";
import ModalUbs from "../../components/ModalUbs.jsx";
import Pagination from "../../components/Paginations.jsx";
import ModalTransferirServidor from "../../components/ModalTransferirServidor.jsx";
import {perfilLabel} from "../../utils/utils.js";

function Servidores() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [servidores, setServidores] = useState([]);
    const [mensagem, setMensagem] = useState("");
    const [mensagemErro, setMensagemErro] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [servidorSelecionado, setServidorSelecionado] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);
    const [filtro, setFiltro] = useState("");
    const [pagina, setPagina] = useState(0);
    const [unidades, setUnidades] = useState([]);
    const [servicos, setServicos] = useState([]);
    const [especializados, setEsp] = useState([]);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [modoFiltrado, setModoFiltrado] = useState(false);
    const [erros, setErros] = useState({});
    const [transferencia, setTransferencia] = useState({
        perfil: "",
        unidadeSaudeId: "",
        tipoServico: ""
    });

    const tamanhoPagina = 10;

    const carregarDados = useCallback(async (paginaAtual = pagina) => {
        try {
            setCarregando(true);
            const response = await api.get(`/servidores?page=${paginaAtual}&size=${tamanhoPagina}`);
            setServidores(response.data.content);
            setTotalPaginas(response.data.page.totalPages);
        } catch {
            setMensagemErro("Erro ao carregar servidores.");
        } finally {
            setCarregando(false);
        }
    }, [pagina]);

    const buscarUsuarios = useCallback(async (paginaAtual = pagina) => {
        if (!filtro.trim()) {
            return;
        }
        try {
            setCarregando(true);
            const response = await api.get(`/servidores/filtrados/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`);
            setServidores(response.data.content);
            setTotalPaginas(response.data.page.totalPages);
        } catch {
            setMensagemErro("Erro ao buscar servidores.");
        } finally {
            setCarregando(false);
        }
    }, [pagina, filtro]);

    useEffect(() => {
        const executar = async () => {
            if (modoFiltrado) {
                await buscarUsuarios(pagina);
            } else {
                await carregarDados(pagina);
            }
        };
        void executar();
    }, [pagina, modoFiltrado]);

    useEffect(() => {
        async function carregarUnidades() {
            try {
                const [ubsResponse, servicosResponse, espResponse] = await Promise.all([
                    api.get("/unidades-saude/ubs"),
                    api.get("/unidades-saude/outro"),
                    api.get("/unidades-saude/especializado")
                ]);

                setUnidades(ubsResponse.data);
                setServicos(servicosResponse.data);
                setEsp(espResponse.data);

            } catch {
                setMensagemErro("Erro ao carregar os serviços.");
                setMensagemSucesso("");
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
            await buscarUsuarios(0);
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

    async function abrirCardUbs(unidadeSaudeId) {
        if(unidadeSaudeId === null){
            return
        }
        try {
            setCarregandoUbs(true);
            const response = await api.get(`/unidades-saude/${unidadeSaudeId}`);
            setUbsSelecionada(response.data);
        } catch {
            setMensagem("Erro ao carregar dados do serviço.");
        } finally {
            setCarregandoUbs(false);
        }
    }

    function abrirCardTransferencia(servidor) {
        if(servidor === null){
            return
        }
        setServidorSelecionado(servidor);

        setTransferencia({
            perfil: servidor.perfil,
            unidadeSaudeId: String(servidor.unidadeSaudeId ?? ""),
            tipoServico: servidor.tipoServico
        });
    }

    async function salvarTransferencia(e) {

        e.preventDefault();

        const payload = {
            perfil: transferencia.perfil || null,
            unidadeSaudeId: transferencia.unidadeSaudeId
                ? Number(transferencia.unidadeSaudeId)
                : null,
        };
        try {
            await api.put(
                `/servidores/transferir/${servidorSelecionado.id}`,
                payload
            );
            setMensagemSucesso("Servidor transferido com sucesso!");
            setMensagemErro("")
            fecharModal();
            if (modoFiltrado) {
                await buscarUsuarios(pagina);
            } else {
                await carregarDados(pagina);
            }
        } catch (error) {
            tratarErro(error);
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

    function fecharModal() {
        setServidorSelecionado(null)
        setErros({});
        setMensagem("");
        setTransferencia({
            perfil: "",
            unidadeSaudeId: "",
            tipoServico: ""
        });
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando servidores...
                </div>
            </div>
        );
    }
    return (
        <div className="usuarios-container">
            <div className="usuarios-page">
                <div className="usuarios-header">
                    <div>
                        <h1 className="usuarios-title">
                            Servidores
                        </h1>
                        <p className="usuarios-subtitle">
                            Visualize e gerencie os servidores cadastrados
                        </p>
                    </div>
                    <div className="perfil-badge">
                        {servidor?.perfil === 'GESTAO_MUNICIPAL' ? servidor.perfil : servidor.unidadeSaude}
                    </div>
                </div>
                {mensagemErro && (
                    <div className="alert-card">
                        <span>{mensagemErro}</span>
                        <span onClick={() => setMensagemErro("")}>✕</span>
                    </div>
                )}

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <span onClick={() => setMensagemSucesso("")}>✕</span>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">
                            <input
                                className="usuario-search"
                                placeholder="Buscar servidor..."
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        executarBusca();
                                    }
                                }}
                            />
                            <span
                                className="buscar-btn"
                                onClick={executarBusca}
                            >
                                Buscar
                            </span>
                            <span
                                className="buscar-btn"
                                onClick={limparFiltro}
                            >
                                Limpar filtro
                            </span>

                        </div>
                        <span
                            className="buscar-btn"
                            onClick={() => navigate("/servidores/cadastro")}
                        >
                            + Novo servidor
                        </span>

                    </div>

                    <table className="usuarios-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Perfil</th>
                            <th>Serviço vinculado</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                            {servidores.map((s) => (

                                <tr key={s.id}>
                                    <td>
                                        <div className="usuario-nome">
                                            {s.nome}
                                        </div>
                                    </td>
                                    <td>{s.email}</td>
                                    <td>{perfilLabel[s.perfil]}</td>
                                    <td>
                                        <span
                                            className="ubs-badge ubs-clickable"
                                            onClick={() => abrirCardUbs(s.unidadeSaudeId)}
                                        >
                                            {s.perfil === "GESTAO_MUNICIPAL"
                                                ? "Sem serviço vinculado"
                                                : s.unidadeSaudeNome}
                                        </span>
                                    </td>
                                    <td>
                                        {(s.perfil === "SOLICITANTE" || s.perfil === "SERVIDOR_APS") && (
                                            <span
                                                className="btn-editar"
                                                onClick={() => abrirCardTransferencia(s)}
                                            >
                                                Transferir
                                            </span>
                                        )}
                                    </td>
                                </tr>
                            ))}


                        </tbody>
                    </table>

                    {servidorSelecionado && (
                        <ModalTransferirServidor
                            servidor={servidorSelecionado}
                            unidades={unidades}
                            servicos={servicos}
                            especializados={especializados}
                            transferencia={transferencia}
                            setTransferencia={setTransferencia}
                            erros={erros}
                            onSalvar={salvarTransferencia}
                            onFechar={fecharModal}
                            mensagem={mensagem}
                            setMensagem={setMensagem}
                        />
                    )}

                    {servidores.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum servidor encontrado.
                        </div>
                    )}
                    <Pagination
                        pagina={pagina}
                        totalPaginas={totalPaginas}
                        onChangePagina={setPagina}
                    />
                </div>
            </div>

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
        </div>
    );
}

export default Servidores;