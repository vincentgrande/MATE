package fr.varko.mate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Plateform (val id: String, val name: String): Parcelable {
    constructor() : this("","")
}