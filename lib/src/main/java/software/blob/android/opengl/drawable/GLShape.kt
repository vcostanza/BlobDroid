package software.blob.android.opengl.drawable

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES30.*
import android.opengl.GLUtils
import android.opengl.Matrix
import software.blob.android.opengl.shader.GLBasicShader
import software.blob.android.opengl.shader.GLShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * OpenGL shape renderer
 */
open class GLShape(initialCapacity: Int = 10) : GLDrawable {

    // Basic parameters
    protected var vertices: FloatBuffer? = null
    protected var vertexCount = 0
    var drawMode = GL_TRIANGLES
    var lineWidth = 0f
    var visible = true

    // Texture parameters
    protected var texHandle: IntArray? = null
    protected var texCoords: FloatBuffer? = null

    // Transformations
    protected val transformMatrix = FloatArray(16)
    protected var transformInvalid = true
    var x = 0f
        set(x) { field = changed(field, x) }
    var y = 0f
        set(y) { field = changed(field, y) }
    var rotation = 0f
        set(rot) { field = changed(field, rot) }
    var scaleX = 1f
        set(sx) { field = changed(field, sx) }
    var scaleY = 1f
        set(sy) { field = changed(field, sy) }

    /**
     * Set the color of the drawing
     */
    var color = Color.WHITE
        set(value) {
            if (field != value) {
                field = value
                _colorFlt[0] = Color.red(value) / 255f
                _colorFlt[1] = Color.green(value) / 255f
                _colorFlt[2] = Color.blue(value) / 255f
                _colorFlt[3] = Color.alpha(value) / 255f
            }
        }
    private val _colorFlt = floatArrayOf(1f, 1f, 1f, 1f)

    init {
        vertices = createFloatBuffer(initialCapacity)
        vertices?.limit(0)
        resetTransform()
    }

    /**
     * Add vertices to the shape
     * @param pos Add position
     * @param coords Coordinates to add
     */
    fun addVertices(pos: Int, coords: FloatArray) {
        val limit = pos + coords.size
        var verts = vertices
        if (verts == null || limit >= verts.capacity()) {
            val newVerts = createFloatBuffer(limit)
            if (verts != null)
                newVerts.put(verts)
            verts = newVerts
        }
        verts.limit(limit)
        verts.position(pos)
        verts.put(coords)
        verts.position(0)
        vertexCount = limit / COORDS_PER_VERTEX
        vertices = verts
    }

    /**
     * Add vertices to the shape
     * @param coords Coordinates to add
     */
    fun addVertices(coords: FloatArray) {
        addVertices(vertices?.limit() ?: 0, coords)
    }

    /**
     * Add a single vertex to this shape
     * @param x X coordinate
     * @param y Y coordinate
     */
    fun addVertex(x: Float, y: Float) {
        addVertices(floatArrayOf(x, y))
    }

    /**
     * Set vertices for the shape
     * @param coords Coordinates
     */
    fun setVertices(coords: FloatArray) {
        addVertices(0, coords)
    }

    /**
     * Reset the vertices position and limit
     */
    fun resetVertices() {
        vertices?.clear()
        vertices?.limit(0)
        vertexCount = 0
    }

    /**
     * Translate the shape by X and Y amount
     * @param x X translation
     * @param y Y translation
     */
    fun translate(x: Float, y: Float) {
        this.x += x
        this.y += y
        transformInvalid = true
    }

    /**
     * Rotate the shape by a given amount
     * @param deg Degrees to rotate the shape
     */
    fun rotate(deg: Float) {
        this.rotation += deg
        transformInvalid = true
    }

    /**
     * Scale the shape by X and Y amount
     * @param xScale X scale
     * @param yScale Y scale
     */
    fun scale(xScale: Float, yScale: Float) {
        this.scaleX *= xScale
        this.scaleY *= yScale
        transformInvalid = true
    }

    /**
     * Set the texture bitmap
     * @param bitmap Bitmap to use for the texture
     */
    fun setTexture(bitmap: Bitmap) {
        deleteTexture(texHandle)
        texHandle = loadTexture(bitmap)
    }

