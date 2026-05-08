function EnderecoForm({ endereco, erros, onChange, onBuscarCep }) {
  return (
    <>
      <h3>Endereço</h3>

      <label className="label">CEP <span className="p-required">*</span></label>
      <input className="form-control" type={"number"} name="cep" value={endereco.cep} onChange={onBuscarCep}/>
        {erros["endereco.cep"] && <span className="campo-erro">{erros["endereco.cep"]}</span>}

      <label className="label">Rua <span className="p-required">*</span></label>
      <input className="form-control" name="rua" value={endereco.rua} onChange={onChange}/>
        {erros["endereco.rua"] && <span className="campo-erro">{erros["endereco.rua"]}</span>}

      <label className="label">Número <span className="p-required">*</span></label>
      <input className="form-control" type={"number"} name="numero" value={endereco.numero} onChange={onChange}/>
        {erros["endereco.numero"] && <span className="campo-erro">{erros["endereco.numero"]}</span>}

      <label className="label">Bairro <span className="p-required">*</span></label>
      <input className="form-control" name="bairro" value={endereco.bairro} onChange={onChange}/>
        {erros["endereco.bairro"] && <span className="campo-erro">{erros["endereco.bairro"]}</span>}

      <label className="label">Cidade <span className="p-required">*</span></label>
      <input className="form-control" name="cidade" value={endereco.cidade} onChange={onChange}/>
        {erros["endereco.cidade"] && <span className="campo-erro">{erros["endereco.cidade"]}</span>}

      <label className="label">Estado <span className="p-required">*</span></label>
      <input className="form-control" name="estado" value={endereco.estado} onChange={onChange}/>
        {erros["endereco.estado"] && <span className="campo-erro">{erros["endereco.estado"]}</span>}
    </>
  );
}

export default EnderecoForm;