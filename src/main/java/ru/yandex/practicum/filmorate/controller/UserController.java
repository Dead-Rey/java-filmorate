package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceDb;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserServiceDb userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public void updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable @Positive Long id) {
        return userService.findUserById(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        userService.addFriend(id, friendId);
        return userService.getFriends(id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        userService.removeFriend(id, friendId);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getFriendsCommon(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}