/*// limpar-territorios.js
import fs from "fs";

const geojson = JSON.parse(fs.readFileSync("../assets/territorios.json", "utf8"));

const featuresLimpas = geojson.features.filter((feature) => {
    const props = feature.properties || {};
    const nome = props.name || "";

    return (
        props.CNES &&
        (
            nome.startsWith("US ") ||
            nome.startsWith("UBS ") ||
            nome.startsWith("CF ")
        )
    );
});

const novoGeojson = {
    type: "FeatureCollection",
    features: featuresLimpas,
};

fs.writeFileSync(
    "../assets/territorios-ubs.geojson",
    JSON.stringify(novoGeojson),
    "utf8"
);

console.log(`Antes: ${geojson.features.length}`);
console.log(`Depois: ${featuresLimpas.length}`);*/