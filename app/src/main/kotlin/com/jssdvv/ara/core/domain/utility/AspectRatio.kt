package com.jssdvv.ara.core.domain.utility

/**
 * Class representing an aspect ratio with a given width and height.
 *
 * @param [width] The width of the component.
 * @param [height] The height of the component.
 *
 * @property [aspectRatio] The aspect ratio calculated as width divided by height.
 * @property [aspectRatioInverse] The inverse aspect ratio calculated as height divided by width.
 *
 * @constructor Creates an AspectRatio instance with specified width and height.
 */
class AspectRatio(
    val width: Float,
    val height: Float,
) {
    val aspectRatio: Float
        get() = width / height

    val aspectRatioInverse: Float
        get() = height / width
}