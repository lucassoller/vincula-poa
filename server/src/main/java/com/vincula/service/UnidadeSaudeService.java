package com.vincula.service;

import com.vincula.dto.unidadeSaude.*;
import com.vincula.dto.usuario.UsuarioResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Usuario;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.TipoServico;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.specification.ServicoSpecification;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaFacade auditoriaFacade;

    public UnidadeSaudeService(UnidadeSaudeRepository unidadeSaudeRepository,
                               EnderecoMapper enderecoMapper,
                               AuditoriaFacade auditoriaFacade) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaFacade = auditoriaFacade;
    }

    public UnidadeSaudeResponseDTO criar(UnidadeSaudeDTO dto) {
        validarCnesCreate(dto);
        UnidadeSaude entity = toEntity(dto);
        UnidadeSaude salvo = unidadeSaudeRepository.save(entity);
        auditoriaFacade.unidadeSaudeCriada(salvo.getId());
        return toDTO(salvo);
    }

    public List<UnidadeSaudeShortResponseDTO> listarTodasUnidades() {
        return unidadeSaudeRepository.findAllByTipoServicoOrderByNomeAsc(TipoServico.UBS)
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public List<UnidadeSaudeShortResponseDTO> listarTodosServicos() {
        return unidadeSaudeRepository.findAllByOrderByTipoServicoAndNomeAsc()
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public Page<UnidadeSaudeResponseDTO> listarTodosFiltrados(
            FiltroServicoRequestDTO filtro,
            Pageable pageable) {

        Specification<UnidadeSaude> specification =
                ServicoSpecification.comFiltros(filtro);

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("nome")
        );

        return unidadeSaudeRepository.findAll(specification, pageableOrdenado)
                .map(this::toDTO);
    }

    public UnidadesResponseDTO listarServicos() {

        List<UnidadeSaude> todas = unidadeSaudeRepository.findAllByOrderByTipoServicoAndNomeAsc();

        List<UnidadeSaudeShortResponseDTO> todasDto = new ArrayList<>();
        List<UnidadeSaudeShortResponseDTO> ubs = new ArrayList<>();
        List<UnidadeSaudeShortResponseDTO> servicos = new ArrayList<>();
        List<UnidadeSaudeShortResponseDTO> outros = new ArrayList<>();
        List<UnidadeSaudeShortResponseDTO> especializados = new ArrayList<>();

        for (UnidadeSaude unidade : todas) {

            UnidadeSaudeShortResponseDTO dto = toShortDTO(unidade);

            todasDto.add(dto);

            if (unidade.getTipoServico() == TipoServico.UBS) {
                ubs.add(dto);
            } else {
                servicos.add(dto);
                if (unidade.getTipoServico() == TipoServico.OUTRO) {
                    outros.add(dto);
                }else{
                    especializados.add(dto);
                }
            }
        }

        return new UnidadesResponseDTO(
                todasDto,
                ubs,
                servicos,
                outros,
                especializados
        );
    }

    public UnidadeSaudeResponseDTO buscarPorId(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);
        return toDTO(entity);
    }

    public UnidadeSaudeResponseDTO buscarPorCnes(String cnes) {
        UnidadeSaude entity = buscarUnidadeSaudePorCnes(cnes);
        return toDTO(entity);
    }

    public UnidadeSaudeResponseDTO atualizar(Long id, UnidadeSaudeDTO dto) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);
        validarCnesUpdate(dto, id);
        String descricaoLog = AuditoriaDescricaoUtil.unidadeSaudeAtualizada(entity, dto);

        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());
        entity.setTipoServico(dto.getTipoServico());
        enderecoMapper.updateEntityFromDto(dto.getEndereco(), entity.getEndereco());
        UnidadeSaude atualizado = unidadeSaudeRepository.save(entity);
        auditoriaFacade.unidadeSaudeAtualizada(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        Long unidadeId = entity.getId();

        unidadeSaudeRepository.delete(entity);

        auditoriaFacade.unidadeSaudeDeletada(unidadeId);
    }

    private void validarCnesCreate(UnidadeSaudeDTO dto) {
        if (unidadeSaudeRepository.existsByCnes(dto.getCnes())) {
            throw new ConflictException("CNES já cadastrado");
        }
    }

    private void validarCnesUpdate(UnidadeSaudeDTO dto, Long id) {
        if (unidadeSaudeRepository.existsByCnesAndIdNot(dto.getCnes(), id)) {
            throw new ConflictException("CNES já cadastrado");
        }
    }

    private UnidadeSaude buscarUnidadeSaudePorId(Long id) {
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    private UnidadeSaude buscarUnidadeSaudePorCnes(String cnes){
        return unidadeSaudeRepository.findByCnes(cnes)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    public UnidadeSaude toEntity(UnidadeSaudeDTO dto){
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());

        UnidadeSaude entity = new UnidadeSaude();
        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());
        entity.setTelefone2(dto.getTelefone2());
        entity.setEndereco(endereco);
        entity.setTipoServico(dto.getTipoServico());

        return entity;
    }

    public UnidadeSaudeResponseDTO toDTO(UnidadeSaude entity) {
        UnidadeSaudeResponseDTO dto = new UnidadeSaudeResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        dto.setTelefone(entity.getTelefone());
        dto.setTelefone2(entity.getTelefone2());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        dto.setTipoServico(entity.getTipoServico());
        return dto;
    }


    public UnidadeSaudeShortResponseDTO toShortDTO(UnidadeSaude entity) {
        UnidadeSaudeShortResponseDTO dto = new UnidadeSaudeShortResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        return dto;
    }

    private UsuarioResponseDTO toUsuarioDTO(Usuario entity) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setTelefone(entity.getTelefone());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setDocumento(entity.getDocumento());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
        dto.setUnidadeSaudeNome(entity.getUnidadeSaude().getNome());
        dto.setSexo(entity.getSexo());

        return dto;
    }
}