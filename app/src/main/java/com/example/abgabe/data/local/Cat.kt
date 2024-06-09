package com.example.abgabe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Cat(
    @PrimaryKey val id: UUID,
    val name: String,
    val breed: String,
    val temperament: String,
    val origin: String,
    val lifeExpectancy: String,
    val imageUrl: String,
    var qrCodePath: String,
    var qrCodeByteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cat

        return qrCodeByteArray.contentEquals(other.qrCodeByteArray)
    }

    override fun hashCode(): Int {
        return qrCodeByteArray.contentHashCode()
    }
}