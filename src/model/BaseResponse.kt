package cn.nikeo.server.model

import com.google.gson.annotations.SerializedName

/**
 * The main response structure
 * [errorCode] code to indicate if there was an error.
 * [success]  always returning true or false if the response is success will returning true otherwise will get false.
 * [message] it’s better to return the response message from back-end.
 * [data] which is the main key and will have the data that should be displayed.
 */
data class BaseResponse<T>(
    @SerializedName("error_code") var errorCode: Int? = null,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T
)
