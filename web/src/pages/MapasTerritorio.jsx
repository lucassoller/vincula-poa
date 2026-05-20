import {
    MapContainer,
    TileLayer,
    GeoJSON,
    CircleMarker,
    Popup,
    Pane
} from "react-leaflet";

import { useEffect, useState } from "react";
import './mapasTerritorio.css'

function MapaTerritorios() {

    const [territorios, setTerritorios] = useState(null);
    const [pontos, setPontos] = useState(null);

    useEffect(() => {

        fetch("/territorios-ubs.geojson")
            .then((res) => res.json())
            .then(setTerritorios)
            .catch(() => console.error("Erro ao carregar territórios"));

        fetch("/unidades-pontos.geojson")
            .then((res) => res.json())
            .then(setPontos)
            .catch(() => console.error("Erro ao carregar pontos"));

    }, []);

    return (
        <div style={{ height: "100vh", width: "100%" }}>

            <MapContainer
                center={[-30.03, -51.23]}
                zoom={11}
                style={{ height: "100%", width: "100%" }}
            >

                <TileLayer
                    attribution='&copy; OpenStreetMap'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />

                <Pane
                    name="territorios"
                    style={{
                        zIndex: 400,
                        pointerEvents: "auto",
                    }}
                >

                    {territorios && (
                        <GeoJSON
                            data={territorios}
                            pane="territorios"
                            style={() => ({
                                color: "#0288d1",
                                weight: 2,
                                fillOpacity: 0.25,
                            })}
                            interactive={true}
                            onEachFeature={(feature, layer) => {

                                layer.bringToBack();

                                const props = feature.properties || {};

                                const cnes = String(
                                    props.CNES || "-"
                                ).replace(".0", "");

                                layer.bindPopup(`
                                    <div style="min-width:240px">
                
                                        <strong>
                                            ${props.name || "Território"}
                                        </strong>
                
                                        <br/><br/>
                
                                        <b>CNES:</b>
                                        ${cnes}
                                        <br/>
                
                                        <b>Distrito:</b>
                                        ${props.Distrito_S || "-"}
                                        <br/>
                
                                        <b>Coordenadoria:</b>
                                        ${props.Coordenado || "-"}
                                        <br/>
                                    </div>
                                `);
                            }}
                        />)}
                </Pane>

                <Pane name="pontos" style={{ zIndex: 650 }}>

                    {pontos?.features?.map((feature, index) => {

                        const props = feature.properties || {};

                        const [lng, lat] = feature.geometry.coordinates;

                        const cnes = String(
                            props.CNES ||
                            props.cnes ||
                            "-"
                        ).replace(".0", "");

                        const nome =
                            props["Unidade de Saúde"] ||
                            props.UnidadeSaude ||
                            props.unidade_saude ||
                            props.name ||
                            "Unidade";

                        const distrito =
                            props["Distrito Sanitário"] ||
                            props.DistritoSanitario ||
                            props.Distrito_S ||
                            "-";

                        const coordenadoria =
                            props["Coordenadoria de Saúde"] ||
                            props.CoordenadoriaSaude ||
                            props.Coordenado ||
                            "-";

                        const endereco =
                            props["Endereço"] ||
                            props.Endereco ||
                            props.endereco ||
                            "-";

                        const telefone =
                            props["Telefones"] ||
                            props.Telefone ||
                            props.telefone ||
                            "-";

                        return (
                            <CircleMarker
                                key={index}
                                center={[lat, lng]}
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

                                        <strong>{nome}</strong>

                                        <br /><br />

                                        <b>CNES:</b> {cnes}<br />

                                        <b>Distrito:</b> {distrito}<br />

                                        <b>Coordenadoria:</b> {coordenadoria}<br />

                                        <b>Endereço:</b> {endereco}<br />

                                        <b>Telefone:</b> {telefone}<br />

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