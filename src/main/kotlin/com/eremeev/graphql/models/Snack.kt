package com.eremeev.graphql.models

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import java.math.BigDecimal
import java.util.*

data class Snack(
    @Id
    val id: UUID? = null,
    val name: String,
    val amount: BigDecimal
) {
    @Transient var reviews: List<Review> = emptyList()
}
