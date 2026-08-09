import {
    MapContainer,
    TileLayer,
    GeoJSON,
    CircleMarker,
    Popup,
    Pane
} from "react-leaflet";
import L from "leaflet";
import { useEffect, useState } from "react";
import api from "../../api/api.js";
import "../../styles/mapasTerritorio.css";

function MapaTerritorios() {
    const [territorios, setTerritorios] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [mensagem, setMensagem] = useState("");

    useEffect(() => {
        async function carregarMapa() {
            try {
                setCarregando(true);

                const response = await api.get("/territorios/mapa");
                setTerritorios(response.data || []);
            } catch {
                setMensagem("Erro ao carregar mapa");
            } finally {
                setCarregando(false);
            }
        }

        void carregarMapa();
    }, []);

    if (carregando) {
        return (
            <div className="loading-container">
                <div className="loading-card">
                    <p>Carregando mapa...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="usuarios-container">
            <div className="mapa-page">

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>

                        <span onClick={() => setMensagem("")}>
                            ✕
                        </span>
                    </div>
                )}

                <MapContainer
                    center={[-30.03, -51.23]}
                    zoom={11}
                    style={{ height: "100%", width: "100%" }}
                >
                    <TileLayer
                        attribution="&copy; OpenStreetMap"
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />

                    <Pane
                        name="territorios"
                        style={{ zIndex: 400 }}
                    >
                        {territorios.map((territorio) => {
                            if (!territorio.geom) {
                                return null;
                            }

                            let geometry;

                            try {
                                geometry =
                                    typeof territorio.geom === "string"
                                        ? JSON.parse(territorio.geom)
                                        : territorio.geom;
                            } catch {
                                return null;
                            }

                            return (
                                <GeoJSON
                                    key={`territorio-${territorio.id ?? territorio.cnes}`}
                                    data={geometry}
                                    pane="territorios"
                                    style={() => ({
                                        color: "#0288d1",
                                        weight: 2,
                                        fillOpacity: 0.25,
                                    })}
                                    onEachFeature={(feature, layer) => {
                                        layer.bringToBack();

                                        layer.bindPopup(`
                                            <div style="min-width:240px">

                                                <strong>
                                                    ${territorio.nome || "Território"}
                                                </strong>

                                                <br/><br/>

                                                <b>CNES:</b>
                                                ${territorio.cnes || "-"}

                                                <br/>

                                                <b>Distrito:</b>
                                                ${territorio.distrito || "-"}

                                            </div>
                                        `);
                                    }}
                                />
                            );
                        })}
                    </Pane>

                    <Pane
                        name="pontos"
                        style={{ zIndex: 650 }}
                    >
                        {territorios.map((territorio) => {
                            if (!territorio.geom) {
                                return null;
                            }

                            let geometry;

                            try {
                                geometry =
                                    typeof territorio.geom === "string"
                                        ? JSON.parse(territorio.geom)
                                        : territorio.geom;
                            } catch {
                                return null;
                            }

                            const layer = L.geoJSON(geometry);
                            const bounds = layer.getBounds();

                            if (!bounds.isValid()) {
                                return null;
                            }

                            const centro = bounds.getCenter();

                            return (
                                <CircleMarker
                                    key={`ponto-${territorio.id ?? territorio.cnes}`}
                                    center={[
                                        centro.lat,
                                        centro.lng
                                    ]}
                                    radius={8}
                                    pane="pontos"
                                    pathOptions={{
                                        color: "#0f172a",
                                        fillColor: "#2563eb",
                                        fillOpacity: 1,
                                        weight: 2,
                                    }}
                                >
                                    <Popup>
                                        <div style={{ minWidth: "260px" }}>

                                            <strong>
                                                {territorio.nome}
                                            </strong>

                                            <br /><br />

                                            <b>CNES:</b>{" "}
                                            {territorio.cnes || "-"}

                                            <br />

                                            <b>Distrito:</b>{" "}
                                            {territorio.distrito || "-"}

                                            <br />

                                            <b>Endereço:</b>{" "}
                                            {territorio.endereco
                                                ? `${territorio.endereco.rua || ""}${
                                                    territorio.endereco.numero
                                                        ? ", " + territorio.endereco.numero
                                                        : ""
                                                }${
                                                    territorio.endereco.bairro
                                                        ? " - " + territorio.endereco.bairro
                                                        : ""
                                                }`
                                                : "-"}

                                            <br />

                                            <b>Telefone:</b>{" "}
                                            {territorio.telefone && territorio.telefone2
                                                ? `${territorio.telefone} - ${territorio.telefone2}`
                                                : territorio.telefone || "-"}

                                        </div>
                                    </Popup>
                                </CircleMarker>
                            );
                        })}
                    </Pane>

                </MapContainer>
            </div>
        </div>
    );
}

export default MapaTerritorios;