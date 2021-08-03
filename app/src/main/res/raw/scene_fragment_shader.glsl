precision mediump float;

uniform vec4 u_Color;

varying float triangleNum;
varying float triangleTexture;
varying vec2 textureCoordinates;

uniform sampler2D u_TextureUnit;

void main()
{
    int tt = int(floor(triangleTexture));

    vec4 frag_color;

    if (tt == 0) {
        frag_color = texture2D(u_TextureUnit, textureCoordinates);
    } else if (tt == 1) {
        frag_color = vec4(1f, 0f, 0f, 1f);
    } else if (tt == 2) {
        frag_color = vec4(0f, 0f, 1f, 1f);
    } else if (tt == 3) {
        frag_color = vec4(0.5f, 1f, 0.5f, 1f);
    } else if (tt == 4) {
        frag_color = vec4(1f, 0.5f, 0.5f, 1f);
    } else {
        frag_color = vec4(0.5f, 0.5f, 1f, 1f);
    }

    gl_FragColor = frag_color;
}
