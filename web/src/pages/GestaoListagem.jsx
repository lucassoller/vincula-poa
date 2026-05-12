import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./gestaoListagem.css";

function GestaoListagem() {
    const navigate = useNavigate();

    const [tipo, setTipo] = useState("");
    const [filtroUsuario, setFiltroUsuario] = useState("");
    const [filtroUbs, setFiltroUbs] = useState("");
    const [filtroPaciente, setFiltroPaciente] = useState("");
    const [filtroGeral, setFiltroGeral] = useState("");

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
                                onChange={(e) => setTipo(e.target.value)}
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
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Perfil</label>
                                    <select className="input-field">
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
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>UBS vinculada</label>
                                    <select className="input-field">
                                        <option value="">Todas as UBS</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="gestao-actions">
                        <button type="button" className="primary-btn">
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
                            }}
                        >
                            Limpar filtros
                        </button>
                    </div>

                    <div className="gestao-placeholder">
                        <p>Selecione uma listagem e clique em “Listar”.</p>
                    </div>

                </div>

            </div>
        </div>
    );
}

export default GestaoListagem;