import { useState } from "react";
import api from "../api/api";
import { converterGoogleMaps } from "../utils/googleMapsImport";
import "./importarMapa.css";

function ImportarMapa() {

    const [url, setUrl] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [carregando, setCarregando] = useState(false);

    async function importar(e) {

        e.preventDefault();
        setMensagem("");

        if (!url.trim()) {
            setMensagem("Informe o link do Google Maps.");
            return;
        }

        try {
            setCarregando(true);
            const geojson = await converterGoogleMaps(url);
            await api.post("/territorios/importar", geojson);

            setMensagem("Mapa importado com sucesso!");
            setUrl("");
        } catch (error) {
            setMensagem(
                error.response?.data?.message ||
                "Erro ao importar mapa."
            );

        } finally {
            setCarregando(false);
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">
                <div className="cadastro-header">
                    <div>
                        <h1>Importar mapa</h1>
                        <p>
                            Importe territórios e unidades de saúde
                            diretamente do Google My Maps
                        </p>
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <button
                            type="button"
                            onClick={() => setMensagem("")}
                        >
                            ✕
                        </button>
                    </div>
                )}
                <form
                    className="cadastro-card"
                    onSubmit={importar}
                >
                    <div className="form-grid full">
                        <div className="form-group">
                            <label>
                                Link do Google Maps <span>*</span>
                            </label>
                            <input
                                className="input-field"
                                type="text"
                                value={url}
                                onChange={(e) =>
                                    setUrl(e.target.value)
                                }
                                placeholder="Cole o link do Google My Maps"
                            />
                        </div>
                    </div>
                    <div className="form-actions">
                        <button
                            type="submit"
                            className="buscar-btn"
                            disabled={carregando}
                        >
                            {carregando
                                ? "Importando..."
                                : "Importar mapa"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ImportarMapa;