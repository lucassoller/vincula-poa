import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useEffect, useRef, useState } from "react";
import "./navbar.css"

function Navbar() {
    const navigate = useNavigate();
    const {usuario, logout} = useAuth();

    const [menuAberto, setMenuAberto] = useState(false);
    const menuRef = useRef(null);

    useEffect(() => {
        function fecharAoClicarFora(event) {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setMenuAberto(false);
            }
        }

        document.addEventListener("mousedown", fecharAoClicarFora);

        return () => {
            document.removeEventListener("mousedown", fecharAoClicarFora);
        };
    }, []);

    const inicial = usuario?.nome?.charAt(0)?.toUpperCase() || "U";
    return (
        <nav className="navbar">
            <div className="navbar-logo">Vincula POA</div>

            <div className="navbar-links">
                <Link to="/dashboard" className="navbar-link">
                    Dashboard
                </Link>

                <Link to="/pacientes" className="navbar-link">
                    Pacientes
                </Link>

                <Link to="/demandas" className="navbar-link">
                    Demandas
                </Link>

                <Link to="/auditoria" className="navbar-link">
                    Auditoria
                </Link>

                <Link to="/pacientes/cadastro" className="navbar-link">
                    Cadastrar Paciente
                </Link>

                <Link to="/usuarios/cadastro" className="navbar-link">
                    Cadastrar Usuário
                </Link>

                <Link to="/unidades-saude/cadastro" className="navbar-link">
                    Cadastrar UBS
                </Link>

                {usuario?.perfil === "GESTAO_MUNICIPAL" && (
                    <Link to="/gestao/listar" className="navbar-link">
                        Listar tudo
                    </Link>
                )}
            </div>

            <div className="user-menu" ref={menuRef}>
                <button
                    type="button"
                    className="user-button"
                    onClick={() => setMenuAberto(!menuAberto)}
                    onMouseDown={(e) => e.preventDefault()}
                >
                <span className="avatar">
                    {inicial}
                </span>

                    <span className="user-name">
                    {usuario?.nome || "Usuário"}
                </span>

                    <span className="arrow">
                    ▾
                </span>
                </button>

                {menuAberto && (
                    <div className="user-dropdown">
                        <button
                            className="dropdown-item"
                            onClick={() => {
                                setMenuAberto(false);
                                navigate("/meu-perfil");
                            }}
                        >
                            Meu perfil
                        </button>

                        <button
                            className="dropdown-item"
                            onClick={() => {
                                setMenuAberto(false);
                                navigate("/alterar-senha");
                            }}
                        >
                            Alterar senha
                        </button>

                        <div className="dropdown-divider"></div>

                        <button
                            className="dropdown-item logout-item"
                            onClick={() => {
                                logout();
                                navigate("/");
                            }}
                        >
                            Sair
                        </button>
                    </div>
                )}
            </div>
        </nav>
    );
}


export default Navbar;