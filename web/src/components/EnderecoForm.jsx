function EnderecoForm({ endereco, onChange, onBuscarCep }) {
  return (
    <>
      <h3>Endereço</h3>

      <label className="label">CEP</label>
      <input className="form-control" name="cep" value={endereco.cep} onChange={onBuscarCep}/>

      <label className="label">Rua</label>
      <input className="form-control" name="rua" value={endereco.rua} onChange={onChange}/>

      <label className="label">Número</label>
      <input className="form-control" name="numero" value={endereco.numero} onChange={onChange}/>

      <label className="label">Bairro</label>
      <input className="form-control" name="bairro" value={endereco.bairro} onChange={onChange}/>

      <label className="label">Cidade</label>
      <input className="form-control" name="cidade" value={endereco.cidade} onChange={onChange}/>

      <label className="label">Estado</label>
      <input className="form-control" name="estado" value={endereco.estado} onChange={onChange}/>
    </>
  );
}

export default EnderecoForm;