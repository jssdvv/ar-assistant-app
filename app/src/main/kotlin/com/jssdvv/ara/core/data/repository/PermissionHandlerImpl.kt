package com.jssdvv.ara.core.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.domain.repository.RationaleProvider
import com.jssdvv.ara.core.domain.utility.PermissionState

/**
 * This class is responsible for managing permission states and counts using shared preferences.
 */
class PermissionHandlerImpl(
    private val context: Context,
    private val rationaleProvider: RationaleProvider,
) : PermissionHandler {

    companion object {
        private const val USER_SET_FLAG = "_USER_SET"
    }

    private val preferenceFileKey by lazy {
        context.getString(R.string.preference_permissions_file_key)
    }

    private val sharedPreference by lazy {
        context.getSharedPreferences(
            preferenceFileKey,
            Context.MODE_PRIVATE
        )
    }

    /**
     * Gets the name of the permission from the permission string.
     *
     * For example, if the permission string is `android.permission.CAMERA`, this function will
     * return **CAMERA**.
     *
     * @param [permission] The permission string from the [Manifest.permission] class.
     *
     * @return A [String] with the name of the permission.
     */
    private fun getPermissionName(permission: String): String =
        permission.substringAfterLast(".")

    /**
     * Constructs the preference value key for a given permission.
     *
     * This key is used to store and retrieve preference values associated with the specified
     * permission from the shared preferences.
     *
     * @param [permission] The permission string from the [Manifest.permission] class.
     * @param [keyValueComplement] An optional string to append to the permission name for
     * creating unique keys.
     *
     * @return A [String] representing the complete preference value key.
     */
    private fun getPreferenceValueKey(permission: String, keyValueComplement: String = ""): String =
        context.getString(
            R.string.preference_permission_value_key,
            getPermissionName(permission) + keyValueComplement
        )

    /**
     * Checks if the given permission has been flagged by **USER_SET**.
     *
     * @param [permission] The permission string from the [Manifest.permission] class.
     *
     * @return [Boolean] value, **true** if the permission has the flag **USER_SET**, **false** otherwise.
     */
    override fun hasPermissionDialogBeenInteracted(permission: String): Boolean {
        val preferenceValueKey = getPreferenceValueKey(permission, USER_SET_FLAG)
        return sharedPreference.getBoolean(preferenceValueKey, false)
    }

    /**
     * Keeps track of the user interaction with the permission dialog in a key-value pair.
     *
     * When the user interacts with the permission dialog, the runtime permission is flagged by
     * **USER_SET** when clicking any of the options: While using the app, Only this time or Deny.
     *
     * On Android 11 or higher, when the user denies the permission dialog after getting the flag
     * **USER_SET**, the runtime permission is now flagged by **USER_FIXED**, which means that the
     * permission dialog won't be shown again. According to the official documentation, on previous
     * versions, users would see the system permissions dialog each time your app requested a
     * permission, unless the user had previously selected a "don't ask again" checkbox or option.
     *
     * For more details check the [Permissions dialog visibility docs](https://developer.android.com/about/versions/11/privacy/permissions#dialog-visibility).
     *
     * @param [permission] The permission string from the [android.Manifest.permission] class.
     */
    override fun onPermissionDialogInteraction(permission: String) {
        val preferenceValueKey = getPreferenceValueKey(permission, USER_SET_FLAG)
        sharedPreference
            .edit()
            .putBoolean(preferenceValueKey, true)
            .apply()
    }

    /**
     * Checks if the specified permission has been granted.
     *
     * This function internally uses [ContextCompat.checkSelfPermission] to check the permission
     * state. If the permission is granted, it returns **true**, otherwise it returns **false**.
     *
     * @param [permission] The permission string from the [Manifest.permission] class.
     *
     * @return [Boolean] value, **true** if the permission is granted, **false** otherwise.
     */
    override fun isPermissionGranted(permission: String): Boolean =
        when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }

    /**
     * Checks if the permission is permanently denied based on the user interaction with the
     * permission dialog and whether the system should show the permission dialog again.
     *
     * This function internally uses [hasPermissionDialogBeenInteracted] to check if the user has
     * interacted with the permission dialog and receives [shouldShowDialog] to determine if the
     * system should show the permission dialog again and likewise know if the permission is
     * permanently denied.
     *
     * @param [permission] The permission string from the [Manifest.permission] class.
     * @param [shouldShowDialog] A boolean indicating whether the permission dialog should be shown.
     * This is typically used to determine if the rationale should be shown to the user.
     *
     * @return [Boolean] value, **true** if the permission is permanently denied, meaning the user has interacted
     * with the permission dialog but the system should not show the dialog again; **false** otherwise.
     *
     * @see hasPermissionDialogBeenInteracted
     */
    override fun isPermissionPermanentlyDenied(
        permission: String,
        shouldShowDialog: Boolean,
    ): Boolean =
        hasPermissionDialogBeenInteracted(permission) && !shouldShowDialog

    /**
     * Gets the current state of the specified permission.
     *
     * This function evaluates the permission state based on whether the permission is granted,
     * permanently denied, or just denied with a rationale that can be shown to the user.
     *
     * @param [permission] The permission string from the [Manifest.permission] class.
     * @param [shouldShowDialog] A boolean indicating whether the permission dialog should be shown.
     * This is typically used to determine if the rationale should be shown to the user.
     *
     * @return A [PermissionState] representing the current state of the permission.
     */
    override fun getPermissionState(
        permission: String,
        shouldShowDialog: Boolean,
    ): PermissionState {
        return when {
            isPermissionGranted(permission) -> PermissionState.Granted

            isPermissionPermanentlyDenied(
                permission,
                shouldShowDialog
            ) -> PermissionState.PermanentlyDenied(
                rationaleProvider.getPermanentlyDeniedPermissionRationale(permission)
            )

            else -> PermissionState.Denied(
                rationaleProvider.getDeniedPermissionRationale(permission)
            )
        }
    }
}