import { useEffect, useState } from "react";
import "./gestaoListagem.css";
import api from "../api/api.js";
import {formatarDataHora} from "../utils/utils.js";
import Pagination from "../components/Paginations.jsx";

function GestaoListagem() {

    const [tipo, setTipo] = useState("");

    const [filtroUsuario, setFiltroUsuario] = useState("");
    const [filtroUbs, setFiltroUbs] = useState("");
    const [filtroPaciente, setFiltroPaciente] = useState("");
    const [filtroPerfil, setFiltroPerfil] = useState("");
    const [filtroUbsPaciente, setFiltroUbsPaciente] = useState("");
    const [filtroStatus, setFiltroStatus] = useState("");

    const [usuariosSelect, setUsuariosSelect] = useState([]);
    const [unidadesSelect, setUnidadesSelect] = useState([]);
    const [pacientesSelect, setPacientesSelect] = useState([]);

    const [resultado, setResultado] = useState([]);

    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);

    const [carregando, setCarregando] = useState(false);

    const tamanhoPagina = 10;

    useEffect(() => {

        async function carregarDados() {

            try {

                const [
                    usuariosSelectRes,
                    unidadesSelectRes,
                    pacientesSelectRes
                ] = await Promise.all([
                    api.get("/usuarios/all"),
                    api.get("/unidades-saude/all"),
                    api.get("/pacientes/all")
                ]);

                setUsuariosSelect(usuariosSelectRes.data);
                setUnidadesSelect(unidadesSelectRes.data);
                setPacientesSelect(pacientesSelectRes.data);

            } catch {

                console.error("Erro ao carregar dados");
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


            if (tipo === "PACIENTES") {

                if (filtroPaciente) {

                    response = await api.get(
                        `/pacientes/${filtroPaciente}`
                    );

                    setResultado([response.data]);
                    setPagina(0);
                    setTotalPaginas(1);

                    return;
                }

                if (filtroUbsPaciente) {

                    response = await api.get(
                        `/pacientes/unidadeSaude/${filtroUbsPaciente}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );

                } else {

                    response = await api.get(
                        `/pacientes?page=${paginaAtual}&size=${tamanhoPagina}`
                    );
                }

                setResultado(response.data.content);
                setTotalPaginas(response.data.page.totalPages);
                setPagina(paginaAtual);
            }

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

                if (filtroPerfil) {

                    response = await api.get(
                        `/usuarios/perfil/${filtroPerfil}?page=${paginaAtual}&size=${tamanhoPagina}`
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

                if (filtroPaciente) {

                    response = await api.get(
                        `/demandas/paciente/${filtroPaciente}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );

                } else if (filtroUbsPaciente) {

                    response = await api.get(
                        `/demandas/unidade/${filtroUbsPaciente}?page=${paginaAtual}&size=${tamanhoPagina}`
                    );

                } else if (filtroUsuario) {

                    response = await api.get(
                        `/demandas/usuario/${filtroUsuario}?page=${paginaAtual}&size=${tamanhoPagina}`
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

        } catch (error) {

            console.error("Erro ao listar:", error);

        } finally {

            setCarregando(false);
        }
    }

    function limparFiltros() {

        setTipo("");

        setFiltroUsuario("");
        setFiltroUbs("");
        setFiltroPaciente("");
        setFiltroPerfil("");
        setFiltroUbsPaciente("");
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

                        <p>
                            Consulte demandas, pacientes, unidades de saúde e usuários
                        </p>
                    </div>
                </div>

                <div className="gestao-card">
                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Tipo de listagem</label>

                            <select
                                className="input-field"
                                value={tipo}
                                onChange={(e) => {

                                    setTipo(e.target.value);

                                    setFiltroUsuario("");
                                    setFiltroUbs("");
                                    setFiltroPaciente("");
                                    setFiltroPerfil("");
                                    setFiltroUbsPaciente("");
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
                                <option value="PACIENTES">
                                    Pacientes
                                </option>
                                <option value="UBS">
                                    Unidades Básicas de Saúde
                                </option>
                                <option value="USUARIOS">
                                    Usuários
                                </option>

                            </select>
                        </div>
                    </div>

                    {tipo === "USUARIOS" && (
                        <div className="gestao-filter-box">

                            <h2>Filtros de usuários</h2>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Usuário</label>

                                    <select
                                        className="input-field"
                                        value={filtroUsuario}
                                        disabled={!!filtroPerfil}
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
                                                {usuario.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">

                                    <label>Perfil</label>

                                    <select
                                        className="input-field"
                                        value={filtroPerfil}
                                        disabled={!!filtroUsuario}
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

                                        <option value="EXECUTOR_APS">
                                            Executor APS
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
                                                {ubs.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    {tipo === "PACIENTES" && (
                        <div className="gestao-filter-box">

                            <h2>Filtros de pacientes</h2>

                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>Paciente</label>

                                    <select
                                        className="input-field"
                                        value={filtroPaciente}
                                        disabled={!!filtroUbsPaciente}
                                        onChange={(e) =>
                                            setFiltroPaciente(e.target.value)
                                        }
                                    >

                                        <option value="">
                                            Todos os pacientes
                                        </option>

                                        {pacientesSelect.map((paciente) => (
                                            <option
                                                key={paciente.id}
                                                value={paciente.id}
                                            >
                                                {paciente.nomeCompleto}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">

                                    <label>UBS vinculada</label>

                                    <select
                                        className="input-field"
                                        value={filtroUbsPaciente}
                                        disabled={!!filtroPaciente}
                                        onChange={(e) =>
                                            setFiltroUbsPaciente(e.target.value)
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
                                                {ubs.nome}
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
                                    <label>Paciente</label>

                                    <select
                                        className="input-field"
                                        value={filtroPaciente}
                                        disabled={!!filtroUbsPaciente || !!filtroUsuario || !!filtroStatus}
                                        onChange={(e) =>
                                            setFiltroPaciente(e.target.value)
                                        }
                                    >

                                        <option value="">
                                            Todos os pacientes
                                        </option>

                                        {pacientesSelect.map((paciente) => (
                                            <option
                                                key={paciente.id}
                                                value={paciente.id}
                                            >
                                                {paciente.nomeCompleto}
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
                                        value={filtroUbsPaciente}
                                        disabled={!!filtroPaciente || !!filtroUsuario || !!filtroStatus}
                                        onChange={(e) =>
                                            setFiltroUbsPaciente(e.target.value)
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
                                                {ubs.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                            <div className="form-grid two">
                                <div className="form-group">
                                    <label>
                                        Usuário responsável
                                    </label>

                                    <select
                                        className="input-field"
                                        value={filtroUsuario}
                                        disabled={!!filtroUbsPaciente || !!filtroPaciente || !!filtroStatus}
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
                                                {usuario.nome}
                                            </option>
                                        ))}
                                    </select>

                                </div>
                                <div className="form-group">

                                    <label>Status</label>
                                    <select
                                        className="input-field"
                                        value={filtroStatus}
                                        disabled={!!filtroUbsPaciente || !!filtroPaciente || !!filtroUsuario}
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
                                {tipo === "PACIENTES" && (
                                    <table className="pacientes-table">
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
                                                <td>{p.documento}</td>
                                                <td>{p.telefone || "-"}</td>
                                                <td>{p.unidadeSaudeNome || "-"}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                {tipo === "USUARIOS" && (
                                    <table className="pacientes-table">
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
                                    <table className="pacientes-table">
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
                                                <td>{u.telefone || "-"}</td>
                                                <td>{u.telefone2 || "-"}</td>
                                                <td>{u.endereco?.bairro || "-"}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                {tipo === "DEMANDAS" && (

                                    <table className="pacientes-table">

                                        <thead>
                                        <tr>
                                            <th>Paciente</th>
                                            <th>Criador</th>
                                            <th>UBS</th>
                                            <th>Status</th>
                                            <th>Data abertura</th>
                                        </tr>
                                        </thead>

                                        <tbody>

                                        {resultado.map((d) => (
                                            <tr key={d.id}>
                                                <td>{d.pacienteNome || "-"}</td>
                                                <td>{d.usuarioCriadorNome || "-"}</td>
                                                <td>{d.unidadeResponsavelNome || "-"}</td>
                                                <td>{d.status || "-"}</td>
                                                <td>{formatarDataHora(d.dataHoraCriacao)}</td>
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