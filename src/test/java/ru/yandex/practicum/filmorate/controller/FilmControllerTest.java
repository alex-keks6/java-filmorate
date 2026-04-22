package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FilmControllerTest {
    private static final String PATH = "/films";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmController filmController;

    @Test
    void postIncorrectFilmEmptyName() throws Exception {
        String requestBody = getContentFromFile("create/request/film/incorrectFilm1.json");

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void putIncorrectFilmIncorrectReleaseDate() throws Exception {
        String requestBody = getContentFromFile("create/request/film/incorrectFilm2.json");
        String requestBodyBase = getContentFromFile("create/request/film/correctFilm.json");

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyBase));
        try {
            mockMvc.perform(MockMvcRequestBuilders.put(PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().is(400));
        } catch (Exception exp) {
            Assertions.assertEquals("Request processing failed: " +
                    "ru.yandex.practicum.filmorate.exception.ValidationException: " +
                    "Некорректная дата релиза фильма: 1790-01-01. " +
                    "Релиз не может быть раньше дня рождения кино (28.12.1895)", exp.getMessage());
        }
    }

    @Test
    void postEmptyFilm() throws Exception {
        String requestBody = getContentFromFile("create/request/film/emptyFilm.json");

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    private String getContentFromFile(String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (IOException exp) {
            throw new RuntimeException("Не открывается файл", exp);
        }
    }
}
