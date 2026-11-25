package com.example.trellomain.repository;

import com.example.trellomain.entity.Card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
