import { useEffect, useState } from "react";
import api from "../api/api";
import "./auditoria.css";
import Pagination from "../components/Paginations.jsx";
import {formatarDataHora, formatarEnum} from "../utils/utils.js";

function Auditoria() {

    const [logs, setLogs] = useState([]);
    const [usuarios, setUsuarios] = useState([]);
    const [usuarioId, setUsuarioId] = useState("");
    const [inicio, setInicio] = useState("");
    const [fim, setFim] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const tamanhoPagina = 10;

    async function carregarAuditoria(
        paginaAtual = pagina,
        filtros = {
            usuarioId,
            inicio,
            fim
        }
    ) {

        try {

            setCarregando(true);
            setMensagem("");

            const temUsuario = filtros.usuarioId !== "";
            const temPeriodo = filtros.inicio !== "" && filtros.fim !== "";

            let url = `/auditoria?page=${paginaAtual}&size=${tamanhoPagina}`;

            if (temUsuario && temPeriodo) {

                url =
                    `/auditoria/usuario/${filtros.usuarioId}/periodo` +
                    `?inicio=${filtros.inicio}T00:00:00` +
                    `&fim=${filtros.fim}T23:59:59` +
                    `&page=${paginaAtual}&size=${tamanhoPagina}`;

            } else if (temUsuario) {

                url =
                    `/auditoria/usuario/${filtros.usuarioId}` +
                    `?page=${paginaAtual}&size=${tamanhoPagina}`;

            } else if (temPeriodo) {

                url =
                    `/auditoria/periodo` +
                    `?inicio=${filtros.inicio}T00:00:00` +
                    `&fim=${filtros.fim}T23:59:59` +
                    `&page=${paginaAtual}&size=${tamanhoPagina}`;
            }

            const response = await api.get(url);
            setLogs(response.data.content);
            setTotalPaginas(response.data.page.totalPages);
        } catch {
            setMensagem("Erro ao carregar auditoria.");
        } finally {
            setCarregando(false);
        }
    }

    useEffect(() => {
        async function carregarUsuarios() {
            try {
                const usuariosResponse = await api.get("/usuarios/all");
                setUsuarios(usuariosResponse.data);
            } catch {
                setMensagem("Erro ao carregar usuários.");
            }
        }

        void carregarUsuarios();

    }, []);

    useEffect(() => {
        async function executar() {
            await carregarAuditoria();
        }
        void executar();
    }, [pagina]);

    async function aplicarFiltros() {
        if ((inicio && !fim) || (!inicio && fim)) {
            setMensagem("Informe início e fim do período.");
            return;
        }

        if (pagina !== 0) {
            setPagina(0);
            return;
        }

        await carregarAuditoria(0, {
            usuarioId,
            inicio,
            fim
        });
    }

    async function limparFiltros() {
        setUsuarioId("");
        setInicio("");
        setFim("");

        if (pagina !== 0) {
            setPagina(0);
            return;
        }

        await carregarAuditoria(0, {
            usuarioId: "",
            inicio: "",
            fim: ""
        });
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando auditoria...
                </div>
            </div>
        );
    }

    return (
        <div className="auditoria-container">
            <div className="auditoria-page">
                <div className="auditoria-header">
                    <div>
                        <h1>Auditoria</h1>
                        <p>
                            Visualize registros de ações realizadas no sistema
                        </p>
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

                <div className="auditoria-filtros">
                    <select
                        className="input-field"
                        value={usuarioId}
                        onChange={(e) => setUsuarioId(e.target.value)}
                    >

                        <option value="">
                            Todos os usuários
                        </option>

                        {usuarios.map((u) => (
                            <option
                                key={u.id}
                                value={u.id}
                            >
                                {u.id + " - " + u.nome}
                            </option>
                        ))}
                    </select>

                    <input
                        type="date"
                        className="input-field"
                        value={inicio}
                        onChange={(e) => setInicio(e.target.value)}
                    />
                    <input
                        type="date"
                        className="input-field"
                        value={fim}
                        onChange={(e) => setFim(e.target.value)}
                    />
                    <button
                        className="buscar-btn"
                        onClick={aplicarFiltros}
                    >
                        Aplicar filtros
                    </button>
                    <button
                        className="buscar-btn"
                        onClick={limparFiltros}
                    >
                        Limpar
                    </button>
                </div>

                <div className="table-card">
                    <table className="auditoria-table">
                        <thead>
                        <tr>
                            <th>Data/Hora</th>
                            <th>Usuário</th>
                            <th>Ação</th>
                            <th>Entidade</th>
                            <th>ID</th>
                            <th>Descrição</th>
                            <th>IP</th>
                        </tr>
                        </thead>
                        <tbody>

                        {logs.map((log) => (
                            <tr key={log.id}>
                                <td>
                                    {formatarDataHora(log.dataHora)}
                                </td>
                                <td>
                                    {log.usuarioNome || "-"}
                                </td>
                                <td>
                                    <span className="acao-badge">
                                        {formatarEnum(log.acao)}
                                    </span>
                                </td>
                                <td>
                                    {log.entidade}
                                </td>

                                <td>
                                    {log.entidadeId ?? "-"}
                                </td>

                                <td>
                                    {log.descricao}
                                </td>

                                <td>
                                    {log.ip || "-"}
                                </td>

                            </tr>
                        ))}

                        </tbody>

                    </table>

                    {logs.length === 0 && (
                        <div className="empty-state">
                            Nenhum registro de auditoria encontrado.
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

export default Auditoria;