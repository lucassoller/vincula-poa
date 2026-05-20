import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./pacientes.css"
import {useNavigate} from "react-router-dom";
import {
    mascaraDocumento,
    mascaraTelefone
} from "../utils/mascaras.js";

import ModalUbs from "../components/ModalUbs.jsx";

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
    const tamanhoPagina = 10;

    useEffect(() => {
        let ativo = true;

        async function carregar() {
            try {
                setCarregando(true);

                let pacientesResponse;

                if (usuario?.perfil === "GESTAO_MUNICIPAL") {
                    pacientesResponse = await api.get(
                        `/pacientes?page=${pagina}&size=${tamanhoPagina}&sort=nomeCompleto,asc`
                    );
                } else if (usuario?.perfil === "EXECUTOR_APS") {
                    pacientesResponse = await api.get(
                        `/pacientes/unidadeSaude/${usuario.unidadeSaudeId}?page=${pagina}&size=${tamanhoPagina}&sort=nomeCompleto,asc`
                    );
                } else {
                    setMensagem("Seu perfil não possui acesso à lista de pacientes.");
                    setPacientes([]);
                    return;
                }

                if (ativo) {
                    setPacientes(pacientesResponse.data.content);
                    setTotalPaginas(pacientesResponse.data.page.totalPages);
                }
            } catch {
                if (ativo) {
                    setMensagem("Erro ao carregar pacientes.");
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        void carregar();

        return () => {
            ativo = false;
        };
    }, [usuario, pagina]);

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando pacientes...
                </div>
            </div>
        );
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

    const pacientesFiltrados = pacientes.filter((paciente) => {
        const busca = filtro.toLowerCase();

        return (
            paciente.nomeCompleto?.toLowerCase().includes(busca) ||
            paciente.documento?.includes(busca) ||
            paciente.telefone?.includes(busca) ||
            paciente.unidadeSaudeNome?.toLowerCase().includes(busca)
        );
    });

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
                        <input
                            className="paciente-search"
                            placeholder="Buscar paciente..."
                            value={filtro}
                            onChange={(e) => setFiltro(e.target.value)}
                        />
                        <button className="novo-paciente-btn" onClick={() => navigate("/pacientes/cadastro")}>
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
                        {pacientesFiltrados.map((paciente) => (
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
                                        <button className="btn-visualizar" onClick={() => navigate(`/pacientes/${paciente.id}`)}>
                                            Visualizar
                                        </button>
                                        <button className="btn-editar" onClick={() => navigate(`/pacientes/${paciente.id}/editar`)}>
                                            Editar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    {pacientesFiltrados.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum paciente encontrado.
                        </div>
                    )}
                    {totalPaginas > 1 && (
                        <div className="pagination">

                            <button
                                type="button"
                                className="pagination-btn"
                                disabled={pagina === 0}
                                onClick={() => setPagina(0)}
                            >
                                Primeira
                            </button>

                            <button
                                type="button"
                                className="pagination-btn"
                                disabled={pagina === 0}
                                onClick={() => setPagina((prev) => prev - 1)}
                            >
                                Anterior
                            </button>

                            <span className="pagination-info">
                                Página {pagina + 1} de {totalPaginas}
                            </span>

                            <button
                                type="button"
                                className="pagination-btn"
                                disabled={pagina + 1 >= totalPaginas}
                                onClick={() => setPagina((prev) => prev + 1)}
                            >
                                Próxima
                            </button>

                            <button
                                type="button"
                                className="pagination-btn"
                                disabled={pagina + 1 >= totalPaginas}
                                onClick={() => setPagina(totalPaginas - 1)}
                            >
                                Última
                            </button>

                        </div>
                    )}
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