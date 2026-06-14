import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./usuarios.css";
import { useNavigate } from "react-router-dom";
import ModalUbs from "../components/ModalUbs.jsx";
import Pagination from "../components/Paginations.jsx";

function Servidores() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [servidores, setServidores] = useState([]);
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);
    const [filtro, setFiltro] = useState("");
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [modoFiltrado, setModoFiltrado] = useState(false);
    const tamanhoPagina = 10;

    const carregarDados = useCallback(async (paginaAtual = pagina) => {
        try {
            setCarregando(true);
            const response = await api.get(`/servidores?page=${paginaAtual}&size=${tamanhoPagina}`);
            setServidores(response.data.content);
            setTotalPaginas(response.data.page.totalPages);
        } catch {
            setMensagem("Erro ao carregar servidores.");
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
            setMensagem("Erro ao buscar servidores.");
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
                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button type="button" onClick={() => setMensagem("")}>✕</button>
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

                        </div>
                        <button
                            className="buscar-btn"
                            onClick={() => navigate("/servidores/cadastro")}
                        >
                            + Novo servidor
                        </button>

                    </div>

                    <table className="usuarios-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Serviço vinculado</th>
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

                                    <td>
                                        {s.unidadeSaudeId !== null ? (
                                            <button
                                                type="button"
                                                className="ubs-badge ubs-clickable"
                                                onClick={() => abrirCardUbs(s.unidadeSaudeId)}
                                            >
                                                {s.unidadeSaudeNome}
                                            </button>
                                        ) : (
                                            "-"
                                        )}
                                    </td>
                                </tr>
                            ))}


                        </tbody>
                    </table>

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