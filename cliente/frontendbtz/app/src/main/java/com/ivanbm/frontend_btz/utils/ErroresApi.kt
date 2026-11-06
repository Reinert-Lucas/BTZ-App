import org.json.JSONObject
import retrofit2.Response

fun <T> Response<T>.mensajeError(porDefecto: String = "Ocurrió un error"): String =
    try {
        val json = JSONObject(errorBody()?.string().orEmpty())
        // 422: primer mensaje de validación; resto: "message"
        json.optJSONObject("errors")?.keys()?.asSequence()?.firstOrNull()
            ?.let { json.getJSONObject("errors").optJSONArray(it)?.optString(0) }
            ?.takeIf { it.isNotBlank() }
            ?: json.optString("message", porDefecto)
    } catch (_: Exception) {
        porDefecto
    }