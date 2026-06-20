import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.jsx";
import api from "../../api/api.js";
import "../../styles/usuarios.css";
import { useNavigate } from "react-router-dom";
import {mascaraDocumento, mascaraTelefone} from "../../utils/mascaras.js";
import ModalUbs from "../../components/ModalUbs.jsx";
import Pagination from "../../components/Paginations.jsx";
import ModalUsuario from "../../components/ModalUsuario.jsx";

function Usuarios() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [usuarioSelecionado, setUsuarioSelecionado] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);
    const [filtro, setFiltro] = useState("");
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [modoFiltrado, setModoFiltrado] = useState(false);
    const tamanhoPagina = 10;

    const carregarDados = useCallback(async (paginaAtual = pagina) => {
        try {
            setCarregando(true);
            let usuariosResponse;
            if(servidor?.perfil === 'SERVIDOR_APS'){
                usuariosResponse = await api.get(`/usuarios/unidadeSaude/${servidor.unidadeSaudeId}?page=${paginaAtual}&size=${tamanhoPagina}`);
            }else if(servidor?.perfil === 'SOLICITANTE'){
                usuariosResponse = await api.get(`/usuarios/unidadeSolicitante/${servidor.unidadeSaudeId}?page=${paginaAtual}&size=${tamanhoPagina}`);
            }else{
                usuariosResponse = await api.get(`/usuarios?page=${paginaAtual}&size=${tamanhoPagina}`);
            }

            setUsuarios(usuariosResponse.data.content);
            setTotalPaginas(usuariosResponse.data.page.totalPages);
        } catch {
            setMensagem("Erro ao carregar usuários.");
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
            let usuariosResponse;
            if(servidor?.perfil === 'SERVIDOR_APS'){
                usuariosResponse = await api.get(`/usuarios/filtrados/unidadeSaude/${servidor.unidadeSaudeId}/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`);
            }else if(servidor?.perfil === 'SOLICITANTE'){
                usuariosResponse = await api.get(`/usuarios/filtrados/unidadeSolicitante/${servidor.unidadeSaudeId}/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`);
            }else{
                usuariosResponse = await api.get(`/usuarios/filtrados/${filtro}?page=${paginaAtual}&size=${tamanhoPagina}`);
            }

            setUsuarios(usuariosResponse.data.content);
            setTotalPaginas(usuariosResponse.data.page.totalPages);
        } catch {
            setMensagem("Erro ao buscar usuários.");
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
        setMensagem("");
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
                    Carregando usuários...
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
                            Usuários
                        </h1>
                        <p className="usuarios-subtitle">
                            Visualize e gerencie os usuários cadastrados
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
                                placeholder="Buscar usuário..."
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
                            onClick={() => navigate("/usuarios/cadastro")}
                        >
                            + Novo usuário
                        </span>

                    </div>

                    <table className="usuarios-table">
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

                        {usuarios.map((usuario) => (

                            <tr key={usuario.id}>
                                <td>
                                    <div className="usuario-nome">
                                        {usuario.nomeCompleto}
                                    </div>
                                </td>
                                <td>
                                    {mascaraDocumento(usuario.documento)}
                                </td>
                                <td>
                                    {mascaraTelefone(usuario.telefone) || "-"}
                                </td>
                                <td>
                                    <span
                                        className="ubs-badge ubs-clickable"
                                        onClick={() => abrirCardUbs(usuario.unidadeSaudeId)}
                                    >
                                        {usuario.unidadeSaudeNome}
                                    </span>
                                </td>
                                <td>
                                    <div className="acoes-container">
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => setUsuarioSelecionado(usuario)}
                                        >
                                            Visualizar
                                        </span>
                                        <span
                                            className="btn-editar"
                                            onClick={() => navigate(`/usuarios/${usuario.id}/editar`)}
                                        >
                                            Editar
                                        </span>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {usuarios.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum usuário encontrado.
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

            {usuarioSelecionado && (
                <ModalUsuario
                    usuarioSelecionado={usuarioSelecionado}
                    setUsuarioSelecionado={setUsuarioSelecionado}
                />
            )}

            {carregandoUbs && (
                <div className="ubs-overlay">
                    <div className="loading-card">
                        <p>Carregando dados da UBS...</p>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Usuarios;