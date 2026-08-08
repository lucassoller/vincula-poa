import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.jsx";
import api from "../../api/api.js";
import "../../styles/usuarios.css";
import { useNavigate } from "react-router-dom";
import ModalUbs from "../../components/Modal/ModalUbs.jsx";
import Pagination from "../../components/Paginations.jsx";
import ModalTransferirServidor from "../../components/Modal/ModalTransferirServidor.jsx";
import {perfilLabel} from "../../utils/utils.js";
import ModalFiltrosServidor from "../../components/Modal/ModalFiltrosServidor.jsx";

function Servidores() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [servidores, setServidores] = useState([]);
    const [servidoresBusca, setServidoresBusca] = useState([]);
    const [mensagem, setMensagem] = useState("");
    const [mensagemErro, setMensagemErro] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [ubsSelecionada, setUbsSelecionada] = useState(null);
    const [servidorSelecionado, setServidorSelecionado] = useState(null);
    const [carregandoUbs, setCarregandoUbs] = useState(false);
    const [pagina, setPagina] = useState(0);
    const [unidades, setUnidades] = useState([]);
    const [servicos, setServicos] = useState([]);
    const [especializados, setEspecializados] = useState([]);
    const [outros, setOutros] = useState([]);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [mostrarFiltros, setMostrarFiltros] = useState(false);
    const [erros, setErros] = useState({});
    const [transferencia, setTransferencia] = useState({
        perfil: "",
        servicoId: "",
        tipoServico: ""
    });

    const tamanhoPagina = 10;

    const [filtros, setFiltros] = useState({
        id: "",
        nome: "",
        perfil: [],
        servico: ""
    });

    async function carregarDados(paginaAtual = pagina, filtrosAtuais = filtros) {

        try {

            setCarregando(true);

            const payload = {
                id: filtrosAtuais.id || null,
                nome: filtrosAtuais.nome || null,
                perfil: filtrosAtuais.perfil || null,
                servicoId: filtrosAtuais.servico || null,
            };

            if(servidor.perfil === 'SOLICITANTE' || servidor.perfil === 'SOLICITANTE'){
                payload.servicoId = servidor.servicoId;
            }

            const servidoresResponse = await api.post(
                `/servidores/filtrados?page=${paginaAtual}&size=${tamanhoPagina}`,
                payload
            );

            setServidores(servidoresResponse.data.content);
            setTotalPaginas(servidoresResponse.data.page.totalPages);

        } catch {

            setMensagem("Erro ao carregar usuários.")

        } finally {

            setCarregando(false);

        }
    }

    const buscarServidoresAutocomplete = useCallback(async (nome) => {

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
            nome: "",
            perfil: [],
            servico: ""
        };

        setFiltros(filtrosVazios);
        setPagina(0);
        await carregarDados(0, filtrosVazios);
    }

    useEffect(() => {
        async function carregarServicos() {
            try {
                const servicosResponse = await api.get("/servicos/all");
                setServicos(servicosResponse.data.todos)
                setUnidades(servicosResponse.data.ubs);
                setOutros(servicosResponse.data.outros);
                setEspecializados(servicosResponse.data.especializados);

            } catch {
                setMensagemErro("Erro ao carregar os serviços.");
                setMensagemSucesso("");
            }
        }
        void carregarServicos();
    }, []);

    async function abrirCardUbs(servicoId) {
        if(servicoId === null){
            return
        }
        try {
            setCarregandoUbs(true);
            const response = await api.get(`/servicos/${servicoId}`);
            setUbsSelecionada(response.data);
        } catch {
            setMensagem("Erro ao carregar dados do serviço.");
        } finally {
            setCarregandoUbs(false);
        }
    }

    function abrirCardTransferencia(servidor) {
        if(servidor === null){
            return
        }
        setServidorSelecionado(servidor);

        setTransferencia({
            perfil: servidor.perfil,
            servicoId: String(servidor.servicoId ?? ""),
            tipoServico: servidor.tipoServico
        });
    }

    async function salvarTransferencia(e) {

        e.preventDefault();

        const payload = {
            perfil: transferencia.perfil || null,
            servicoId: transferencia.servicoId
                ? Number(transferencia.servicoId)
                : null,
        };
        try {
            await api.put(
                `/servidores/transferir/${servidorSelecionado.id}`,
                payload
            );
            setMensagemSucesso("Servidor transferido com sucesso!");
            setMensagemErro("")
            fecharModal();
            setPagina(0);
            await carregarDados(0, filtros);
        } catch (error) {
            tratarErro(error);
        }
    }

    function tratarErro(error) {
        setMensagemSucesso("");
        if (error.response?.data?.errors) {
            setErros(error.response.data.errors);
            setMensagem(error.response.data.message || "Dados inválidos.");
        } else {
            setMensagem(error.response?.data?.message || "Erro ao realizar ação.");
        }
    }

    function fecharModal() {
        setServidorSelecionado(null)
        setErros({});
        setMensagem("");
        setTransferencia({
            perfil: "",
            servicoId: "",
            tipoServico: ""
        });
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando servidores...
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
                            Servidores
                        </h1>
                        <p className="usuarios-subtitle">
                            Visualize e gerencie os servidores cadastrados
                        </p>
                    </div>
                    <div className="perfil-badge">
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) ? perfilLabel[servidor.perfil] : servidor.servico}
                    </div>
                </div>
                {mensagemErro && (
                    <div className="alert-card">
                        <span>{mensagemErro}</span>
                        <span onClick={() => setMensagemErro("")}>✕</span>
                    </div>
                )}

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <span onClick={() => setMensagemSucesso("")}>✕</span>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <div className="search-container">
                            <span
                                className="buscar-btn"
                                onClick={() => setMostrarFiltros(true)}
                            >
                                Filtrar servidores
                            </span>
                            <span
                                className="buscar-btn"
                                onClick={limparFiltros}
                            >
                                Limpar filtro
                            </span>

                        </div>
                        <span
                            className="buscar-btn"
                            onClick={() => navigate("/servidores/cadastro")}
                        >
                            + Novo servidor
                        </span>

                    </div>

                    <table className="usuarios-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Perfil</th>
                            <th>Serviço vinculado</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                            {servidores.map((s) => (

                                <tr key={s.id}>
                                    <td>
                                        <div className="usuario-nome">
                                            {s.nome}
                                        </div>
                                    </td>
                                    <td>{s.email}</td>
                                    <td>{perfilLabel[s.perfil]}</td>
                                    <td>
                                        <span
                                            className="ubs-badge ubs-clickable"
                                            onClick={() => abrirCardUbs(s.servicoId)}
                                        >
                                            {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(s.perfil)
                                                ? "Sem serviço vinculado"
                                                : s.servicoNome}
                                        </span>
                                    </td>
                                    <td>
                                        {(s.perfil === "SOLICITANTE" || s.perfil === "SERVIDOR_APS") && (
                                            <span
                                                className="btn-visualizar"
                                                onClick={() => abrirCardTransferencia(s)}
                                            >
                                                Transferir
                                            </span>
                                        )}
                                    </td>
                                </tr>
                            ))}


                        </tbody>
                    </table>

                    {servidorSelecionado && (
                        <ModalTransferirServidor
                            servidor={servidorSelecionado}
                            unidades={unidades}
                            outros={outros}
                            especializados={especializados}
                            transferencia={transferencia}
                            setTransferencia={setTransferencia}
                            erros={erros}
                            onSalvar={salvarTransferencia}
                            onFechar={fecharModal}
                            mensagem={mensagem}
                            setMensagem={setMensagem}
                        />
                    )}

                    {servidores.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum servidor encontrado.
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
                    <div className="loading-card">
                        <p>Carregando dados do serviço...</p>
                    </div>
                </div>
            )}

            <ModalFiltrosServidor
                aberto={mostrarFiltros}
                onFechar={() => setMostrarFiltros(false)}
                filtros={filtros}
                setFiltros={setFiltros}
                servicos={servicos}
                onAplicar={() => {
                    setMostrarFiltros(false);
                    void executarBusca();
                }}
                servidor={servidor}
                onLimpar={limparFiltros}
                buscarServidores={buscarServidoresAutocomplete}
                setServidores={setServidoresBusca}
                servidores={servidoresBusca}
            />
        </div>
    );
}

export default Servidores;