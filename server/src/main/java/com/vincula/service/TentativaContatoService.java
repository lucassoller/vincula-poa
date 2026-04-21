package com.vincula.service;

import com.vincula.dto.TentativaContatoDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.TentativaContato;
import com.vincula.entity.Usuario;
import com.vincula.enums.StatusDemanda;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TentativaContatoService {

    private final TentativaContatoRepository tentativaRepository;
    private final DemandaRepository demandaRepository;
    private final UsuarioService usuarioService;

    public TentativaContatoService(TentativaContatoRepository tentativaRepository,
                                   DemandaRepository demandaRepository,
                                   UsuarioService usuarioService) {
        this.tentativaRepository = tentativaRepository;
        this.demandaRepository = demandaRepository;
        this.usuarioService = usuarioService;
    }

    public TentativaContatoDTO criar(TentativaContatoDTO dto) {

        TentativaContato entity = toEntity(dto);

        TentativaContato salvo = tentativaRepository.save(entity);
        return toDTO(salvo);
    }

    public List<TentativaContatoDTO> listarPorDemanda(Long id) {
        return tentativaRepository.findByDemandaId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TentativaContatoDTO> listarPorUsuario(Long id) {
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

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        TentativaContato entity = new TentativaContato();
        entity.setDemanda(demanda);
        entity.setUsuario(usuario);
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setDataHora(LocalDateTime.now());
        return entity;
    }

    private Demanda buscarDemandaPorId(Long id){
        return demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));
    }

    private TentativaContatoDTO toDTO(TentativaContato entity) {
        TentativaContatoDTO dto = new TentativaContatoDTO();

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