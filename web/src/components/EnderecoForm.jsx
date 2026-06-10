function EnderecoForm({register, erros, unidadeSaude}) {
    return (
        <>
            <div className="endereco-header">
                <h2>Endereço</h2>
                <p>Informe os dados de localização do usuário</p>
            </div>

            <div className="form-grid two">

                <div className="form-group">
                    <label>
                        Rua <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        {...register("endereco.rua")}
                    />

                    {erros["endereco.rua"] && (
                        <small>{erros["endereco.rua"]}</small>
                    )}
                </div>

                <div className="form-group">
                    <label>
                        Número <span>*</span>
                    </label>

                    <input
                        className="input-field"
                        type="text"
                        {...register("endereco.numero")}
                    />

                    {erros["endereco.numero"] && (
                        <small>{erros["endereco.numero"]}</small>
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
                        {...register("endereco.bairro")}
                    />

                    {erros["endereco.bairro"] && (
                        <small>{erros["endereco.bairro"]}</small>
                    )}
                </div>
                <div className="form-group">
                    <label>
                        Complemento
                    </label>

                    <input
                        className="input-field"
                        type="text"
                        {...register("endereco.complemento")}
                    />

                    {erros["endereco.complemento"] && (
                        <small>{erros["endereco.complemento"]}</small>
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
                    />

                    {erros["endereco.cidade"] && (
                        <small>{erros["endereco.cidade"]}</small>
                    )}
                </div>
                <div className="form-group">
                    <label>
                        Estado
                    </label>

                    <input
                        className="input-field"
                        {...register("endereco.estado")}
                        disabled={true}
                    />

                    {erros["endereco.estado"] && (
                        <small>{erros["endereco.estado"]}</small>
                    )}
                </div>
            </div>

            <div className="form-grid two">
                {unidadeSaude && unidadeSaude !== "" &&(
                    <div className="form-group">
                        <label>
                            UBS do usuário
                        </label>

                        <input
                            className="input-field"
                            value={unidadeSaude}
                            disabled
                        />
                    </div>
                )}

            </div>
        </>
    );
}

export default EnderecoForm;