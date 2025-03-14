package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.todo.entity.Todo;

@Getter
public class TodoSearchResponse {

    private final String title;
    private final Long managerCount;
    private final Long commentCount;

    public TodoSearchResponse(String title, Long managerCount, Long commentCount) {
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }

    public static TodoSearchResponse from(Todo todo) {
        return new TodoSearchResponse(todo.getTitle(), (long)todo.getManagers().size(), (long)todo.getComments().size());
    }
}
