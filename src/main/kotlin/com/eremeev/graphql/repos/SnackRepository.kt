package com.eremeev.graphql.repos

import com.eremeev.graphql.models.Snack
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SnackRepository: ReactiveCrudRepository<Snack, UUID> {
}
