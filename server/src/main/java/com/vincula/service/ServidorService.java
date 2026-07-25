package com.vincula.service;

import com.vincula.dto.senha.MudancaSenhaDTO;
import com.vincula.dto.servidor.*;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import com.vincula.enums.TipoServico;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.repository.ServidorRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ServidorService {

    private final ServidorRepository servidorRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaFacade auditoriaFacade;

    public ServidorService(ServidorRepository servidorRepository,
                           UnidadeSaudeRepository unidadeSaudeRepository,
                           PasswordEncoder passwordEncoder,
                           AuditoriaFacade auditoriaFacade) {
        this.servidorRepository = servidorRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaFacade = auditoriaFacade;
    }

    public ServidorResponseDTO criar(ServidorDTO dto) {
        validarDuplicidadeCreate(dto);

        Servidor entity = toEntity(dto);
        Servidor salvo = servidorRepository.save(entity);

        auditoriaFacade.servidorCriado(salvo.getId());

        return toDTO(salvo);
    }

    public Page<ServidorResponseDTO> listarTodos(Pageable pageable) {
        return servidorRepository.findAllByOrderByNomeAsc(pageable)
                .map(this::toDTO);
    }

    public Page<ServidorResponseDTO> listarTodosFiltrados(String filtro, Pageable pageable) {
        return servidorRepository.findFiltrados(filtro, pageable)
                .map(this::toDTO);
    }

    public List<ServidorShortResponseDTO> listarTodos() {
        return servidorRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public Page<ServidorResponseDTO> listarTodosPorPerfil(PerfilServidor perfil, Pageable pageable) {
        return servidorRepository.findByPerfilOrderByNomeAsc(perfil, pageable)
                .map(this::toDTO);
    }

    public ServidorResponseDTO buscarPorId(Long id) {
        Servidor entity = buscarServidorPorId(id);
        return toDTO(entity);
    }

    public ServidorResponseDTO buscarPorEmail(String email) {
        Servidor entity = servidorRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Servidor do sistema não encontrado"));
        return toDTO(entity);
    }

    public ServidorResponseDTO buscarPorLogin(String login) {
        Servidor entity = servidorRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Servidor do sistema não encontrado"));
        return toDTO(entity);
    }

    public ServidorResponseDTO atualizar(Long id, ServidorDTO dto) {
        Servidor entity = buscarServidorPorId(id);

        validarDuplicidadeUpdate(dto, id);

        String descricaoLog = AuditoriaDescricaoUtil.servidorAtualizado(entity, dto);

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setPerfil(dto.getPerfil());
        entity.setAtivo(dto.getAtivo());
        entity.setUnidadeSaude(resolverUnidadeSaude(dto));

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            entity.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        }

        Servidor atualizado = servidorRepository.save(entity);

        auditoriaFacade.servidorAtualizado(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public ServidorResponseDTO atualizarMeuPerfil(MeuPerfilDTO dto) {
        Servidor entity = buscarServidorAutenticado();

        validarDuplicidadeUpdate(dto, entity.getId());

        String descricaoLog = AuditoriaDescricaoUtil.servidorAtualizado(entity, dto);

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());

        Servidor atualizado = servidorRepository.save(entity);

        auditoriaFacade.servidorAtualizado(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public ServidorResponseDTO transferirServidor(Long id, TransferirServidorDTO dto) {

        Servidor entity = buscarServidorPorId(id);
        if (entity.getPerfil() == PerfilServidor.GESTAO_MUNICIPAL ||
                entity.getPerfil() == PerfilServidor.VIGILANCIA ||
                entity.getPerfil() == PerfilServidor.COORDENADORIA){
            throw new BusinessException("Não é possível transferir um servidor do tipo gestão");
        }

        if(dto.getPerfil() == PerfilServidor.GESTAO_MUNICIPAL ||
                dto.getPerfil() == PerfilServidor.VIGILANCIA ||
                dto.getPerfil() == PerfilServidor.COORDENADORIA){
            throw new BusinessException("Não é possível mudar o perfil de um servidor para o tipo gestão");
        }

        UnidadeSaude unidadeSaude = buscarUnidadePorId(dto.getUnidadeSaudeId());
        String descricaoLog = AuditoriaDescricaoUtil.servidorAtualizado(entity, dto);

        if(dto.getPerfil() == PerfilServidor.SOLICITANTE && unidadeSaude.getTipoServico() == TipoServico.UBS){
            throw new BusinessException("Solicitante só pode ser vinculado a serviço do tipo outro ou serviço especializado");
        }else if(dto.getPerfil() == PerfilServidor.SERVIDOR_APS && unidadeSaude.getTipoServico() != TipoServico.UBS){
            throw new BusinessException("Servidor APS só pode ser vinculado a serviço do tipo UBS");
        }

        entity.setPerfil(dto.getPerfil());
        entity.setUnidadeSaude(unidadeSaude);

        Servidor atualizado = servidorRepository.save(entity);

        auditoriaFacade.servidorAtualizado(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public ServidorResponseDTO atualizarMinhaSenha(MudancaSenhaDTO dto) {
        Servidor entity = buscarServidorAutenticado();

        if (!passwordEncoder.matches(dto.getSenhaAtual(), entity.getSenhaHash())) {
            throw new BusinessException("Senha atual inválida");
        }

        if(!Objects.equals(dto.getNovaSenha(), dto.getConfirmarSenha())){
            throw new BusinessException("As senhas não coincidem");
        }

        entity.setSenhaHash(passwordEncoder.encode(dto.getNovaSenha()));

        Servidor atualizado = servidorRepository.save(entity);

        auditoriaFacade.servidorAtualizado(atualizado.getId(), "Senha atualizada");

        return toDTO(atualizado);
    }

    public void alterarSenha(Long servidorId, MudancaSenhaDTO dto) {

        Servidor servidor = buscarServidorAutenticado();
        if(!Objects.equals(servidor.getId(), servidorId)){
            throw new BusinessException("Não é possível alterar a senha de outro servidor");
        }

        if (!passwordEncoder.matches(dto.getSenhaAtual(), servidor.getSenhaHash())) {
            throw new BusinessException("Senha atual inválida");
        }

        servidor.setSenhaHash(passwordEncoder.encode(dto.getNovaSenha()));

        Servidor atualizado = servidorRepository.save(servidor);

        auditoriaFacade.servidorSenhaAlteradaLogado(atualizado.getId());
    }

    public void deletar(Long id) {
        Servidor entity = buscarServidorPorId(id);

        Long servidorId = entity.getId();

        servidorRepository.delete(entity);

        auditoriaFacade.servidorDeletado(servidorId);
    }

    private void validarDuplicidadeCreate(ServidorDTO dto) {
        if (servidorRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        if (servidorRepository.existsByLogin(dto.getLogin())) {
            throw new ConflictException("Login já cadastrado");
        }
    }

    private void validarDuplicidadeUpdate(ServidorDTO dto, Long id) {
        if (servidorRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ConflictException("Email já cadastrado");
        }

        if (servidorRepository.existsByLoginAndIdNot(dto.getLogin(), id)) {
            throw new ConflictException("Login já cadastrado");
        }
    }

    private void validarDuplicidadeUpdate(MeuPerfilDTO dto, Long id) {
        if (servidorRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ConflictException("Email já cadastrado");
        }

        if (servidorRepository.existsByLoginAndIdNot(dto.getLogin(), id)) {
            throw new ConflictException("Login já cadastrado");
        }
    }

    public Servidor buscarServidorAutenticado() {
        String login = com.vincula.security.SecurityUtils.getLoginServidorLogado();

        if (login == null) {
            throw new BusinessException("Servidor não autenticado");
        }

        return servidorRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Servidor autenticado não encontrado"));
    }

    private Servidor buscarServidorPorId(Long id){
        return servidorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Servidor do sistema não encontrado"));
    }

    private UnidadeSaude buscarUnidadePorId(Long id){
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    private UnidadeSaude resolverUnidadeSaude(ServidorDTO dto) {
        if(dto.getPerfil() == PerfilServidor.GESTAO_MUNICIPAL ||
                dto.getPerfil() == PerfilServidor.VIGILANCIA ||
                dto.getPerfil() == PerfilServidor.COORDENADORIA){
            if(dto.getUnidadeSaudeId() != null){
                throw new BusinessException("Perfil do tipo gestão não deve estar vinculado a uma unidade de saúde");
            }
            return null;
        }else{
            if(dto.getUnidadeSaudeId() == null){
                throw new BusinessException(dto.getPerfil() + " deve estar vinculado a um serviço de saúde");
            }else{
                UnidadeSaude unidadeSaude = buscarUnidadePorId(dto.getUnidadeSaudeId());

                if(dto.getPerfil() == PerfilServidor.SOLICITANTE && unidadeSaude.getTipoServico() == TipoServico.UBS){
                    throw new BusinessException("Solicitante só pode ser vinculado a serviço do tipo outro ou serviço especializado");
                }else if(dto.getPerfil() == PerfilServidor.SERVIDOR_APS && unidadeSaude.getTipoServico() != TipoServico.UBS){
                    throw new BusinessException("Servidor APS só pode ser vinculado a serviço do tipo UBS");
                }
                return unidadeSaude;
            }
        }
    }

    public ServidorResponseDTO getServidorAutenticadoDTO() {
        Servidor servidor = buscarServidorAutenticado();
        return toDTO(servidor);
    }

    private Servidor toEntity(ServidorDTO dto) {
        if(!Objects.equals(dto.getSenha(), dto.getConfirmarSenha())){
            throw new BusinessException("As senhas não coincidem");
        }
        Servidor entity = new Servidor();

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        entity.setPerfil(dto.getPerfil());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        entity.setUnidadeSaude(resolverUnidadeSaude(dto));

        return entity;
    }

    private ServidorResponseDTO toDTO(Servidor entity) {
        ServidorResponseDTO dto = new ServidorResponseDTO();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setLogin(entity.getLogin());
        dto.setPerfil(entity.getPerfil());
        dto.setAtivo(entity.getAtivo());
        if (entity.getUnidadeSaude() != null) {
            dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
            dto.setUnidadeSaudeNome(entity.getUnidadeSaude().getNome());
            dto.setTipoServico(entity.getUnidadeSaude().getTipoServico());
        }

        return dto;
    }

    private ServidorShortResponseDTO toShortDTO(Servidor entity) {
        ServidorShortResponseDTO dto = new ServidorShortResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}