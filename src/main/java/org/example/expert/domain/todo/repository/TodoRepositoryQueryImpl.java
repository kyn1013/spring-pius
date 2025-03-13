package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

//import static org.example.expert.domain.todo.entity.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        // static으로 Q객체를 import 해도 되고, 그냥 생성해서 사용해도 됨
        QTodo todo = QTodo.todo;

        return Optional.ofNullable(jpaQueryFactory
                .select(todo)
                .from(todo)
                .where(todo.id.eq(todoId))
                .leftJoin(todo.user)
                .fetchJoin()
                .fetchOne());
    }
}
