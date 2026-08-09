package com.vincula.service;

import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.Servico;
import com.vincula.repository.TerritorioUbsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TerritorializacaoServiceTest {
    @Mock
    private TerritorioUbsRepository repository;

    @InjectMocks
    private TerritorializacaoService territorializacaoService;
}