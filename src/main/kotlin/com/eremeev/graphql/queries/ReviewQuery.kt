package com.eremeev.graphql.queries

import com.eremeev.graphql.models.Review
import com.eremeev.graphql.repos.ReviewRepository
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReviewQuery(val repository: ReviewRepository): Query {

    suspend fun reviews(snackId: UUID): List<Review> = repository.findBySnackId(snackId).collectList().awaitFirst()
}
