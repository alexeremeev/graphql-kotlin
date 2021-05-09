package com.eremeev.graphql.models

import org.springframework.data.annotation.Id
import java.util.*

data class Review(
    @Id
    val id: UUID? = null,
    val snackId: UUID,
    val rating: Int,
    val text: String
)
