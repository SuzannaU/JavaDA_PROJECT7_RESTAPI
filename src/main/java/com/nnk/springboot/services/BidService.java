package com.nnk.springboot.services;

import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.repositories.BidRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BidService {

    private final BidRepository bidRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public List<Bid> findAll() {
        return bidRepository.findAll();
    }

    public Bid save(Bid bid) {
        return bidRepository.save(bid);
    }

    public Bid findById(Integer id) {
        return bidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Bid Id: " + id));
    }

    public void delete(Bid bid) {
        bidRepository.delete(bid);
    }
}
