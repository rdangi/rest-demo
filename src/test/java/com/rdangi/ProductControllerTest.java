package com.rdangi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import java.util.Optional;

import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository mockRepository;

    @Before
    public void init() {
        Product product = new Product(1L, "A Guide to the Bodhisattva Way of Life", "Santideva", new BigDecimal("15.41"));
        when(mockRepository.findById(1L)).thenReturn(Optional.of(product));
    }

    //@WithMockUser(username = "USER")
    @WithMockUser("USER")
    @Test
    public void find_login_ok() throws Exception {

        mockMvc.perform(get("/products/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("A Guide to the Bodhisattva Way of Life")))
                .andExpect(jsonPath("$.description", is("Santideva")))
                .andExpect(jsonPath("$.price", is(15.41)));
    }

    @Test
    public void find_nologin_401() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}