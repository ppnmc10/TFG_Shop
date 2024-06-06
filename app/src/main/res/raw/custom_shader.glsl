// custom_shader.glsl
uniform float2 resolution;
uniform float4 color;
uniform float4 color2;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    half4 mixedColor = mix(color, color2, uv.x);
    return mixedColor;
}
