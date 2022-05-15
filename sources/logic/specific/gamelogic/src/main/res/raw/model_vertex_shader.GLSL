uniform mat4 u_MVP;

attribute vec3 a_position;

attribute float a_isEmpty;

attribute vec2 a_textureCoordinates;

varying float isEmpty;

varying vec2 textureCoordinates;

void main()
{
    if (a_isEmpty > 0.0) {
        // move point out of scene
        gl_Position = vec4(0, 0, -2, 1.0);
    } else {
        gl_Position = u_MVP * vec4(a_position, 1.0);
    }
    isEmpty = a_isEmpty;
    textureCoordinates = a_textureCoordinates;
}
