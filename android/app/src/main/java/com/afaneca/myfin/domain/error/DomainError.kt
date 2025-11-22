package com.afaneca.myfin.domain.error

/**
 * Represents a generic error that can occur in the domain layer, typically produced by a repository.
 * This is intended to be mapped by a Use Case into a more specific, feature-level error.
 */
sealed interface DomainError {
    data object Network : DomainError
    data object Unauthorized : DomainError
    data object ServerError : DomainError
    data object InvalidFormat : DomainError
    data object Unknown : DomainError
}
