package com.vincula.service;

import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.tentativaContato.TentativaContatoResponseDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.TentativaContato;
import com.vincula.entity.Usuario;
import com.vincula.enums.StatusDemanda;
import com.vincula.enums.TipoTentativaContato;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TentativaContatoService {

    private final TentativaContatoRepository tentativaRepository;
    private final DemandaRepository demandaRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaFacade auditoriaFacade;

    public TentativaContatoService(TentativaContatoRepository tentativaRepository,
                                   DemandaRepository demandaRepository,
                                   UsuarioService usuarioService,
                                   AuditoriaFacade auditoriaFacade) {
        this.tentativaRepository = tentativaRepository;
        this.demandaRepository = demandaRepository;
        this.usuarioService = usuarioService;
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

        validarTipoOutro(dto);

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
        auditoriaFacade.tentativaContatoVisualizado(0L);
        return tentativaRepository.findByDemandaId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TentativaContatoResponseDTO> listarPorUsuario(Long id) {
        auditoriaFacade.tentativaContatoVisualizado(0L);
        return tentativaRepository.findByUsuario(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }


    private TentativaContato toEntity(TentativaContatoDTO dto){
        Demanda demanda = buscarDemandaPorId(dto.getDemandaId());

        if (demanda.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível registrar tentativa de contato em uma demanda finalizada");
        }

        validarTipoOutro(dto);

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        boolean primeiraTentativa = !tentativaRepository.existsByDemandaId(demanda.getId());

        TentativaContato entity = new TentativaContato();
        entity.setDemanda(demanda);
        entity.setUsuario(usuario);
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

    private void validarTipoOutro(TentativaContatoDTO dto) {
        if (dto.getTipo() == TipoTentativaContato.OUTRO &&
                (dto.getDescricao() == null || dto.getDescricao().isBlank())) {
            throw new BusinessException("Descrição obrigatória para tipo OUTRO");
        }
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
        dto.setUsuarioId(entity.getUsuario().getId());
        dto.setUsuarioNome(entity.getUsuario().getNome());

        return dto;
    }
}