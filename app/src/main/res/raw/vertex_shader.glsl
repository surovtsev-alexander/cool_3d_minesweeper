uniform mat4 u_VP_Matrix;
uniform mat4 u_M_Matrix;

attribute vec3 a_Position;

attribute float a_TriangleNum;
attribute float a_TriangleTexture;

void main()
{
    /*
    gl_Position = a_Position;
    */
    gl_Position = u_VP_Matrix * u_M_Matrix * vec4(a_Position, 1.0);
    gl_PointSize = 10.0;
}
