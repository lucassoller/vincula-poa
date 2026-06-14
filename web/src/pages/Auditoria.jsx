import { useEffect, useState } from "react";
import api from "../api/api";
import "./auditoria.css";
import Pagination from "../components/Paginations.jsx";
import {formatarDataHora, formatarEnum} from "../utils/utils.js";
import {useAuth} from "../context/AuthContext.jsx";

function Auditoria() {

    const [logs, setLogs] = useState([]);
    const [servidores, setServidores] = useState([]);
    const [servidorId, setServidorId] = useState("");
    const [inicio, setInicio] = useState("");
    const [fim, setFim] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const tamanhoPagina = 10;
    const { servidor } = useAuth();

    async function carregarAuditoria(
        paginaAtual = pagina,
        filtros = {
            servidorId,
            inicio,
            fim
        }
    ) {

        try {

            setCarregando(true);
            setMensagem("");

            const temServidor = filtros.servidorId !== "";
            const temPeriodo = filtros.inicio !== "" && filtros.fim !== "";

            let url = `/auditoria?page=${paginaAtual}&size=${tamanhoPagina}`;

            if (temServidor && temPeriodo) {

                url =
                    `/auditoria/servidor/${filtros.servidorId}/periodo` +
                    `?inicio=${filtros.inicio}T00:00:00` +
                    `&fim=${filtros.fim}T23:59:59` +
                    `&page=${paginaAtual}&size=${tamanhoPagina}`;

            } else if (temServidor) {

                url =
                    `/auditoria/servidor/${filtros.servidorId}` +
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
        async function carregarServidores() {
            try {
                const servidoresResponse = await api.get("/servidores/all");
                setServidores(servidoresResponse.data);
            } catch {
                setMensagem("Erro ao carregar servidores.");
            }
        }

        void carregarServidores();

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
            servidorId,
            inicio,
            fim
        });
    }

    async function limparFiltros() {
        setServidorId("");
        setInicio("");
        setFim("");

        if (pagina !== 0) {
            setPagina(0);
            return;
        }

        await carregarAuditoria(0, {
            servidorId: "",
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
                <div className="cadastro-header">
                    <div>
                        <h1>Auditoria</h1>
                        <p>
                            Visualize registros de ações realizadas no sistema
                        </p>
                    </div>
                    <div className="perfil-badge">
                        {servidor?.perfil === 'GESTAO_MUNICIPAL' ? servidor.perfil : servidor.unidadeSaude}
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
                        value={servidorId}
                        onChange={(e) => setServidorId(e.target.value)}
                    >

                        <option value="">
                            Todos os servidores
                        </option>

                        {servidores.map((u) => (
                            <option
                                key={u.id}
                                value={u.id}
                            >
                                {u.nome + " - " + u.email}
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
                            <th>Servidor</th>
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
                                    {log.servidorNome || "-"}
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