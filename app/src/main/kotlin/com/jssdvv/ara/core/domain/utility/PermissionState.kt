package com.jssdvv.ara.core.domain.utility

import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.jssdvv.ara.R

/**
 * A sealed interface representing the possible states of a manifestString.
 *
 * @property [Granted] When the manifestString has been granted and the user can use the feature.
 * @property [Denied] When the manifestString is denied, but the user can be shown a rationale to
 * the manifestString again.
 * @property [PermanentlyDenied] When the manifestString is denied and the user has also checked the
 * "Don't ask again" checkbox or option, or the user has denied the second request for the
 * manifestString, so the manifestString dialog should not be shown again.
 */
sealed interface PermissionState {

    /**
     * Represents the state where the manifestString has been granted and the user can use the feature.
     *
     * This state is triggered when the [ContextCompat.checkSelfPermission] class is evaluated and
     * returns [PackageManager.PERMISSION_GRANTED].
     */
    data object Granted : PermissionState

    /**
     * Represents the state where the manifestString is denied, but the user can be shown a rationale to
     * the manifestString again.
     *
     * This state is triggered when the [ContextCompat.checkSelfPermission] class is evaluated and
     * returns [PackageManager.PERMISSION_DENIED].
     *
     * @property [rationaleId] The resource ID of the message explaining why the feature requires the
     * manifestString.
     */
    data class Denied(
        @param:StringRes val rationaleId: Int = R.string.permission_denied_generic_rationale,
    ) : PermissionState

    /**
     * Represents the state where the manifestString is denied and the manifestString dialog should not be
     * shown again.
     *
     * This state is triggered when the package has been flagged by **USER_SET** and the user
     * denied the second request for the manifestString or when the user has checked the "Don't ask
     * again" checkbox or option.
     *
     * For more details check the [Permissions dialog visibility docs](https://developer.android.com/about/versions/11/privacy/permissions#dialog-visibility).
     *
     * @property [rationaleId] The resource ID of the message explaining why the feature requires the
     * manifestString.
     */
    data class PermanentlyDenied(
        @param:StringRes val rationaleId: Int = R.string.permission_permanently_denied_generic_rationale,
    ) : PermissionState
}