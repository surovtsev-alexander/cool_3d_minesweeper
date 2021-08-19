precision mediump float;

uniform vec4 u_Color;

varying float isEmpty;

varying vec2 textureCoordinates;

uniform sampler2D u_TextureUnit;

void main()
{
    if (isEmpty > 0.0) {
        discard;
    }

    vec4 frag_color;

    frag_color = texture2D(u_TextureUnit, textureCoordinates);

    gl_FragColor = frag_color;
}
