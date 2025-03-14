package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        // static으로 Q객체를 import 해도 되고, 그냥 생성해서 사용해도 됨
//        QTodo todo = QTodo.todo;

        return Optional.ofNullable(jpaQueryFactory
                .select(todo)
                .from(todo)
                .where(todo.id.eq(todoId))
                .leftJoin(todo.user)
                .fetchJoin()
                .fetchOne());
    }

    @Override
    public Page<Todo> findBySearch(Pageable pageable, String title, LocalDate createdStartAt, LocalDate createdEndAt, String managerNickName) {
        var query = jpaQueryFactory.select(todo)
                .from(todo)
                .join(todo.managers)
                .fetchJoin()
                .join(todo.comments)
                .fetchJoin()
                .where(titleEq(title)
                        , managerNickNameEq(managerNickName)
                        , createdStartAtEq(createdStartAt)
                        , createdEndAtEq(createdEndAt))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        var todos = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(todo.count())
                .from(todo)
                .where(titleEq(title)
                        , managerNickNameEq(managerNickName)
                        , createdStartAtEq(createdStartAt)
                        , createdEndAtEq(createdEndAt));

        return PageableExecutionUtils.getPage(todos, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleEq(String title) {
        if (title == null) {
            return null;
        }
        return todo.title.like("%" + title + "%");
    }

    private BooleanExpression managerNickNameEq(String managerNickName) {
        if (managerNickName == null) {
            return null;
        }
        return todo.managers.any().user.nickName.like("%" + managerNickName +"%");
    }

    private BooleanExpression createdStartAtEq(LocalDate createdStartAt) {
        if (createdStartAt == null) {
            return null;
        }

        LocalDateTime startAt = createdStartAt.atTime(LocalTime.MAX);

        return todo.createdAt.goe(startAt); // createdAt >= startAt
    }

    private BooleanExpression createdEndAtEq(LocalDate createdEndAt) {
        if (createdEndAt == null) {
            return null;
        }

        LocalDateTime endAt = createdEndAt.atStartOfDay();

        return todo.createdAt.loe(endAt); // createdAt <= endAt
    }
}
