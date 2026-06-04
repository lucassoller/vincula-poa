import { useEffect, useState } from "react";
import "./gestaoListagem.css";
import api from "../api/api.js";
import {formatarDataHora} from "../utils/utils.js";
import Pagination from "../components/Paginations.jsx";
import {mascaraDocumento, mascaraTelefone} from "../utils/mascaras.js";

function GestaoListagem() {

    const [tipo, setTipo] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [filtroServidor, setFiltroServidor] = useState("");
    const [filtroUbs, setFiltroUbs] = useState("");
    const [filtroUsuario, setFiltroUsuario] = useState("");
    const [filtroPerfil, setFiltroPerfil] = useState("");
    const [filtroUbsUsuario, setFiltroUbsUsuario] = useState("");
    const [filtroStatus, setFiltroStatus] = useState("");
    const [servidoresSelect, setServidoresSelect] = useState([]);
    const [unidadesSelect, setUnidadesSelect] = useState([]);
    const [usuariosSelect, setUsuariosSelect] = useState([]);
    const [resultado, setResultado] = useState([]);
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [carregando, setCarregando] = useState(false);

    const tamanhoPagina = 10;

    useEffect(() => {
        async function carregarDados() {
            try {
                const [
                    servidoresSelectRes,
                    unidadesSelectRes,
                    usuariosSelectRes,
                ] = await Promise.all([
                    api.get("/servidores/all"),
                    api.get("/unidades-saude/all"),
                    api.get("/usuarios/all"),
                ]);
                setServidoresSelect(servidoresSelectRes.data);
                setUnidadesSelect(unidadesSelectRes.data);
                setUsuariosSelect(usuariosSelectRes.data);
            } catch {
                setMensagem("Erro ao carregar dados");
            }
        }

        void carregarDados();

    }, []);

    useEffect(() => {
        if (tipo) {
            void listar(pagina);
        }
    }, [pagina]);

    async function listar(paginaAtual = 0) {
        try {
            setCarregando(true);
            let response;

            if (tipo === "USUARIOS") {
                if (filtroUsuario) {
                    response = await api.get(
                        `/usuarios/${filtroUsuario}`
                    );
                    setResultado([response.data]);
                    setPagina(0);
                    setTotalPaginas(1);
                    return;
                }

                if (filtroUbsUsuario) {
                    response = await api.get(
                        `/usuarios/unidadeSaude/${filtroUbsUsuario}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                } else {
                    response = await api.get(
                        `/usuarios?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                }
                setResultado(response.data.content);
                setTotalPaginas(response.data.page.totalPages);
                setPagina(paginaAtual);
            }

            if (tipo === "SERVIDORES") {
                if (filtroServidor) {
                    response = await api.get(
                        `/servidores/${filtroServidor}`
                    );
                    setResultado([response.data]);
                    setPagina(0);
                    setTotalPaginas(1);
                    return;
                }

                if (filtroPerfil) {
                    response = await api.get(
                        `/servidores/perfil/${filtroPerfil}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                } else {
                    response = await api.get(
                        `/servidores?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                }
                setResultado(response.data.content);
                setTotalPaginas(response.data.page.totalPages);
                setPagina(paginaAtual);
            }

            if (tipo === "UBS") {
                if (filtroUbs) {
                    response = await api.get(
                        `/unidades-saude/${filtroUbs}`
                    );
                    setResultado([response.data]);
                    setPagina(0);
                    setTotalPaginas(1);
                    return;
                }
                response = await api.get(
                    `/unidades-saude?page=${paginaAtual}&size=${tamanhoPagina}`
                );
                setResultado(response.data.content);
                setTotalPaginas(response.data.page.totalPages);
                setPagina(paginaAtual);
            }

            if (tipo === "DEMANDAS") {
                if (filtroUsuario) {
                    response = await api.get(
                        `/demandas/usuario/${filtroUsuario}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                } else if (filtroUbsUsuario) {
                    response = await api.get(
                        `/demandas/unidade/${filtroUbsUsuario}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                } else if (filtroServidor) {
                    response = await api.get(
                        `/demandas/servidor/${filtroServidor}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                } else if (filtroStatus) {
                    response = await api.get(
                        `/demandas/status/${filtroStatus}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                } else {
                    response = await api.get(
                        `/demandas?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                }
                setResultado(response.data.content);
                setTotalPaginas(response.data.page.totalPages);
                setPagina(paginaAtual);
            }

            if (tipo === "TENTATIVAS") {
                response = await api.get(
                    `/tentativas-contato?page=${paginaAtual}&size=${tamanhoPagina}`
                );
                setResultado(response.data.content);
                setTotalPaginas(response.data.page.totalPages);
                setPagina(paginaAtual);
            }

        } catch {
            setMensagem("Erro ao carregar dados.");

        } finally {
            setCarregando(false);
        }
    }

    function limparFiltros() {
        setTipo("");
        setFiltroServidor("");
        setFiltroUbs("");
        setFiltroUsuario("");
        setFiltroPerfil("");
        setFiltroUbsUsuario("");
        setFiltroStatus("");
        setResultado([]);
        setPagina(0);
        setTotalPaginas(0);
    }

    return (
        <div className="gestao-container">
            <div className="gestao-page">
                <div className="gestao-header">
                    <div>
                        <h1>Central de Gestão</h1>
                        <p>Consulte demandas, usuários, unidades de saúde e servidores</p>
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button
                            type="button"
                            onClick={() => setMensagem("")}
                        >
                            ✕
                        </button>
                    </div>
                )}

                <div className="gestao-card">
                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Tipo de listagem</label>
                            <select
                                className="input-field"
                                value={tipo}
                                onChange={(e) => {
                                    setTipo(e.target.value);
                                    setFiltroServidor("");
                                    setFiltroUbs("");
                                    setFiltroUsuario("");
                                    setFiltroPerfil("");
                                    setFiltroUbsUsuario("");
                                    setFiltroStatus("");
                                    setResultado([]);
                                    setPagina(0);
                                    setTotalPaginas(0);
                                }}
                            >
                                <option value="">
                                    Selecione
                                </option>
                                <option value="DEMANDAS">
                                    Demandas
                                </option>
                                <option value="USUARIOS">
                                    Usuarios
                                </option>
                                <option value="TENTATIVAS">
                                    Tentativas de contato
                                </option>
                                <option value="UBS">
                                    Unidades Básicas de Saúde
                                </option>
                                <option value="SERVIDORES">
                                    Servidores
                                </option>
                            </select>
                        </div>
                    </div>

                    {tipo === "SERVIDORES" && (
                        <div className="gestao-filter-box">
                            <h2>Filtros de servidores</h2>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Servidor</label>
                                    <select
                                        className="input-field"
                                        value={filtroServidor}
                                        disabled={!!filtroPerfil}
                                        onChange={(e) =>
                                            setFiltroServidor(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todos os servidores
                                        </option>

                                        {servidoresSelect.map((servidor) => (
                                            <option
                                                key={servidor.id}
                                                value={servidor.id}
                                            >
                                                {servidor.nome + " - " + servidor.email}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>Perfil</label>
                                    <select
                                        className="input-field"
                                        value={filtroPerfil}
                                        disabled={!!filtroServidor}
                                        onChange={(e) =>
                                            setFiltroPerfil(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todos os perfis
                                        </option>

                                        <option value="SOLICITANTE">
                                            Solicitante
                                        </option>

                                        <option value="SERVIDOR_APS">
                                            Servidor APS
                                        </option>

                                        <option value="GESTAO_MUNICIPAL">
                                            Gestão Municipal
                                        </option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    {tipo === "UBS" && (
                        <div className="gestao-filter-box">
                            <h2>Filtros de UBS</h2>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Unidade Básica de Saúde</label>
                                    <select
                                        className="input-field"
                                        value={filtroUbs}
                                        onChange={(e) =>
                                            setFiltroUbs(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todas as UBS
                                        </option>
                                        {unidadesSelect.map((ubs) => (
                                            <option
                                                key={ubs.id}
                                                value={ubs.id}
                                            >
                                                {ubs.nome + " - " + ubs.cnes}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    {tipo === "USUARIOS" && (
                        <div className="gestao-filter-box">
                            <h2>Filtros de usuários</h2>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Usuário</label>
                                    <select
                                        className="input-field"
                                        value={filtroUsuario}
                                        disabled={!!filtroUbsUsuario}
                                        onChange={(e) =>
                                            setFiltroUsuario(e.target.value)
                                        }
                                    >

                                        <option value="">
                                            Todos os usuários
                                        </option>
                                        {usuariosSelect.map((usuario) => (
                                            <option
                                                key={usuario.id}
                                                value={usuario.id}
                                            >
                                                {usuario.nomeCompleto + " - " + mascaraDocumento(usuario.documento)}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>UBS vinculada</label>
                                    <select
                                        className="input-field"
                                        value={filtroUbsUsuario}
                                        disabled={!!filtroUsuario}
                                        onChange={(e) =>
                                            setFiltroUbsUsuario(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todas as UBS
                                        </option>
                                        {unidadesSelect.map((ubs) => (
                                            <option
                                                key={ubs.id}
                                                value={ubs.id}
                                            >
                                                {ubs.nome + " - " + ubs.cnes}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    {tipo === "DEMANDAS" && (
                        <div className="gestao-filter-box">
                            <h2>Filtros de demandas</h2>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Usuário</label>
                                    <select
                                        className="input-field"
                                        value={filtroUsuario}
                                        disabled={!!filtroUbsUsuario || !!filtroServidor || !!filtroStatus}
                                        onChange={(e) =>
                                            setFiltroUsuario(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todos os usuarios
                                        </option>
                                        {usuariosSelect.map((usuario) => (
                                            <option
                                                key={usuario.id}
                                                value={usuario.id}
                                            >
                                                {usuario.nomeCompleto + " - " + mascaraDocumento(usuario.documento)}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Unidade de Saúde
                                    </label>
                                    <select
                                        className="input-field"
                                        value={filtroUbsUsuario}
                                        disabled={!!filtroUsuario || !!filtroServidor || !!filtroStatus}
                                        onChange={(e) =>
                                            setFiltroUbsUsuario(e.target.value)
                                        }
                                    >

                                        <option value="">
                                            Todas as UBS
                                        </option>
                                        {unidadesSelect.map((ubs) => (
                                            <option
                                                key={ubs.id}
                                                value={ubs.id}
                                            >
                                                {ubs.nome + " - " + ubs.cnes}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>
                                        Servidor responsável
                                    </label>
                                    <select
                                        className="input-field"
                                        value={filtroServidor}
                                        disabled={!!filtroUbsUsuario || !!filtroUsuario || !!filtroStatus}
                                        onChange={(e) =>
                                            setFiltroServidor(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todos os servidores
                                        </option>
                                        {servidoresSelect.map((servidor) => (
                                            <option
                                                key={servidor.id}
                                                value={servidor.id}
                                            >
                                                {servidor.nome + " - " + servidor.email}
                                            </option>
                                        ))}
                                    </select>

                                </div>
                                <div className="form-group">
                                    <label>Status</label>
                                    <select
                                        className="input-field"
                                        value={filtroStatus}
                                        disabled={!!filtroUbsUsuario || !!filtroUsuario || !!filtroServidor}
                                        onChange={(e) =>
                                            setFiltroStatus(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            Todos os status
                                        </option>
                                        <option value="ABERTA">
                                            Aberta
                                        </option>
                                        <option value="EM_ANDAMENTO">
                                            Em andamento
                                        </option>
                                        <option value="FINALIZADA">
                                            Finalizada
                                        </option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="gestao-actions">
                        <button
                            type="button"
                            className="buscar-btn"
                            onClick={() => listar(0)}
                        >
                            Listar
                        </button>

                        <button
                            type="button"
                            className="buscar-btn"
                            onClick={limparFiltros}
                        >
                            Limpar filtros
                        </button>

                    </div>

                    <div className="gestao-placeholder">
                        {carregando && (
                            <p>Carregando...</p>
                        )}

                        {!carregando && resultado.length === 0 && (
                            <p>Selecione uma listagem e clique em “Listar”.</p>
                        )}

                        {!carregando && resultado.length > 0 && (
                            <div className="table-card gestao-table-card">
                                {tipo === "USUARIOS" && (
                                    <table className="usuarios-table">
                                        <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>CPF/CNS</th>
                                            <th>Telefone</th>
                                            <th>UBS</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        {resultado.map((p) => (
                                            <tr key={p.id}>
                                                <td>{p.nomeCompleto}</td>
                                                <td>{mascaraDocumento(p.documento)}</td>
                                                <td>{mascaraTelefone(p.telefone) || "-"}</td>
                                                <td>{p.unidadeSaudeNome || "-"}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                {tipo === "SERVIDORES" && (
                                    <table className="usuarios-table">
                                        <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>Email</th>
                                            <th>Login</th>
                                            <th>Perfil</th>
                                            <th>UBS</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        {resultado.map((u) => (
                                            <tr key={u.id}>
                                                <td>{u.nome}</td>
                                                <td>{u.email}</td>
                                                <td>{u.login}</td>
                                                <td>{u.perfil}</td>
                                                <td>{u.unidadeSaudeNome || "-"}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                {tipo === "UBS" && (
                                    <table className="usuarios-table">
                                        <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>CNES</th>
                                            <th>Telefone</th>
                                            <th>Telefone adicional</th>
                                            <th>Bairro</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {resultado.map((u) => (
                                            <tr key={u.id}>
                                                <td>{u.nome}</td>
                                                <td>{u.cnes}</td>
                                                <td>{mascaraTelefone(u.telefone) || "-"}</td>
                                                <td>{mascaraTelefone(u.telefone2) || "-"}</td>
                                                <td>{u.endereco?.bairro || "-"}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                {tipo === "DEMANDAS" && (
                                    <table className="usuarios-table">
                                        <thead>
                                        <tr>
                                            <th>Usuário</th>
                                            <th>ID</th>
                                            <th>Criador</th>
                                            <th>UBS</th>
                                            <th>Status</th>
                                            <th>Data abertura</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {resultado.map((d) => (
                                            <tr key={d.id}>
                                                <td>{d.usuarioNome || "-"}</td>
                                                <td>{d.id}</td>
                                                <td>{d.servidorCriadorNome || "-"}</td>
                                                <td>{d.unidadeResponsavelNome || "-"}</td>
                                                <td>{d.status || "-"}</td>
                                                <td>{formatarDataHora(d.dataHoraCriacao)}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                {tipo === "TENTATIVAS" && (
                                    <table className="usuarios-table">
                                        <thead>
                                        <tr>
                                            <th>ID demanda</th>
                                            <th>Servidor responsável</th>
                                            <th>Tipo</th>
                                            <th>Descrição</th>
                                            <th>Data criação</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {resultado.map((u) => (
                                            <tr key={u.id}>
                                                <td>{u.demandaId}</td>
                                                <td>{u.servidorNome}</td>
                                                <td>{u.tipo}</td>
                                                <td>{u.descricao || "-"}</td>
                                                <td>{formatarDataHora(u.dataHora)}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                <Pagination
                                    pagina={pagina}
                                    totalPaginas={totalPaginas}
                                    onChangePagina={setPagina}
                                />
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default GestaoListagem;