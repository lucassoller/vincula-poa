package com.vincula.util;

import com.vincula.entity.Servidor;
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

    public void registrarComServidor(Servidor servidor,
                                    TipoAcaoAuditoria acao,
                                    String entidade,
                                    Long entidadeId,
                                    String descricao) {
        auditoriaService.registrarComServidor(servidor, acao, entidade, entidadeId, descricao);
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

    public void enderecoCriado(Long id) {
        registrar(TipoAcaoAuditoria.ENDERECO_CRIADO, "Endereco", id, "Endereco criado");
    }

    public void enderecoAtualizado(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.ENDERECO_ATUALIZADO, "Endereco", id, descricao);
    }

    public void enderecoDeletado(Long id) {
        registrar(TipoAcaoAuditoria.ENDERECO_ATUALIZADO, "Endereco", id, "Endereco deletado");
    }

    public void demandaCriada(Long id, Long usuarioID) {
        registrar(TipoAcaoAuditoria.DEMANDA_CRIADA, "Demanda", id, "Demanda criada para o usuário " + usuarioID);
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

    public void servidorCriado(Long id) {
        registrar(TipoAcaoAuditoria.SERVIDOR_CRIADO, "Servidor", id, "Servidor criado");
    }

    public void servidorAtualizado(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.SERVIDOR_ATUALIZADO, "Servidor", id, descricao);
    }

    public void servidorDeletado(Long id) {
        registrar(TipoAcaoAuditoria.SERVIDOR_DELETADO, "Servidor", id, "Servidor deletado");
    }

    public void servidorSenhaAlteradaDeslogado(Long id) {
        registrarComServidor(null, TipoAcaoAuditoria.SERVIDOR_SENHA_ALTERADA, "Servidor", id, "Senha alterada");
    }

    public void servidorSenhaAlteradaLogado(Long id) {
        registrar(TipoAcaoAuditoria.SERVIDOR_SENHA_ALTERADA, "Servidor", id, "Senha alterada");
    }

    public void servicoCriado(Long id) {
        registrar(TipoAcaoAuditoria.SERVICO_CRIADO, "Servico", id, "Serviço criado");
    }

    public void servicoAtualizado(Long id, String descricao) {
        registrar(TipoAcaoAuditoria.SERVICO_ATUALIZADO, "Servico", id, descricao);
    }

    public void servicoDeletado(Long id) {
        registrar(TipoAcaoAuditoria.SERVICO_DELETADO, "Servico", id, "Serviço deletado");
    }

    public void loginRealizado(Servidor servidor) {
        registrarComServidor(
                servidor,
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                servidor.getId(),
                "Login realizado pelo servidor " + servidor.getLogin()
        );
    }

    public void logoutRealizado(Servidor servidor) {
        registrarComServidor(
                servidor,
                TipoAcaoAuditoria.LOGOUT_REALIZADO,
                "Servidor",
                servidor.getId(),
                "Logout realizado pelo servidor " + servidor.getLogin()
        );
    }

    public void emailEnviado(String email) {
        registrar(
                TipoAcaoAuditoria.EMAIL_ENVIADO,
                "Servidor",
                0L,
                "Email enviado para recuperação de senha do servidor " + email
        );
    }


    public void emailFalhou(String email) {
        registrar(TipoAcaoAuditoria.EMAIL_FALHOU, "Servidor", 0L, "Email de recuperação falhou do servidor " + email);
    }

    public void exportacaoCsvRealizada(String descricao) {
        registrar(TipoAcaoAuditoria.EXPORTACAO_CSV_REALIZADA, "Indicador", 0L, descricao);
    }

    public void exportacaoCsvRealizadaDemanda(String descricao) {
        registrar(TipoAcaoAuditoria.EXPORTACAO_CSV_REALIZADA, "Demanda", 0L, descricao);
    }
}