package com.jssdvv.ara.machines.domain.utility.geometry

import com.google.android.filament.Engine
import com.google.android.filament.RenderableManager.PrimitiveType
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.geometries.Geometry
import io.github.sceneview.geometries.Geometry.Vertex
import io.github.sceneview.geometries.UvCoordinate
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import dev.romainguy.kotlin.math.TWO_PI
import kotlin.math.cos
import kotlin.math.sin

class Arrow {
    class Builder : Geometry.Builder(PrimitiveType.TRIANGLES) {
        var shaftRadius: Float = DEFAULT_SHAFT_RADIUS
            private set
        var shaftHeight: Float = DEFAULT_SHAFT_HEIGHT
            private set
        var sideCount: Int = DEFAULT_SIDE_COUNT
            private set
        var headRadius: Float = DEFAULT_HEAD_RADIUS
            private set
        var headHeight: Float = DEFAULT_HEAD_HEIGHT
            private set
        var center: Position = DEFAULT_CENTER
            private set

        fun shaftRadius(radius: Float) = apply { this.shaftRadius = radius }
        fun shaftHeight(height: Float) = apply { this.shaftHeight = height }
        fun sideCount(sideCount: Int) = apply { this.sideCount = sideCount }
        fun headRadius(radius: Float) = apply {
            if (radius < shaftRadius) {
                this.headRadius = shaftRadius * 2.6F
            } else {
                this.headRadius = radius
            }
        }

        fun headHeight(height: Float) = apply {
            if (height <= 0) {
                this.headHeight = shaftHeight * 1.2F
            } else {
                this.headHeight = height
            }
        }

        fun center(center: Position) = apply { this.center = center }

        override fun build(engine: Engine): Geometry {
            val vertices =
                getVertices(center, shaftRadius, shaftHeight, sideCount, headRadius, headHeight)
            val indices = getIndices(sideCount)
            return Geometry
                .Builder(primitiveType)
                .vertices(vertices)
                .primitivesIndices(indices)
                .build(engine)
        }
    }

