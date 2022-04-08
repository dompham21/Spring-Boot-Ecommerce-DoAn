package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Integer> {
    @Query("SELECT p FROM Poster p WHERE p.type = 'right'")
    public List<Poster> listPosterRight();

    @Query("SELECT p FROM Poster p WHERE p.type = 'right' AND p.isActive <> FALSE")
    public List<Poster> listPosterRightUser();

    @Query("SELECT p FROM Poster p WHERE p.type = 'left'")
    public List<Poster> listPosterLeft();

    @Query("SELECT p FROM Poster p WHERE p.type = 'left' AND p.isActive <> FALSE")
    public List<Poster> listPosterLeftUser();

    @Query("SELECT p FROM Poster p WHERE p.type = :type AND p.id = :id")
    public Poster getPosterByIdAndType(Integer id, String type);

    @Query("SELECT COUNT(p.id) from Poster p WHERE p.id = :id")
    public Long countById(Integer id);
}
