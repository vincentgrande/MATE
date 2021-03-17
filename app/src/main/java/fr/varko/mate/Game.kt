package fr.varko.mate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Game (val id: Long, val name: String, val img: String, val isRankable: String, val isSelected: Boolean): Parcelable {

    constructor() : this(0,"","", "", false)
}