package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.repository.TodoRepositoryQuery;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(), user.getNickName())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate updatedStartAt, LocalDate updatedEndAt) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> todos = null;
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (updatedStartAt != null && updatedEndAt != null) {
            start = updatedStartAt.atStartOfDay();
            end = updatedEndAt.atStartOfDay();
        } else if (updatedStartAt != null && updatedEndAt == null) {
            start = updatedStartAt.atStartOfDay();
        } else if (updatedStartAt == null && updatedEndAt != null) {
            end = updatedEndAt.atStartOfDay();
        }

        System.out.println(weather);
        System.out.println(updatedStartAt);
        System.out.println(updatedEndAt);

        if (weather == null && updatedStartAt == null && updatedEndAt == null){
            todos = todoRepository.findAll(pageable);
        } else if (weather != null && updatedStartAt == null && updatedEndAt == null) {
            todos = todoRepository.findAllByWeather(pageable, weather);
        } else if (weather == null && updatedStartAt != null && updatedEndAt == null) {
            todos = todoRepository.findAllByStartTime(pageable, start);
        } else if (weather == null && updatedStartAt == null && updatedEndAt != null) {
            todos = todoRepository.findAllByEndTime(pageable, end);
        } else if (weather != null && updatedStartAt != null && updatedEndAt == null) {
            todos = todoRepository.findAllByWeatherAndStartTime(pageable, weather, start);
        } else if (weather != null && updatedStartAt == null && updatedEndAt != null) {
            todos = todoRepository.findAllByWeatherAndEndTime(pageable, weather, end);
        } else if (weather == null && updatedStartAt != null && updatedEndAt != null) {
            todos = todoRepository.findAllByStartTimeAndEndTime(pageable, start, end);
        } else {
            todos = todoRepository.findAllByWeatherAndStartTimeAndEndTime(pageable, weather, start, end);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickName()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail(), user.getNickName()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
