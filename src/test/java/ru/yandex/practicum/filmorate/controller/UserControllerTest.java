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
public class UserControllerTest {
    private static final String PATH = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Test
    void postCorrectUser() throws Exception {
        String requestBody = getContentFromFile("create/request/user/correctUser.json");
        String responseBody = getContentFromFile("create/response/user/user.json");

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(responseBody));
    }

    @Test
    void postIncorrectUserSpaceInLogin() throws Exception {
        String requestBody = getContentFromFile("create/request/user/incorrectUser1.json");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().is(400));
        } catch (Exception exp) {
            Assertions.assertEquals("Request processing failed: " +
                    "ru.yandex.practicum.filmorate.exception.ValidationException: " +
                    "Логин не должен содержать пробелов", exp.getMessage());
        }
    }

    @Test
    void putIncorrectUserEmailWithoutAt() throws Exception {
        String requestBody = getContentFromFile("create/request/user/incorrectUser2.json");
        String requestBodyBase = getContentFromFile("create/request/user/correctUser.json");


        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyBase));

        mockMvc.perform(MockMvcRequestBuilders.put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void postEmptyUser() throws Exception {
        String requestBody = getContentFromFile("create/request/user/emptyUser.json");

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
