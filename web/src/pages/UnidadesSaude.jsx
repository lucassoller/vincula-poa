import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./pacientes.css";
import { useNavigate } from "react-router-dom";
import Pagination from "../components/Paginations.jsx";
import { mascaraTelefone } from "../utils/mascaras.js";

function UnidadesSaude() {
    const navigate = useNavigate();
    const { usuario } = useAuth();
    const [unidades, setUnidades] = useState([]);
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
            setMensagem("Erro ao carregar unidades de saúde.");
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
            setMensagem("Erro ao buscar unidades.");
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
                    Carregando unidades de saúde...
                </div>
            </div>
        );
    }

    return (
        <div className="pacientes-container">
            <div className="pacientes-page">
                <div className="pacientes-header">
                    <div>
                        <h1 className="pacientes-title">Unidades de Saúde</h1>
                        <p className="pacientes-subtitle">
                            Visualize e gerencie as UBS cadastradas
                        </p>
                    </div>

                    <div className="perfil-badge">
                        {usuario?.perfil}
                    </div>

                </div>

                {mensagem && (
                    <div className="alerta-geral">
                        {mensagem}
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">
                            <input
                                className="paciente-search"
                                placeholder="Buscar UBS..."
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        void executarBusca();
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
                        {(usuario?.perfil === "GESTAO_MUNICIPAL" || usuario?.perfil === "EXECUTOR_APS") && (
                            <button
                                className="buscar-btn"
                                onClick={() => navigate("/unidades-saude/cadastro")}
                            >
                                + Nova UBS
                            </button>
                        )}
                    </div>
                    <table className="pacientes-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>CNES</th>
                            <th>Telefone</th>
                            <th>Telefone adicional</th>
                            <th>Rua</th>
                            <th>Bairro</th>
                            {usuario?.perfil === "GESTAO_MUNICIPAL" && (
                                <th>Ações</th>
                            )}
                        </tr>
                        </thead>

                        <tbody>

                        {unidades.map((ubs) => (

                            <tr key={ubs.id}>
                                <td>
                                    <div className="paciente-nome">
                                        {ubs.nome}
                                    </div>
                                </td>
                                <td>{ubs.cnes}</td>
                                <td>{mascaraTelefone(ubs.telefone) || "-"}</td>
                                <td>{mascaraTelefone(ubs.telefone2) || "-"}</td>
                                <td>{ubs.endereco?.rua + " - " + ubs.endereco?.numero || "-"}</td>
                                <td>{ubs.endereco?.bairro || "-"}</td>
                                <td>
                                    <div className="acoes-container">
                                        {usuario?.perfil === "GESTAO_MUNICIPAL" && (
                                            <button
                                                className="btn-editar"
                                                onClick={() =>
                                                    navigate(`/unidades-saude/${ubs.id}/editar`)
                                                }
                                            >
                                                Editar
                                            </button>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}

                        </tbody>
                    </table>

                    {unidades.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhuma UBS encontrada.
                        </div>
                    )}

                    <Pagination
                        pagina={pagina}
                        totalPaginas={totalPaginas}
                        onChangePagina={setPagina}
                    />
                </div>
            </div>
        </div>
    );
}

export default UnidadesSaude;