package com.eremeev.graphql

import com.eremeev.graphql.models.Review
import com.eremeev.graphql.models.Snack
import com.eremeev.graphql.repos.ReviewRepository
import com.eremeev.graphql.repos.SnackRepository
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal

@SpringBootTest
@ContextConfiguration(loader = CustomContextLoader::class)
@ExtendWith(SpringExtension::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureWebTestClient
class ApiTest(val client: WebTestClient, val snackRepository: SnackRepository, val reviewRepository: ReviewRepository) {

    @Test
    fun `given saved snack should return in data`() {
        snackRepository.save(
            Snack(
                name = "Test snack",
                amount = BigDecimal.valueOf(20.00)
            )
        ).block()
        val request = GraphQLRequest(
            """
                    query {
                      snacks {
                        id
                        name
                        amount
                      }
                    }
                """.trimIndent()
        )
        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("data.snacks").isNotEmpty
            .jsonPath("data.snacks[0].id").exists()
            .jsonPath("data.snacks[0].name").isEqualTo("Test snack")
            .jsonPath("data.snacks[0].amount").isEqualTo(20)
    }

    @Test
    fun `given snack with review should return in data`() {
        val snack = snackRepository.save(
            Snack(
                name = "Test snack",
                amount = BigDecimal.valueOf(20.00)
            )
        ).block() ?: throw IllegalStateException("should be saved!")

        reviewRepository.save(
            Review(
                snackId = checkNotNull(snack.id),
                rating = 5,
                text = "Great snack!"
            )
        ).block()

        val request = GraphQLRequest(
            """
                query {
                  snacks {
                    id
                    name
                    amount
                    reviews {
                      id
                      rating
                      text
                    }
                  }
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("data.snacks").isNotEmpty
            .jsonPath("data.snacks[0].id").exists()
            .jsonPath("data.snacks[0].name").isEqualTo("Test snack")
            .jsonPath("data.snacks[0].amount").isEqualTo(20)
            .jsonPath("data.snacks[0].reviews").exists()
            .jsonPath("data.snacks[0].reviews[0].id").exists()
            .jsonPath("data.snacks[0].reviews[0].rating").isEqualTo(5)
            .jsonPath("data.snacks[0].reviews[0].text").isEqualTo("Great snack!")
    }

    @Test
    fun `given add snack mutation should return in data`() {
        val request = GraphQLRequest(
            """
                mutation {
                  addSnack(name: "Beer snack", amount: "0.5") {
                    id
                    name
                    amount
                  }
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("data.addSnack").isNotEmpty
            .jsonPath("data.addSnack.id").exists()
            .jsonPath("data.addSnack.name").isEqualTo("Beer snack")
            .jsonPath("data.addSnack.amount").isEqualTo(0.5)
            .jsonPath("data.addSnack.reviews").doesNotExist()
    }

    @Test
    fun `given add review to snack should return in data`() {
        val snack = snackRepository.save(
            Snack(
                name = "Test snack",
                amount = BigDecimal.valueOf(20.00)
            )
        ).block() ?: throw IllegalStateException("should be saved!")

        val request = GraphQLRequest(
            """
                mutation {
                  addReview(snackId: "${snack.id}", rating: 5, text: "great snack") {
                    id
                    rating
                    text
                  }
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("data.addReview").isNotEmpty
            .jsonPath("data.addReview.id").exists()
            .jsonPath("data.addReview.rating").isEqualTo(5)
            .jsonPath("data.addReview.text").isEqualTo("great snack")
    }

    @Test
    fun `given delete review should delete`() {
        val snack = snackRepository.save(
            Snack(
                name = "Test snack",
                amount = BigDecimal.valueOf(20.00)
            )
        ).block() ?: throw IllegalStateException("should be saved!")

        val review = reviewRepository.save(
            Review(
                snackId = checkNotNull(snack.id),
                rating = 5,
                text = "Great snack!"
            )
        ).block() ?: throw IllegalStateException("should be saved!")

        val deleteRequest = GraphQLRequest(
            """
                mutation {
                  deleteReview(id: "${review.id}") 
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(deleteRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()

        val queryRequest =  GraphQLRequest(
            """
                query {
                  snacks {
                    id
                    name
                    amount
                    reviews {
                      id
                      rating
                      text
                    }
                  }
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(queryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("data.snacks").isNotEmpty
            .jsonPath("data.snacks[0].id").exists()
            .jsonPath("data.snacks[0].name").isEqualTo("Test snack")
            .jsonPath("data.snacks[0].amount").isEqualTo(20)
            .jsonPath("data.snacks[0].reviews").isEmpty
    }

    @Test
    fun `given delete snack should delete`() {
        val snack = snackRepository.save(
            Snack(
                name = "Test snack",
                amount = BigDecimal.valueOf(20.00)
            )
        ).block() ?: throw IllegalStateException("should be saved!")

        val review = reviewRepository.save(
            Review(
                snackId = checkNotNull(snack.id),
                rating = 5,
                text = "Great snack!"
            )
        ).block() ?: throw IllegalStateException("should be saved!")

        val deleteRequest = GraphQLRequest(
            """
                mutation {
                  deleteSnack(id: "${snack.id}") 
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(deleteRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()

        val queryRequest =  GraphQLRequest(
            """
                query {
                  snacks {
                    id
                    name
                    amount
                    reviews {
                      id
                      rating
                      text
                    }
                  }
                }
            """.trimIndent()
        )

        client.post()
            .uri("/graphql")
            .contentType(APPLICATION_JSON)
            .bodyValue(queryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("data.snacks").isEmpty
    }

    @AfterEach
    fun teardown() {
        snackRepository.deleteAll().block()
        reviewRepository.deleteAll().block()
    }
}
