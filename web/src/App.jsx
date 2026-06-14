import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import Login from "./pages/Login";
import Indicador from "./pages/Indicador.jsx";
import Usuarios from "./pages/Usuarios.jsx";
import Demandas from "./pages/Demandas";
import Auditoria from "./pages/Auditoria";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import UsuarioCadastro from "./pages/UsuarioCadastro.jsx";
import UsuarioEditar from "./pages/UsuarioEditar.jsx";
import ServidorCadastro from "./pages/ServidorCadastro";
import UnidadeSaudeCadastro from "./pages/UnidadeSaudeCadastro";
import UnidadeSaudeEditar from "./pages/UnidadeSaudeEditar";
import MeuPerfil from "./pages/MeuPerfil.jsx";
import AlterarSenha from "./pages/AlterarSenha.jsx";
import DemandaCadastro from "./pages/DemandaCadastro.jsx";
import MapaTerritorios from "./pages/MapasTerritorio.jsx";
import ImportarMapa from "./pages/ImportarMapa.jsx";
import UnidadesSaude from "./pages/UnidadesSaude.jsx";
import {useAuth} from "./context/AuthContext.jsx";
import EsqueciSenha from "./pages/EsqueciSenha.jsx";
import RedefinirSenha from "./pages/RedefinirSenha.jsx";
import Servidores from "./pages/Servidores.jsx";

function App() {
    const { servidor } = useAuth();
    return (
      <BrowserRouter>
        <Routes>
            <Route path="/"
                 element={<Login />} />

            <Route
                path="/esqueci-senha"
                element={<EsqueciSenha />}
            />

            <Route
                path="/redefinir-senha"
                element={<RedefinirSenha />}
            />
            <Route
                path="/mapa"
                element={
                    <Layout>
                        <MapaTerritorios />
                    </Layout>
                }
            />
            <Route
              path="/indicadores"
              element={
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                    <Layout>
                        <Indicador />
                    </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/usuarios"
              element={
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                    <Layout>
                        <Usuarios />
                    </Layout>
                </ProtectedRoute>
              }
            />
            <Route
                path="/mapa/importar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <ImportarMapa />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/usuarios/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <UsuarioCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/usuarios/:id/editar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <UsuarioEditar />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/demandas"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <Demandas />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/demandas/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <DemandaCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
              path="/auditoria"
              element={
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                    <Layout>
                        <Auditoria />
                    </Layout>
                </ProtectedRoute>
              }
            />

            <Route
                path="/servidores"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <Servidores />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/servidores/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <ServidorCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/unidades-saude/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <UnidadeSaudeCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/unidades-saude"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <UnidadesSaude />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/unidades-saude/:id/editar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <UnidadeSaudeEditar />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/meu-perfil"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <MeuPerfil />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/alterar-senha"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE"]}>
                        <Layout>
                            <AlterarSenha />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="*"
                element={
                    servidor
                        ? <Navigate to="/indicadores" replace />
                        : <Navigate to="/" replace />
                }
            />
        </Routes>
      </BrowserRouter>
    );
}

export default App;