uniform mat4 u_M_Matrix;
uniform mat4 u_VP_Matrix;
uniform float u_PointSize;

attribute vec3 a_Position;


void main()
{
    gl_Position = u_VP_Matrix * u_M_Matrix * vec4(a_Position, 1.0);
    gl_PointSize = u_PointSize;
}
