uniform mat4 u_MVP_Matrix;

attribute vec3 a_Position;

attribute float a_TriangleNum;
attribute float a_TriangleTexture;

attribute vec2 a_TextureCoordinates;

/* unused */
varying float triangleNum;
/* unused */
varying float triangleTexture;

varying vec2 textureCoordinates;

void main()
{
    gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
    /*
    gl_PointSize = 10.0;
    */
    triangleNum = a_TriangleNum;
    triangleTexture = a_TriangleTexture;
    textureCoordinates = a_TextureCoordinates;
}
