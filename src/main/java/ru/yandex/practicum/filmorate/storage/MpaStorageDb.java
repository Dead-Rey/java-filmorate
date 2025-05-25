package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaStorageDb implements MpaStorage {

    private final MpaRepository repository;

    @Override
    public List<Mpa> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        return repository.findById(id);
    }
}