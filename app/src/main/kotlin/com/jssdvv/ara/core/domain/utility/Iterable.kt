package com.jssdvv.ara.core.domain.utility

inline fun <T, R : Iterable<T>> R.forEachApply(block: T.() -> Unit): R {
    for (element in this) { element.block() }
    return this
}