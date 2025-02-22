package com.ylabz.hoverbike

import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3

fun axisAngleQuaternion(angleDegrees: Float, axis: Vector3): Quaternion {
    val rad = Math.toRadians(angleDegrees.toDouble())
    val sinHalf = kotlin.math.sin(rad / 2).toFloat()
    val cosHalf = kotlin.math.cos(rad / 2).toFloat()
    val length = kotlin.math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z)
    val nx = axis.x / length
    val ny = axis.y / length
    val nz = axis.z / length
    return Quaternion(
        x = nx * sinHalf,
        y = ny * sinHalf,
        z = nz * sinHalf,
        w = cosHalf
    )
}