package com.jssdvv.ara.core.data.repository

import android.Manifest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.RationaleProvider

class RationaleProviderImpl : RationaleProvider {

    companion object {
        // Used permissions in the app
        private const val CAMERA = Manifest.permission.CAMERA
        private const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
        private const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    override fun getDeniedPermissionRationale(permission: String): Int =
        when (permission) {
            CAMERA -> R.string.denied_camera_permission_rationale
            READ_MEDIA_IMAGES -> R.string.denied_write_external_storage_permission_rationale
            READ_MEDIA_VISUAL_USER_SELECTED -> R.string.denied_write_external_storage_permission_rationale
            READ_EXTERNAL_STORAGE -> R.string.denied_read_media_images_permission_rationale
            WRITE_EXTERNAL_STORAGE -> R.string.denied_write_external_storage_permission_rationale
            else -> R.string.denied_generic_permission_rationale
        }

    override fun getPermanentlyDeniedPermissionRationale(permission: String): Int =
        when (permission) {
            CAMERA -> R.string.permanently_denied_camera_permission_rationale
            READ_MEDIA_IMAGES -> R.string.permanently_denied_write_external_storage_permission_rationale
            READ_MEDIA_VISUAL_USER_SELECTED -> R.string.permanently_denied_write_external_storage_permission_rationale
            READ_EXTERNAL_STORAGE -> R.string.permanently_denied_read_media_images_permission_rationale
            WRITE_EXTERNAL_STORAGE -> R.string.permanently_denied_write_external_storage_permission_rationale
            else -> R.string.permanently_denied_generic_permission_rationale
        }
}