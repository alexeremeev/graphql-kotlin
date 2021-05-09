package com.eremeev.graphql.queries

import com.eremeev.graphql.models.Snack
import com.eremeev.graphql.repos.ReviewRepository
import com.eremeev.graphql.repos.SnackRepository
import com.expediagroup.graphql.server.operations.Query
import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.util.*

@Component
class SnackQuery(val snackRepository: SnackRepository, val reviewRepository: ReviewRepository) : Query {

    suspend fun snacks(environment: DataFetchingEnvironment): List<Snack> {
        val isReviewsRequested = isReviewsRequested(environment)
        return snackRepository.findAll()
            .flatMap {
                Mono.just(it).zipWith(
                    if (isReviewsRequested) reviewRepository.findBySnackId(it.id)
                        .collectList() else Mono.just(emptyList())
                )
            }
            .map { it.t1.reviews = it.t2; it.t1 }
            .collectList()
            .awaitFirst()
    }

    suspend fun snack(environment: DataFetchingEnvironment, id: UUID): Snack? {
        val isReviewsRequested = isReviewsRequested(environment)
        return snackRepository.findById(id)
            .flatMap {
                Mono.just(it).zipWith(
                    if (isReviewsRequested) reviewRepository.findBySnackId(it.id)
                        .collectList() else Mono.just(emptyList())
                )
            }
            .map { it.t1.reviews = it.t2; it.t1 }
            .awaitFirstOrNull()
    }


    private fun isReviewsRequested(environment: DataFetchingEnvironment): Boolean {
        val field = environment.mergedField.fields.firstOrNull { it.name == "snacks" }
        val reviews: List<Field> = field?.selectionSet?.children?.filterIsInstance<Field>().orEmpty()
        return reviews.any { it.name == "reviews" }
    }
}
