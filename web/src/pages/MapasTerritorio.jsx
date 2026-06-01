import {MapContainer, TileLayer, GeoJSON, CircleMarker, Popup, Pane} from "react-leaflet";
import { useEffect, useState } from "react";
import api from "../api/api";
import "./mapasTerritorio.css";

function MapaTerritorios() {
    const [territorios, setTerritorios] = useState([]);
    const [geoJson, setGeoJson] = useState(null);

    useEffect(() => {

        api.get("/territorios/mapa")
            .then((response) => {

                const territoriosApi = response.data || [];

                setTerritorios(territoriosApi);

                const featureCollection = {
                    type: "FeatureCollection",
                    features: territoriosApi.map((territorio) => ({

                        type: "Feature",

                        geometry:
                            typeof territorio.geojson === "string"
                                ? JSON.parse(territorio.geojson)
                                : territorio.geojson,

                        properties: {
                            id: territorio.id,
                            nome: territorio.nome,
                            cnes: territorio.cnes,
                            distrito: territorio.distrito,
                            endereco: territorio.endereco,
                        },
                    })),
                };
                setGeoJson(featureCollection);
            })
            .catch(() => {
                console.error("Erro ao carregar mapa");
            });
    }, []);

    return (
        <div style={{ height: "100vh", width: "100%" }}>

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
                    style={{zIndex: 400,}}
                >

                    {geoJson && (
                        <GeoJSON
                            data={geoJson}
                            pane="territorios"
                            style={() => ({
                                color: "#0288d1",
                                weight: 2,
                                fillOpacity: 0.25,
                            })}
                            onEachFeature={(feature, layer) => {

                                layer.bringToBack();

                                const props = feature.properties || {};

                                layer.bindPopup(`
                                    <div style="min-width:240px">

                                        <strong>${props.nome || "Território"}</strong>
                                        <br/><br/>

                                        <b>CNES:</b>
                                        ${props.cnes || "-"}
                                        <br/>

                                        <b>Distrito:</b>
                                        ${props.distrito || "-"}
                                        <br/>
                                    </div>
                                `);
                            }}
                        />
                    )}
                </Pane>

                <Pane
                    name="pontos"
                    style={{zIndex: 650}}
                >

                    {territorios.map((territorio, index) => {
                        const endereco = territorio.endereco;

                        if (!endereco.latitude || !endereco.longitude) {
                            return null;
                        }

                        const enderecoFormatado = `
                            ${endereco.rua || ""}
                            ${endereco.numero ? ", " + endereco.numero : ""}
                            ${endereco.bairro ? " - " + endereco.bairro : ""}
                        `;

                        return (
                            <CircleMarker
                                key={index}
                                center={[
                                    endereco.latitude,
                                    endereco.longitude
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
                                        <strong>{territorio.nome}</strong>
                                        <br /><br />
                                        <b>CNES:</b>
                                        {" "}
                                        {territorio.cnes || "-"}
                                        <br />

                                        <b>Distrito:</b>
                                        {" "}
                                        {territorio.distrito || "-"}
                                        <br />

                                        <b>Endereço:</b>
                                        {" "}
                                        {enderecoFormatado}
                                        <br />

                                        <b>Telefone:</b>{" "}
                                        {
                                            territorio.telefone && territorio.telefone2
                                                ? `${territorio.telefone} - ${territorio.telefone2}`
                                                : territorio.telefone
                                                    ? territorio.telefone
                                                    : "-"
                                        }
                                        <br />
                                    </div>
                                </Popup>
                            </CircleMarker>
                        );
                    })}
                </Pane>
            </MapContainer>
        </div>
    );
}

export default MapaTerritorios;