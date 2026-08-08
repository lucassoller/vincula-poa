package com.vincula.util;

import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.servidor.TransferirServidorDTO;
import com.vincula.dto.usuario.UsuarioDTO;
import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.servico.ServicoDTO;
import com.vincula.dto.servidor.MeuPerfilDTO;
import com.vincula.dto.servidor.ServidorDTO;
import com.vincula.entity.*;

import java.util.Objects;

public class AuditoriaDescricaoUtil {

    private AuditoriaDescricaoUtil() {
    }

    public static String demandaAtualizada(Demanda entity, DemandaDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Motivo", entity.getMotivoBuscaAtiva(), dto.getMotivoBuscaAtiva());
        adicionarAlteracao(sb, "Descrição da busca", entity.getDescricaoBusca(), dto.getDescricaoBusca());
        adicionarAlteracao(sb, "Prazo", entity.getPrazoDemanda(), dto.getPrazoDemanda());

        return sb.isEmpty() ? "Demanda atualizada sem alterações relevantes" : sb.toString();
    }

    public static String usuarioAtualizado(Usuario entity, UsuarioDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Nome", entity.getNomeCompleto(), dto.getNomeCompleto());
        adicionarAlteracao(sb, "Telefone", entity.getTelefone(), dto.getTelefone());
        adicionarAlteracao(sb, "Documento", entity.getDocumento(), dto.getDocumento());
        adicionarAlteracao(sb, "Data de nascimento", entity.getDataNascimento(), dto.getDataNascimento());
        adicionarAlteracao(sb, "Sexo", entity.getSexo(), dto.getSexo());

        if (entity.getEndereco() != null && dto.getEndereco() != null) {
            String enderecoAlteracao = alterarEndereco(entity.getEndereco(), dto.getEndereco());

            if (!enderecoAlteracao.isBlank()) {
                sb.append("Endereço alterado: ").append(enderecoAlteracao);
            }
        }

        return sb.isEmpty() ? "Usuário atualizado sem alterações relevantes" : sb.toString();
    }

    public static String tentativaContatoAtualizada(TentativaContato entity, TentativaContatoDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Tipo", entity.getTipo(), dto.getTipo());
        adicionarAlteracao(sb, "Descrição", entity.getDescricao(), dto.getDescricao());

        return sb.isEmpty() ? "Tentativa de contato atualizada sem alterações relevantes" : sb.toString();
    }

    public static String demandaEncerrada(Demanda demanda) {
        return "Demanda encerrada com desfecho [" + demanda.getDesfecho()
                + "] e descrição [" + demanda.getDescricaoDesfecho() + "]";
    }

    public static String demandaRedirecionada(Demanda demanda) {
        return "Demanda redirecionada do serviço ["
                + demanda.getServicoResponsavelAnterior().getId() + " - " + demanda.getServicoResponsavelAnterior().getNome()
                + "] para ["
                + demanda.getServicoResponsavel().getId() + " - " + demanda.getServicoResponsavel().getNome()
                + "]. Motivo: " + demanda.getMotivoRedirecionamento();
    }

    public static String servicoAtualizada(Servico entity, ServicoDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Nome", entity.getNome(), dto.getNome());
        adicionarAlteracao(sb, "CNES", entity.getCnes(), dto.getCnes());
        adicionarAlteracao(sb, "Telefone", entity.getTelefone(), dto.getTelefone());

        if (entity.getEndereco() != null && dto.getEndereco() != null) {
            String enderecoAlteracao = alterarEndereco(entity.getEndereco(), dto.getEndereco());

            if (!enderecoAlteracao.isBlank()) {
                sb.append("Endereço alterado: ").append(enderecoAlteracao);
            }
        }

        return sb.isEmpty() ? "Serviço atualizado sem alterações relevantes" : sb.toString();
    }

    public static String enderecoAtualizado(Endereco entity, EnderecoDTO dto) {
        String enderecoAlteracao = alterarEndereco(entity, dto);
        if (enderecoAlteracao.isBlank()) {
            return "Endereço atualizado sem alterações relevantes";
        }
        return enderecoAlteracao;
    }

    public static String servidorAtualizado(Servidor entity, ServidorDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Nome", entity.getNome(), dto.getNome());
        adicionarAlteracao(sb, "Email", entity.getEmail(), dto.getEmail());
        adicionarAlteracao(sb, "Login", entity.getLogin(), dto.getLogin());
        adicionarAlteracao(sb, "Perfil", entity.getPerfil(), dto.getPerfil());
        adicionarAlteracao(sb, "Ativo", entity.getAtivo(), dto.getAtivo());

        Long servicoAntes = entity.getServico() != null ? entity.getServico().getId() : null;
        adicionarAlteracao(sb, "Serviço", servicoAntes, dto.getServicoId());

        return sb.isEmpty() ? "Servidor atualizado sem alterações relevantes" : sb.toString();
    }

    public static String servidorAtualizado(Servidor entity, TransferirServidorDTO dto) {
        StringBuilder sb = new StringBuilder();
        adicionarAlteracao(sb, "Perfil", entity.getPerfil(), dto.getPerfil());

        Long servicoAntes = entity.getServico() != null ? entity.getServico().getId() : null;
        adicionarAlteracao(sb, "Serviço", servicoAntes, dto.getServicoId());

        return sb.isEmpty() ? "Servidor atualizado sem alterações relevantes" : sb.toString();
    }

    public static String servidorAtualizado(Servidor entity, MeuPerfilDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Nome", entity.getNome(), dto.getNome());
        adicionarAlteracao(sb, "Email", entity.getEmail(), dto.getEmail());
        adicionarAlteracao(sb, "Login", entity.getLogin(), dto.getLogin());

        return sb.isEmpty() ? "Servidor atualizado sem alterações relevantes" : sb.toString();
    }

    private static String alterarEndereco(Endereco entity, EnderecoDTO dto){
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Rua", entity.getRua(), dto.getRua());
        adicionarAlteracao(sb, "Número", entity.getNumero(), dto.getNumero());
        adicionarAlteracao(sb, "Bairro", entity.getBairro(), dto.getBairro());
        adicionarAlteracao(sb, "Cidade", entity.getCidade(), dto.getCidade());
        adicionarAlteracao(sb, "Complemento", entity.getComplemento(), dto.getComplemento());
        adicionarAlteracao(sb, "Estado", entity.getEstado(), dto.getEstado());

        return sb.toString();
    }

    private static void adicionarAlteracao(StringBuilder sb, String campo, Object antes, Object depois) {
        if (!Objects.equals(antes, depois)) {
            sb.append(campo)
                    .append(" alterado de [")
                    .append(antes)
                    .append("] para [")
                    .append(depois)
                    .append("]. ");
        }
    }
}