    companion object {
        val DEFAULT_CENTER = Position(0F)
        const val DEFAULT_SIDE_COUNT = 8
        const val DEFAULT_HEAD_RADIUS = 0.008F // 8mm
        const val DEFAULT_SHAFT_RADIUS = 0.001F // 3mm
        const val DEFAULT_HEAD_HEIGHT = 0.02F // 2cm = 20% shaft
        const val DEFAULT_SHAFT_HEIGHT = 0.08F // 8cm

        fun getVertices(
            center: Position,
            shaftRadius: Float,
            shaftHeight: Float,
            sideCount: Int,
            headRadius: Float,
            headHeight: Float
        ): List<Vertex> = buildList {

            val upperConeApexVertices = mutableListOf<Vertex>()
            val lowerConeCircleVertices = mutableListOf<Vertex>()

            val outerRingCircleVertices = mutableListOf<Vertex>()
            val innerRingCircleVertices = mutableListOf<Vertex>()

            val upperCylinderCircleVertices = mutableListOf<Vertex>()
            val lowerCylinderCircleVertices = mutableListOf<Vertex>()

            val lowerCapCircleVertices = mutableListOf<Vertex>()
            val lowerCapCenterVertex = Vertex(
                position = center,
                normal = Direction(y = -1F),
                uvCoordinate = UvCoordinate(0.5F, 0.5F)
            )

            val thetaIncrement = TWO_PI / sideCount
            var theta = 0F
            val uStep = 1F / sideCount

            for (side in 0..sideCount) {

                // Positions
                val arrowApexPosition = Position(y = shaftHeight + headHeight) + center

                val outerRingCircumferencePosition = Position(
                    x = headRadius * cos(theta),
                    y = shaftHeight,
                    z = headRadius * sin(theta)
                ) + center

                val upperShaftCircumferencePosition = Position(
                    x = shaftRadius * cos(theta),
                    y = shaftHeight,
                    z = shaftRadius * sin(theta)
                ) + center

                val lowerShaftCircumferencePosition = Position(
                    x = shaftRadius * cos(theta),
                    y = 0F,
                    z = shaftRadius * sin(theta)
                ) + center

                // Normals
                val coneNormal = normalize(
                    Direction(
                        x = headHeight * cos(theta),
                        y = headRadius,
                        z = headHeight * sin(theta)
                    )
                )

                val ringNormal = Float3(y = -1F)

                val cylinderNormal = normalize(
                    Direction(
                        x = cos(theta),
                        y = 0F,
                        z = sin(theta)
                    )
                )

                val circleNormal = Float3(y = -1F)

                // UVs
                val coneApexUv = UvCoordinate(
                    x = 0.5F,
                    y = 0.5F
                )

                val lowerConeCircleUv = UvCoordinate(
                    x = (cos(theta) + 1F) / 2F,
                    y = (sin(theta) + 1F) / 2F
                )

                val outerRingUv = UvCoordinate(
                    x = (cos(theta) + 1F) / 2F,
                    y = (sin(theta) + 1F) / 2F
                )

                val innerRingUv = UvCoordinate(
                    x = ((shaftRadius * cos(theta) / headRadius) + 1F) / 2F,
                    y = ((shaftRadius * sin(theta) / headRadius) + 1F) / 2F
                )

                val upperCylinderUv = UvCoordinate(
                    x = uStep * side,
                    y = 1F
                )

                val lowerCylinderUv = UvCoordinate(
                    x = uStep * side,
                    y = 0F
                )

                upperConeApexVertices.add(
                    Vertex(
                        position = arrowApexPosition,
                        normal = coneNormal,
                        uvCoordinate = coneApexUv
                    )
                )

                lowerConeCircleVertices.add(
                    Vertex(
                        position = outerRingCircumferencePosition,
                        normal = coneNormal,
                        uvCoordinate = lowerConeCircleUv
                    )
                )

                outerRingCircleVertices.add(
                    Vertex(
                        position = outerRingCircumferencePosition,
                        normal = ringNormal,
                        uvCoordinate = outerRingUv
                    )
                )

                innerRingCircleVertices.add(
                    Vertex(
                        position = upperShaftCircumferencePosition,
                        normal = ringNormal,
                        uvCoordinate = innerRingUv
                    )
                )

                upperCylinderCircleVertices.add(
                    Vertex(
                        position = upperShaftCircumferencePosition,
                        normal = cylinderNormal,
                        uvCoordinate = upperCylinderUv
                    )
                )

                lowerCylinderCircleVertices.add(
                    Vertex(
                        position = lowerShaftCircumferencePosition,
                        normal = cylinderNormal,
                        uvCoordinate = lowerCylinderUv
                    )
                )

                lowerCapCircleVertices.add(
                    Vertex(
                        position = lowerShaftCircumferencePosition,
                        normal = circleNormal,
                        uvCoordinate = outerRingUv
                    )
                )

                theta += thetaIncrement
            }

            // Lower Circle Cap
            add(lowerCapCenterVertex)
            addAll(lowerCapCircleVertices)

            // Shaft Cylinder
            addAll(lowerCylinderCircleVertices)
            addAll(upperCylinderCircleVertices)

            // Arrow Cone Ring
            addAll(innerRingCircleVertices)
            addAll(outerRingCircleVertices)

            // Arrow Cone To Apex
            addAll(lowerConeCircleVertices)
            addAll(upperConeApexVertices)
        }

        fun getIndices(sideCount: Int): List<List<Int>> {
            val allIndices = mutableListOf<Int>()
            val multiplier = sideCount + 1

            for (vertex in 0 until sideCount) {

                val pieCenter = 0
                val pieLeft = vertex + 1
                val pieRight = vertex + 2

                val cylinderBottomLeft = vertex + multiplier + 1
                val cylinderBottomRight = vertex + multiplier + 2

                val cylinderTopLeft = vertex + 2 * multiplier + 1
                val cylinderTopRight = vertex + 2 * multiplier + 2

                val innerRingLeft = vertex + 3 * multiplier + 1
                val innerRingRight = vertex + 3 * multiplier + 2

                val outerRingLeft = vertex + 4 * multiplier + 1
                val outerRingRight = vertex + 4 * multiplier + 2

                val coneCircleLeft = vertex + 5 * multiplier + 1
                val coneCircleRight = vertex + 5 * multiplier + 2

                val coneApex = vertex + 6 * multiplier + 1

                allIndices.addAll(
                    listOf(
                        // Lower Cap Circle
                        pieCenter,
                        pieLeft,
                        pieRight,
                        // Cylinder Bottom Triangle
                        cylinderBottomLeft,
                        cylinderTopLeft,
                        cylinderBottomRight,
                        // Cylinder Top Triangle
                        cylinderTopRight,
                        cylinderBottomRight,
                        cylinderTopLeft,
                        // Inner Ring Circle
                        innerRingLeft,
                        outerRingLeft,
                        innerRingRight,
                        // Outer Ring Circle
                        outerRingLeft,
                        outerRingRight,
                        innerRingRight,
                        // Cone Apex Triangle
                        coneCircleRight,
                        coneCircleLeft,
                        coneApex
                    )
                )
            }
            return listOf(allIndices)
        }
    }
}