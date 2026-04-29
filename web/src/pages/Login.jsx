import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

function Login() {
    const navigate = useNavigate();

    const [login, setLogin] = useState("");
    const [senha, setSenha] = useState("");
    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(false);

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
        <div style={styles.container}>
            <form onSubmit={entrar} style={styles.card}>
                <h1 style={styles.titulo}>Vincula POA</h1>
                <p style={styles.subtitulo}>Acesse o sistema</p>

                {erro && <div style={styles.erro}>{erro}</div>}

                <label style={styles.label}>Login</label>
                <input
                    style={styles.input}
                    value={login}
                    onChange={(e) => setLogin(e.target.value)}
                    placeholder="Digite seu login"
                />

                <label style={styles.label}>Senha</label>
                <input
                    style={styles.input}
                    type="password"
                    value={senha}
                    onChange={(e) => setSenha(e.target.value)}
                    placeholder="Digite sua senha"
                />

                <button style={styles.botao} disabled={carregando}>
                    {carregando ? "Entrando..." : "Entrar"}
                </button>
            </form>
        </div>
    );
}

const styles = {
    container: {
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "#f3f4f6",
    },
    card: {
        width: "360px",
        background: "#fff",
        padding: "32px",
        borderRadius: "12px",
        boxShadow: "0 10px 25px rgba(0,0,0,0.08)",
        display: "flex",
        flexDirection: "column",
    },
    titulo: {
        margin: 0,
        textAlign: "center",
        color: "#1f2937",
    },
    subtitulo: {
        textAlign: "center",
        color: "#6b7280",
        marginBottom: "24px",
    },
    label: {
        marginBottom: "6px",
        fontWeight: "600",
        color: "#374151",
    },
    input: {
        padding: "12px",
        marginBottom: "16px",
        borderRadius: "8px",
        border: "1px solid #d1d5db",
        fontSize: "14px",
    },
    botao: {
        padding: "12px",
        borderRadius: "8px",
        border: "none",
        background: "#2563eb",
        color: "#fff",
        fontWeight: "700",
        cursor: "pointer",
    },
    erro: {
        background: "#fee2e2",
        color: "#991b1b",
        padding: "10px",
        borderRadius: "8px",
        marginBottom: "16px",
        textAlign: "center",
    },
};

export default Login;