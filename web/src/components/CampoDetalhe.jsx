function CampoDetalhe({ label, valor }) {
    return (
        <div className="detalhe-campo">
            <span>{label}</span>
            <strong>{valor || "-"}</strong>
        </div>
    );
}

export default CampoDetalhe;