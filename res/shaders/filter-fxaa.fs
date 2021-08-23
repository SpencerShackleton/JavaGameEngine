#version 120

varying vec2 texCoord0;
 
uniform sampler2D R_filterTexture;
uniform vec3 R_inverseFilterTextureSize;
uniform float R_fxaaSpanMax;
uniform float R_fxaaReduceMin;
uniform float R_fxaaReduceMul;

float rgb2luma(vec3 rgb){
    return (dot(rgb, vec3(0.299, 0.587, 0.114)));
}

void main() {
	//float MAX_SPAN = 8.0;
	//float REDUCE_MIN = 1.0/128.0;
	//float REDUCE_MUL = 1.0/8.0;

	vec3 luma = vec3(0.299, 0.587, 0.114);
	vec2 offset = R_inverseFilterTextureSize.xy;
	float lumaTL = rgb2luma(texture2D(R_filterTexture, texCoord0.xy + (vec2(-1.0, -1.0) * offset)).xyz);
	float lumaTR = rgb2luma(texture2D(R_filterTexture, texCoord0.xy + (vec2(1.0, -1.0) * offset)).xyz);
	float lumaBL = rgb2luma(texture2D(R_filterTexture, texCoord0.xy + (vec2(-1.0, 1.0) * offset)).xyz);
	float lumaBR = rgb2luma(texture2D(R_filterTexture, texCoord0.xy + (vec2(1.0, 1.0) * offset)).xyz);
	float lumaM = rgb2luma(texture2D(R_filterTexture, texCoord0.xy).xyz);
	
	vec2 dir;
	dir.x = -((lumaTL + lumaTR) - (lumaBL + lumaBR));
	dir.y = ((lumaTL + lumaBL) - (lumaTR + lumaBR));
	
	float dirReduce = max((lumaTL + lumaTR + lumaBL + lumaBR) * 0.25 * R_fxaaReduceMul, R_fxaaReduceMin);
	float dirAdjustment = 1.0/(min(abs(dir.x), abs(dir.y) + dirReduce));
	
	dir = min(vec2(R_fxaaSpanMax, R_fxaaSpanMax), 
			  max(vec2(-R_fxaaSpanMax, -R_fxaaSpanMax), dir * dirAdjustment)) * offset;
	
	vec3 res1 = (1.0/2.0) * (
				 texture2D(R_filterTexture, texCoord0.xy + (dir * vec2(1.0/3.0 - 0.5))).xyz + 
				 texture2D(R_filterTexture, texCoord0.xy + (dir * vec2(2.0/3.0 - 0.5))).xyz);
				 
	vec3 res2 = res1 * (1.0/2.0) + (1.0/4.0) * (
				 texture2D(R_filterTexture, texCoord0.xy + (dir * vec2(0.0/3.0 - 0.5))).xyz + 
				 texture2D(R_filterTexture, texCoord0.xy + (dir * vec2(3.0/3.0 - 0.5))).xyz);
	
	float lumaMin = min(lumaM, min(min(lumaTL, lumaTR), min(lumaBL, lumaBR)));
	float lumaMax = max(lumaM, max(max(lumaTL, lumaTR), max(lumaBL, lumaBR)));
	float lumaResult2 = dot(luma, res2);
	
	if (lumaResult2 < lumaMin || lumaResult2 > lumaMax)
		gl_FragColor = vec4(res1, 1.0);
	else
		gl_FragColor = vec4(res2, 1.0);
}