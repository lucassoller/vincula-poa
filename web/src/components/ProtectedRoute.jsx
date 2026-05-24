import { Navigate } from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

function ProtectedRoute({ children,  perfisPermitidos = []}) {
    const { usuario } = useAuth();

    if (!usuario) {
        return <Navigate to="/" replace />;
    }

    const permitido = perfisPermitidos.includes(usuario.perfil);

    if (!permitido) {
        return <Navigate to="/indicadores" replace />;
    }

    return children;
}

export default ProtectedRoute;