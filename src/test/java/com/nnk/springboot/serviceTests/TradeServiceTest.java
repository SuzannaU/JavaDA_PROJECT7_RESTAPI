package com.nnk.springboot.serviceTests;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.TradeService;
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
public class TradeServiceTest {

    @Autowired
    private TradeService tradeService;

    @MockitoBean
    private TradeRepository tradeRepository;

    @Test
    public void findAll_shouldReturnList() {
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());

        List<Trade> trades = tradeService.findAll();

        assertNotNull(trades);
        verify(tradeRepository).findAll();
    }

    @Test
    public void save_shouldReturnSavedTrade() {
        Trade trade = new Trade("account", "type", 10d);
        when(tradeRepository.save(any(Trade.class))).thenAnswer(i -> i.getArgument(0));

        Trade savedTrade = tradeService.save(trade);

        assertNotNull(savedTrade);
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    public void findById_shouldReturnTrade() {
        when(tradeRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Trade("account", "type", 10d)));

        Trade trade = tradeService.findById(1);

        assertNotNull(trade);
        verify(tradeRepository).findById(anyInt());
    }

    @Test
    public void findById_withInvalidId_shouldThrowException() {
        when(tradeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tradeService.findById(1));

        verify(tradeRepository).findById(anyInt());
    }

    @Test
    public void delete_shouldCallRepo() {
        Trade trade = new Trade("account", "type", 10d);
        doNothing().when(tradeRepository).delete(any(Trade.class));

        tradeService.delete(trade);

        verify(tradeRepository).delete(trade);
    }
}
