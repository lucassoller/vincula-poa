import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {

    const [usuario, setUsuario] = useState(null);

    function login(usuarioLogado, token) {

        localStorage.setItem("token", token);
        localStorage.setItem("usuario", JSON.stringify(usuarioLogado));

        setUsuario(usuarioLogado);
    }

    function logout() {

        localStorage.removeItem("token");
        localStorage.removeItem("usuario");

        setUsuario(null);
    }

    return (
        <AuthContext.Provider
            value={{
                usuario,
                setUsuario,
                login,
                logout
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    return useContext(AuthContext);
}