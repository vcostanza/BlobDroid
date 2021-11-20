package software.blob.android.opengl.drawable

import android.graphics.*
import software.blob.android.opengl.shader.GLShader
import kotlin.math.max

/**
 * Text rendered in a rectangle
 */
class GLTextRect(text: String = "") : GLShape(6) {

    /**
     * The width of the text in pixels
     */
    var width: Float = 0f
        private set

    /**
     * The height of the text in pixels
     */
    var height: Float = 0f
        private set

    /**
     * The text that is rendered
     */
    var text = text
        set(text) {
            if (field != text) {
                field = text
                _invalid = true
            }
        }

    /**
     * The text size in floating-point pixels
     */
    var textSize = 32f
        set(size) {
            if (field != size) {
                field = size
                _invalid = true
            }
        }

    /**
     * The text color (ARGB integer)
     */
    var textColor = Color.WHITE
        set(color) {
            if (field != color) {
                field = color
                _invalid = true
            }
        }

    // Whether the text is invalid and needs to be rasterized again
    private var _invalid = true

    // Used during redraws
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also { it.style = Paint.Style.FILL }
    private val path = Path()
    private val bounds = RectF()

    override fun draw(shader: GLShader) {
        // Need to regenerate the texture
        if (_invalid) {
            setupTexture()
            _invalid = false
        }

        super.draw(shader)
    }

    /**
     * Use Android's nice built in text rendering to draw the text to a bitmap
     * and use the bitmap as a texture for the rectangle
     */
    private fun setupTexture() {
        // Get the bounds of the text
        paint.textSize = textSize
        paint.color = textColor
        paint.getTextPath(text, 0, text.length, 0f, 0f, path)
        path.computeBounds(bounds, false)

        // Set the size of the rectangle
        width = max(bounds.right, 1f)
        height = max(bounds.height(), 1f)
        setVertices(floatArrayOf(
            0f, 0f,
            width, height,
            width, 0f,
            0f, height,
            width, height,
            0f, 0f
        ))

        setTextureCoords(floatArrayOf(
            0f, 0f,
            1f, 1f,
            1f, 0f,
            0f, 1f,
            1f, 1f,
            0f, 0f
        ))

        // Render the text to a bitmap using a canvas
        val bmp = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawText(text, 0f, height, paint)

        // Load the bitmap into a texture
        setTexture(bmp)
    }
}