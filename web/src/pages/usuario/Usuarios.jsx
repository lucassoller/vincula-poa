import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.jsx";
import api from "../../api/api.js";
import "../../styles/usuarios.css";
import { useNavigate } from "react-router-dom";
import {mascaraDocumento, mascaraTelefone} from "../../utils/mascaras.js";
import ModalUbs from "../../components/Modal/ModalUbs.jsx";
import Pagination from "../../components/Paginations.jsx";
import ModalUsuario from "../../components/Modal/ModalUsuario.jsx";
import {perfilLabel} from "../../utils/utils.js";
import ModalFiltrosUsuario from "../../components/Modal/ModalFiltrosUsuario.jsx";

function Usuarios() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [usuariosBusca, setUsuariosBusca] = useState([]);
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [usuarioSelecionado, setUsuarioSelecionado] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [mostrarFiltros, setMostrarFiltros] = useState(false);
    const [unidades, setUnidades] = useState([]);

    const [filtros, setFiltros] = useState({
        id: "",
        nomeCompleto: "",
        unidade: "",
        solicitante: "",
        faixaEtaria: []
    });

    const tamanhoPagina = 10;

    async function carregarDados(paginaAtual = pagina, filtrosAtuais = filtros) {

        try {

            setCarregando(true);

            const payload = {
                id: filtrosAtuais.id || null,
                nomeCompleto: filtrosAtuais.nomeCompleto || null,
                unidadeSaudeId: filtrosAtuais.unidade || null,
                unidadeSolicitanteId: filtrosAtuais.solicitante || null,
                faixaEtaria: filtrosAtuais.faixaEtaria || null
            };

            if(servidor.perfil === 'SOLICITANTE'){
                payload.unidadeSolicitanteId = servidor.unidadeSaudeId;
            }

            const usuariosResponse = await api.post(
                `/usuarios/filtrados?page=${paginaAtual}&size=${tamanhoPagina}`,
                payload
            );

            setUsuarios(usuariosResponse.data.content);
            setTotalPaginas(usuariosResponse.data.page.totalPages);

        } catch {

            setMensagem("Erro ao carregar usuários.")

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

            const response = await api.get(
                `/usuarios/filtrados/buscas?nomeCompleto=${nome}`
            );

            setUsuariosBusca(response.data);

        } catch {
            setMensagem("Erro ao buscar usuários");
        }

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
            id: "",
            nomeCompleto: "",
            unidade: "",
            solicitante: "",
            faixaEtaria: []
        };

        setFiltros(filtrosVazios);
        setPagina(0);
        await carregarDados(0, filtrosVazios);
    }

    useEffect(() => {
        async function carregarUnidades() {
            try {
                const response = await api.get("/unidades-saude/ubs");
                setUnidades(response.data);

            } catch {
                setMensagem("Erro ao carregar unidades.");
            }
        }

        void carregarUnidades();
    }, []);

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
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.unidadeSaude}
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
                            <span
                                className="buscar-btn"
                                onClick={() => setMostrarFiltros(true)}
                            >
                                Filtrar usuários
                            </span>

                            <span
                                className="buscar-btn"
                                onClick={() => limparFiltros()}
                            >
                                Limpar filtros
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
                                    <div className="acoes-container-usuario">
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => setUsuarioSelecionado(usuario)}
                                        >
                                            Ver mais
                                        </span>
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => navigate(`/usuarios/${usuario.id}/editar`)}
                                        >
                                            Editar
                                        </span>
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => navigate("/demandas", {
                                                state: {
                                                    usuarioId: usuario.id,
                                                    nomeCompleto: usuario.nomeCompleto
                                                }
                                            })}
                                        >
                                            Demandas
                                        </span>
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => navigate("/demandas/cadastro" ,{
                                                state: {
                                                    usuarioId: usuario.id,
                                                }
                                            })}
                                        >
                                            Nova demanda
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

            <ModalFiltrosUsuario
                aberto={mostrarFiltros}
                onFechar={() => setMostrarFiltros(false)}
                filtros={filtros}
                setFiltros={setFiltros}
                unidades={unidades}
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

export default Usuarios;