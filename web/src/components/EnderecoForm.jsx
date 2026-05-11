function EnderecoForm({ endereco, erros, onChange, onBuscarCep }) {
    return (
        <>
            <div className="endereco-header">
                <h2>Endereço</h2>
                <p>Informe os dados de localização do paciente</p>
            </div>

            <div className="form-grid two">

                <div className="form-group">
                    <label>
                        CEP <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        type="text"
                        name="cep"
                        value={endereco.cep}
                        onChange={onBuscarCep}
                        placeholder="00000-000"
                    />

                    {erros["endereco.cep"] && (
                        <small>{erros["endereco.cep"]}</small>
                    )}
                </div>

                <div className="form-group">
                    <label>
                        Número <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        type="text"
                        name="numero"
                        value={endereco.numero}
                        onChange={onChange}
                        placeholder="Número"
                    />

                    {erros["endereco.numero"] && (
                        <small>{erros["endereco.numero"]}</small>
                    )}
                </div>

            </div>

            <div className="form-grid full">

                <div className="form-group">
                    <label>
                        Rua <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        name="rua"
                        value={endereco.rua}
                        onChange={onChange}
                        placeholder="Rua"
                    />

                    {erros["endereco.rua"] && (
                        <small>{erros["endereco.rua"]}</small>
                    )}
                </div>

            </div>

            <div className="form-grid two">

                <div className="form-group">
                    <label>
                        Bairro <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        name="bairro"
                        value={endereco.bairro}
                        onChange={onChange}
                        placeholder="Bairro"
                    />

                    {erros["endereco.bairro"] && (
                        <small>{erros["endereco.bairro"]}</small>
                    )}
                </div>

                <div className="form-group">
                    <label>
                        Cidade <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        name="cidade"
                        value={endereco.cidade}
                        onChange={onChange}
                        placeholder="Cidade"
                    />

                    {erros["endereco.cidade"] && (
                        <small>{erros["endereco.cidade"]}</small>
                    )}
                </div>

            </div>

            <div className="form-grid two">

                <div className="form-group">
                    <label>
                        Estado <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        name="estado"
                        value={endereco.estado}
                        onChange={onChange}
                        placeholder="Estado"
                    />

                    {erros["endereco.estado"] && (
                        <small>{erros["endereco.estado"]}</small>
                    )}
                </div>

            </div>
        </>
    );
}

export default EnderecoForm;