#include "sampling.glh"

bool inRange(float val) {
	return val >= 0.01 && val <= 0.99;
}

float calcShadowAmt(sampler2D shadowMap, vec4 initialShadowMapCoords) {
	vec3 shadowMapCoords = (initialShadowMapCoords.xyz/initialShadowMapCoords.w);
	//TODO: Remove Dynamic Branching Here
	if (inRange(shadowMapCoords.z) && inRange(shadowMapCoords.x) && inRange(shadowMapCoords.y))
		return sampleVarianceShadowMap(shadowMap, shadowMapCoords.xy, shadowMapCoords.z, R_shadowVarianceMin, R_shadowLightBleedReduction);
	else 
		return 1.0;
}

void main() {
	vec3 directionToEye = normalize(C_eyePos - worldPos0);
	vec2 texCoords = calcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0,
						   dispMapScale, dispMapBias);
	
	vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture2D(normalMap, texCoords).xyz - 1));
    
    vec4 lightingAmt = calcLightingEffect(normal, worldPos0) * calcShadowAmt(R_shadowMap, shadowMapCoords0);
    gl_FragColor = texture2D(diffuse, texCoords) * lightingAmt;
}