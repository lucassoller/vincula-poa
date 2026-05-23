function Pagination({pagina, totalPaginas, onChangePagina,})
{
    if (totalPaginas <= 1) {
        return null;
    }

    return (

        <div className="pagination">

            <button
                type="button"
                className="pagination-btn"
                disabled={pagina === 0}
                onClick={() => onChangePagina(0)}
            >
                Primeira
            </button>

            <button
                type="button"
                className="pagination-btn"
                disabled={pagina === 0}
                onClick={() => onChangePagina(pagina - 1)}
            >
                Anterior
            </button>

            <span className="pagination-info">
                Página {pagina + 1} de {totalPaginas}
            </span>

            <button
                type="button"
                className="pagination-btn"
                disabled={pagina + 1 >= totalPaginas}
                onClick={() => onChangePagina(pagina + 1)}
            >
                Próxima
            </button>

            <button
                type="button"
                className="pagination-btn"
                disabled={pagina + 1 >= totalPaginas}
                onClick={() => onChangePagina(totalPaginas - 1)}
            >
                Última
            </button>

        </div>

    );
}

export default Pagination;
