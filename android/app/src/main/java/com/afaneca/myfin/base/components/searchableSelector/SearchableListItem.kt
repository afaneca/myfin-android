package com.afaneca.myfin.base.components.searchableSelector

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep


@Keep
@Parcelize
data class SearchableListItem(val id: String, val name: String, val description: String? = null) :
    Parcelable