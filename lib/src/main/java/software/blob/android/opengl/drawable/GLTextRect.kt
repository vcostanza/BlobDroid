package software.blob.android.opengl.drawable

import android.graphics.*
import software.blob.android.opengl.shader.GLShader
import kotlin.math.max

/**
 * Text rendered in a rectangle
 */
class GLTextRect(text: String = "") : GLShape(6) {

    private var _width = 0f
    private var _height = 0f
    private var _invalid = true

    // Used during redraws
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also { it.style = Paint.Style.FILL }
    private val path = Path()
    private val bounds = RectF()

    /**
     * The width and height of the text in pixels
     */
    val width: Float get() { return _width }
    val height: Float get() { return _height }

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
        _width = max(bounds.right, 1f)
        _height = max(bounds.height(), 1f)
        setVertices(floatArrayOf(
            0f, 0f,
            _width, _height,
            _width, 0f,
            0f, _height,
            _width, _height,
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
        val bmp = Bitmap.createBitmap(_width.toInt(), _height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawText(text, 0f, _height, paint)

        // Load the bitmap into a texture
        setTexture(bmp)
    }
}