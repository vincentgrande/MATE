package fr.varko.mate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid: String, val username: String, val description: String, val profileImageUrl: String, val playedGames: String, val plateform: String): Parcelable {
    constructor() : this("","","", "","","")
}