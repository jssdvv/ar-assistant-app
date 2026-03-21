package com.jssdvv.ara.core.domain.utility

inline fun <T> Iterable<T>.forEachApply(block: T.() -> Unit) {
    for (element in this) {
        element.apply(block)
    }
}