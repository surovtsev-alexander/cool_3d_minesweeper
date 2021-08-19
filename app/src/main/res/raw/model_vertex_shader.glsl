uniform mat4 u_MVP_Matrix;

attribute vec3 a_Position;

attribute float a_isEmpty;

attribute vec2 a_TextureCoordinates;

varying float isEmpty;

varying vec2 textureCoordinates;

void main()
{
    gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
    isEmpty = a_isEmpty;
    textureCoordinates = a_TextureCoordinates;
}
