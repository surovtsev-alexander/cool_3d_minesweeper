precision mediump float;

uniform vec4 u_Color;

varying float triangleTexture;

void main()
{
    gl_FragColor = u_Color;

    int tt = int(floor(triangleTexture));

    if (tt == 0) {
        gl_FragColor = vec4(0f, 1f, 0f, 1f);
    } else if (tt == 1) {
        gl_FragColor = vec4(0f, 0f, 0f, 1f);
    } else if (tt == 2) {
        gl_FragColor = vec4(0f, 0f, 0f, 1f);
    } else if (tt == 3) {
        gl_FragColor = vec4(0f, 0f, 0f, 1f);
    } else if (tt == 4) {
        gl_FragColor = vec4(0f, 0f, 0f, 1f);
    } else {
        gl_FragColor = vec4(0f, 0f, 0f, 1f);
    }
}
