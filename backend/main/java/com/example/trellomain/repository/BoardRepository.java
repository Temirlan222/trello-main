package com.example.trellomain.repository;

import com.example.trellomain.entity.Board;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
