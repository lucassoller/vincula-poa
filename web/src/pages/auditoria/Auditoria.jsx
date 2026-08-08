import {useCallback, useEffect, useState} from "react";
import api from "../../api/api.js";
import "../../styles/auditoria.css";
import Pagination from "../../components/Paginations.jsx";
import {formatarDataHora, formatarEnum, perfilLabel} from "../../utils/utils.js";
import {useAuth} from "../../context/AuthContext.jsx";
import ModalFiltrosAuditoria from "../../components/Modal/ModalFiltrosAuditoria.jsx";

function Auditoria() {

    const [logs, setLogs] = useState([]);
    const [servidoresBusca, setServidoresBusca] = useState([]);
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [mostrarFiltros, setMostrarFiltros] = useState(false);
    const [servicos, setServicos] = useState([]);

    const tamanhoPagina = 10;
    const { servidor } = useAuth();

    const [filtros, setFiltros] = useState({
        id: "",
        nome: "",
        perfil: [],
        dataInicial: "",
        dataFinal: "",
        servico: "",
    });

    async function carregarDados(paginaAtual = pagina, filtrosAtuais = filtros) {

        try {

            setCarregando(true);

            const payload = {
                id: filtrosAtuais.id || null,
                nome: filtrosAtuais.nome || null,
                perfil: filtrosAtuais.perfil,
                dataInicial: filtrosAtuais.dataInicial,
                dataFinal: filtrosAtuais.dataFinal,
                servicoId: filtrosAtuais.servico || null,
            };

            const auditoriaReponse = await api.post(
                `/auditoria/filtrados?page=${paginaAtual}&size=${tamanhoPagina}`,
                payload
            );

            setLogs(auditoriaReponse.data.content);
            setTotalPaginas(auditoriaReponse.data.page.totalPages);

        } catch {

            setMensagem("Erro ao carregar dados da auditoria.")

        } finally {

            setCarregando(false);

        }
    }

    const buscaServidoresAutocomplete = useCallback(async (nome) => {

        if (!nome || nome.trim().length < 3) {
            setServidoresBusca([]);
            return;
        }

        try {

            const response = await api.get(
                `/servidores/filtrados/buscas?nome=${nome}`
            );

            setServidoresBusca(response.data);

        } catch {
            setMensagem("Erro ao buscar servidores");
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
            nome: "",
            perfil: [],
            dataInicial: "",
            dataFinal: "",
            servico: "",
        };

        setFiltros(filtrosVazios);
        setPagina(0);
        await carregarDados(0, filtrosVazios);
    }

    useEffect(() => {
        async function carregarServicos() {
            try {
                const response = await api.get("/servicos/ubs");
                setServicos(response.data);

            } catch {
                setMensagem("Erro ao carregar serviços.");
            }
        }

        void carregarServicos();
    }, []);

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
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.servico}
                    </div>

                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span
                            onClick={() => setMensagem("")}
                        >
                            ✕
                        </span>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">
                            <span
                                className="buscar-btn"
                                onClick={() => setMostrarFiltros(true)}
                            >
                                Filtrar logs
                            </span>
                                <span
                                    className="buscar-btn"
                                    onClick={() => limparFiltros()}
                                >
                                Limpar filtros
                            </span>
                        </div>
                    </div>
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

            <ModalFiltrosAuditoria
                aberto={mostrarFiltros}
                onFechar={() => setMostrarFiltros(false)}
                filtros={filtros}
                setFiltros={setFiltros}
                servicos={servicos}
                onAplicar={() => {
                    setMostrarFiltros(false);
                    void executarBusca();
                }}
                onLimpar={limparFiltros}
                buscarServidores={buscaServidoresAutocomplete}
                setServidores={setServidoresBusca}
                servidores={servidoresBusca}
            />

        </div>
    );
}

export default Auditoria;