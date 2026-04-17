package com.jssdvv.ara.core.presentation.common.state

import androidx.compose.runtime.Immutable
import com.jssdvv.ara.core.domain.utility.PermissionState

typealias ManifestString = String

@Immutable
data class Permission(
    val manifestString: ManifestString,
    val state: PermissionState
)

fun Set<Permission>.allGranted(): Boolean = this
    .isNotEmpty() && this
    .all { it.state is PermissionState.Granted }

fun Set<Permission>.anyPermanentlyDenied(): Boolean = this
    .any { it.state is PermissionState.PermanentlyDenied }

fun Set<Permission>.anyDenied(): Boolean = this
    .any { it.state is PermissionState.Denied }

fun Set<Permission>.permanentlyDeniedRationales(): Set<Int> = this
    .map { it.state }
    .filterIsInstance<PermissionState.PermanentlyDenied>()
    .mapTo(mutableSetOf()) { it.rationaleId }

fun Set<Permission>.deniedPermissions(): Set<Permission> = this
    .filterTo(mutableSetOf()) { it.state is PermissionState.Denied }