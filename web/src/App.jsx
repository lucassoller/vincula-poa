import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Pacientes from "./pages/Pacientes";
import Demandas from "./pages/Demandas";
import Auditoria from "./pages/Auditoria";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import PacienteCadastro from "./pages/PacienteCadastro";
import UsuarioCadastro from "./pages/UsuarioCadastro";
import UnidadeSaudeCadastro from "./pages/UnidadeSaudeCadastro";


function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
            <Route path="/cadastro" element={ <Layout> <PacienteCadastro /> </Layout> }/>
            <Route path="/cadastroo" element={ <Layout> <UnidadeSaudeCadastro /> </Layout> }/>
            <Route path="/cadastrooo" element={ <Layout> <UsuarioCadastro /> </Layout> }/>
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
        </Routes>
      </BrowserRouter>
  );
}

export default App;