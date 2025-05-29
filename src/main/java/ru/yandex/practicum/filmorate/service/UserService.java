package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    List<User> getMutualFriends(Long userId, Long otherUserId);
    List<User> getFriends(Long userId);

    // Новые методы для контроллера
    User createUser(User user);
    User updateUser(User user);
    User findUserById(Long id);
    List<User> getUsers();
}