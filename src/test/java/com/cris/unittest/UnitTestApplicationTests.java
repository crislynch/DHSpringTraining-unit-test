package com.cris.unittest;

import com.cris.unittest.Controllers.UserController;
import com.cris.unittest.Entities.User;
import com.cris.unittest.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class UnitTestApplicationTests {

	@Autowired
	UserController userController;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	public void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
	}

	private User makeUser() {
		User user = new User();
		user.setPhoneNumber("pd");
		user.setEmail("pd");
		user.setFullName("pd");
		user.setBirthDate("pd");
		return user;
	}

	@Test
	void createUser() throws Exception {
		User user = makeUser();

		String userJSON =objectMapper.writeValueAsString(user);

		MvcResult result = this.mvc.perform(post("/user/")
						.contentType(MediaType.APPLICATION_JSON).content(userJSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		User userResult = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

		assertThat(userResult.getId()).isNotNull();

		assertThat(userResult.getEmail()).isEqualTo(user.getEmail());
		assertThat(userResult.getPhoneNumber()).isEqualTo(user.getPhoneNumber());

	}

	@Test
	void getSingleUser() throws Exception {
		User user = makeUser();

		userRepository.save(user);

		MvcResult result = this.mvc.perform(get("/user/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		User userResult = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

		assertThat(userResult.getId()).isNotNull();

		assertThat(userResult.getId()).isEqualTo(user.getId());
		assertThat(userResult.getEmail()).isEqualTo(user.getEmail());
	}

	@Test
	void getAllUsers() throws Exception {
		User user = makeUser();
		User user1 = makeUser();
		user1.setEmail("uanm");

		userRepository.save(user);
		userRepository.save(user1);

		MvcResult result = this.mvc.perform(get("/user/").contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List usersResult = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

		assertThat(usersResult).hasSize(2);

		assertThat(usersResult).extracting("fullName").contains("pd", "pd");
		assertThat(usersResult).extracting("email").contains("pd", "uanm");
	}

	@Test
	void updateUser() throws Exception {
		User user = makeUser();
		User user1 = makeUser();
		user1.setEmail("rinconiglio");
		user1.setPhoneNumber("spasticanza");

		userRepository.save(user);

		String userJSON = objectMapper.writeValueAsString(user1);

		MvcResult result = this.mvc.perform(put("/user/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content(userJSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		User resultUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

		assertThat(resultUser.getId()).isNotNull();
	}

	@Test
	void deleteUser() throws Exception {
		User user = makeUser();

		userRepository.save(user);

		this.mvc.perform(delete("/user/" + user.getId()))
				.andExpect(status().isOk());

		assertThat(userRepository.findById(user.getId())).isEmpty();
	}

}
