import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./pacientes.css";
import { useNavigate } from "react-router-dom";
import {mascaraDocumento, mascaraTelefone} from "../utils/mascaras.js";
import ModalUbs from "../components/ModalUbs.jsx";
import Pagination from "../components/Paginations.jsx";

function Pacientes() {
    const navigate = useNavigate();
    const { usuario } = useAuth();
    const [pacientes, setPacientes] = useState([]);
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
            let pacientesResponse;
            if (usuario?.perfil === "GESTAO_MUNICIPAL") {
                pacientesResponse = await api.get(
                    `/pacientes?page=${paginaAtual}&size=${tamanhoPagina}`
                );
            } else if (usuario?.perfil === "USUARIO_APS") {
                pacientesResponse = await api.get(
                    `/pacientes/unidadeSaude/${usuario.unidadeSaudeId}?page=${paginaAtual}&size=${tamanhoPagina}`
                );
            } else {
                setMensagem("Seu perfil não possui acesso à lista de pacientes.");
                setPacientes([]);
                return;
            }
            setPacientes(pacientesResponse.data.content);
            setTotalPaginas(pacientesResponse.data.page.totalPages);
        } catch {
            setMensagem("Erro ao carregar pacientes.");
        } finally {
            setCarregando(false);
        }
    }, [usuario, pagina]);

    const buscarPacientes = useCallback(async (paginaAtual = pagina) => {
        if (!filtro.trim()) {
            return;
        }
        try {
            setCarregando(true);
            let pacientesResponse;
            if (usuario?.perfil === "GESTAO_MUNICIPAL") {
                pacientesResponse = await api.get(
                    `/pacientes/filtrados/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`
                );
            } else if (usuario?.perfil === "USUARIO_APS") {
                pacientesResponse = await api.get(
                    `/pacientes/filtrados/unidadeSaude/${usuario.unidadeSaudeId}/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`
                );
            } else {
                setMensagem("Seu perfil não possui acesso à lista de pacientes.");
                setPacientes([]);
                return;
            }
            setPacientes(pacientesResponse.data.content);
            setTotalPaginas(pacientesResponse.data.page.totalPages);
        } catch {
            setMensagem("Erro ao buscar pacientes.");
        } finally {
            setCarregando(false);
        }
    }, [usuario, pagina, filtro]);

    useEffect(() => {
        const executar = async () => {
            if (modoFiltrado) {
                await buscarPacientes(pagina);
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
            await buscarPacientes(0);
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
            setMensagem("Erro ao carregar dados da UBS.");
        } finally {
            setCarregandoUbs(false);
        }
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando pacientes...
                </div>
            </div>
        );
    }
    return (
        <div className="pacientes-container">
            <div className="pacientes-page">
                <div className="pacientes-header">
                    <div>
                        <h1 className="pacientes-title">
                            Pacientes
                        </h1>
                        <p className="pacientes-subtitle">
                            Visualize e gerencie os pacientes cadastrados
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
                                placeholder="Buscar paciente..."
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
                            onClick={() => navigate("/pacientes/cadastro")}
                        >
                            + Novo paciente
                        </button>

                    </div>

                    <table className="pacientes-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Documento</th>
                            <th>Telefone</th>
                            <th>UBS</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>

                        {pacientes.map((paciente) => (

                            <tr key={paciente.id}>
                                <td>
                                    <div className="paciente-nome">
                                        {paciente.nomeCompleto}
                                    </div>
                                </td>
                                <td>
                                    {mascaraDocumento(paciente.documento)}
                                </td>
                                <td>
                                    {mascaraTelefone(paciente.telefone) || "-"}
                                </td>
                                <td>
                                    <button
                                        type="button"
                                        className="ubs-badge ubs-clickable"
                                        onClick={() => abrirCardUbs(paciente.unidadeSaudeId)}
                                    >
                                        {paciente.unidadeSaudeNome}
                                    </button>
                                </td>
                                <td>
                                    <div className="acoes-container">
                                        <button
                                            className="btn-visualizar"
                                            onClick={() => navigate(`/pacientes/${paciente.id}`)}
                                        >
                                            Visualizar
                                        </button>

                                        <button
                                            className="btn-editar"
                                            onClick={() => navigate(`/pacientes/${paciente.id}/editar`)}
                                        >
                                            Editar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {pacientes.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum paciente encontrado.
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
                    <div className="ubs-card">
                        <p>Carregando dados da UBS...</p>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Pacientes;