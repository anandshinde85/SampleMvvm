package com.anand.mvvmskeletonarchitecture.common.util

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L, val cacheFlag: Boolean = false) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R, val expiredFlag: Boolean = false) : Either<Nothing, R>()

    /*
        Represents none of the either sides of [Either] class which by convention is a "Loading" state
        workaround for representing loading state in terms of Either,
        This is required as we push the loading state from repository level rather than the viewmodel level (old implementation)
    */
    object Loading : Either<Nothing, Nothing>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun either(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
            else -> {
                // explicitly left blank
            }
        }

    fun right(): R {
        if (this is Right) return b
        else throw IllegalAccessException("Either is not of right type")
    }

    fun left(): L {
        if (this is Left) return a
        else throw IllegalAccessException("Either is not of left type")
    }
}

// Credits to Alex Hart -> https://proandroiddev.com/kotlins-nothing-type-946de7d464fb
// Composes 2 functions
/*

fun <L> left(a: L, cacheFlag: Boolean) = Left(a, cacheFlag)
fun <R> right(b: R, expiredFlag: Boolean) = Right(b, expiredFlag)


fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> fn(b)
    }

fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> = this.flatMap(fn.c(::right))

   */