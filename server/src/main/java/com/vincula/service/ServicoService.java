package com.vincula.service;

import com.vincula.dto.servico.*;
import com.vincula.dto.usuario.UsuarioResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Usuario;
import com.vincula.entity.Servico;
import com.vincula.enums.TipoServico;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.ServicoRepository;
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
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaFacade auditoriaFacade;

    public ServicoService(ServicoRepository servicoRepository,
                               EnderecoMapper enderecoMapper,
                               AuditoriaFacade auditoriaFacade) {
        this.servicoRepository = servicoRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaFacade = auditoriaFacade;
    }

    public ServicoResponseDTO criar(ServicoDTO dto) {
        validarCnesCreate(dto);
        Servico entity = toEntity(dto);
        Servico salvo = servicoRepository.save(entity);
        auditoriaFacade.servicoCriado(salvo.getId());
        return toDTO(salvo);
    }

    public List<ServicoShortResponseDTO> listarTodasServicos() {
        return servicoRepository.findAllByTipoServicoOrderByNomeAsc(TipoServico.UBS)
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public List<ServicoShortResponseDTO> listarTodosServicos() {
        return servicoRepository.findAllByOrderByTipoServicoAndNomeAsc()
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public Page<ServicoResponseDTO> listarTodosFiltrados(
            FiltroServicoRequestDTO filtro,
            Pageable pageable) {

        Specification<Servico> specification =
                ServicoSpecification.comFiltros(filtro);

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("nome")
        );

        return servicoRepository.findAll(specification, pageableOrdenado)
                .map(this::toDTO);
    }

    public ServicosResponseDTO listarServicos() {

        List<Servico> todas = servicoRepository.findAllByOrderByTipoServicoAndNomeAsc();

        List<ServicoShortResponseDTO> todasDto = new ArrayList<>();
        List<ServicoShortResponseDTO> ubs = new ArrayList<>();
        List<ServicoShortResponseDTO> servicos = new ArrayList<>();
        List<ServicoShortResponseDTO> outros = new ArrayList<>();
        List<ServicoShortResponseDTO> especializados = new ArrayList<>();

        for (Servico servico : todas) {

            ServicoShortResponseDTO dto = toShortDTO(servico);

            todasDto.add(dto);

            if (servico.getTipoServico() == TipoServico.UBS) {
                ubs.add(dto);
            } else {
                servicos.add(dto);
                if (servico.getTipoServico() == TipoServico.OUTRO) {
                    outros.add(dto);
                }else{
                    especializados.add(dto);
                }
            }
        }

        return new ServicosResponseDTO(
                todasDto,
                ubs,
                servicos,
                outros,
                especializados
        );
    }

    public ServicoResponseDTO buscarPorId(Long id) {
        Servico entity = buscarServicoPorId(id);
        return toDTO(entity);
    }

    public ServicoResponseDTO buscarPorCnes(String cnes) {
        Servico entity = buscarServicoPorCnes(cnes);
        return toDTO(entity);
    }

    public ServicoResponseDTO atualizar(Long id, ServicoDTO dto) {
        Servico entity = buscarServicoPorId(id);
        validarCnesUpdate(dto, id);
        String descricaoLog = AuditoriaDescricaoUtil.servicoAtualizada(entity, dto);

        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());
        entity.setTipoServico(dto.getTipoServico());
        enderecoMapper.updateEntityFromDto(dto.getEndereco(), entity.getEndereco());
        Servico atualizado = servicoRepository.save(entity);
        auditoriaFacade.servicoAtualizado(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Servico entity = buscarServicoPorId(id);

        Long servicoId = entity.getId();

        servicoRepository.delete(entity);

        auditoriaFacade.servicoDeletado(servicoId);
    }

    private void validarCnesCreate(ServicoDTO dto) {
        if (servicoRepository.existsByCnes(dto.getCnes())) {
            throw new ConflictException("CNES já cadastrado");
        }
    }

    private void validarCnesUpdate(ServicoDTO dto, Long id) {
        if (servicoRepository.existsByCnesAndIdNot(dto.getCnes(), id)) {
            throw new ConflictException("CNES já cadastrado");
        }
    }

    private Servico buscarServicoPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    private Servico buscarServicoPorCnes(String cnes){
        return servicoRepository.findByCnes(cnes)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    public Servico toEntity(ServicoDTO dto){
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());

        Servico entity = new Servico();
        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());
        entity.setTelefone2(dto.getTelefone2());
        entity.setEndereco(endereco);
        entity.setTipoServico(dto.getTipoServico());

        return entity;
    }

    public ServicoResponseDTO toDTO(Servico entity) {
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        dto.setTelefone(entity.getTelefone());
        dto.setTelefone2(entity.getTelefone2());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        dto.setTipoServico(entity.getTipoServico());
        return dto;
    }


    public ServicoShortResponseDTO toShortDTO(Servico entity) {
        ServicoShortResponseDTO dto = new ServicoShortResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        return dto;
    }
}