package com.weatherapp.myweatherapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MyweatherappApplicationTests {


	@Test
	void contextLoads(@Autowired MockMvc mvc) throws Exception {
		System.out.println("Starting Tests");

		MvcResult r = mvc.perform(get("/forecast/" + "Paris")).andExpect(status().isOk()).andReturn();
		assert r.getResponse().getContentAsString().contains("Paris");
	}

}
