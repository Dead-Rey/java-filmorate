package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable @Positive Long id) {
        return userStorage.findUserById(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        userService.addFriend(id, friendId);
        return userStorage.findAllFriend(userStorage.findUserById(id).getFriends());
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        userService.removeFriend(id, friendId);
        return userStorage.findAllFriend(userStorage.findUserById(id).getFriends());
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Long id) {
        return userStorage.findAllFriend(userStorage.findUserById(id).getFriends());
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getFriendsCommon(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}