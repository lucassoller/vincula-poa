import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext.jsx";
import api from "../../api/api.js";
import "../../styles/usuarios.css";
import { useNavigate } from "react-router-dom";
import Pagination from "../../components/Paginations.jsx";
import { mascaraTelefone } from "../../utils/mascaras.js";
import ModalUbs from "../../components/Modal/ModalUbs.jsx";
import {perfilLabel, tipoServico} from "../../utils/utils.js";
import ModalFiltrosServico from "../../components/Modal/ModalFiltrosServico.jsx";

function UnidadesSaude() {
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [unidades, setUnidades] = useState([]);
    const [unidadeDetalhada, setUnidadeDetalhada] = useState(null);
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [filtro, setFiltro] = useState("");
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [mostrarFiltros, setMostrarFiltros] = useState(false);
    const [servicosBusca, setServicosBusca] = useState([]);

    const tamanhoPagina = 10;

    const [filtros, setFiltros] = useState({
        id: "",
        nome: "",
        tipoServico: []
    });

    async function carregarDados(paginaAtual = pagina, filtrosAtuais = filtros) {

        try {

            setCarregando(true);

            const payload = {
                id: filtrosAtuais.id || null,
                nome: filtrosAtuais.nome || null,
                tipoServico: filtrosAtuais.tipoServico || null,
            };

            const servicosReponse = await api.post(
                `/unidades-saude/filtrados?page=${paginaAtual}&size=${tamanhoPagina}`,
                payload
            );

            setUnidades(servicosReponse.data.content);
            setTotalPaginas(servicosReponse.data.page.totalPages);

        } catch {

            setMensagem("Erro ao carregar serviços.")

        } finally {

            setCarregando(false);

        }
    }

    const buscarServicosAutocomplete = useCallback(async (nome) => {

        if (!nome || nome.trim().length < 3) {
            setServicosBusca([]);
            return;
        }

        try {

            const response = await api.get(
                `/unidades-saude/filtrados/buscas?nome=${nome}`
            );

            setServicosBusca(response.data);

        } catch {
            setMensagem("Erro ao buscar serviços");
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
            tipoServico: []
        };

        setFiltros(filtrosVazios);
        setPagina(0);
        await carregarDados(0, filtrosVazios);
    }

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando serviços de saúde...
                </div>
            </div>
        );
    }

    return (
        <div className="usuarios-container">
            <div className="usuarios-page">
                <div className="usuarios-header">
                    <div>
                        <h1 className="usuarios-title">Serviços de saúde</h1>
                        <p className="usuarios-subtitle">
                            Visualize e gerencie os serviços cadastrados
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
                                Filtrar serviços
                            </span>

                            <span
                                className="buscar-btn"
                                onClick={limparFiltros}
                            >
                                Limpar filtro
                            </span>

                        </div>
                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) && (
                            <span
                                className="buscar-btn"
                                onClick={() => navigate("/unidades-saude/cadastro")}
                            >
                                + Novo serviço
                            </span>
                        )}
                    </div>
                    <table className="usuarios-table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>CNES</th>
                            <th>Tipo de serviço</th>
                            <th>Telefone</th>
                            <th>Telefone adicional</th>
                            <th>Bairro</th>
                            <th>Ações</th>
                        </tr>
                        </thead>

                        <tbody>

                        {unidades.map((ubs) => (

                            <tr key={ubs.id}>
                                <td>
                                    <div className="usuario-nome">
                                        {ubs.nome}
                                    </div>
                                </td>
                                <td>{ubs.cnes}</td>
                                <td>
                                    <span className="ubs-badge">
                                        {tipoServico[ubs.tipoServico]}
                                    </span>
                                </td>

                                <td>{mascaraTelefone(ubs.telefone) || "-"}</td>
                                <td>{mascaraTelefone(ubs.telefone2) || "-"}</td>

                                <td className="bairro-coluna">{ubs.endereco?.bairro || "-"}</td>
                                <td>
                                    <div className="acoes-container">
                                        <span
                                            className="btn-visualizar"
                                            onClick={() => setUnidadeDetalhada(ubs)}
                                        >
                                            Ver mais
                                        </span>
                                        {['GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA'].includes(servidor?.perfil) && (
                                            <span
                                                className="btn-editar"
                                                onClick={() =>
                                                    navigate(`/unidades-saude/${ubs.id}/editar`)
                                                }
                                            >
                                                Editar
                                            </span>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}

                        </tbody>
                    </table>

                    {unidades.length === 0 && !mensagem && (
                        <div className="empty-state">
                            Nenhum serviço encontrado.
                        </div>
                    )}

                    <Pagination
                        pagina={pagina}
                        totalPaginas={totalPaginas}
                        onChangePagina={setPagina}
                    />
                </div>
            </div>
            {unidadeDetalhada && (
                <ModalUbs
                    ubsSelecionada={unidadeDetalhada}
                    setUbsSelecionada={setUnidadeDetalhada}
                />
            )}

            <ModalFiltrosServico
                aberto={mostrarFiltros}
                onFechar={() => setMostrarFiltros(false)}
                filtros={filtros}
                setFiltros={setFiltros}
                onAplicar={() => {
                    setMostrarFiltros(false);
                    void executarBusca();
                }}
                servidor={servidor}
                onLimpar={limparFiltros}
                buscarServicos={buscarServicosAutocomplete}
                setServicos={setServicosBusca}
                servicos={servicosBusca}
            />

        </div>
    );
}

export default UnidadesSaude;