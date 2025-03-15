package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    public Optional<Todo> findByTitle(String title) {
        return Optional.ofNullable(jpaQueryFactory.select(todo)
                .from(todo)
                .where(todo.title.eq(title))
                .fetchOne());
    }

    @Override
    public Page<Todo> findBySearchKeyword(Pageable pageable, String title, LocalDate createdStartAt, LocalDate createdEndAt, String managerNickName) {
        List<Todo> results = jpaQueryFactory.select(todo)
                .from(todo)
                .join(todo.managers)
                .fetchJoin()
                .where(titleEq(title)
                        ,managerNickNameEq(managerNickName)
                        ,createdStartAtEq(createdStartAt)
                        ,createdEndAtEq(createdEndAt))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(todo.count())
                .from(todo)
                .where(titleEq(title),managerNickNameEq(managerNickName),createdStartAtEq(createdStartAt),createdEndAtEq(createdEndAt));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }


    private BooleanExpression titleEq(String title) {
        if (title == null) {
            return null;
        }
        return todo.title.contains(title);
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

        LocalDateTime startAt = createdStartAt.atStartOfDay();


        return todo.createdAt.goe( startAt ); // createdAt >= startAt
    }

    private BooleanExpression createdEndAtEq(LocalDate createdEndAt) {
        if (createdEndAt == null) {
            return null;
        }

        LocalDateTime endAt = createdEndAt.atTime(LocalTime.MAX);

        return todo.createdAt.loe(endAt); // createdAt <= endAt
    }

}
