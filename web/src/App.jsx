import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
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


function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                    <Layout>
                        <Dashboard />
                    </Layout>
                </ProtectedRoute>
              }
          />

          <Route
              path="/pacientes"
              element={
                <ProtectedRoute>
                    <Layout>
                        <Pacientes />
                    </Layout>
                </ProtectedRoute>
              }
          />
        <Route
            path="/pacientes/:id"
            element={
                <ProtectedRoute>
                    <Layout>
                        <PacienteDetalhe />
                    </Layout>
                </ProtectedRoute>
            }
        />
        <Route
            path="/pacientes/:id/editar"
            element={
                <ProtectedRoute>
                    <Layout>
                        <PacienteEditar />
                    </Layout>
                </ProtectedRoute>
            }
        />

          <Route
              path="/demandas"
              element={
                <ProtectedRoute>
                    <Layout>
                        <Demandas />
                    </Layout>
                </ProtectedRoute>
              }
          />

          <Route
              path="/auditoria"
              element={
                <ProtectedRoute>
                    <Layout>
                        <Auditoria />
                    </Layout>
                </ProtectedRoute>
              }
          />
            <Route
                path="/pacientes/cadastro"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <PacienteCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/usuarios/cadastro"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <UsuarioCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/unidades-saude/cadastro"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <UnidadeSaudeCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/unidades-saude/:id/editar"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <UnidadeSaudeEditar />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/gestao/listar"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <GestaoListagem />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/meu-perfil"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <MeuPerfil />
                        </Layout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/alterar-senha"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <AlterarSenha />
                        </Layout>
                    </ProtectedRoute>} />
            <Route
                path="/demandas/cadastro"
                element={
                    <ProtectedRoute>
                        <Layout>
                            <DemandaCadastro />
                        </Layout>
                    </ProtectedRoute>
                }
            />

        </Routes>
      </BrowserRouter>
  );
}

export default App;