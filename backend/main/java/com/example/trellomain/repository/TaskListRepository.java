package com.example.trellomain.repository;

import com.example.trellomain.entity.TaskList;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByBoard_Id(Long boardId);
}
