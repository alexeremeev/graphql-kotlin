package com.eremeev.graphql.mutations

import com.eremeev.graphql.models.Snack
import com.eremeev.graphql.repos.ReviewRepository
import com.eremeev.graphql.repos.SnackRepository
import com.expediagroup.graphql.server.operations.Mutation
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class SnackMutation(val snackRepository: SnackRepository, val reviewRepository: ReviewRepository): Mutation {

    suspend fun addSnack(name: String, amount: BigDecimal): Snack {
        val snack = Snack(
            name = name,
            amount = amount
        )
        return snackRepository.save(snack).awaitFirst()
    }

    suspend fun deleteSnack(id: UUID): Boolean {
        snackRepository.deleteById(id).then(reviewRepository.deleteBySnackId(id)).subscribe()
        return true
    }
}
