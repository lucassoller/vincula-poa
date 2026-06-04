import { Navigate } from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

function ProtectedRoute({ children,  perfisPermitidos = []}) {
    const { servidor } = useAuth();

    if (!servidor) {
        return <Navigate to="/" replace />;
    }

    const permitido = perfisPermitidos.includes(servidor.perfil);

    if (!permitido) {
        if(servidor.perfil === "SOLICITANTE"){
            return <Navigate to="/demandas" replace />;
        }else{
            return <Navigate to="/indicadores" replace />;
        }
    }

    return children;
}

export default ProtectedRoute;