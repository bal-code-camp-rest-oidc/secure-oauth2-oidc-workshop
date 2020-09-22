package com.example.library.server.api;

import com.example.library.server.DataInitializer;
import com.example.library.server.api.resource.BookResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@DisplayName("Verify book api")
class BookApiJwtAuthorizationTest {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    this.mockMvc =
            MockMvcBuilders.webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
  }

  @DisplayName("can authorize to")
  @Nested
  class CanAuthorize {

    @Test
    @DisplayName("get list of books")
    void verifyGetBooks() throws Exception {

      mockMvc.perform(get("/books").with(jwt()))
              .andExpect(status().isOk())
              .andDo(print())
              .andExpect(content().string(
                      "{\"_embedded\":{\"bookResourceList\":[{\"identifier\":\"f9bf70d6-e56d-4cab-be6b-294cd05f599f\",\"isbn\":\"9780132350884\",\"title\":\"Clean Code\",\"description\":\"Even bad code can function. But if code isnâ\u0080\u0099t clean, it can bring a development organization to its knees. Every year, countless hours and significant resources are lost because of poorly written code. But it doesnâ\u0080\u0099t have to be that way. Noted software expert Robert C. Martin presents a revolutionary paradigm with Clean Code: A Handbook of Agile Software Craftsmanship . Martin has teamed up with his colleagues from Object Mentor to distill their best agile practice of cleaning code â\u0080\u009Con the flyâ\u0080\u009D into a book that will instill within you the values of a software craftsman and make you a better programmerâ\u0080\u0094but only if you work at it.\",\"authors\":[\"Bob C. Martin\"],\"borrowed\":true,\"borrowedBy\":{\"identifier\":\"c47641ee-e63c-4c13-8cd2-1c2490aee0b3\",\"email\":\"bruce.wayne@example.com\",\"firstName\":\"Bruce\",\"lastName\":\"Wayne\",\"_links\":{\"self\":{\"href\":\"http://localhost/users/c47641ee-e63c-4c13-8cd2-1c2490aee0b3\"}}},\"_links\":{\"self\":{\"href\":\"http://localhost/books/f9bf70d6-e56d-4cab-be6b-294cd05f599f\"},\"update\":{\"href\":\"http://localhost/books/f9bf70d6-e56d-4cab-be6b-294cd05f599f\"},\"borrow\":{\"href\":\"http://localhost/books/f9bf70d6-e56d-4cab-be6b-294cd05f599f/borrow\"},\"return\":{\"href\":\"http://localhost/books/f9bf70d6-e56d-4cab-be6b-294cd05f599f/return\"}}},{\"identifier\":\"3038627d-627e-448d-8422-0a5705c9e8f1\",\"isbn\":\"9781449374648\",\"title\":\"Cloud Native Java\",\"description\":\"What separates the traditional enterprise from the likes of Amazon, Netflix, and Etsy? Those companies have refined the art of cloud native development to maintain their competitive edge and stay well ahead of the competition. This practical guide shows Java/JVM developers how to build better software, faster, using Spring Boot, Spring Cloud, and Cloud Foundry.\",\"authors\":[\"Josh Long\",\"Kenny Bastiani\"],\"borrowed\":true,\"borrowedBy\":{\"identifier\":\"69c10574-9064-40e4-85bd-5c68547f3f48\",\"email\":\"bruce.banner@example.com\",\"firstName\":\"Bruce\",\"lastName\":\"Banner\",\"_links\":{\"self\":{\"href\":\"http://localhost/users/69c10574-9064-40e4-85bd-5c68547f3f48\"}}},\"_links\":{\"self\":{\"href\":\"http://localhost/books/3038627d-627e-448d-8422-0a5705c9e8f1\"},\"update\":{\"href\":\"http://localhost/books/3038627d-627e-448d-8422-0a5705c9e8f1\"},\"borrow\":{\"href\":\"http://localhost/books/3038627d-627e-448d-8422-0a5705c9e8f1/borrow\"},\"return\":{\"href\":\"http://localhost/books/3038627d-627e-448d-8422-0a5705c9e8f1/return\"}}},{\"identifier\":\"081314cb-4abf-43e5-9b38-7d7261edb10d\",\"isbn\":\"9781617291203\",\"title\":\"Spring in Action: Covers Spring 4\",\"description\":\"Spring in Action, Fourth Edition is a hands-on guide to the Spring Framework, updated for version 4. It covers the latest features, tools, and practices including Spring MVC, REST, Security, Web Flow, and more. You'll move between short snippets and an ongoing example as you learn to build simple and efficient J2EE applications. Author Craig Walls has a special knack for crisp and entertaining examples that zoom in on the features and techniques you really need.\",\"authors\":[\"Craig Walls\"],\"borrowed\":false,\"_links\":{\"self\":{\"href\":\"http://localhost/books/081314cb-4abf-43e5-9b38-7d7261edb10d\"},\"update\":{\"href\":\"http://localhost/books/081314cb-4abf-43e5-9b38-7d7261edb10d\"},\"borrow\":{\"href\":\"http://localhost/books/081314cb-4abf-43e5-9b38-7d7261edb10d/borrow\"},\"return\":{\"href\":\"http://localhost/books/081314cb-4abf-43e5-9b38-7d7261edb10d/return\"}}},{\"identifier\":\"02c3d1fb-ca32-46bd-818f-704012b3fe9c\",\"isbn\":\"9781942788003\",\"title\":\"The DevOps Handbook\",\"description\":\"Wondering if The DevOps Handbook is for you? Authors, Gene Kim, Jez Humble, Patrick Debois and John Willis developed this book for anyone looking to transform their IT organizationâ\u0080\u0094especially those who want to make serious changes through the DevOps methodology to increase productivity, profitability and win the marketplace.\",\"authors\":[\"Gene Kim\",\"Jez Humble\",\"Patrick Debois\"],\"borrowed\":false,\"_links\":{\"self\":{\"href\":\"http://localhost/books/02c3d1fb-ca32-46bd-818f-704012b3fe9c\"},\"update\":{\"href\":\"http://localhost/books/02c3d1fb-ca32-46bd-818f-704012b3fe9c\"},\"borrow\":{\"href\":\"http://localhost/books/02c3d1fb-ca32-46bd-818f-704012b3fe9c/borrow\"},\"return\":{\"href\":\"http://localhost/books/02c3d1fb-ca32-46bd-818f-704012b3fe9c/return\"}}}]},\"_links\":{\"self\":{\"href\":\"http://localhost/books\"},\"create\":{\"href\":\"http://localhost/books\"}}}"))
              ;
    }

    @Test
    @DisplayName("get single book")
    void verifyGetBook() throws Exception {

      final Jwt jwt =
              Jwt.withTokenValue("token")
                      .header("alg", "none")
                      .claim("sub", "bwanye")
                      .claim("groups", new String[] {"library_user"})
                      .build();

      mockMvc
              .perform(
                      get("/books/{bookId}", DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)
                              .with(jwt().jwt(jwt)))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete a book")
    void verifyDeleteBook() throws Exception {
      mockMvc
              .perform(
                      delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER)
                              .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_CURATOR"))))
              .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("create a new book")
    void verifyCreateBook() throws Exception {

      BookResource bookResource =
              new BookResource(
                      UUID.randomUUID(),
                      "1234566",
                      "title",
                      "description",
                      Collections.singletonList("Author"),
                      false,
                      null);

      mockMvc
              .perform(
                      post("/books")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(bookResource))
                              .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_CURATOR"))))
              .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("update a book")
    void verifyUpdateBook() throws Exception {

      BookResource bookResource =
              new BookResource(
                      DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER,
                      "9781617291203",
                      "Spring in Action: Covers Spring 5",
                      "Spring in Action, Fifth Edition is a hands-on guide to the Spring Framework, "
                              + "updated for version 4. It covers the latest features, tools, and practices "
                              + "including Spring MVC, REST, Security, Web Flow, and more. You'll move between "
                              + "short snippets and an ongoing example as you learn to build simple and efficient "
                              + "J2EE applications. Author Craig Walls has a special knack for crisp and "
                              + "entertaining examples that zoom in on the features and techniques you really need.",
                      Collections.singletonList("Craig Walls"),
                      false,
                      null);

      mockMvc
              .perform(
                      put("/books/{bookId}", DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(bookResource))
                              .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_CURATOR"))))
              .andExpect(status().isOk());
    }
  }

  @DisplayName("cannot authorize to")
  @Nested
  class CannotAuthorize {

    @Test
    @DisplayName("get list of books")
    void verifyGetBooksUnAuthenticated() throws Exception {

      mockMvc.perform(get("/books")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("get single book")
    void verifyGetBook() throws Exception {

      mockMvc
              .perform(
                      get("/books/{bookId}",
                              DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
              .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete a book")
    void verifyDeleteBookUnAuthorized() throws Exception {
      mockMvc
              .perform(
                      delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER))
              .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete a book with wrong role")
    void verifyDeleteBookWrongRole() throws Exception {
      mockMvc
              .perform(
                      delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER)
                              .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_USER"))))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("create a new book")
    void verifyCreateBookUnAuthorized() throws Exception {

      BookResource bookResource =
              new BookResource(
                      UUID.randomUUID(),
                      "1234566",
                      "title",
                      "description",
                      Collections.singletonList("Author"),
                      false,
                      null);

      mockMvc
              .perform(
                      post("/books")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(bookResource)))
              .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("update a book")
    void verifyUpdateBookUnAuthorized() throws Exception {

      BookResource bookResource =
              new BookResource(
                      DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER,
                      "9781617291203",
                      "Spring in Action: Covers Spring 5",
                      "Spring in Action, Fifth Edition is a hands-on guide to the Spring Framework, "
                              + "updated for version 4. It covers the latest features, tools, and practices "
                              + "including Spring MVC, REST, Security, Web Flow, and more. You'll move between "
                              + "short snippets and an ongoing example as you learn to build simple and efficient "
                              + "J2EE applications. Author Craig Walls has a special knack for crisp and "
                              + "entertaining examples that zoom in on the features and techniques you really need.",
                      Collections.singletonList("Craig Walls"),
                      false,
                      null);

      mockMvc
              .perform(
                      put("/books/{bookId}", DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(bookResource)))
              .andExpect(status().isUnauthorized());
    }
  }
}