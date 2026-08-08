import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import Login from "./pages/auth/Login.jsx";
import Indicador from "./pages/indicador/Indicador.jsx";
import Usuarios from "./pages/usuario/Usuarios.jsx";
import Demandas from "./pages/demanda/Demandas.jsx";
import Auditoria from "./pages/auditoria/Auditoria.jsx";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import UsuarioCadastro from "./pages/usuario/UsuarioCadastro.jsx";
import UsuarioEditar from "./pages/usuario/UsuarioEditar.jsx";
import ServidorCadastro from "./pages/servidor/ServidorCadastro.jsx";
import ServicoCadastro from "./pages/servico/ServicoCadastro.jsx";
import ServicoEditar from "./pages/servico/ServicoEditar.jsx";
import MeuPerfil from "./pages/servidor/MeuPerfil.jsx";
import AlterarSenha from "./pages/auth/AlterarSenha.jsx";
import DemandaCadastro from "./pages/demanda/DemandaCadastro.jsx";
import MapaTerritorios from "./pages/mapa/MapasTerritorio.jsx";
import ImportarMapa from "./pages/mapa/ImportarMapa.jsx";
import Servicos from "./pages/servico/Servicos.jsx";
import {useAuth} from "./context/AuthContext.jsx";
import EsqueciSenha from "./pages/auth/EsqueciSenha.jsx";
import RedefinirSenha from "./pages/auth/RedefinirSenha.jsx";
import Servidores from "./pages/servidor/Servidores.jsx";

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
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                    <Layout>
                        <Indicador />
                    </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/usuarios"
              element={
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                    <Layout>
                        <Usuarios />
                    </Layout>
                </ProtectedRoute>
              }
            />
            <Route
                path="/mapa/importar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <ImportarMapa />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/usuarios/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <UsuarioCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/usuarios/:id/editar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <UsuarioEditar />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/demandas"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <Demandas />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/demandas/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <DemandaCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
              path="/auditoria"
              element={
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "VIGILANCIA", "COORDENADORIA"]}>
                    <Layout>
                        <Auditoria />
                    </Layout>
                </ProtectedRoute>
              }
            />

            <Route
                path="/servidores"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <Servidores />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/servidores/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <ServidorCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/servicos/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <ServicoCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/servicos"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <Servicos />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/servicos/:id/editar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <ServicoEditar />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/meu-perfil"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
                        <Layout>
                            <MeuPerfil />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/alterar-senha"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "SERVIDOR_APS", "SOLICITANTE", "VIGILANCIA", "COORDENADORIA"]}>
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