package fr.varko.mate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Plateform (val id: Int, val name: String): Parcelable {
    constructor() : this(0,"")
}