function EnderecoForm({register, erros}) {
    return (
        <>
            <div className="endereco-header">
                <h2>Endereço</h2>
                <p>Informe os dados de localização do paciente</p>
            </div>

            <div className="form-grid full">

                <div className="form-group">
                    <label>
                        Rua <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        {...register("endereco.rua")}
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
                        Número <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        type="text"
                        {...register("endereco.numero")}
                        placeholder="Número"
                    />

                    {erros["endereco.numero"] && (
                        <small>{erros["endereco.numero"]}</small>
                    )}
                </div>

                <div className="form-group">
                    <label>
                        Bairro <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        {...register("endereco.bairro")}
                        placeholder="Bairro"
                    />

                    {erros["endereco.bairro"] && (
                        <small>{erros["endereco.bairro"]}</small>
                    )}
                </div>

            </div>

            <div className="form-grid two">

                <div className="form-group">
                    <label>
                        Cidade <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        {...register("endereco.cidade")}
                        placeholder="Cidade"
                    />

                    {erros["endereco.cidade"] && (
                        <small>{erros["endereco.cidade"]}</small>
                    )}
                </div>

                <div className="form-group">
                    <label>
                        Estado <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        {...register("endereco.estado")}
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