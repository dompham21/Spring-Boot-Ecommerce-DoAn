package com.luv2code.doan.service;

import com.luv2code.doan.entity.Poster;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.PosterNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.PosterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PosterService {
    private final Logger log = LoggerFactory.getLogger(PosterService.class);

    @Autowired
    private PosterRepository posterRepository;


    public List<Poster> listPosterRight() {
        return posterRepository.listPosterRight();
    }

    public List<Poster> listPosterLeft() {
        return posterRepository.listPosterLeft();
    }

    public List<Poster> listPosterRightUser() {
        return posterRepository.listPosterRightUser();
    }

    public List<Poster> listPosterLeftUser() {
        return posterRepository.listPosterLeftUser();
    }


    public Poster getPosterByIdAndType(Integer id, String type) throws PosterNotFoundException {
        try {
            return posterRepository.getPosterByIdAndType(id, type);

        }
        catch(NoSuchElementException ex) {
            throw new PosterNotFoundException("Could not find any poster with ID " + id);

        }
    }

    public Poster savePoster(Poster poster) {
        return posterRepository.save(poster);
    }

    public void deletePoster(Integer id) throws ProductNotFoundException {
        Long count = posterRepository.countById(id);
        if (count == null || count == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        posterRepository.deleteById(id);
    }

}
