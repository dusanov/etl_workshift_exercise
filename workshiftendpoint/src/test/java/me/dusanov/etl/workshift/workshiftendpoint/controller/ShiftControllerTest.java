package me.dusanov.etl.workshift.workshiftendpoint.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShiftControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup () {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void getAllHappyPath() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status()
                .isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/shifts");

        this.mockMvc.perform(builder)
                .andExpect(ok);
                //TODO: once we're returnig somethig, validate:
                //.andExpect(jsonPath("$[0].name", containsString("el calafate")));
    }

    @Test
    void get() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status()
                .isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/shifts/1");

        this.mockMvc.perform(builder)
                .andExpect(ok);
                //TODO: once we're returnig somethig, validate:
                //.andExpect(jsonPath("$[0].name", containsString("el calafate")));
    }
}