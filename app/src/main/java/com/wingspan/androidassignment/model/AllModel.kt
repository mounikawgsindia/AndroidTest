package com.wingspan.androidassignment.model

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

data class AccountsResponseModel(var ActName:String?,var actid:String?)
data class RoomResponseModel(var ActName:String?,var actid:String?,var alterName:String?)

