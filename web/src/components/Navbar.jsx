import {Link, useNavigate} from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useEffect, useRef, useState } from "react";
import "./navbar.css";

function Navbar() {

    const navigate = useNavigate();
    const { servidor, logout } = useAuth();
    const [menuAberto, setMenuAberto] = useState(false);
    const menuRef = useRef(null);

    useEffect(() => {
        function fecharAoClicarFora(event) {
            if (
                menuRef.current &&
                !menuRef.current.contains(event.target)
            ) {
                setMenuAberto(false);
            }

        }

        document.addEventListener("mousedown", fecharAoClicarFora);
        return () => {
            document.removeEventListener(
                "mousedown",
                fecharAoClicarFora
            );
        };

    }, []);

    const inicial =
        servidor?.nome?.charAt(0)?.toUpperCase() || "U";

    return (
        <nav className="navbar">
            <div className="navbar-logo">
                Vincula POA
            </div>

            <div className="navbar-links">
                    <Link
                        to="/indicadores"
                        className="navbar-link"
                    >
                        Indicadores
                    </Link>

                {servidor?.perfil === "GESTAO_MUNICIPAL" && (
                    <Link
                        to="/auditoria"
                        className="navbar-link"
                    >
                        Auditoria
                    </Link>
                )}

                {servidor?.perfil === "GESTAO_MUNICIPAL" && (
                    <Link
                        to="/gestao/listar"
                        className="navbar-link"
                    >
                        Listar tudo
                    </Link>
                )}

                <div className="nav-dropdown">
                    <span className="navbar-link">
                        Usuários ▾
                    </span>

                    <div className="nav-dropdown-menu">
                        <Link
                            to="/usuarios"
                            className="dropdown-link"
                        >
                            Listar usuários
                        </Link>
                        <Link
                            to="/usuarios/cadastro"
                            className="dropdown-link"
                        >
                            Cadastrar usuário
                        </Link>
                    </div>
                </div>


                <div className="nav-dropdown">
                    <span className="navbar-link">
                        Demandas ▾
                    </span>

                    <div className="nav-dropdown-menu">
                        <Link
                            to="/demandas"
                            className="dropdown-link"
                        >
                            Listar demandas
                        </Link>
                        <Link
                            to="/demandas/cadastro"
                            className="dropdown-link"
                        >
                            Nova demanda
                        </Link>
                    </div>
                </div>

                <div className="nav-dropdown">
                    <span className="navbar-link">
                        UBS ▾
                    </span>

                    <div className="nav-dropdown-menu">
                        <Link
                            to="/unidades-saude"
                            className="dropdown-link"
                        >
                            Listar UBS
                        </Link>

                        {servidor?.perfil === "GESTAO_MUNICIPAL" && (
                        <Link
                            to="/unidades-saude/cadastro"
                            className="dropdown-link"
                        >
                            Cadastrar UBS
                        </Link>
                        )}

                        {servidor?.perfil === "GESTAO_MUNICIPAL" && (
                            <Link
                                to="/servidores/cadastro"
                                className="dropdown-link"
                            >
                                Cadastrar servidor
                            </Link>

                        )}

                    </div>

                </div>

                <div className="nav-dropdown">

                    <span className="navbar-link">
                        Mapa UBS ▾
                    </span>

                    <div className="nav-dropdown-menu">

                        <Link
                            to="/mapa"
                            className="dropdown-link"
                        >
                            Visualizar mapa
                        </Link>
                        {servidor?.perfil === "GESTAO_MUNICIPAL" && (
                        <Link
                            to="/mapa/importar"
                            className="dropdown-link"
                        >
                            Importar mapa
                        </Link>
                        )}

                    </div>

                </div>

            </div>

            <div
                className="user-menu"
                ref={menuRef}
            >

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
                        {servidor?.nome || "Servidor"}
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