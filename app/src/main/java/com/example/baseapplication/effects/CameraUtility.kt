import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

class CameraUtility(private val context: Context) {
    var imageUri: Uri? = createImageUri()

    fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "MyImage_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Define o caminho relativo para salvar a imagem
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Visit-Aveiro")
            }
        }

        imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        return imageUri
    }
}
