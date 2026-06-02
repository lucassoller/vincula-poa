import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import Login from "./pages/Login";
import Indicador from "./pages/Indicador.jsx";
import Pacientes from "./pages/Pacientes";
import Demandas from "./pages/Demandas";
import Auditoria from "./pages/Auditoria";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import PacienteCadastro from "./pages/PacienteCadastro";
import PacienteEditar from "./pages/PacienteEditar";
import UsuarioCadastro from "./pages/UsuarioCadastro";
import UnidadeSaudeCadastro from "./pages/UnidadeSaudeCadastro";
import PacienteDetalhe from "./pages/PacienteDetalhe";
import UnidadeSaudeEditar from "./pages/UnidadeSaudeEditar";
import GestaoListagem from "./pages/GestaoListagem.jsx";
import MeuPerfil from "./pages/MeuPerfil.jsx";
import AlterarSenha from "./pages/AlterarSenha.jsx";
import DemandaCadastro from "./pages/DemandaCadastro.jsx";
import MapaTerritorios from "./pages/MapasTerritorio.jsx";
import ImportarMapa from "./pages/ImportarMapa.jsx";
import UnidadesSaude from "./pages/UnidadesSaude.jsx";
import {useAuth} from "./context/AuthContext.jsx";
import EsqueciSenha from "./pages/EsqueciSenha.jsx";
import RedefinirSenha from "./pages/RedefinirSenha.jsx";

function App() {
    const { usuario } = useAuth();
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
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS"]}>
                    <Layout>
                        <Indicador />
                    </Layout>
                </ProtectedRoute>
              }
            />
            <Route
              path="/pacientes"
              element={
                <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS"]}>
                    <Layout>
                        <Pacientes />
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
                path="/pacientes/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS"]}>
                        <Layout>
                            <PacienteCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/pacientes/:id"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS"]}>
                        <Layout>
                            <PacienteDetalhe />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/pacientes/:id/editar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS"]}>
                        <Layout>
                            <PacienteEditar />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/demandas"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS", "SOLICITANTE"]}>
                        <Layout>
                            <Demandas />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/demandas/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS", "SOLICITANTE"]}>
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
                path="/usuarios/cadastro"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <UsuarioCadastro />
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
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS", "SOLICITANTE"]}>
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
                path="/gestao/listar"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL"]}>
                        <Layout>
                            <GestaoListagem />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/meu-perfil"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS", "SOLICITANTE"]}>
                        <Layout>
                            <MeuPerfil />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/alterar-senha"
                element={
                    <ProtectedRoute perfisPermitidos={["GESTAO_MUNICIPAL", "USUARIO_APS", "SOLICITANTE"]}>
                        <Layout>
                            <AlterarSenha />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="*"
                element={
                    usuario
                        ? <Navigate to="/indicadores" replace />
                        : <Navigate to="/" replace />
                }
            />
        </Routes>
      </BrowserRouter>
    );
}

export default App;