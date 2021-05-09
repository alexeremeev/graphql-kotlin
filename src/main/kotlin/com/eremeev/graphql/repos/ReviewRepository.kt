package com.eremeev.graphql.repos

import com.eremeev.graphql.models.Review
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface ReviewRepository : ReactiveCrudRepository<Review, UUID> {

    fun findBySnackId(snackId: UUID?): Flux<Review>

    @Modifying
    fun deleteBySnackId(snackId: UUID?): Mono<Void>
}