    /**
     * Set the 2D texture coordinates (UVs)
     * @param uvs UV coordinates
     */
    fun setTextureCoords(uvs: FloatArray) {
        val limit = uvs.size
        var tc = texCoords
        if (tc == null || limit >= tc.capacity())
            tc = createFloatBuffer(limit)
        tc.clear()
        tc.put(uvs)
        tc.position(0)
        tc.limit(limit)
        texCoords = tc
    }

    /**
     * Reset transformation matrix
     */
    protected fun resetTransform() {
        Matrix.setIdentityM(transformMatrix, 0)
    }

    /**
     * Shorthand method for setting a transform value and invalidating the transform
     * matrix if the values are not equal
     */
    protected fun changed(v1: Float, v2: Float): Float {
        if (v1 != v2) transformInvalid = true
        return v2
    }

    /**
     * Apply transformation to the matrix using the translation, rotation, and scale vars
     */
    protected fun applyTransform() {
        if (transformInvalid) {
            resetTransform()
            Matrix.scaleM(transformMatrix, 0, scaleX, scaleY, 1f)
            Matrix.rotateM(transformMatrix, 0, rotation, 0f, 0f, 1f)
            Matrix.translateM(transformMatrix, 0, x, y, 0f)
            transformInvalid = false
        }
    }

    /**
     * Draw the shape
     * @param shader Shader used to render this shape
     */
    override fun draw(shader: GLShader) {

        if (!visible || vertices == null || shader !is GLBasicShader) return

        // Color vector
        if (shader.colorIdx != -1)
            glUniform4fv(shader.colorIdx, 1, _colorFlt, 0)

        // Textures
        var texEnabled = false
        if (shader.textureIdx != -1 && shader.texCoordIdx != -1) {
            if (texCoords != null && texHandle != null) {
                glUniform1i(shader.texEnabled, 1)
                glActiveTexture(GL_TEXTURE0)
                glBindTexture(GL_TEXTURE_2D, texHandle!![0])
                glUniform1i(shader.textureIdx, 0)
                glVertexAttribPointer(shader.texCoordIdx, COORDS_PER_TEXTURE,
                    GL_FLOAT, false, 0, texCoords)
                glEnableVertexAttribArray(shader.texCoordIdx)
                texEnabled = true
            } else
                glUniform1i(shader.texEnabled, 0)
        }

        // Line width
        if (lineWidth > 0)
            glLineWidth(lineWidth)

        // Transformation matrix
        if (shader.transformIdx != -1) {
            applyTransform()
            glUniformMatrix4fv(shader.transformIdx, 1, false, transformMatrix, 0)
        }

        // Draw the shape
        glVertexAttribPointer(shader.vertexIdx, COORDS_PER_VERTEX,
            GL_FLOAT, false, VERTEX_STRIDE, vertices)
        glDrawArrays(drawMode, 0, vertexCount)

        // Need to disable texture vertex array when we're done
        if (texEnabled)
            glDisableVertexAttribArray(shader.texCoordIdx)
    }

    companion object {

        // Float uses 4 bytes
        private const val SIZE_OF_FLOAT = 4

        // Always 2 coordinates per vertex in 2D
        private const val COORDS_PER_VERTEX = 2

        // Always 2 coordinates for 2D texture
        private const val COORDS_PER_TEXTURE = 2

        // Number of bytes per vertex
        private const val VERTEX_STRIDE = COORDS_PER_VERTEX * SIZE_OF_FLOAT

        /**
         * Create a float buffer with a given capacity
         * @param capacity Capacity (number of floating point values)
         */
        protected fun createFloatBuffer(capacity: Int): FloatBuffer {
            val bb = ByteBuffer.allocateDirect(capacity * SIZE_OF_FLOAT)
            bb.order(ByteOrder.nativeOrder())
            return bb.asFloatBuffer()
        }

        /**
         * Load a bitmap into a texture and return the handle
         * @param bitmap Bitmap to load
         * @return Texture handle
         */
        protected fun loadTexture(bitmap: Bitmap): IntArray {
            val handle = IntArray(1)
            glGenTextures(1, handle, 0)
            if (handle[0] != 0) {
                glBindTexture(GL_TEXTURE_2D, handle[0])
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
            }
            return handle
        }

        /**
         * Remove a texture from GL memory
         * @param handle Texture handle
         */
        protected fun deleteTexture(handle: IntArray?) {
            if (handle != null && handle.size == 1)
                glDeleteTextures(1, handle, 0)
        }
    }
}