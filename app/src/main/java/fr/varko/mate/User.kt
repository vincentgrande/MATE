package fr.varko.mate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid: String, val username: String, val profileImageUrl: String, val playedGames: String): Parcelable {
    constructor() : this("","","", "")
}