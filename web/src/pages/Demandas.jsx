import { useEffect, useState, useCallback  } from "react";
import api from "../api/api";
import { useAuth } from "../context/AuthContext.jsx";
import "./demandas.css";
import {prazoLabel, formatarDataHora, statusLabel, motivoBuscaLabel} from "../utils/demandaUtils";
import ModalTentativaContato from "../components/ModalTentativaContato";
import ModalRedirecionarDemanda from "../components/ModalRedirecionarDemanda";
import ModalEncerrarDemanda from "../components/ModalEncerrarDemanda";
import ModalDetalhesDemanda from "../components/ModalDetalhesDemanda";
import {useNavigate} from "react-router-dom";

function Demandas() {
    const navigate = useNavigate();
    const { usuario } = useAuth();
    const [demandas, setDemandas] = useState([]);
    const [unidades, setUnidades] = useState([]);
    const [demandaSelecionada, setDemandaSelecionada] = useState(null);
    const [acao, setAcao] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [erros, setErros] = useState({});
    const [demandaDetalhada, setDemandaDetalhada] = useState(null);
    const [tentativasContato, setTentativasContato] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [filtro, setFiltro] = useState("");

    const [tentativa, setTentativa] = useState({
        tipo: "",
        descricao: "",
    });

    const [redirecionamento, setRedirecionamento] = useState({
        novaUnidadeResponsavelId: "",
        motivoRedirecionamento: "",
    });

    const [encerramento, setEncerramento] = useState({
        desfechoDemanda: "",
        descricaoDesfecho: "",
    });

    const carregarDados = useCallback(async () => {
        try {
            setCarregando(true);

            let demandasResponse;

            if (usuario?.perfil === "GESTAO_MUNICIPAL") {
                demandasResponse = await api.get("/demandas?page=0&size=10");
            } else if (usuario?.perfil === "EXECUTOR_APS") {
                demandasResponse = await api.get(
                    `/demandas/unidade/${usuario.unidadeSaudeId}?page=0&size=10`
                );
            } else {
                demandasResponse = { data: [] };
            }

            const unidadesResponse = await api.get("/unidades-saude/all");

            setDemandas(demandasResponse.data.content);
            setUnidades(unidadesResponse.data);

        } catch {
            setMensagem("Erro ao carregar demandas.");
        } finally {
            setCarregando(false);
        }
    }, [usuario]);

    useEffect(() => {
        const executar = async () => {
            await carregarDados();
        };

        void executar();
    }, [carregarDados]);

    function abrirAcao(demanda, tipoAcao) {
        setDemandaSelecionada(demanda);
        setAcao(tipoAcao);
        setMensagem("");
        setErros({});
    }

    function fecharModal() {
        setDemandaSelecionada(null);
        setAcao("");
        setErros({});
        setTentativa({ tipo: "", descricao: "" });
        setRedirecionamento({ novaUnidadeResponsavelId: "", motivoRedirecionamento: "" });
        setEncerramento({ desfechoDemanda: "", descricaoDesfecho: "" });
    }

    async function salvarTentativa(e) {
        e.preventDefault();

        const payload = {
            demandaId: demandaSelecionada.id,
            tipo: tentativa.tipo || null,
            descricao: tentativa.descricao,
        };

        try {
            await api.post("/tentativas-contato", payload);
            setMensagem("Tentativa registrada com sucesso!");
            fecharModal();
            await carregarDados();
        } catch (error) {
            tratarErro(error);
        }
    }

    async function salvarRedirecionamento(e) {
        e.preventDefault();

        const payload = {
            novaUnidadeResponsavelId: redirecionamento.novaUnidadeResponsavelId
                ? Number(redirecionamento.novaUnidadeResponsavelId)
                : null,
            motivoRedirecionamento: redirecionamento.motivoRedirecionamento,
        };

        try {
            await api.patch(`/demandas/${demandaSelecionada.id}/redirecionar`, payload);
            setMensagem("Demanda redirecionada com sucesso!");
            fecharModal();
            await carregarDados();
        } catch (error) {
            tratarErro(error);
        }
    }

    async function salvarEncerramento(e) {
        e.preventDefault();

        const payload = {
            desfechoDemanda: encerramento.desfechoDemanda || null,
            descricaoDesfecho: encerramento.descricaoDesfecho,
        };

        try {
            await api.patch(`/demandas/${demandaSelecionada.id}/encerrar`, payload);
            setMensagem("Demanda encerrada com sucesso!");
            fecharModal();
            await carregarDados();
        } catch (error) {
            tratarErro(error);
        }
    }

    async function abrirDetalhes(d) {
        try {
            const response = await api.get(`/demandas/${d.id}`);

            setDemandaDetalhada(response.data);

            const tentativasResponse = await api.get(
                `/tentativas-contato/demanda/${d.id}`
            );

            setTentativasContato(tentativasResponse.data);

        } catch {
            setMensagem("Erro ao carregar detalhes da demanda.");
        }
    }

    function tratarErro(error) {
        if (error.response?.data?.errors) {
            setErros(error.response.data.errors);
            setMensagem(error.response.data.message || "Dados inválidos.");
        } else {
            setMensagem(error.response?.data?.message || "Erro ao realizar ação.");
        }
    }

    const demandasFiltradas = demandas.filter((d) => {
        const busca = filtro.toLowerCase();

        return (
            d.pacienteNome?.toLowerCase().includes(busca) ||
            d.motivoBuscaAtiva?.toLowerCase().includes(busca) ||
            d.status?.toLowerCase().includes(busca) ||
            d.unidadeResponsavelNome?.toLowerCase().includes(busca) ||
            prazoLabel[d.prazoDemanda]?.toLowerCase().includes(busca)
        );
    });

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    Carregando demandas...
                </div>
            </div>
        );
    }

    return (
        <div className="demandas-container">
            <div className="demandas-page">
                <div className="demandas-header">
                    <div>
                        <h1>Demandas</h1>
                        <p>Gerencie buscas ativas, tentativas, redirecionamentos e encerramentos</p>
                    </div>

                    <span className="perfil-badge">{usuario?.perfil}</span>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button type="button" onClick={() => setMensagem("")}>✕</button>
                    </div>
                )}

                <div className="table-card">
                    <div className="table-topbar">
                        <input
                            className="paciente-search"
                            placeholder="Buscar demanda..."
                            value={filtro}
                            onChange={(e) => setFiltro(e.target.value)}
                        />

                        <button
                            className="novo-paciente-btn"
                            onClick={() => navigate("/demandas/cadastro")}
                        >
                            + Nova demanda
                        </button>
                    </div>
                    <table className="demandas-table">
                        <thead>
                        <tr>
                            <th>Paciente</th>
                            <th>Motivo</th>
                            <th>Data de abertura</th>
                            <th>Data de encerramento</th>
                            <th>Status</th>
                            <th>Prazo</th>
                            <th>Unidade</th>
                            <th>Ações</th>
                        </tr>
                        </thead>

                        <tbody>
                        {demandasFiltradas.map((d) => (
                            <tr key={d.id}>
                                <td><b>{d.pacienteNome || d.pacienteId}</b></td>
                                <td>{motivoBuscaLabel[d.motivoBuscaAtiva]}</td>
                                <td>{formatarDataHora(d.dataHoraCriacao)}</td>
                                <td>{formatarDataHora(d.dataHoraFinalizacao) || "-" }</td>
                                <td>
                                        <span className={`status-badge status-${d.status}`}>
                                            {statusLabel[d.status]}
                                        </span>
                                </td>
                                <td>{prazoLabel[d.prazoDemanda] || "-"}</td>
                                <td>{d.unidadeResponsavelNome || d.unidadeResponsavelId}</td>
                                <td>
                                    <div className="acoes-container">
                                        <button className="btn-visualizar" onClick={() => abrirDetalhes(d)}>
                                            Ver mais
                                        </button>
                                        {d.status !== "FINALIZADA" && (
                                            <>
                                                <button className="btn-tentativa" onClick={() => abrirAcao(d, "TENTATIVA")}>
                                                    Tentativa contato
                                                </button>

                                                <button className="btn-editar" onClick={() => abrirAcao(d, "REDIRECIONAR")}>
                                                    Redirecionar
                                                </button>

                                                <button className="btn-encerrar" onClick={() => abrirAcao(d, "ENCERRAR")}>
                                                    Encerrar
                                                </button>
                                            </>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {demandasFiltradas.length === 0 && (
                        <div className="empty-state">Nenhuma demanda encontrada.</div>
                    )}
                </div>
            </div>

            {demandaSelecionada && acao === "TENTATIVA" && (
                <ModalTentativaContato
                    demanda={demandaSelecionada}
                    tentativa={tentativa}
                    setTentativa={setTentativa}
                    erros={erros}
                    onSalvar={salvarTentativa}
                    onFechar={fecharModal}
                />
            )}

            {demandaSelecionada && acao === "REDIRECIONAR" && (
                <ModalRedirecionarDemanda
                    demanda={demandaSelecionada}
                    unidades={unidades}
                    redirecionamento={redirecionamento}
                    setRedirecionamento={setRedirecionamento}
                    erros={erros}
                    onSalvar={salvarRedirecionamento}
                    onFechar={fecharModal}
                />
            )}

            {demandaSelecionada && acao === "ENCERRAR" && (
                <ModalEncerrarDemanda
                    demanda={demandaSelecionada}
                    encerramento={encerramento}
                    setEncerramento={setEncerramento}
                    erros={erros}
                    onSalvar={salvarEncerramento}
                    onFechar={fecharModal}
                />
            )}

            {demandaDetalhada && (
                <ModalDetalhesDemanda
                    demanda={demandaDetalhada}
                    tentativasContato={tentativasContato}
                    onFechar={() => {
                        setDemandaDetalhada(null);
                        setTentativasContato([]);
                    }}
                />
            )}
        </div>
    );
}

export default Demandas;