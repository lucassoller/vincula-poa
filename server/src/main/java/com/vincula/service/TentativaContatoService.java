package com.vincula.service;

import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.tentativaContato.TentativaContatoResponseDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.TentativaContato;
import com.vincula.entity.Servidor;
import com.vincula.enums.StatusDemanda;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TentativaContatoService {

    private final TentativaContatoRepository tentativaRepository;
    private final DemandaRepository demandaRepository;
    private final ServidorService servidorService;
    private final AuditoriaFacade auditoriaFacade;

    public TentativaContatoService(TentativaContatoRepository tentativaRepository,
                                   DemandaRepository demandaRepository,
                                   ServidorService servidorService,
                                   AuditoriaFacade auditoriaFacade) {
        this.tentativaRepository = tentativaRepository;
        this.demandaRepository = demandaRepository;
        this.servidorService = servidorService;
        this.auditoriaFacade = auditoriaFacade;
    }

    public TentativaContatoResponseDTO criar(TentativaContatoDTO dto) {
        TentativaContato entity = toEntity(dto);
        TentativaContato salvo = tentativaRepository.save(entity);
        auditoriaFacade.tentativaContatoCriada(salvo.getId(), salvo.getDemanda().getId());
        return toDTO(salvo);
    }

    public TentativaContatoResponseDTO atualizar(Long id, TentativaContatoDTO dto) {
        TentativaContato entity = buscarTentativaPorId(id);

        if (entity.getDemanda().getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível alterar tentativa de contato de uma demanda finalizada");
        }

        String descricaoLog = AuditoriaDescricaoUtil.tentativaContatoAtualizada(entity, dto);

        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());

        TentativaContato atualizado = tentativaRepository.save(entity);

        auditoriaFacade.tentativaContatoAtualizada(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        TentativaContato entity = buscarTentativaPorId(id);
        Long tentativaId = entity.getId();
        tentativaRepository.delete(entity);
        auditoriaFacade.tentativaContatoDeletada(tentativaId);
    }

    public List<TentativaContatoResponseDTO> listarPorDemanda(Long id) {
        return tentativaRepository.findByDemandaId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TentativaContatoResponseDTO> listarPorServidor(Long id) {
        return tentativaRepository.findByServidorId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Page<TentativaContatoResponseDTO> listarTodas(Pageable pageable) {
        return tentativaRepository.findAllByOrderByDemandaIdAsc(pageable)
                .map(this::toDTO);
    }


    private TentativaContato toEntity(TentativaContatoDTO dto){
        Demanda demanda = buscarDemandaPorId(dto.getDemandaId());

        if (demanda.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível registrar tentativa de contato em uma demanda finalizada");
        }

        Servidor servidor = servidorService.buscarServidorAutenticado();

        boolean primeiraTentativa = !tentativaRepository.existsByDemandaId(demanda.getId());

        TentativaContato entity = new TentativaContato();
        entity.setDemanda(demanda);
        entity.setServidor(servidor);
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setDataHora(LocalDateTime.now());

        if (primeiraTentativa && demanda.getStatus() == StatusDemanda.ABERTA) {
            StatusDemanda statusAnterior = demanda.getStatus();

            demanda.setStatus(StatusDemanda.EM_ANDAMENTO);
            demandaRepository.save(demanda);

            auditoriaFacade.statusDemandaAlterado(demanda.getId(), "Status alterado automaticamente de [" + statusAnterior + "] para [" + demanda.getStatus() + "] após primeira tentativa de contato");
        }

        return entity;
    }

    private TentativaContato buscarTentativaPorId(Long id) {
        return tentativaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tentativa de contato não encontrada"));
    }

    private Demanda buscarDemandaPorId(Long id){
        return demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));
    }

    private TentativaContatoResponseDTO toDTO(TentativaContato entity) {
        TentativaContatoResponseDTO dto = new TentativaContatoResponseDTO();

        dto.setId(entity.getId());
        dto.setDemandaId(entity.getDemanda().getId());
        dto.setTipo(entity.getTipo());
        dto.setDescricao(entity.getDescricao());
        dto.setDataHora(entity.getDataHora());
        dto.setServidorId(entity.getServidor().getId());
        dto.setServidorNome(entity.getServidor().getNome());

        return dto;
    }
}