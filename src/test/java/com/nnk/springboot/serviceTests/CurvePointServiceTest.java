package com.nnk.springboot.serviceTests;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.services.CurvePointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class CurvePointServiceTest {

    @Autowired
    private CurvePointService curvePointService;

    @MockitoBean
    private CurvePointRepository curvePointRepository;

    @Test
    public void findAll_shouldReturnList() {
        when(curvePointRepository.findAll()).thenReturn(new ArrayList<>());

        List<CurvePoint> curves = curvePointService.findAll();

        assertNotNull(curves);
        verify(curvePointRepository).findAll();
    }

    @Test
    public void save_shouldReturnSavedCurvePoint() {
        CurvePoint curvePoint = new CurvePoint(1, 1.0, 1.0);
        when(curvePointRepository.save(any(CurvePoint.class))).thenAnswer(i -> i.getArgument(0));

        CurvePoint savedCurvePoint = curvePointService.save(curvePoint);

        assertNotNull(savedCurvePoint);
        verify(curvePointRepository).save(any(CurvePoint.class));
    }

    @Test
    public void findById_shouldReturnCurvePoint() {
        when(curvePointRepository.findById(anyInt()))
                .thenReturn(Optional.of(new CurvePoint(1, 1.0, 1.0)));

        CurvePoint curvePoint = curvePointService.findById(1);

        assertNotNull(curvePoint);
        verify(curvePointRepository).findById(anyInt());
    }

    @Test
    public void findById_withInvalidId_shouldThrowException() {
        when(curvePointRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> curvePointService.findById(1));

        verify(curvePointRepository).findById(anyInt());
    }

    @Test
    public void delete_shouldCallRepo() {
        CurvePoint curvePoint = new CurvePoint(1, 1.0, 1.0);;
        doNothing().when(curvePointRepository).delete(any(CurvePoint.class));

        curvePointService.delete(curvePoint);

        verify(curvePointRepository).delete(curvePoint);
    }
}
