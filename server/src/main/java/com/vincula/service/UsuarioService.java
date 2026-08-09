package com.vincula.service;

import com.vincula.dto.usuario.*;
import com.vincula.entity.*;
import com.vincula.enums.PerfilServidor;
import com.vincula.enums.Sexo;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.UsuarioRepository;
import com.vincula.specification.AutocompleteUsuarioSpecification;
import com.vincula.specification.UsuarioSpecification;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaFacade auditoriaFacade;
    private final TerritorializacaoService territorializacaoService;
    private final GeocodingService geocodingService;
    private final ServidorService servidorService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          EnderecoMapper enderecoMapper,
                          AuditoriaFacade auditoriaFacade,
                          TerritorializacaoService territorializacaoService,
                          GeocodingService geocodingService, ServidorService servidorService) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaFacade = auditoriaFacade;
        this.territorializacaoService = territorializacaoService;
        this.geocodingService = geocodingService;
        this.servidorService = servidorService;
    }

    public UsuarioResponseDTO criar(UsuarioDTO dto) {
        validarDocumentoCreate(dto);

        Usuario entity = toEntity(dto);

        Usuario salvo = usuarioRepository.save(entity);

        auditoriaFacade.usuarioCriado(salvo.getId());

        return toDTO(salvo);
    }

    public Page<UsuarioResponseDTO> listarTodosFiltrados(
            FiltroUsuarioRequestDTO filtro,
            Pageable pageable) {

        Specification<Usuario> specification =
                UsuarioSpecification.comFiltros(filtro);

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("nomeCompleto")
        );

        return usuarioRepository.findAll(specification, pageableOrdenado)
                .map(this::toDTO);
    }

    public List<UsuarioShortResponseDTO> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeCompletoAsc()
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public List<UsuarioShortResponseDTO> listarTodosFiltradosPorNomeOuDocumento(String filtro) {
        return usuarioRepository.buscarPorNomeOuDocumento(filtro)
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public List<UsuarioShortResponseDTO> listarTodosFiltradosPorNomeCompleto(AutocompleteUsuarioRequestDTO dto) {
        Specification<Usuario> specification =
                AutocompleteUsuarioSpecification.comFiltros(dto);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("nomeCompleto")
        );

        return usuarioRepository.findAll(specification, pageable)
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        return toDTO(usuario);
    }

    public UsuarioResponseDTO buscarPorDocumento(String documento) {
        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        return toDTO(usuario);
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorId(id);

        validarDocumentoUpdate(id, dto);

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(ChronoLocalDate.from(LocalDate.now()))) {
            throw new BusinessException("A data de nascimento não pode ser futura");
        }

        String descricaoLog = AuditoriaDescricaoUtil.usuarioAtualizado(usuario, dto);

        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setTelefone(dto.getTelefone());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setDocumento(dto.getDocumento());
        usuario.setSexo(dto.getSexo() != null ? dto.getSexo() : Sexo.NAO_INFORMADO);

        enderecoMapper.updateEntityFromDto(dto.getEndereco(), usuario.getEndereco());

        geocodingService.preencherCoordenadas(usuario.getEndereco());

        if(usuario.getEndereco().getLatitude() == null || usuario.getEndereco().getLongitude() == null){
            throw new BusinessException("Serviço não encontrado para esse endereço");
        }

        Servico servico = territorializacaoService.buscarUbsPorCoordenada(
                usuario.getEndereco().getLatitude(),
                usuario.getEndereco().getLongitude());

        if(servico == null){
            throw new BusinessException("Serviço não encontrado para esse endereço");
        }

        usuario.setServico(servico);

        Usuario atualizado = usuarioRepository.save(usuario);

        auditoriaFacade.usuarioAtualizado(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);

        Long usuarioId = usuario.getId();

        usuarioRepository.delete(usuario);

        auditoriaFacade.usuarioDeletado(usuarioId);
    }

    private void validarDocumentoCreate(UsuarioDTO dto) {
        if (usuarioRepository.existsByDocumento(dto.getDocumento())) {
            throw new ConflictException("CPF já cadastrado");
        }
    }

    private void validarDocumentoUpdate(Long id, UsuarioDTO dto) {
        if (usuarioRepository.existsByDocumentoAndIdNot(dto.getDocumento(), id)) {
            throw new ConflictException("CPF já cadastrado");
        }
    }

    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());
        Servidor servidor = servidorService.buscarServidorAutenticado();

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(ChronoLocalDate.from(LocalDate.now()))) {
            throw new BusinessException("A data de nascimento não pode ser futura");
        }

        Usuario entity = new Usuario();
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setTelefone(dto.getTelefone());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setDocumento(dto.getDocumento());
        entity.setSexo(dto.getSexo() != null ? dto.getSexo() : Sexo.NAO_INFORMADO);
        entity.setEndereco(endereco);

        geocodingService.preencherCoordenadas(entity.getEndereco());

        if(entity.getEndereco().getLatitude() == null || entity.getEndereco().getLongitude() == null){
            throw new BusinessException("Serviço não encontrado para esse endereço");
        }

        Servico servico = territorializacaoService.buscarUbsPorCoordenada(
                entity.getEndereco().getLatitude(),
                entity.getEndereco().getLongitude());

        if(servico == null){
            throw new BusinessException("Serviço não encontrado para esse endereço");
        }

        entity.setServico(servico);

        if(servidor.getPerfil() == PerfilServidor.SOLICITANTE){
            entity.setServicoSolicitante(servidor.getServico());
        }

        return entity;
    }

    private UsuarioResponseDTO toDTO(Usuario entity) {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setTelefone(entity.getTelefone());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setDocumento(entity.getDocumento());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        dto.setServicoId(entity.getServico().getId());
        dto.setServicoNome(entity.getServico().getNome());
        dto.setSexo(entity.getSexo());

        if(entity.getServicoSolicitante() != null){
            dto.setServicoSolicitanteId(entity.getServicoSolicitante().getId());
            dto.setServicoSolicitanteNome(entity.getServicoSolicitante().getNome());
        }

        return dto;
    }

    private UsuarioShortResponseDTO toShortDTO(Usuario entity) {

        UsuarioShortResponseDTO dto = new UsuarioShortResponseDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setDocumento(entity.getDocumento());
        dto.setServicoNome(entity.getServico().getNome());
        dto.setServicoId(entity.getServico().getId());


        if(entity.getServicoSolicitante() != null){
            dto.setServicoSolicitanteId(entity.getServicoSolicitante().getId());
            dto.setServicoSolicitanteNome(entity.getServicoSolicitante().getNome());
        }

        return dto;
    }
}