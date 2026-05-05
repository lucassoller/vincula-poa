import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./login.css"
import api from "../api/api";

function Login() {
    const navigate = useNavigate();

    const [login, setLogin] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(false);
    const [mostrarSenha, setMostrarSenha] = useState(false);

    async function entrar(e) {
        e.preventDefault();
        setErro("");
        setCarregando(true);

        try {
            const response = await api.post("/auth/login", {
                login,
                senha,
            });

            localStorage.setItem("token", response.data.token);

            navigate("/dashboard");
        } catch (error) {
            const e = error.response.data.message;
            const login = error.response.data.errors?.login;
            const senha = error.response.data.errors?.senha;
            setErro(login || senha || e);
        } finally {
            setCarregando(false);
        }
    }

    return (
        <div className={"container-login"}>
            <div className={"image-body"}>
                <div className={"image-child"}>
                    <div className={"image-card"}></div>
                </div>
            </div>
            <div className={"login-body"}>
                <form onSubmit={entrar} className={"card-form"}>

                    <label className={"label"}>Login</label>
                    <input
                        className={"input input-margin"}
                        value={login}
                        onChange={(e) => setLogin(e.target.value)}
                        placeholder="Digite seu login"
                    />

                    <label className={"label"}>Senha</label>
                    <div className="input-wrapper">
                        <input
                            className={"input senha-input"}
                            type={mostrarSenha ? "text" : "password"}
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                            placeholder="Digite sua senha"
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

                    <div className="login-senha">
                        Esqueci minha senha
                    </div>


                    <button className={"botao"} disabled={carregando}>
                        {carregando ? "Entrando..." : "Entrar"}
                    </button>
                    {erro && (<div className={"erro"}>
                        <div className={"erro-alert"}>{erro}
                            <span className="close" onClick={() => setErro("")}>✖</span>
                        </div>

                    </div>)}

                </form>

            </div>
        </div>
    );
}

export default Login;