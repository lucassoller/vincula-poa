import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./usuarios.css";
import { useNavigate } from "react-router-dom";
import Pagination from "../components/Paginations.jsx";
import { mascaraTelefone } from "../utils/mascaras.js";
import ModalUbs from "../components/ModalUbs.jsx";
import {tipoServico} from "../utils/utils.js";

function UnidadesSaude() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [unidades, setUnidades] = useState([]);
    const [unidadeDetalhada, setUnidadeDetalhada] = useState(null);
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [filtro, setFiltro] = useState("");
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [modoFiltrado, setModoFiltrado] = useState(false);
    const tamanhoPagina = 10;

    const carregarDados = useCallback(async (paginaAtual = pagina) => {

        try {
            setCarregando(true);
            const response = await api.get(
                `/unidades-saude?page=${paginaAtual}&size=${tamanhoPagina}`
            );
            setUnidades(response.data.content);
            setTotalPaginas(response.data.page.totalPages);

        } catch {
            setMensagem("Erro ao carregar serviço.");
        } finally {
            setCarregando(false);
        }
    }, [pagina]);

    const buscarUnidades = useCallback(async (paginaAtual = pagina) => {

        if (!filtro.trim()) {
            return;
        }
        try {
            setCarregando(true);
            const response = await api.get(
                `/unidades-saude/filtradas/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`
            );
            setUnidades(response.data.content);
            setTotalPaginas(response.data.page.totalPages);

        } catch {
            setMensagem("Erro ao buscar serviços.");
        } finally {
            setCarregando(false);
        }

    }, [pagina, filtro]);

    useEffect(() => {
        const executar = async () => {
            if (modoFiltrado) {
                await buscarUnidades(pagina);
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
            await buscarUnidades(0);
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

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando serviços de saúde...
                </div>
            </div>
        );
    }

    return (
        <div className="usuarios-container">
            <div className="usuarios-page">
                <div className="usuarios-header">
                    <div>
                        <h1 className="usuarios-title">Serviços de saúde</h1>
                        <p className="usuarios-subtitle">
                            Visualize e gerencie os serviços cadastrados
                        </p>
                    </div>

                    <div className="perfil-badge">
                        {servidor?.perfil === 'GESTAO_MUNICIPAL' ? servidor.perfil : servidor.unidadeSaude}
                    </div>

                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">
                            <input
                                className="usuario-search"
                                placeholder="Buscar serviço..."
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        void executarBusca();
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
                        {(servidor?.perfil === "GESTAO_MUNICIPAL") && (
                            <span
                                className="buscar-btn"
                                onClick={() => navigate("/unidades-saude/cadastro")}
                            >
                                + Novo serviço
                            </span>
                        )}
                    </div>
                    <table className="usuarios-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>CNES</th>
                            <th>Tipo de serviço</th>
                            <th>Telefone</th>
                            <th>Telefone adicional</th>
                            <th>Bairro</th>
                            <th>Ações</th>
                        </tr>
                        </thead>

                        <tbody>

                        {unidades.map((ubs) => (

                            <tr key={ubs.id}>
                                <td>
                                    <div className="usuario-nome">
                                        {ubs.nome}
                                    </div>
                                </td>
                                <td>{ubs.cnes}</td>
                                <td>
                                    <span className="ubs-badge">
                                        {ubs.tipoServico}
                                    </span>
                                </td>

                                <td>{mascaraTelefone(ubs.telefone) || "-"}</td>
                                <td>{mascaraTelefone(ubs.telefone2) || "-"}</td>

                                <td className="bairro-coluna">{ubs.endereco?.bairro || "-"}</td>
                                <td>
                                    <div className="acoes-container">
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => setUnidadeDetalhada(ubs)}
                                        >
                                            Ver mais
                                        </span>
                                        {servidor?.perfil === "GESTAO_MUNICIPAL" && (
                                            <span
                                                className="btn-editar"
                                                onClick={() =>
                                                    navigate(`/unidades-saude/${ubs.id}/editar`)
                                                }
                                            >
                                                Editar
                                            </span>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}

                        </tbody>
                    </table>

                    {unidades.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum serviço encontrado.
                        </div>
                    )}

                    <Pagination
                        pagina={pagina}
                        totalPaginas={totalPaginas}
                        onChangePagina={setPagina}
                    />
                </div>
            </div>
            {unidadeDetalhada && (
                <ModalUbs
                    ubsSelecionada={unidadeDetalhada}
                    setUbsSelecionada={setUnidadeDetalhada}
                />
            )}
        </div>
    );
}

export default UnidadesSaude;