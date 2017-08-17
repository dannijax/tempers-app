package chapters.utils.location


//fun calculateDistance(latLngStart: LatLng?, latLngFinish: LatLng?): Float {
//    if (latLngStart?.latitude == 0.0 && latLngStart?.longitude == 0.0 || latLngStart == null) return -1f
//    val result = FloatArray(1)
//    Location.distanceBetween(latLngStart.latitude, latLngStart.longitude,
//            latLngFinish?.latitude ?: 0.0, latLngFinish?.longitude ?: 0.0, result)
//    return result[0]
//}
//
//fun convertDistance(distance: Float?, c: Context): String {
//    val d = distance?.toInt() ?: 0
//    if (d < 1000) {
//        return String.format(c.getString(R.string.m), d)
//    } else {
//        val km = d / 1000
//        return String.format(c.getString(R.string.km), km)
//    }
//}