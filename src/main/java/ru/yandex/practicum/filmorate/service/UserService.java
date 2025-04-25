package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Getter
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(Long userId, Long friendId) {
        log.info("Попытка добавить друга с ID {} для пользователя с ID {}", friendId, userId);

        if (userStorage.findUserById(userId).getFriends().contains(friendId)) {
            log.warn("Пользователь с ID {} уже дружит с пользователем с ID {}", userId, friendId);
            throw new AlreadyExistsException("Вы уже дружите с: " + userStorage.findUserById(friendId).getName());
        }

        Set<Long> friends = userStorage.findUserById(userId).getFriends();
        friends.add(friendId);
        userStorage.findUserById(userId).setFriends(friends);

        Set<Long> friendOfFriend = userStorage.findUserById(friendId).getFriends();
        friendOfFriend.add(userId);
        userStorage.findUserById(friendId).setFriends(friendOfFriend);

        log.info("Пользователь с ID {} и пользователь с ID {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Попытка удалить друга с ID {} для пользователя с ID {}", friendId, userId);

        Set<Long> friends = userStorage.findUserById(userId).getFriends();
        if (!friends.contains(friendId)) {
            log.warn("Пользователь с ID {} не является другом пользователя с ID {}", friendId, userId);
        }

        friends.remove(friendId);
        userStorage.findUserById(userId).setFriends(friends);

        Set<Long> friendOfFriend = userStorage.findUserById(friendId).getFriends();
        friendOfFriend.remove(userId);
        userStorage.findUserById(friendId).setFriends(friendOfFriend);

        log.info("Пользователь с ID {} больше не является другом пользователя с ID {}", userId, friendId);
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        log.info("Запрос на получение общих друзей между пользователями с ID {} и ID {}", userId, friendId);

        Set<Long> mutualFriends = new HashSet<>(userStorage.findUserById(userId).getFriends());
        mutualFriends.retainAll(userStorage.findUserById(friendId).getFriends());

        List<User> mutualFriendsList = userStorage.findAllFriend(mutualFriends);

        log.info("Пользователи с ID {} и ID {} имеют {} общих друзей", userId, friendId, mutualFriendsList.size());
        return mutualFriendsList;
    }
}