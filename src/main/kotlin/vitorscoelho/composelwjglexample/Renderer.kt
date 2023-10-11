package vitorscoelho.composelwjglexample.lwjgl

import org.joml.Matrix4f
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack.stackPush

private const val HALF_SIZE = 150f

class Renderer {
    private val vertices = floatArrayOf(
        -HALF_SIZE, -HALF_SIZE,
        +HALF_SIZE, -HALF_SIZE,
        +HALF_SIZE, +HALF_SIZE,

        -HALF_SIZE, -HALF_SIZE,
        +HALF_SIZE, +HALF_SIZE,
        -HALF_SIZE, +HALF_SIZE,
    )
    private val program: Program by lazy { Program() }
    private val vaoId: Int by lazy { glGenVertexArrays() }

    fun setup() {
        glBindVertexArray(vaoId)

        stackPush().use { stack ->
            val vboId: Int = glGenBuffers()
            val verticesBuffer = stack.mallocFloat(vertices.size).put(0, vertices)//.flip()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)
        }

        glVertexAttribPointer(Program.POSITION_LOCATION, Program.POSITION_VERTEX_SIZE, GL_FLOAT, false, 0, 0)
        glBindVertexArray(0)
    }

    fun render(transformationMatrix: Matrix4f) {
        program.bind()
        stackPush().use { stack ->
            glUniformMatrix4fv(
                program.transformationMatrixUniformLocation,
                false,
                transformationMatrix.get(stack.mallocFloat(16))
            )
        }
        glBindVertexArray(vaoId)
        glDrawArrays(GL_TRIANGLES, 0, vertices.size / Program.POSITION_VERTEX_SIZE)
        program.unbind()
    }

    fun dispose() {
        program.dispose()
    }
}