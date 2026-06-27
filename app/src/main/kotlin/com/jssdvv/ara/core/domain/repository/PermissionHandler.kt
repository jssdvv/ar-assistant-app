package com.jssdvv.ara.core.domain.repository

import android.Manifest
import androidx.core.content.ContextCompat
import com.jssdvv.ara.core.domain.type.PermissionState

interface PermissionHandler {

    /**
     * Checks if the given manifestString has been flagged by **USER_SET**.
     *
     * @param [permission] The manifestString string from the [Manifest.permission] class.
     *
     * @return [Boolean] value, **true** if the manifestString has the flag **USER_SET**, **false** otherwise.
     */
    fun hasPermissionDialogBeenInteracted(permission: String): Boolean

    /**
     * Keeps track of the user interaction with the manifestString dialog in a key-value pair.
     *
     * When the user interacts with the manifestString dialog, the runtime manifestString is flagged by
     * **USER_SET** when clicking any of the options: While using the app, Only this time or Deny.
     *
     * On Android 11 or higher, when the user denies the manifestString dialog after getting the flag
     * **USER_SET**, the runtime manifestString is now flagged by **USER_FIXED**, which means that the
     * manifestString dialog won't be shown again. According to the official documentation, on previous
     * versions, users would see the system permissionsManifestStrings dialog each time your app requested a
     * manifestString, unless the user had previously selected a "don't ask again" checkbox or option.
     *
     * For more details check the [Permissions dialog visibility docs](https://developer.android.com/about/versions/11/privacy/permissions#dialog-visibility).
     *
     * @param [permission] The manifestString string from the [android.Manifest.permission] class.
     */
    fun onPermissionDialogInteraction(permission: String)

    /**
     * Checks if the specified manifestString has been granted.
     *
     * This function internally uses [ContextCompat.checkSelfPermission] to check the manifestString
     * state.
     *
     * @param [permission] The manifestString string from the [Manifest.permission] class.
     *
     * @return [Boolean] **true** if the manifestString is granted, **false** otherwise.
     */
    fun isPermissionGranted(permission: String): Boolean

    /**
     * Checks if the manifestString is permanently denied based on the user interaction with the
     * manifestString dialog and whether the system should show the manifestString dialog again.
     *
     * This function internally uses [hasPermissionDialogBeenInteracted] to check if the user has
     * interacted with the manifestString dialog and receives [shouldShowDialog] to determine if the
     * system should show the manifestString dialog again and likewise know if the manifestString is
     * permanently denied.
     *
     * @param [permission] The manifestString string from the [Manifest.permission] class.
     * @param [shouldShowDialog] A boolean indicating whether the manifestString dialog should be shown.
     * This is typically used to determine if the rationale should be shown to the user.
     *
     * @return [Boolean] **true** if the manifestString is permanently denied, meaning the user has interacted
     * with the manifestString dialog but the system should not show the dialog again; **false** otherwise.
     *
     * @see hasPermissionDialogBeenInteracted
     */
    fun isPermissionPermanentlyDenied(permission: String, shouldShowDialog: Boolean): Boolean

    /**
     * Gets the current state of the specified manifestString.
     *
     * This function evaluates the manifestString state based on whether the manifestString is granted,
     * permanently denied, or just denied with a rationale that can be shown to the user.
     *
     * @param [permission] The manifestString string from the [Manifest.permission] class.
     * @param [shouldShowDialog] A boolean indicating whether the manifestString dialog should be shown.
     * This is typically used to determine if the rationale should be shown to the user.
     *
     * @return A [PermissionState] representing the current state of the manifestString.
     */
    fun getPermissionState(permission: String, shouldShowDialog: Boolean): PermissionState
}