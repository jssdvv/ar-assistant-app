package com.jssdvv.ara.core.domain.type

enum class UriType(
    val schemes: Set<String>,
) {
    /**
     * Used for accessing data from a ContentProvider.
     *
     * Format: ```content://[authority]/[path]/[file]```
     */
    CONTENT(
        schemes = setOf("content")
    ),

    /**
     * Used for accessing app resources (drawable, raw, etc).
     *
     * Format: ```android.resource://[package]/[id]```
     */
    RESOURCE(
        schemes = setOf("android.resource")
    ),

    /**
     * Used for accessing data from the local file system.
     * Also, used for accessing data from the assets folder.
     *
     * Format: ```file:///[path]/[file]```
     *
     * Assets Format With Prefix: ```file:///android_asset/[path]/[file]```
     */
    FILE(
        schemes = setOf("file")
    ),

    /**
     * Used for embedding data in the UriType.
     *
     * Format: ```data:[<media_type>][;base64],<data>```
     */
    DATA(
        schemes = setOf("data")
    ),

    /**
     * Used for accessing data over the network.
     *
     * Format: ```http(s)://[authority]/[path]/[file]```
     */
    NETWORK(
        schemes = setOf("http", "https")
    ),

    /**
     * Used for link to play store listings.
     *
     * Format: ```market://details?id=[package_name]```
     */
    MARKET(
        schemes = setOf("market")
    ),

    /**
     * Used for referencing an installed package.
     *
     * Format: ```package:[package_name]```
     */
    PACKAGE(
        schemes = setOf("package")
    )
}