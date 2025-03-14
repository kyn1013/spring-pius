package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;


public interface TodoRepositoryQuery {

    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    Page<Todo> findBySearch(Pageable pageable, String title, LocalDate createdStartAt, LocalDate createdEndAt, String managerNickName);
}
