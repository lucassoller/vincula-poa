package com.vincula.util;

import com.vincula.dto.*;
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

    public static String pacienteAtualizado(Paciente entity, PacienteDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Nome", entity.getNomeCompleto(), dto.getNomeCompleto());
        adicionarAlteracao(sb, "Telefone", entity.getTelefone(), dto.getTelefone());
        adicionarAlteracao(sb, "Email", entity.getEmail(), dto.getEmail());
        adicionarAlteracao(sb, "CPF", entity.getCpf(), dto.getCpf());
        adicionarAlteracao(sb, "CNS", entity.getCns(), dto.getCns());
        adicionarAlteracao(sb, "Data de nascimento", entity.getDataNascimento(), dto.getDataNascimento());

        if (entity.getEndereco() != null && dto.getEndereco() != null) {
            String enderecoAlteracao = alterarEndereco(entity.getEndereco(), dto.getEndereco());

            if (!enderecoAlteracao.isBlank()) {
                sb.append("Endereço alterado: ").append(enderecoAlteracao);
            }
        }

        return sb.isEmpty() ? "Paciente atualizado sem alterações relevantes" : sb.toString();
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
        return "Demanda redirecionada da unidade ["
                + demanda.getUnidadeResponsavelAnterior().getId() + " - " + demanda.getUnidadeResponsavelAnterior().getNome()
                + "] para ["
                + demanda.getUnidadeResponsavel().getId() + " - " + demanda.getUnidadeResponsavel().getNome()
                + "]. Motivo: " + demanda.getMotivoRedirecionamento();
    }

    public static String unidadeSaudeAtualizada(UnidadeSaude entity, UnidadeSaudeDTO dto) {
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

        return sb.isEmpty() ? "Unidade de saúde atualizada sem alterações relevantes" : sb.toString();
    }

    public static String enderecoAtualizado(Endereco entity, EnderecoDTO dto) {
        String enderecoAlteracao = alterarEndereco(entity, dto);
        if (enderecoAlteracao.isBlank()) {
            return "Endereço atualizado sem alterações relevantes";
        }
        return enderecoAlteracao;
    }

    public static String observacaoAtualizada(Observacao entity, ObservacaoDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Descrição", entity.getDescricao(), dto.getDescricao());

        return sb.isEmpty() ? "Observação atualizada sem alterações relevantes" : sb.toString();
    }

    public static String usuarioAtualizado(Usuario entity, UsuarioDTO dto) {
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Nome", entity.getNome(), dto.getNome());
        adicionarAlteracao(sb, "Email", entity.getEmail(), dto.getEmail());
        adicionarAlteracao(sb, "Login", entity.getLogin(), dto.getLogin());
        adicionarAlteracao(sb, "Perfil", entity.getPerfil(), dto.getPerfil());
        adicionarAlteracao(sb, "Ativo", entity.getAtivo(), dto.getAtivo());

        Long unidadeAntes = entity.getUnidadeSaude() != null ? entity.getUnidadeSaude().getId() : null;
        adicionarAlteracao(sb, "Unidade de saúde", unidadeAntes, dto.getUnidadeSaudeId());

        return sb.isEmpty() ? "Usuário atualizado sem alterações relevantes" : sb.toString();
    }

    private static String alterarEndereco(Endereco entity, EnderecoDTO dto){
        StringBuilder sb = new StringBuilder();

        adicionarAlteracao(sb, "Rua", entity.getRua(), dto.getRua());
        adicionarAlteracao(sb, "Número", entity.getNumero(), dto.getNumero());
        adicionarAlteracao(sb, "Bairro", entity.getBairro(), dto.getBairro());
        adicionarAlteracao(sb, "Cidade", entity.getCidade(), dto.getCidade());
        adicionarAlteracao(sb, "Estado", entity.getEstado(), dto.getEstado());
        adicionarAlteracao(sb, "CEP", entity.getCep(), dto.getCep());

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