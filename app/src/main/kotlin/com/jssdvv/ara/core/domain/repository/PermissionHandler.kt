package com.jssdvv.ara.core.domain.repository

import com.jssdvv.ara.core.domain.utility.PermissionState

interface PermissionHandler {
    fun hasPermissionDialogBeenInteracted(permission: String): Boolean
    fun onPermissionDialogInteraction(permission: String)
    fun isPermissionGranted(permission: String): Boolean
    fun isPermissionPermanentlyDenied(permission: String, shouldShowDialog: Boolean): Boolean
    fun getPermissionState(permission: String, shouldShowDialog: Boolean, ): PermissionState
}