import { useEffect, useState } from "react";
import api from "../api/api";
import { useAuth } from "../context/AuthContext.jsx";
import "./demandas.css";

const prazoLabel = {
    "D1": "1 dia",
    "D2": "2 dias",
    "D3": "3 dias",
    "D7": "7 dias",
    "D15": "15 dias",
    "D20": "20 dias",
    "D30": "30 dias",
};

function formatarDataHora(data) {
    if (!data) {
        return "-";
    }

    const d = new Date(data);

    const dia = String(d.getDate()).padStart(2, "0");
    const mes = String(d.getMonth() + 1).padStart(2, "0");
    const ano = d.getFullYear();

    const hora = String(d.getHours()).padStart(2, "0");
    const minuto = String(d.getMinutes()).padStart(2, "0");

    return `${dia}/${mes}/${ano} ${hora}:${minuto}`;
}

function Demandas() {


    const { usuario } = useAuth();

    const [demandas, setDemandas] = useState([]);
    const [unidades, setUnidades] = useState([]);
    const [demandaSelecionada, setDemandaSelecionada] = useState(null);
    const [acao, setAcao] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [erros, setErros] = useState({});

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

    useEffect(() => {
        carregarDados();
    }, [usuario]);

    async function carregarDados() {
        try {
            let demandasResponse;

            if (usuario?.perfil === "GESTAO_MUNICIPAL") {
                demandasResponse = await api.get("/demandas");
            } else if (usuario?.perfil === "EXECUTOR_APS") {
                demandasResponse = await api.get(`/demandas/unidade/${usuario.unidadeSaudeId}`);
            } else {
                demandasResponse = { data: [] };
            }

            const unidadesResponse = await api.get("/unidades-saude");

            setDemandas(demandasResponse.data);
            setUnidades(unidadesResponse.data);
        } catch {
            setMensagem("Erro ao carregar demandas.");
        }
    }

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
            carregarDados();
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
            await api.put(`/demandas/${demandaSelecionada.id}/redirecionar`, payload);
            setMensagem("Demanda redirecionada com sucesso!");
            fecharModal();
            carregarDados();
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
            await api.put(`/demandas/${demandaSelecionada.id}/encerrar`, payload);
            setMensagem("Demanda encerrada com sucesso!");
            fecharModal();
            carregarDados();
        } catch (error) {
            tratarErro(error);
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
                        {demandas.map((d) => (
                            <tr key={d.id}>
                                <td>{d.pacienteNome || d.pacienteId}</td>
                                <td>{d.motivoBuscaAtiva}</td>
                                <td>{formatarDataHora(d.dataHoraCriacao)}</td>
                                <td>{formatarDataHora(d.dataHoraFinalizacao) || "-" }</td>
                                <td>
                                        <span className={`status-badge status-${d.status}`}>
                                            {d.status}
                                        </span>
                                </td>
                                <td>{prazoLabel[d.prazoDemanda] || "-"}</td>
                                <td>{d.unidadeResponsavelNome || d.unidadeResponsavelId}</td>
                                <td>
                                    <div className="acoes-container">
                                        {d.status !== "FINALIZADA" && (
                                            <>
                                                <button className="btn-visualizar" onClick={() => abrirAcao(d, "TENTATIVA")}>
                                                    Tentativa
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

                    {demandas.length === 0 && (
                        <div className="empty-state">Nenhuma demanda encontrada.</div>
                    )}
                </div>
            </div>

            {demandaSelecionada && (
                <div className="modal-overlay">
                    <div className="modal-card">
                        <div className="modal-header">
                            <div>
                                <h2>{tituloModal(acao)}</h2>
                                <p>Demanda #{demandaSelecionada.id}</p>
                            </div>

                            <button className="modal-close" onClick={fecharModal}>✕</button>
                        </div>

                        {acao === "TENTATIVA" && (
                            <form onSubmit={salvarTentativa}>
                                <div className="form-group">
                                    <label>Tipo de tentativa <span>*</span></label>
                                    <select
                                        className="input-field"
                                        value={tentativa.tipo}
                                        onChange={(e) => setTentativa({ ...tentativa, tipo: e.target.value })}
                                    >
                                        <option value="">Selecione</option>
                                        <option value="TELEFONE">Telefone</option>
                                        <option value="WHATSAPP">WhatsApp</option>
                                        <option value="EMAIL">Email</option>
                                        <option value="VISITA_DOMICILIAR">Visita domiciliar</option>
                                        <option value="OUTRO">Outro</option>
                                    </select>
                                    {erros.tipo && <small>{erros.tipo}</small>}
                                </div>

                                <div className="form-group">
                                    <label>Descrição</label>
                                    <textarea
                                        className="input-field textarea-field"
                                        value={tentativa.descricao}
                                        onChange={(e) => setTentativa({ ...tentativa, descricao: e.target.value })}
                                    />
                                    {erros.descricao && <small>{erros.descricao}</small>}
                                </div>

                                <AcoesModal texto="Registrar tentativa" fechar={fecharModal} />
                            </form>
                        )}

                        {acao === "REDIRECIONAR" && (
                            <form onSubmit={salvarRedirecionamento}>
                                <div className="form-group">
                                    <label>Nova unidade responsável <span>*</span></label>
                                    <select
                                        className="input-field"
                                        value={redirecionamento.novaUnidadeResponsavelId}
                                        onChange={(e) =>
                                            setRedirecionamento({
                                                ...redirecionamento,
                                                novaUnidadeResponsavelId: e.target.value,
                                            })
                                        }
                                    >
                                        <option value="">Selecione</option>
                                        {unidades.map((u) => (
                                            <option key={u.id} value={u.id}>
                                                {u.nome}
                                            </option>
                                        ))}
                                    </select>
                                    {erros.novaUnidadeResponsavelId && <small>{erros.novaUnidadeResponsavelId}</small>}
                                </div>

                                <div className="form-group">
                                    <label>Motivo do redirecionamento</label>
                                    <textarea
                                        className="input-field textarea-field"
                                        value={redirecionamento.motivoRedirecionamento}
                                        onChange={(e) =>
                                            setRedirecionamento({
                                                ...redirecionamento,
                                                motivoRedirecionamento: e.target.value,
                                            })
                                        }
                                    />
                                </div>

                                <AcoesModal texto="Redirecionar demanda" fechar={fecharModal} />
                            </form>
                        )}

                        {acao === "ENCERRAR" && (
                            <form onSubmit={salvarEncerramento}>
                                <div className="form-group">
                                    <label>Desfecho <span>*</span></label>
                                    <select
                                        className="input-field"
                                        value={encerramento.desfechoDemanda}
                                        onChange={(e) =>
                                            setEncerramento({
                                                ...encerramento,
                                                desfechoDemanda: e.target.value,
                                            })
                                        }
                                    >
                                        <option value="">Selecione</option>
                                        <option value="ENCONTRADO_VINCULADO">Encontrado e vinculado</option>
                                        <option value="ENCONTRADO_RECUSOU">Encontrado e recusou</option>
                                        <option value="NAO_LOCALIZADO">Não localizado</option>
                                        <option value="ENDERECO_INCORRETO">Endereço incorreto</option>
                                        <option value="MUDOU_TERRITORIO">Mudou de território</option>
                                        <option value="OBITO">Óbito</option>
                                        <option value="OUTRO">Outro</option>
                                    </select>
                                    {erros.desfechoDemanda && <small>{erros.desfechoDemanda}</small>}
                                </div>

                                <div className="form-group">
                                    <label>Descrição do desfecho</label>
                                    <textarea
                                        className="input-field textarea-field"
                                        value={encerramento.descricaoDesfecho}
                                        onChange={(e) =>
                                            setEncerramento({
                                                ...encerramento,
                                                descricaoDesfecho: e.target.value,
                                            })
                                        }
                                    />
                                </div>

                                <AcoesModal texto="Encerrar demanda" fechar={fecharModal} />
                            </form>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

function tituloModal(acao) {
    if (acao === "TENTATIVA") return "Registrar tentativa de contato";
    if (acao === "REDIRECIONAR") return "Redirecionar demanda";
    if (acao === "ENCERRAR") return "Encerrar demanda";
    return "";
}

function AcoesModal({ texto, fechar }) {
    return (
        <div className="modal-actions">
            <button type="submit" className="primary-btn">{texto}</button>
            <button type="button" className="secondary-btn" onClick={fechar}>Cancelar</button>
        </div>
    );
}

export default Demandas;