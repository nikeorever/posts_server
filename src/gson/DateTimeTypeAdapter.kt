package cn.nikeo.server.gson

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class DateTimeTypeAdapter : TypeAdapter<DateTime>() {
    override fun read(`in`: JsonReader): DateTime? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null;
        }
        return deserializeToDateTime(`in`.nextString())
    }

    private fun deserializeToDateTime(json: String): DateTime? = synchronized(this) {
        return@synchronized runCatching {
            DateTime.parse(json)
        }.getOrElse { e ->
            throw JsonSyntaxException(json, e)
        }
    }

    override fun write(out: JsonWriter, value: DateTime?) {
        if (value == null) {
            out.nullValue()
            return
        }

        val dateFormatAsString: String = ISODateTimeFormat.yearMonthDay().print(value)
        out.value(dateFormatAsString)
    }
}