package com.eremeev.graphql.mutations

import com.eremeev.graphql.models.Review
import com.eremeev.graphql.repos.ReviewRepository
import com.expediagroup.graphql.server.operations.Mutation
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReviewMutation(val repository: ReviewRepository): Mutation {

    suspend fun addReview(snackId: UUID, rating: Int, text: String): Review {
        val review = Review(
            snackId = snackId,
            rating = rating,
            text = text
        )
        return repository.save(review).awaitFirst()
    }

    suspend fun deleteReview(id: UUID): Boolean {
        repository.deleteById(id).subscribe()
        return true
    }

    suspend fun deleteReviews(snackId: UUID): Boolean {
        repository.deleteBySnackId(snackId).subscribe()
        return true
    }
}
