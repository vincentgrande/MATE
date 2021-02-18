package fr.varko.mate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Game (val id: String, val name: String, val img: String, val isRankable: String, val isSelected: Boolean): Parcelable {

    constructor() : this("","","", "", false)
}