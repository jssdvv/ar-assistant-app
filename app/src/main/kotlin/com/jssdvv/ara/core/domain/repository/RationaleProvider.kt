package com.jssdvv.ara.core.domain.repository

interface RationaleProvider {
    fun getDeniedPermissionRationale(permission: String): Int
    fun getPermanentlyDeniedPermissionRationale(permission: String): Int
}