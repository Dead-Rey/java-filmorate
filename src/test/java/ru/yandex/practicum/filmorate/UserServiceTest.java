package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();
        userService = new UserService(users);
    }

    @Test
    public void testCreateUser_Succes() {
        User user = new User(1L, "testUser ", "testEmail@example.com", null, null);
        User createdUser  = userService.createUser (user);
        assertEquals(user, createdUser );
        assertTrue(users.contains(user));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        User user1 = new User(1L,  "testEmail@example.com","user1", null, null);
        User user2 = new User(2L,  "testEmail@example.com", "user2", null, null);
        userService.createUser (user1);

        Exception exception = assertThrows(AlreadyExistsException.class, () -> userService.createUser (user2));
        assertEquals("Данный email уже зарегистрирован", exception.getMessage());
    }

    @Test
    public void testCreateUser_LoginAlreadyExists() {
        User user1 = new User(1L,  "testEmail1@example.com","testUser ", null, null);
        User user2 = new User(2L,  "testEmail2@example.com","testUser ", null, null);
        userService.createUser (user1);

        Exception exception = assertThrows(AlreadyExistsException.class, () -> userService.createUser (user2));
        assertEquals("Данный логин уже зарегистрирован", exception.getMessage());
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User(1L, "testUser ", "testEmail@example.com", null, null);
        userService.createUser (user);

        User updatedUser  = new User(1L, "testUser Updated", "testEmailUpdated@example.com", null, null);
        User result = userService.updateUser(updatedUser);

        assertEquals(updatedUser .getLogin(), result.getLogin());
        assertEquals(updatedUser .getEmail(), result.getEmail());
    }

    @Test
    public void testUpdateUser_NotFound() {
        User user = new User(1L, "testUser ", "testEmail@example.com", null, null);

        Exception exception = assertThrows(NotFoundException.class, () -> userService.updateUser (user));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testUpdateUser_EmailAlreadyExists() {
        User user1 = new User(1L, "testEmail@example.com", "user1", null, null);
        User user2 = new User(2L, "testEmail2@example.com", "user2", null, null);
        userService.createUser (user1);
        userService.createUser (user2);

        User updatedUser1 = new User(1L, "testEmail2@example.com", "user1_updated", null, null);
        Exception exception = assertThrows(AlreadyExistsException.class, () -> userService.updateUser(updatedUser1));
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
    }

    @Test
    public void testUpdateUser_LoginAlreadyExists() {
        User user1 = new User(1L, "testEmail1@example.com", "user1", null, null);
        User user2 = new User(2L, "testEmail2@example.com", "user2", null, null);
        userService.createUser (user1);
        userService.createUser (user2);

        User updatedUser1 = new User(1L, "testEmail1@example.com", "user2", null, null);
        Exception exception = assertThrows(AlreadyExistsException.class, () -> userService.updateUser (updatedUser1));
        assertEquals("Пользователь с таким логином уже существует", exception.getMessage());
    }
}
