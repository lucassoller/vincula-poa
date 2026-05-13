import {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import "./gestaoListagem.css";
import api from "../api/api.js";

function GestaoListagem() {
    const navigate = useNavigate();

    const [tipo, setTipo] = useState("");
    const [filtroUsuario, setFiltroUsuario] = useState("");
    const [filtroUbs, setFiltroUbs] = useState("");
    const [filtroPaciente, setFiltroPaciente] = useState("");
    const [filtroGeral, setFiltroGeral] = useState("");
    const [filtroPerfil, setFiltroPerfil] = useState("");
    const [usuarios, setUsuarios] = useState([]);
    const [unidades, setUnidades] = useState([]);
    const [pacientes, setPacientes] = useState([]);
    const [resultado, setResultado] = useState([]);
    const [filtroUbsPaciente, setFiltroUbsPaciente] = useState("");

    useEffect(() => {
        async function carregarDados() {
            try {
                const [usuariosRes, unidadesRes, pacientesRes] = await Promise.all([
                    api.get("/usuarios"),
                    api.get("/unidades-saude"),
                    api.get("/pacientes")
                ]);

                setUsuarios(usuariosRes.data);
                setUnidades(unidadesRes.data);
                setPacientes(pacientesRes.data);
            } catch {
                console.error("Erro ao carregar dados da gestão");
            }
        }

        void carregarDados();
    }, []);

    function normalizar(valor) {
        return String(valor || "")
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    function listar() {
        let dados = [];

        if (tipo === "USUARIOS") {
            dados = [...usuarios];

            if (filtroGeral.trim()) {
                const busca = normalizar(filtroGeral);

                dados = dados.filter((u) =>
                    normalizar(u.nome).includes(busca) ||
                    normalizar(u.email).includes(busca) ||
                    normalizar(u.login).includes(busca) ||
                    normalizar(u.perfil).includes(busca) ||
                    normalizar(u.unidadeSaudeNome).includes(busca)
                );
            }

            if (filtroUsuario) {
                dados = dados.filter((u) => String(u.id) === String(filtroUsuario));
            }

            if (filtroPerfil) {
                dados = dados.filter((u) => u.perfil === filtroPerfil);
            }
        }

        if (tipo === "UBS") {
            dados = [...unidades];

            if (filtroGeral.trim()) {
                const busca = normalizar(filtroGeral);

                dados = dados.filter((u) =>
                    normalizar(u.nome).includes(busca) ||
                    normalizar(u.cnes).includes(busca) ||
                    normalizar(u.telefone).includes(busca) ||
                    normalizar(u.email).includes(busca) ||
                    normalizar(u.endereco?.bairro).includes(busca)
                );
            }

            if (filtroUbs) {
                dados = dados.filter((u) => String(u.id) === String(filtroUbs));
            }
        }

        if (tipo === "PACIENTES") {
            dados = [...pacientes];

            if (filtroGeral.trim()) {
                const busca = normalizar(filtroGeral);

                dados = dados.filter((p) =>
                    normalizar(p.nomeCompleto).includes(busca) ||
                    normalizar(p.documento).includes(busca) ||
                    normalizar(p.telefone).includes(busca) ||
                    normalizar(p.email).includes(busca) ||
                    normalizar(p.unidadeSaudeNome).includes(busca)
                );
            }

            if (filtroPaciente) {
                dados = dados.filter((p) => String(p.id) === String(filtroPaciente));
            }

            if (filtroUbsPaciente) {
                dados = dados.filter((p) =>
                    String(p.unidadeSaudeId) === String(filtroUbsPaciente)
                );
            }
        }

        setResultado(dados);
    }

    return (
        <div className="gestao-container">
            <div className="gestao-page">

                <div className="gestao-header">
                    <div>
                        <h1>Central de Gestão</h1>
                        <p>Consulte usuários, unidades de saúde e pacientes do sistema</p>
                    </div>

                    <button
                        type="button"
                        className="secondary-btn"
                        onClick={() => navigate("/dashboard")}
                    >
                        Voltar
                    </button>
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
                                    setFiltroGeral("");
                                    setFiltroPerfil("");
                                    setFiltroUbsPaciente("");
                                    setResultado([]);
                                }}
                            >
                                <option value="">Selecione</option>
                                <option value="USUARIOS">Usuários</option>
                                <option value="UBS">Unidades Básicas de Saúde</option>
                                <option value="PACIENTES">Pacientes</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Filtro geral</label>
                            <input
                                className="input-field"
                                placeholder="Digite para buscar..."
                                disabled={!tipo}
                                value={filtroGeral}
                                onChange={(e) => setFiltroGeral(e.target.value)}
                            />
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
                                        onChange={(e) => setFiltroUsuario(e.target.value)}
                                    >
                                        <option value="">Todos os usuários</option>
                                        {usuarios.map((usuario) => (
                                            <option key={usuario.id} value={usuario.id}>
                                                {usuario.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Perfil</label>
                                    <select className="input-field"
                                            value={filtroPerfil}
                                            onChange={(e) => setFiltroPerfil(e.target.value)}>
                                        <option value="">Todos os perfis</option>
                                        <option value="SOLICITANTE">Solicitante</option>
                                        <option value="EXECUTOR_APS">Executor APS</option>
                                        <option value="GESTAO_MUNICIPAL">Gestão Municipal</option>
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
                                        onChange={(e) => setFiltroUbs(e.target.value)}
                                    >
                                        <option value="">Todas as UBS</option>
                                        {unidades.map((ubs) => (
                                            <option key={ubs.id} value={ubs.id}>
                                                {ubs.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Bairro</label>
                                    <select className="input-field">
                                        <option value="">Todos os bairros</option>
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
                                        onChange={(e) => setFiltroPaciente(e.target.value)}
                                    >
                                        <option value="">Todos os pacientes</option>
                                        {pacientes.map((paciente) => (
                                            <option key={paciente.id} value={paciente.id}>
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
                                        onChange={(e) => setFiltroUbsPaciente(e.target.value)}
                                    >
                                        <option value="">Todas as UBS</option>

                                        {unidades.map((ubs) => (
                                            <option key={ubs.id} value={ubs.id}>
                                                {ubs.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="gestao-actions">
                        <button type="button" className="primary-btn" onClick={listar}>
                            Listar
                        </button>

                        <button
                            type="button"
                            className="danger-btn"
                            onClick={() => {
                                setTipo("");
                                setFiltroUsuario("");
                                setFiltroUbs("");
                                setFiltroPaciente("");
                                setFiltroGeral("");
                                setFiltroPerfil("");
                                setFiltroUbsPaciente("");
                                setResultado([]);
                            }}
                        >
                            Limpar filtros
                        </button>
                    </div>

                    <div className="gestao-placeholder">
                        {resultado.length === 0 && (<p>Selecione uma listagem e clique em “Listar”.</p>)}
                        {resultado.length > 0 && (
                            <div className="table-card gestao-table-card">
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
                                            <th>Email</th>
                                            <th>Bairro</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {resultado.map((u) => (
                                            <tr key={u.id}>
                                                <td>{u.nome}</td>
                                                <td>{u.cnes}</td>
                                                <td>{u.telefone || "-"}</td>
                                                <td>{u.email || "-"}</td>
                                                <td>{u.endereco?.bairro || "-"}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

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
                            </div>
                        )}
                    </div>

                </div>

            </div>
        </div>
    );
}

export default GestaoListagem;