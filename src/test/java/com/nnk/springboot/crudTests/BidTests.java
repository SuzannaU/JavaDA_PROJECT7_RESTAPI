package com.nnk.springboot.crudTests;

import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.repositories.BidRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BidTests {

    @Autowired
    private BidRepository bidRepository;

    @Test
    public void bidTest() {
        Bid bid = new Bid("Account Test", "Type Test", 10d);

        // Save
        bid = bidRepository.save(bid);
        assertNotNull(bid.getBidListId());
        assertEquals(10d, bid.getBidQuantity(), 10d);

        // Update
        bid.setBidQuantity(20d);
        bid = bidRepository.save(bid);
        assertEquals(20d, bid.getBidQuantity(), 20d);

        // Find
        List<Bid> listResult = bidRepository.findAll();
        assertTrue(listResult.size() > 0);

        // Delete
        Integer id = bid.getBidListId();
        bidRepository.delete(bid);
        Optional<Bid> optBid = bidRepository.findById(id);
        assertFalse(optBid.isPresent());
    }
}
