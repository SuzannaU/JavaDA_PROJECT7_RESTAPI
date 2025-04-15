package com.nnk.springboot.serviceTests;

import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.repositories.BidRepository;
import com.nnk.springboot.services.BidService;
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

@SpringBootTest
public class BidServiceTest {

    @Autowired
    private BidService bidService;

    @MockitoBean
    private BidRepository bidRepository;

    @Test
    public void findAll_shouldReturnList() {
        when(bidRepository.findAll()).thenReturn(new ArrayList<>());

        List<Bid> bids = bidService.findAll();

        assertNotNull(bids);
        verify(bidRepository).findAll();
    }

    @Test
    public void save_shouldReturnSavedBid() {
        Bid bid = new Bid("account", "type", 10d);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));

        Bid savedBid = bidService.save(bid);

        assertNotNull(savedBid);
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    public void findById_shouldReturnBid() {
        when(bidRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Bid("account", "type", 10d)));

        Bid bid = bidService.findById(1);

        assertNotNull(bid);
        verify(bidRepository).findById(anyInt());
    }

    @Test
    public void findById_withInvalidId_shouldThrowException() {
        when(bidRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bidService.findById(1));

        verify(bidRepository).findById(anyInt());
    }

    @Test
    public void delete_shouldCallRepo() {
        Bid bid = new Bid("account", "type", 10d);
        doNothing().when(bidRepository).delete(any(Bid.class));

        bidService.delete(bid);

        verify(bidRepository).delete(bid);
    }
}
