package com.vincula.util;

import com.vincula.entity.Usuario;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.service.AuditoriaService;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaFacade {

    private final AuditoriaService auditoriaService;

    public AuditoriaFacade(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    public void registrar(TipoAcaoAuditoria acao,
                          String entidade,
                          Long entidadeId,
                          String descricao) {
        auditoriaService.registrar(acao, entidade, entidadeId, descricao);
    }

    public void registrarComUsuario(Usuario usuario,
                                    TipoAcaoAuditoria acao,
                                    String entidade,
                                    Long entidadeId,
                                    String descricao) {
        auditoriaService.registrarComUsuario(usuario, acao, entidade, entidadeId, descricao);
    }

    public void pacienteCriado(Long id) {
        registrar(TipoAcaoAuditoria.PACIENTE_CRIADO, "Paciente", id, "Paciente criado");
    }

    public void pacienteAtualizado(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.PACIENTE_ATUALIZADO, "Paciente", id, descricao);
    }

    public void pacienteDeletado(Long id) {
        registrar(TipoAcaoAuditoria.PACIENTE_DELETADO, "Paciente", id, "Paciente deletado");
    }

    public void pacienteVisualizado(Long id) {
        registrar(TipoAcaoAuditoria.PACIENTE_VISUALIZADO, "Paciente", id, "Paciente visualizado");
    }

    public void enderecoCriado(Long id) {
        registrar(TipoAcaoAuditoria.ENDERECO_CRIADO, "Endereco", id, "Endereco criado");
    }

    public void enderecoAtualizado(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.ENDERECO_ATUALIZADO, "Endereco", id, descricao);
    }

    public void enderecoDeletado(Long id) {
        registrar(TipoAcaoAuditoria.ENDERECO_ATUALIZADO, "Endereco", id, "Endereco deletado");
    }

    public void enderecoVisualizado(Long id) {
        registrar(TipoAcaoAuditoria.ENDERECO_VISUALIZADO, "Endereco", id, "Endereco visualizado");
    }

    public void demandaCriada(Long id, Long pacienteID) {
        registrar(TipoAcaoAuditoria.DEMANDA_CRIADA, "Demanda", id, "Demanda criada para o paciente " + pacienteID);
    }

    public void demandaAtualizada(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.DEMANDA_ATUALIZADA, "Demanda", id, descricao);
    }

    public void demandaEncerrada(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.DEMANDA_ENCERRADA, "Demanda", id, descricao);
    }

    public void demandaRedirecionada(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.DEMANDA_REDIRECIONADA, "Demanda", id, descricao);
    }

    public void demandaDeletada(Long id) {
        registrar(TipoAcaoAuditoria.DEMANDA_DELETADA, "Demanda", id, "Demanda deletada");
    }

    public void demandaVisualizada(Long id) {
        registrar(TipoAcaoAuditoria.DEMANDA_VISUALIZADA, "Demanda", id, "Demanda visualizada");
    }

    public void statusDemandaAlterado(Long demandaId, String descricao) {
        registrar(TipoAcaoAuditoria.DEMANDA_STATUS_ALTERADO, "Demanda", demandaId, descricao);
    }

    public void tentativaContatoCriada(Long id, Long demandaId) {
        registrar(
                TipoAcaoAuditoria.TENTATIVA_CONTATO_CRIADA,
                "TentativaContato",
                id,
                "Tentativa de contato registrada para demanda ID " + demandaId
        );
    }

    public void tentativaContatoAtualizada(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.TENTATIVA_CONTATO_ATUALIZADA, "TentativaContato", id, descricao);
    }

    public void tentativaContatoDeletada(Long id) {
        registrar(TipoAcaoAuditoria.TENTATIVA_CONTATO_DELETADA, "TentativaContato", id, "Tentativa contato deledada");
    }

    public void tentativaContatoVisualizado(Long id) {
        registrar(TipoAcaoAuditoria.TENTATIVA_CONTATO_VISUALIZADA, "TentativaContato", id, "Tentativa contato visualizada");
    }

    public void usuarioCriado(Long id) {
        registrar(TipoAcaoAuditoria.USUARIO_CRIADO, "Usuario", id, "Usuário criado");
    }

    public void usuarioAtualizado(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.USUARIO_ATUALIZADO, "Usuario", id, descricao);
    }

    public void usuarioDeletado(Long id) {
        registrar(TipoAcaoAuditoria.USUARIO_DELETADO, "Usuario", id, "Usuário deletado");
    }

    public void usuarioVisualizado(Long id) {
        registrar(TipoAcaoAuditoria.USUARIO_VISUALIZADO, "Usuario", id, "Usuário visualizado");
    }

    public void usuarioSenhaAlteradaDeslogado(Long id) {
        registrarComUsuario(null, TipoAcaoAuditoria.USUARIO_SENHA_ALTERADA, "Usuario", id, "Senha alterada");
    }

    public void usuarioSenhaAlteradaLogado(Long id) {
        registrar(TipoAcaoAuditoria.USUARIO_SENHA_ALTERADA, "Usuario", id, "Senha alterada");
    }

    public void unidadeSaudeCriada(Long id) {
        registrar(TipoAcaoAuditoria.UNIDADE_SAUDE_CRIADA, "UnidadeSaude", id, "Unidade de saúde criada");
    }

    public void unidadeSaudeAtualizada(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.UNIDADE_SAUDE_ATUALIZADA, "UnidadeSaude", id, descricao);
    }

    public void unidadeSaudeDeletada(Long id) {
        registrar(TipoAcaoAuditoria.UNIDADE_SAUDE_DELETADA, "UnidadeSaude", id, "Unidade de saúde deletada");
    }

    public void unidadeSaudeVisualizada(Long id) {
        registrar(TipoAcaoAuditoria.UNIDADE_SAUDE_VISUALIZADA, "UnidadeSaude", id, "Unidade de saúde visualizada");
    }

    public void observacaoCriada(Long id, Long pacienteId) {
        registrar(
                TipoAcaoAuditoria.OBSERVACAO_CRIADA,
                "Observacao",
                id,
                "Observação criada para paciente ID " + pacienteId
        );
    }

    public void observacaoAtualizada(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.OBSERVACAO_ATUALIZADA, "Observacao", id, descricao);
    }

    public void observacaoDeletada(Long id) {
        registrar(TipoAcaoAuditoria.OBSERVACAO_DELETADA, "Observacao", id, "Observação deletada");
    }

    public void observacaoVisualizada(Long id) {
        registrar(TipoAcaoAuditoria.OBSERVACAO_VISUALIZADA, "Observacao", id, "Observação visualizada");
    }

    public void loginRealizado(Usuario usuario) {
        registrarComUsuario(
                usuario,
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Usuario",
                usuario.getId(),
                "Login realizado pelo usuário " + usuario.getLogin()
        );
    }

    public void logoutRealizado(Usuario usuario) {
        registrarComUsuario(
                usuario,
                TipoAcaoAuditoria.LOGOUT_REALIZADO,
                "Usuario",
                usuario.getId(),
                "Logout realizado pelo usuário " + usuario.getLogin()
        );
    }

    public void acessoNegado(String message){
        registrar(TipoAcaoAuditoria.ACESSO_NEGADO, "Sistema", 0L, message);
    }

    public void loginFalhou(String login) {
        registrarComUsuario(
                null,
                TipoAcaoAuditoria.LOGIN_FALHOU,
                "Usuario",
                0L,
                "Tentativa de login falhou para: " + login
        );
    }

    public void emailEnviadoPorDemanda(Long demandaId, Long pacienteId) {
        registrar(
                TipoAcaoAuditoria.EMAIL_ENVIADO,
                "Demanda",
                demandaId,
                "Email enviado para paciente ID " + pacienteId
        );
    }

    public void emailEnviadoPorPaciente(Long pacienteId) {
        registrar(
                TipoAcaoAuditoria.EMAIL_ENVIADO,
                "Paciente",
                pacienteId,
                "Email enviado diretamente para paciente ID " + pacienteId
        );
    }

    public void emailFalhou(String entidade, Long entidadeId, String descricao) {
        registrar(TipoAcaoAuditoria.EMAIL_FALHOU, entidade, entidadeId, descricao);
    }

    public void dashboardAcessado(String descricao) {
        registrar(TipoAcaoAuditoria.DASHBOARD_ACESSADO, "Dashboard", 0L, descricao);
    }

    public void exportacaoCsvRealizada(String descricao) {
        registrar(TipoAcaoAuditoria.EXPORTACAO_CSV_REALIZADA, "Dashboard", 0L, descricao);
    }
}