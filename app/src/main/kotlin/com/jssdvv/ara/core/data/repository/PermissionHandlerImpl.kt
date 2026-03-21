package com.jssdvv.ara.core.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
     * creating unique keys.
     *
     * @return A [String] representing the complete preference value key.
     */
    private fun getPreferenceValueKey(permission: String): String =
        context.getString(
            R.string.preference_permission_value_key,
            getPermissionName(permission) + USER_SET_FLAG
        )

    override fun hasPermissionDialogBeenInteracted(permission: String): Boolean {
        val preferenceValueKey = getPreferenceValueKey(permission)
        return sharedPreference.getBoolean(preferenceValueKey, false)
    }

    override fun onPermissionDialogInteraction(permission: String) {
        val preferenceValueKey = getPreferenceValueKey(permission)
        sharedPreference
            .edit()
            .putBoolean(preferenceValueKey, true)
            .apply()
    }

    override fun isPermissionGranted(permission: String): Boolean =
        when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }

    override fun isPermissionPermanentlyDenied(
        permission: String,
        shouldShowDialog: Boolean,
    ): Boolean =
        hasPermissionDialogBeenInteracted(permission) && !shouldShowDialog

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