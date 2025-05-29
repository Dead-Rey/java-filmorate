package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
     User save(User user);
     User createUser(User user);
     User updateUser(User user);
     Optional<User> findById(Long id);
     Optional<User> findByEmail(String email);
     Optional<User> findByLogin(String login);
     List<User> findAllUsers();
     List<User> findAllByIdIn(Set<Long> ids);
     boolean existsById(Long id);
     void addFriend(Long userId, Long friendId);
     void removeFriend(Long userId, Long friendId);
}