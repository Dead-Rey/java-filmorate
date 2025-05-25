package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorageDb;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceDb {

    private final UserStorageDb storage;

    public List<User> getUsers() {
        return storage.findAll();
    }

    public User createUser(User user) {
        Optional<User> userOptional = storage.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new AlreadyExistsException("Пользователь c " + user.getEmail() + " уже существует");
        }
        return storage.save(user);
    }

    public void updateUser(User user) {
        User oldUser = storage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Юзера с id: " + user.getId() + " не существует"));
        if (user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())) {
            Optional<User> foundByEmail = storage.findByEmail(user.getEmail());
            if (foundByEmail.isPresent()) {
                throw new AlreadyExistsException("Юзер с таким email уже существует");
            }
            oldUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null && !user.getLogin().equals(oldUser.getLogin())) {
            Optional<User> foundByLogin = storage.findByLogin(user.getLogin());
            if (foundByLogin.isPresent()) {
                throw new AlreadyExistsException("Юзер с таким login уже существует");
            }
            oldUser.setLogin(user.getLogin());
        }
        if (user.getName() != null && !user.getName().equals(oldUser.getName())) {
            oldUser.setName(user.getName());
        }
        if (user.getBirthday() != null && !user.getBirthday().equals(oldUser.getBirthday())) {
            oldUser.setBirthday(user.getBirthday());
        }
        storage.save(oldUser);
    }

    public User findUserById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Юзера с id: " + id + " не существует"));
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }

        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        storage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        boolean added = user.getFriends().add(friendId);

        if (added) {
            storage.save(user);
        }
    }


    public void removeFriend(Long userId, Long friendId) {
        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        storage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        boolean removed = user.getFriends().remove(friendId);

        if (removed) {
            storage.save(user);
        } else {
            throw new NotFoundException("Пользователь с id " + friendId + " не был в списке друзей");
        }
    }


    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User otherUser = storage.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherUserId + " не найден"));

        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsOther = otherUser.getFriends();

        Set<Long> mutualFriendIds = new HashSet<>(friendsUser);
        mutualFriendIds.retainAll(friendsOther);

        return storage.findAllByIdIn(mutualFriendIds);
    }

    public List<User> getFriends(Long userId) {
        User user = findUserById(userId);
        return storage.findAllByIdIn(user.getFriends());
    }
}