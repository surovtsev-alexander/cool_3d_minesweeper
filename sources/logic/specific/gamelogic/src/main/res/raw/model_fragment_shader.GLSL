precision mediump float;

varying float isEmpty;

varying vec2 textureCoordinates;

uniform sampler2D u_textureUnit;

void main()
{
    /*
    // it is decreases rendering speed
    if (isEmpty > 0.0) {
        discard;
    }
    */

    vec4 frag_color;

    frag_color = texture2D(u_textureUnit, textureCoordinates);

    gl_FragColor = frag_color;
}
