import {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/login.css";
import { useAuth } from "../../context/AuthContext.jsx";
import api from "../../api/api.js";

function Login() {
    const navigate = useNavigate();
    const { login: realizarLogin , logout} = useAuth();
    const [loginServidor, setLoginServidor] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(false);
    const [mostrarSenha, setMostrarSenha] = useState(false);

    useEffect(() => {
        logout();
    }, []);

    async function entrar(e) {
        e.preventDefault();
        setErro("");
        setCarregando(true);

        try {
            const response = await api.post("/public/login", {
                login: loginServidor,
                senha,
            });

            realizarLogin(response.data, response.data.token);
            navigate("/indicadores");
        } catch (error) {
            const mensagem = error.response?.data?.message;
            const erroLogin = error.response?.data?.errors?.login;
            const erroSenha = error.response?.data?.errors?.senha;

            setErro(erroLogin || erroSenha || mensagem || "Erro ao realizar login");
        } finally {
            setCarregando(false);
        }
    }

    return (
        <div className="container-login">
            <div className="image-body">
                <div className="image-child">
                    <div className="image-card"></div>
                </div>
            </div>

            <div className="login-body">
                <form onSubmit={entrar} className="card-form">
                    <label className="label">Login</label>
                    <input
                        className="form-control input"
                        value={loginServidor}
                        onChange={(e) => setLoginServidor(e.target.value)}
                    />

                    <label className="label">Senha</label>
                    <div className="input-wrapper">
                        <input
                            className="form-control senha-input"
                            type={mostrarSenha ? "text" : "password"}
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                        />

                        <span
                            className="eye"
                            onClick={() => setMostrarSenha(!mostrarSenha)}
                        >
                            <img
                                src={mostrarSenha ? "/eye2.svg" : "/eye.svg"}
                                alt="mostrar senha"
                                width={20}
                            />
                        </span>
                    </div>

                    <div className="login-senha" onClick={() => navigate("/esqueci-senha")}>
                        Esqueci minha senha
                    </div>

                    <button className="buscar-btn" disabled={carregando}>
                        {carregando ? "Entrando..." : "Entrar"}
                    </button>

                    {erro && (
                        <div className="erro">
                            <div className="alert-card erro-alert">
                                {erro}
                                <span onClick={() => setErro("")}>✕</span>
                            </div>
                        </div>
                    )}
                </form>
            </div>
        </div>
    );
}

export default Login;