import {createContext, useContext, useState} from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {

    const [servidor, setServidor] = useState(() => {

        const servidorStorage = localStorage.getItem("servidor");

        return servidorStorage
            ? JSON.parse(servidorStorage)
            : null;
    });

    function login(servidorLogado, token) {

        localStorage.setItem("token", token);
        localStorage.setItem("servidor", JSON.stringify(servidorLogado));

        setServidor(servidorLogado);
    }

    function logout() {

        localStorage.removeItem("token");
        localStorage.removeItem("servidor");

        setServidor(null);
    }

    return (
        <AuthContext.Provider
            value={{
                servidor,
                setServidor,
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