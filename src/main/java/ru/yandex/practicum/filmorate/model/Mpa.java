package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mpa")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {

    @Id
    private Integer id;

    private String name;
}