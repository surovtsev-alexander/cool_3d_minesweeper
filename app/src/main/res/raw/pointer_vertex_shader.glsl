uniform mat4 u_MVP;
uniform float u_pointSize;

attribute vec3 a_position;


void main()
{
    gl_Position = u_MVP * vec4(a_position, 1.0);
    gl_PointSize = u_pointSize;
}
