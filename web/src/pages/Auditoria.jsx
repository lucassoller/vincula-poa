import { useEffect, useState } from "react";
import api from "../api/api";
import "./auditoria.css";

function Auditoria() {
    const [logs, setLogs] = useState([]);
    const [usuarios, setUsuarios] = useState([]);
    const [usuarioId, setUsuarioId] = useState("");
    const [inicio, setInicio] = useState("");
    const [fim, setFim] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        async function carregarInicial() {
            try {
                const [logsResponse, usuariosResponse] = await Promise.all([
                    api.get("/auditoria"),
                    api.get("/usuarios"),
                ]);

                setLogs(logsResponse.data);
                setUsuarios(usuariosResponse.data);
            } catch {
                setMensagem("Erro ao carregar auditoria.");
            } finally {
                setCarregando(false);
            }
        }

        void carregarInicial();
    }, []);

    async function aplicarFiltros() {
        try {
            setCarregando(true);
            setMensagem("");

            const temUsuario = usuarioId !== "";
            const temPeriodo = inicio !== "" && fim !== "";

            if ((inicio && !fim) || (!inicio && fim)) {
                setMensagem("Informe início e fim do período.");
                return;
            }

            let url = "/auditoria";

            if (temUsuario && temPeriodo) {
                url = `/auditoria/usuario/${usuarioId}/periodo?inicio=${inicio}T00:00:00&fim=${fim}T23:59:59`;
            } else if (temUsuario) {
                url = `/auditoria/usuario/${usuarioId}`;
            } else if (temPeriodo) {
                url = `/auditoria/periodo?inicio=${inicio}T00:00:00&fim=${fim}T23:59:59`;
            }

            const response = await api.get(url);
            setLogs(response.data);
        } catch {
            setMensagem("Erro ao filtrar auditoria.");
        } finally {
            setCarregando(false);
        }
    }

    function limparFiltros() {
        setUsuarioId("");
        setInicio("");
        setFim("");
        aplicarTodos();
    }

    async function aplicarTodos() {
        try {
            setCarregando(true);
            const response = await api.get("/auditoria");
            setLogs(response.data);
        } catch {
            setMensagem("Erro ao carregar auditoria.");
        } finally {
            setCarregando(false);
        }
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">Carregando auditoria...</div>
            </div>
        );
    }

    return (
        <div className="auditoria-container">
            <div className="auditoria-page">
                <div className="auditoria-header">
                    <div>
                        <h1>Auditoria</h1>
                        <p>Visualize registros de ações realizadas no sistema</p>
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button type="button" onClick={() => setMensagem("")}>✕</button>
                    </div>
                )}

                <div className="auditoria-filtros">
                    <select
                        className="input-field"
                        value={usuarioId}
                        onChange={(e) => setUsuarioId(e.target.value)}
                    >
                        <option value="">Todos os usuários</option>

                        {usuarios.map((u) => (
                            <option key={u.id} value={u.id}>
                                {u.nome}
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

                    <button className="apply-btn" onClick={aplicarFiltros}>
                        Aplicar filtros
                    </button>

                    <button className="danger-btn" onClick={limparFiltros}>
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
                                <td>{formatarDataHora(log.dataHora)}</td>
                                <td>{log.usuarioNome || "-"}</td>
                                <td>
                                        <span className="acao-badge">
                                            {formatarEnum(log.acao)}
                                        </span>
                                </td>
                                <td>{log.entidade}</td>
                                <td>{log.entidadeId ?? "-"}</td>
                                <td>{log.descricao}</td>
                                <td>{log.ip || "-"}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {logs.length === 0 && (
                        <div className="empty-state">
                            Nenhum registro de auditoria encontrado.
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

function formatarDataHora(data) {
    if (!data) return "-";

    const d = new Date(data);

    const dia = String(d.getDate()).padStart(2, "0");
    const mes = String(d.getMonth() + 1).padStart(2, "0");
    const ano = d.getFullYear();
    const hora = String(d.getHours()).padStart(2, "0");
    const minuto = String(d.getMinutes()).padStart(2, "0");

    return `${dia}/${mes}/${ano} ${hora}:${minuto}`;
}

function formatarEnum(valor) {
    if (!valor) return "-";

    return valor
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, (letra) => letra.toUpperCase());
}

export default Auditoria;