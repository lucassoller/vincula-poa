package com.vincula.service;

import com.vincula.dto.TentativaContatoDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.TentativaContato;
import com.vincula.entity.Usuario;
import com.vincula.enums.StatusDemanda;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.enums.TipoTentativaContato;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TentativaContatoService {

    private final TentativaContatoRepository tentativaRepository;
    private final DemandaRepository demandaRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public TentativaContatoService(TentativaContatoRepository tentativaRepository,
                                   DemandaRepository demandaRepository,
                                   UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.tentativaRepository = tentativaRepository;
        this.demandaRepository = demandaRepository;
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    public TentativaContatoDTO criar(TentativaContatoDTO dto) {

        TentativaContato entity = toEntity(dto);

        TentativaContato salvo = tentativaRepository.save(entity);
        auditoriaService.registrar(
                TipoAcaoAuditoria.TENTATIVA_CONTATO_CRIADA,
                "TentativaContato",
                salvo.getId(),
                "Tentativa de contato registrada para demanda ID " + salvo.getDemanda().getId()
        );
        return toDTO(salvo);
    }

    public TentativaContatoDTO atualizar(Long id, TentativaContatoDTO dto) {
        TentativaContato entity = buscarTentativaPorId(id);

        if (entity.getDemanda().getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível alterar tentativa de contato de uma demanda finalizada");
        }

        validarTipoOutro(dto);

        String descricaoLog = AuditoriaDescricaoUtil.tentativaContatoAtualizada(entity, dto);

        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());

        TentativaContato atualizado = tentativaRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.TENTATIVA_CONTATO_ATUALIZADA,
                "TentativaContato",
                atualizado.getId(),
                descricaoLog
        );

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        TentativaContato entity = buscarTentativaPorId(id);
        Long tentativaId = entity.getId();

        tentativaRepository.delete(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.ENDERECO_DELETADO,
                "TentativaContato",
                tentativaId,
                "Tentativa de contato deletada"
        );
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
            demanda.setStatus(StatusDemanda.EM_ANDAMENTO);
            demandaRepository.save(demanda);
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