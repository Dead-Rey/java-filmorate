package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private InMemoryUserStorage userStorage;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();
        userStorage = new InMemoryUserStorage(users);
    }

    @Test
    public void testCreateUser_Succes() {
        User user = new User(1L, "testUser ", "testEmail@example.com", null, null, null);
        User createdUser = userStorage.createUser(user);
        assertEquals(user, createdUser);
        assertTrue(users.contains(user));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        User user1 = new User(1L, "testEmail@example.com", "user1", null, null, null);
        User user2 = new User(2L, "testEmail@example.com", "user2", null, null,null);
        userStorage.createUser(user1);

        Exception exception = assertThrows(AlreadyExistsException.class, () -> userStorage.createUser(user2));
        assertEquals("Данный email уже зарегистрирован", exception.getMessage());
    }

    @Test
    public void testCreateUser_LoginAlreadyExists() {
        User user1 = new User(1L, "testEmail1@example.com", "testUser ", null, null,null);
        User user2 = new User(2L, "testEmail2@example.com", "testUser ", null, null, null);
        userStorage.createUser(user1);

        Exception exception = assertThrows(AlreadyExistsException.class, () -> userStorage.createUser(user2));
        assertEquals("Данный логин уже зарегистрирован", exception.getMessage());
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User(1L, "testUser ", "testEmail@example.com", null, null, null);
        userStorage.createUser(user);

        User updatedUser = new User(1L, "testUser Updated", "testEmailUpdated@example.com", null, null, null);
        User result = userStorage.updateUser(updatedUser);

        assertEquals(updatedUser.getLogin(), result.getLogin());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    public void testUpdateUser_NotFound() {
        User user = new User(1L, "testUser ", "testEmail@example.com", null, null, null);

        Exception exception = assertThrows(NotFoundException.class, () -> userStorage.updateUser(user));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testUpdateUser_EmailAlreadyExists() {
        User user1 = new User(1L, "testEmail@example.com", "user1", null, null, null);
        User user2 = new User(2L, "testEmail2@example.com", "user2", null, null, null);
        userStorage.createUser(user1);
        userStorage.createUser(user2);

        User updatedUser1 = new User(1L, "testEmail2@example.com", "user1_updated", null, null, null);
        Exception exception = assertThrows(AlreadyExistsException.class, () -> userStorage.updateUser(updatedUser1));
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
    }

    @Test
    public void testUpdateUser_LoginAlreadyExists() {
        User user1 = new User(1L, "testEmail1@example.com", "user1", null, null, null);
        User user2 = new User(2L, "testEmail2@example.com", "user2", null, null, null);
        userStorage.createUser(user1);
        userStorage.createUser(user2);

        User updatedUser1 = new User(1L, "testEmail1@example.com", "user2", null, null, null);
        Exception exception = assertThrows(AlreadyExistsException.class, () -> userStorage.updateUser(updatedUser1));
        assertEquals("Пользователь с таким логином уже существует", exception.getMessage());
    }
}