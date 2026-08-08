package com.vincula.service;

import com.vincula.dto.senha.MudancaSenhaDTO;
import com.vincula.dto.servidor.*;
import com.vincula.entity.Servidor;
import com.vincula.entity.Servico;
import com.vincula.enums.PerfilServidor;
import com.vincula.enums.TipoServico;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.ServidorRepository;
import com.vincula.repository.ServicoRepository;
import com.vincula.security.SecurityUtils;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServidorServiceTest {

    @Mock
    private ServidorRepository servidorRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private ServidorService servidorService;

    @Test
    void deveLancarBusinessExceptionQuandoSenhaAtualInvalida() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");
        servidor.setSenhaHash("hash");

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("123");
        dto.setNovaSenha("456");
        dto.setConfirmarSenha("456");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(passwordEncoder.matches("123", "hash"))
                    .thenReturn(false);

            assertThrows(
                    BusinessException.class,
                    () -> servidorService.atualizarMinhaSenha(dto)
            );
        }
    }

    @Test
    void deveLancarBusinessExceptionQuandoNovaSenhaNaoCoincide() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");
        servidor.setSenhaHash("hash");

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("123");
        dto.setNovaSenha("456");
        dto.setConfirmarSenha("789");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(passwordEncoder.matches("123", "hash"))
                    .thenReturn(true);

            assertThrows(
                    BusinessException.class,
                    () -> servidorService.atualizarMinhaSenha(dto)
            );
        }
    }

    @Test
    void deveAtualizarMinhaSenhaComSucesso() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");
        servidor.setSenhaHash("hash");

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("123");
        dto.setNovaSenha("456");
        dto.setConfirmarSenha("456");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(passwordEncoder.matches("123", "hash"))
                    .thenReturn(true);

            when(passwordEncoder.encode("456"))
                    .thenReturn("novoHash");

            when(servidorRepository.save(any()))
                    .thenAnswer(i -> i.getArgument(0));

            servidorService.atualizarMinhaSenha(dto);

            assertEquals("novoHash", servidor.getSenhaHash());

            verify(auditoriaFacade)
                    .servidorAtualizado(1L, "Senha atualizada");
        }
    }

    @Test
    void deveLancarBusinessExceptionQuandoAlterarSenhaDeOutroServidor() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");

        MudancaSenhaDTO dto = new MudancaSenhaDTO();

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            assertThrows(
                    BusinessException.class,
                    () -> servidorService.alterarSenha(2L, dto)
            );
        }
    }

    @Test
    void deveLancarBusinessExceptionQuandoSenhaAtualInvalidaAoAlterarSenha() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");
        servidor.setSenhaHash("hash");

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("123");
        dto.setNovaSenha("456");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(passwordEncoder.matches("123", "hash"))
                    .thenReturn(false);

            assertThrows(
                    BusinessException.class,
                    () -> servidorService.alterarSenha(1L, dto)
            );
        }
    }

    @Test
    void deveAlterarSenhaComSucesso() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");
        servidor.setSenhaHash("hash");

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("123");
        dto.setNovaSenha("456");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(passwordEncoder.matches("123", "hash"))
                    .thenReturn(true);

            when(passwordEncoder.encode("456"))
                    .thenReturn("novoHash");

            when(servidorRepository.save(any()))
                    .thenAnswer(i -> i.getArgument(0));

            servidorService.alterarSenha(1L, dto);

            assertEquals("novoHash", servidor.getSenhaHash());

            verify(auditoriaFacade)
                    .servidorSenhaAlteradaLogado(1L);
        }
    }

    @Test
    void deveLancarBusinessExceptionQuandoNaoHaLoginAutenticado() {

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn(null);

            assertThrows(
                    BusinessException.class,
                    () -> servidorService.buscarServidorAutenticado()
            );
        }
    }

    @Test
    void deveLancarNotFoundQuandoServidorAutenticadoNaoExiste() {

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    NotFoundException.class,
                    () -> servidorService.buscarServidorAutenticado()
            );
        }
    }

    @Test
    void deveBuscarServidorAutenticado() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            Servidor resultado = servidorService.buscarServidorAutenticado();

            assertEquals(1L, resultado.getId());
        }
    }

    @Test
    void deveLancarConflictExceptionQuandoEmailJaExiste() {

        ServidorDTO dto = new ServidorDTO();
        dto.setEmail("teste@email.com");

        when(servidorRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> servidorService.criar(dto)
        );
    }

    @Test
    void deveLancarConflictExceptionQuandoLoginJaExiste() {

        ServidorDTO dto = new ServidorDTO();
        dto.setEmail("teste@email.com");
        dto.setLogin("login");

        when(servidorRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);

        when(servidorRepository.existsByLogin(dto.getLogin()))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> servidorService.criar(dto)
        );
    }

    @Test
    void deveLancarBusinessExceptionQuandoSenhasNaoCoincidem() {

        ServidorDTO dto = new ServidorDTO();

        dto.setSenha("123");
        dto.setConfirmarSenha("456");

        when(servidorRepository.existsByEmail(any()))
                .thenReturn(false);

        when(servidorRepository.existsByLogin(any()))
                .thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> servidorService.criar(dto)
        );
    }

    @Test
    void deveCriarServidor() {

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setNome("UBS Centro");
        servico.setTipoServico(TipoServico.UBS);

        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setServicoId(10L);

        when(servidorRepository.existsByEmail(any()))
                .thenReturn(false);

        when(servidorRepository.existsByLogin(any()))
                .thenReturn(false);

        when(passwordEncoder.encode(any()))
                .thenReturn("hash");

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        when(servidorRepository.save(any()))
                .thenAnswer(i -> {
                    Servidor s = i.getArgument(0);
                    s.setId(1L);
                    return s;
                });

        ServidorResponseDTO response =
                servidorService.criar(dto);

        assertEquals(1L, response.getId());

        verify(auditoriaFacade)
                .servidorCriado(1L);
    }

    @Test
    void deveBuscarPorId() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        ServidorResponseDTO dto =
                servidorService.buscarPorId(1L);

        assertEquals(1L, dto.getId());
    }

    @Test
    void deveLancarNotFoundAoBuscarPorId() {

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> servidorService.buscarPorId(1L)
        );
    }

    @Test
    void deveBuscarPorEmail() {

        Servidor servidor = new Servidor();

        when(servidorRepository.findByEmail("a@a.com"))
                .thenReturn(Optional.of(servidor));

        assertNotNull(
                servidorService.buscarPorEmail("a@a.com")
        );
    }

    @Test
    void deveLancarNotFoundAoBuscarPorEmail() {

        when(servidorRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> servidorService.buscarPorEmail("x")
        );
    }

    @Test
    void deveBuscarPorLogin() {

        Servidor servidor = new Servidor();

        when(servidorRepository.findByLogin("login"))
                .thenReturn(Optional.of(servidor));

        assertNotNull(
                servidorService.buscarPorLogin("login")
        );
    }

    @Test
    void deveLancarNotFoundAoBuscarPorLogin() {

        when(servidorRepository.findByLogin(any()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> servidorService.buscarPorLogin("x")
        );
    }

    @Test
    void deveDeletarServidor() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        servidorService.deletar(1L);

        verify(servidorRepository)
                .delete(servidor);

        verify(auditoriaFacade)
                .servidorDeletado(1L);
    }

    @Test
    void deveAtualizarServidorComSenha() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setNome("UBS Centro");
        servico.setTipoServico(TipoServico.UBS);

        ServidorDTO dto = new ServidorDTO();
        dto.setNome("Novo Nome");
        dto.setEmail("novo@email.com");
        dto.setLogin("novo");
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setAtivo(true);
        dto.setSenha("123456");
        dto.setServicoId(10L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        when(passwordEncoder.encode("123456"))
                .thenReturn("hash");

        when(servidorRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServidorResponseDTO response =
                servidorService.atualizar(1L, dto);

        assertEquals("Novo Nome", response.getNome());

        verify(passwordEncoder)
                .encode("123456");
    }

    @Test
    void naoDeveAlterarSenhaQuandoSenhaForNula() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        Servico servico = new Servico();
        servico.setTipoServico(TipoServico.UBS);
        servico.setId(10L);

        ServidorDTO dto = new ServidorDTO();
        dto.setNome("Novo");
        dto.setEmail("novo@email.com");
        dto.setLogin("novo");
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setAtivo(true);
        dto.setServicoId(10L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        when(servidorRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        servidorService.atualizar(1L, dto);

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void deveLancarConflictQuandoEmailJaExisteNoUpdate() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        ServidorDTO dto = new ServidorDTO();
        dto.setEmail("teste@email.com");

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servidorRepository.existsByEmailAndIdNot(
                dto.getEmail(),
                1L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> servidorService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarConflictQuandoLoginJaExisteNoUpdate() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        ServidorDTO dto = new ServidorDTO();
        dto.setEmail("email@email.com");
        dto.setLogin("login");

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servidorRepository.existsByEmailAndIdNot(any(), eq(1L)))
                .thenReturn(false);

        when(servidorRepository.existsByLoginAndIdNot(any(), eq(1L)))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> servidorService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarExceptionQuandoGestaoMunicipalPossuiServico() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);
        dto.setServicoId(10L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        assertThrows(
                BusinessException.class,
                () -> servidorService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarExceptionQuandoServidorApsSemServico() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        assertThrows(
                BusinessException.class,
                () -> servidorService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarNotFoundQuandoServicoNaoExiste() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setServicoId(99L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servicoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> servidorService.atualizar(1L, dto)
        );
    }

    @Test
    void deveRetornarServidorSemServico() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");
        servidor.setEmail("a@a.com");
        servidor.setLogin("lucas");
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);
        servidor.setAtivo(true);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        ServidorResponseDTO dto =
                servidorService.buscarPorId(1L);

        assertNull(dto.getServicoId());
        assertNull(dto.getServicoNome());
    }

    @Test
    void deveRetornarServidorAutenticadoDto() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setLogin("lucas");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            ServidorResponseDTO dto =
                    servidorService.getServidorAutenticadoDTO();

            assertEquals(1L, dto.getId());
        }
    }

    @Test
    void deveAtualizarMeuPerfil() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Antigo");
        servidor.setLogin("antigo");
        servidor.setEmail("antigo@email.com");

        MeuPerfilDTO dto = new MeuPerfilDTO();
        dto.setNome("Novo");
        dto.setLogin("novo");
        dto.setEmail("novo@email.com");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(servidorRepository.existsByEmailAndIdNot(
                    dto.getEmail(), 1L))
                    .thenReturn(false);

            when(servidorRepository.existsByLoginAndIdNot(
                    dto.getLogin(), 1L))
                    .thenReturn(false);

            when(servidorRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ServidorResponseDTO response =
                    servidorService.atualizarMeuPerfil(dto);

            assertEquals("Novo", response.getNome());
            assertEquals("novo", response.getLogin());
            assertEquals("novo@email.com", response.getEmail());

            verify(auditoriaFacade)
                    .servidorAtualizado(eq(1L), anyString());
        }
    }

    @Test
    void deveLancarConflictExceptionQuandoEmailJaExisteAoAtualizarPerfil() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        MeuPerfilDTO dto = new MeuPerfilDTO();
        dto.setEmail("email@email.com");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(servidorRepository.existsByEmailAndIdNot(
                    dto.getEmail(), 1L))
                    .thenReturn(true);

            assertThrows(
                    ConflictException.class,
                    () -> servidorService.atualizarMeuPerfil(dto)
            );
        }
    }

    @Test
    void deveLancarConflictExceptionQuandoLoginJaExisteAoAtualizarPerfil() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        MeuPerfilDTO dto = new MeuPerfilDTO();
        dto.setEmail("email@email.com");
        dto.setLogin("login");

        try (MockedStatic<SecurityUtils> mocked =
                     Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getLoginServidorLogado)
                    .thenReturn("lucas");

            when(servidorRepository.findByLogin("lucas"))
                    .thenReturn(Optional.of(servidor));

            when(servidorRepository.existsByEmailAndIdNot(
                    dto.getEmail(), 1L))
                    .thenReturn(false);

            when(servidorRepository.existsByLoginAndIdNot(
                    dto.getLogin(), 1L))
                    .thenReturn(true);

            assertThrows(
                    ConflictException.class,
                    () -> servidorService.atualizarMeuPerfil(dto)
            );
        }
    }

    @Test
    void deveLancarExcecaoQuandoServidorForGestaoMunicipal() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        TransferirServidorDTO dto = new TransferirServidorDTO();
        dto.setPerfil(PerfilServidor.SOLICITANTE);
        dto.setServicoId(1L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> servidorService.transferirServidor(1L, dto)
        );

        assertEquals(
                "Não é possível transferir um servidor do tipo gestão",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoNovoPerfilForGestaoMunicipal() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setPerfil(PerfilServidor.SOLICITANTE);

        TransferirServidorDTO dto = new TransferirServidorDTO();
        dto.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);
        dto.setServicoId(1L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> servidorService.transferirServidor(1L, dto)
        );

        assertEquals(
                "Não é possível mudar o perfil de um servidor para o tipo gestão",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoSolicitanteForVinculadoEmUbs() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setPerfil(PerfilServidor.SOLICITANTE);

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setTipoServico(TipoServico.UBS);

        TransferirServidorDTO dto = new TransferirServidorDTO();
        dto.setPerfil(PerfilServidor.SOLICITANTE);
        dto.setServicoId(10L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> servidorService.transferirServidor(1L, dto)
        );

        assertEquals(
                "Solicitante só pode ser vinculado a serviço do tipo outro ou serviço especializado",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorApsForVinculadoEmServicoOutro() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setTipoServico(TipoServico.OUTRO);

        TransferirServidorDTO dto = new TransferirServidorDTO();
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setServicoId(10L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> servidorService.transferirServidor(1L, dto)
        );

        assertEquals(
                "Servidor APS só pode ser vinculado a serviço do tipo UBS",
                ex.getMessage()
        );
    }

    @Test
    void deveTransferirServidorComSucesso() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setPerfil(PerfilServidor.SOLICITANTE);

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setTipoServico(TipoServico.UBS);

        TransferirServidorDTO dto = new TransferirServidorDTO();
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setServicoId(10L);

        when(servidorRepository.findById(1L))
                .thenReturn(Optional.of(servidor));

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        when(servidorRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        ServidorResponseDTO response =
                servidorService.transferirServidor(1L, dto);

        assertEquals(
                PerfilServidor.SERVIDOR_APS,
                response.getPerfil()
        );

        verify(auditoriaFacade)
                .servidorAtualizado(eq(1L), anyString());
    }

    @Test
    void deveLancarExcecaoQuandoSolicitanteForVinculadoEmUbs2() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setTipoServico(TipoServico.UBS);

        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.SOLICITANTE);
        dto.setServicoId(1L);

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> servidorService.criar(dto)
        );

        assertEquals(
                "Solicitante só pode ser vinculado a serviço do tipo outro ou serviço especializado",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorApsForVinculadoEmServicoOutro2() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setTipoServico(TipoServico.OUTRO);

        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);
        dto.setServicoId(1L);

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> servidorService.criar(dto)
        );

        assertEquals(
                "Servidor APS só pode ser vinculado a serviço do tipo UBS",
                ex.getMessage()
        );
    }

    @Test
    void deveCriarGestaoMunicipalSemServico() {
        ServidorDTO dto = new ServidorDTO();
        dto.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);
        dto.setServicoId(null);

        Servidor servidorSalvo = new Servidor();
        servidorSalvo.setId(1L);

        when(servidorRepository.save(any(Servidor.class)))
                .thenReturn(servidorSalvo);

        ServidorResponseDTO response = servidorService.criar(dto);

        assertNotNull(response);
        assertNull(response.getServicoId());
    }
}