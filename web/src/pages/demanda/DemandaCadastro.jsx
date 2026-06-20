import {useEffect, useRef, useState} from "react";
import { useNavigate, useLocation } from "react-router-dom";
import api from "../../api/api.js";
import { useAuth } from "../../context/AuthContext.jsx";
import "../../styles/demandaCadastro.css";
import {useForm} from "react-hook-form";

function DemandaCadastro() {
    const {
        reset,
        register,
        handleSubmit,
        setValue,
    } = useForm({
        defaultValues: {
            motivoBuscaAtiva: "",
            descricaoBusca: "",
            prazoDemanda: "",
            usuarioId: ""
        }
    });
    const location = useLocation();
    const navigate = useNavigate();
    const { servidor } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [erros, setErros] = useState({});
    const [mensagem, setMensagem] = useState("");
    const [mensagemSucesso, setMensagemSucesso] = useState("");
    const [ubsUsuario, setUbsUsuario] = useState("");
    const [buscaUsuario, setBuscaUsuario] = useState("");
    const [sugestoes, setSugestoes] = useState([]);
    const [usuarioSelecionado, setUsuarioSelecionado] = useState(null);
    const autocompleteRef = useRef(null);
    const [carregandoSugestoes, setCarregandoSugestoes] = useState(false);

    useEffect(() => {
        function handleClickOutside(event) {
            if (autocompleteRef.current && !autocompleteRef.current.contains(event.target)) {
                setSugestoes([]);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener(
                "mousedown",
                handleClickOutside
            );
        };

    }, []);

    useEffect(() => {
        const usuario = usuarios.find(p => String(p.id) === String(location.state?.usuarioId));

        if (usuario) {
            setValue("usuarioId", String(usuario.id));

            // eslint-disable-next-line react-hooks/set-state-in-effect
            setBuscaUsuario(`${usuario.nomeCompleto} - ${usuario.documento}`);
            setUbsUsuario(usuario.unidadeSaudeNome || "");
            setUsuarioSelecionado(usuario);
        }
    }, [location.state, usuarios, setValue]);

    useEffect(() => {
        async function carregarDados() {
            try {
                const response = await api.get("/usuarios/all");
                setUsuarios(response.data);
            } catch {
                setMensagemSucesso("");
                setMensagem("Erro ao carregar dados.");
            }
        }

        void carregarDados();
    }, [servidor]);

    useEffect(() => {

        if (usuarioSelecionado) {
            return;
        }

        if (!buscaUsuario.trim()) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setSugestoes([]);
            setUsuarioSelecionado(null);
            setUbsUsuario("");
            setValue("usuarioId", "");
            return;
        }

        const timeout = setTimeout(async () => {

            try {
                setCarregandoSugestoes(true);
                const response = await api.get(
                    `/usuarios/filtrados/busca/${buscaUsuario}`
                );
                setSugestoes(response.data);

            } catch {
                setSugestoes([]);
                setUsuarioSelecionado(null);
                setUbsUsuario("");
                setValue("usuarioId", "");
            } finally {
                setCarregandoSugestoes(false);
            }

        }, 300);

        return () => clearTimeout(timeout);

    }, [buscaUsuario, setValue, usuarioSelecionado]);

    function selecionarUsuario(usuario) {
        setUsuarioSelecionado(usuario);
        setValue("usuarioId", usuario.id);
        setBuscaUsuario(`${usuario.nomeCompleto} - ${usuario.documento}`);
        setUbsUsuario(usuario.unidadeSaudeNome || "");
        setSugestoes([]);
    }

    async function salvar(dados) {
        setErros({});

        try {
            const payload = {
                ...dados,
                motivoBuscaAtiva: dados.motivoBuscaAtiva || null,
                prazoDemanda: dados.prazoDemanda || null,
                usuarioId: dados.usuarioId ? Number(dados.usuarioId) : null,
            };

            await api.post("/demandas", payload);

            setMensagemSucesso("Demanda cadastrada com sucesso!");
            setMensagem("");
            reset();
            setBuscaUsuario("");
            setUsuarioSelecionado(null);
            setSugestoes([]);
            setUbsUsuario("");
            setErros({});
        } catch (error) {
            setMensagemSucesso("");
            if (error.response?.data?.errors) {
                const errors = error.response.data.errors;
                setErros(errors);
                setMensagem(error.response.data.message || "Dados inválidos");
            } else {
                setMensagem(error.response.data.message);
            }
        }
    }

    return (
        <div className="cadastro-container">
            <div className="cadastro-page">

                <div className="cadastro-header">
                    <div>
                        <h1>Nova demanda</h1>
                        <p>Cadastre uma solicitação de busca ativa para acompanhamento</p>
                    </div>
                    <div className="perfil-badge">
                        {servidor?.perfil === 'GESTAO_MUNICIPAL' ? servidor.perfil : servidor.unidadeSaude}
                    </div>
                </div>

                {mensagem && (
                    <div className="alert-card">
                        <span>{mensagem}</span>
                        <span onClick={() => setMensagem("")}>✕</span>
                    </div>
                )}

                {mensagemSucesso && (
                    <div className="success-card">
                        <span>{mensagemSucesso}</span>
                        <span onClick={() => setMensagemSucesso("")}>✕</span>
                    </div>
                )}

                <form className="cadastro-card" onSubmit={handleSubmit(salvar)}>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Usuário <span>*</span></label>
                            <div className="autocomplete-container" ref={autocompleteRef}>
                                <input
                                    type="text"
                                    className="input-field"
                                    value={buscaUsuario}
                                    onChange={(e) => {

                                        const valor = e.target.value;

                                        setBuscaUsuario(valor);

                                        if (
                                            usuarioSelecionado &&
                                            valor !== `${usuarioSelecionado.nomeCompleto} - ${usuarioSelecionado.documento}`
                                        ) {
                                            setUsuarioSelecionado(null);
                                            setValue("usuarioId", "");
                                            setUbsUsuario("");
                                        }
                                    }}
                                    placeholder="Digite o nome completo ou o número do documento de identificação"
                                />

                                {carregandoSugestoes && (
                                    <div className="autocomplete-loading">
                                        <div className="mini-spinner"></div>
                                    </div>
                                )}

                                {Array.isArray(sugestoes) && sugestoes.length > 0 && (
                                    <div className="autocomplete-list">
                                        {sugestoes.map(usuario => (
                                            <div
                                                key={usuario.id}
                                                className="autocomplete-item"
                                                onClick={() => selecionarUsuario(usuario)}
                                            >
                                                {usuario.nomeCompleto} - {usuario.documento}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                            {erros.usuarioId && <small>{erros.usuarioId}</small>}
                        </div>
                        <div className="form-group">
                            <label>UBS do usuário</label>
                            <input
                                type="text"
                                className="input-field"
                                value={ubsUsuario}
                                disabled
                            />
                        </div>
                    </div>

                    <div className="form-grid two">
                        <div className="form-group">
                            <label>Motivo da busca <span>*</span></label>
                            <select
                                className="input-field"
                                {...register("motivoBuscaAtiva")}
                            >
                                <option value="">Selecione</option>
                                <option value="CONDICAO_SAUDE">Condição de saúde</option>
                                <option value="FALTOSO">Faltoso</option>
                                <option value="ABANDONO">Abandono</option>
                                <option value="OUTRO">Outro</option>
                            </select>
                            {erros.motivoBuscaAtiva && <small>{erros.motivoBuscaAtiva}</small>}
                        </div>

                        <div className="form-group">
                            <label>Prazo <span>*</span></label>
                            <select
                                className="input-field"
                                {...register("prazoDemanda")}
                            >
                                <option value="">Selecione</option>
                                <option value="D1">1 dia</option>
                                <option value="D2">2 dias</option>
                                <option value="D3">3 dias</option>
                                <option value="D7">7 dias</option>
                                <option value="D15">15 dias</option>
                                <option value="D20">20 dias</option>
                                <option value="D30">30 dias</option>
                            </select>
                            {erros.prazoDemanda && <small>{erros.prazoDemanda}</small>}
                        </div>
                    </div>

                    <div className="form-grid full">
                        <div className="form-group">
                            <label>Descrição da busca</label>
                            <textarea
                                className="input-field textarea-field"
                                {...register("descricaoBusca")}
                                placeholder="Descreva informações importantes para a busca ativa"
                                maxLength={500}
                            />
                            {erros.descricaoBusca && <small>{erros.descricaoBusca}</small>}
                        </div>
                    </div>

                    <div className="form-actions">
                        <span onClick={handleSubmit(salvar)} className="buscar-btn">
                            Cadastrar
                        </span>

                        <span
                            className="buscar-btn"
                            onClick={() => navigate("/demandas")}
                        >
                            Cancelar
                        </span>
                    </div>

                </form>
            </div>
        </div>
    );
}

export default DemandaCadastro;