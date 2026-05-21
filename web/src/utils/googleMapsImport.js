import { DOMParser } from "@xmldom/xmldom";
import { kml as toGeoJSON } from "@tmcw/togeojson";

export async function converterGoogleMaps(url) {

    const mid =
        url.match(/[?&]mid=([^&]+)/)?.[1];

    if (!mid) {
        throw new Error("MID não encontrado");
    }

    const kmlUrl =
        `https://www.google.com/maps/d/kml?mid=${mid}&forcekml=1`;

    const response =
        await fetch(kmlUrl);

    const kmlText =
        await response.text();

    const xml =
        new DOMParser().parseFromString(
            kmlText,
            "text/xml"
        );

    const geojson =
        toGeoJSON(xml);

    const featuresFiltradas =
        geojson.features.filter((feature) => {

            const nome =
                (
                    feature.properties?.name || ""
                ).toUpperCase();

            return (
                nome.startsWith("US ") ||
                nome.startsWith("UBS ") ||
                nome.startsWith("CF ")
            );
        });

    return {
        type: "FeatureCollection",
        features: featuresFiltradas,
    };
}