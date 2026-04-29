import { Link, useNavigate } from "react-router-dom";

function Navbar() {
    const navigate = useNavigate();

    function logout() {
        localStorage.removeItem("token");
        navigate("/");
    }

    return (
        <nav style={styles.nav}>
            <div style={styles.logo}>Vincula POA</div>

            <div style={styles.links}>
                <Link to="/dashboard" style={styles.link}>Dashboard</Link>
                <Link to="/pacientes" style={styles.link}>Pacientes</Link>
                <Link to="/demandas" style={styles.link}>Demandas</Link>
                <Link to="/auditoria" style={styles.link}>Auditoria</Link>
            </div>

            <button onClick={logout} style={styles.botao}>
                Sair
            </button>
        </nav>
    );
}

const styles = {
    nav: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "12px 24px",
        background: "#1f2937",
        color: "#fff",
    },
    logo: {
        fontWeight: "bold",
        fontSize: "18px",
    },
    links: {
        display: "flex",
        gap: "20px",
    },
    link: {
        color: "#fff",
        textDecoration: "none",
        fontWeight: "500",
    },
    botao: {
        background: "#ef4444",
        border: "none",
        padding: "8px 12px",
        borderRadius: "6px",
        color: "#fff",
        cursor: "pointer",
    },
};

export default Navbar;