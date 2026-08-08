package com.vincula.service;

import com.vincula.dto.senha.MudancaSenhaDTO;
import com.vincula.dto.servidor.*;
import com.vincula.entity.Servico;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import com.vincula.enums.TipoServico;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.ServicoRepository;
import com.vincula.repository.ServidorRepository;
import com.vincula.specification.ServidorSpecification;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ServidorService {

    private final ServidorRepository servidorRepository;
    private final ServicoRepository servicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaFacade auditoriaFacade;

    public ServidorService(ServidorRepository servidorRepository,
                           ServicoRepository servicoRepository,
                           PasswordEncoder passwordEncoder,
                           AuditoriaFacade auditoriaFacade) {
        this.servidorRepository = servidorRepository;
        this.servicoRepository = servicoRepository;
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

    public Page<ServidorResponseDTO> listarTodosFiltrados(
            FiltroServidorRequestDTO filtro,
            Pageable pageable) {

        Specification<Servidor> specification =
                ServidorSpecification.comFiltros(filtro);

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("nome")
        );

        return servidorRepository.findAll(specification, pageableOrdenado)
                .map(this::toDTO);
    }

    public List<ServidorShortResponseDTO> listarTodosFiltradosPorNome(String nome) {
        return servidorRepository.findTop10ByNomeContainingIgnoreCaseOrderByNome(nome)
                .stream()
                .map(this::toShortDTO)
                .toList();
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
        entity.setServico(resolverServico(dto));

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

        Servico servico = buscarServicoPorId(dto.getServicoId());
        String descricaoLog = AuditoriaDescricaoUtil.servidorAtualizado(entity, dto);

        if(dto.getPerfil() == PerfilServidor.SOLICITANTE && servico.getTipoServico() == TipoServico.UBS){
            throw new BusinessException("Solicitante só pode ser vinculado a serviço do tipo outro ou serviço especializado");
        }else if(dto.getPerfil() == PerfilServidor.SERVIDOR_APS && servico.getTipoServico() != TipoServico.UBS){
            throw new BusinessException("Servidor APS só pode ser vinculado a serviço do tipo UBS");
        }

        entity.setPerfil(dto.getPerfil());
        entity.setServico(servico);

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

    private Servico buscarServicoPorId(Long id){
        return servicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    private Servico resolverServico(ServidorDTO dto) {
        if(dto.getPerfil() == PerfilServidor.GESTAO_MUNICIPAL ||
                dto.getPerfil() == PerfilServidor.VIGILANCIA ||
                dto.getPerfil() == PerfilServidor.COORDENADORIA){
            if(dto.getServicoId() != null){
                throw new BusinessException("Perfil do tipo gestão não deve estar vinculado a um serviço");
            }
            return null;
        }else{
            if(dto.getServicoId() == null){
                throw new BusinessException(dto.getPerfil() + " deve estar vinculado a um serviço de saúde");
            }else{
                Servico servico = buscarServicoPorId(dto.getServicoId());

                if(dto.getPerfil() == PerfilServidor.SOLICITANTE && servico.getTipoServico() == TipoServico.UBS){
                    throw new BusinessException("Solicitante só pode ser vinculado a serviço do tipo outro ou serviço especializado");
                }else if(dto.getPerfil() == PerfilServidor.SERVIDOR_APS && servico.getTipoServico() != TipoServico.UBS){
                    throw new BusinessException("Servidor APS só pode ser vinculado a serviço do tipo UBS");
                }
                return servico;
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
        entity.setServico(resolverServico(dto));

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
        if (entity.getServico() != null) {
            dto.setServicoId(entity.getServico().getId());
            dto.setServicoNome(entity.getServico().getNome());
            dto.setTipoServico(entity.getServico().getTipoServico());
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