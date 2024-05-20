package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Sun {
    private DirectionalLight directionalLight;
    private FrameBuffer shadowFrameBuffer;
    private Camera shadowCamera;
    private ShaderProgram shadowShader;
    private TextureRegion shadowMap;

    private static final int SHADOW_MAP_SIZE = 2048;

    public Sun(Color color, Vector3 direction) {
        // Set up the directional light
        directionalLight = new DirectionalLight();
        directionalLight.set(color, direction);

        // Set up the shadow frame buffer
        shadowFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, SHADOW_MAP_SIZE, SHADOW_MAP_SIZE, true);
        shadowMap = new TextureRegion(shadowFrameBuffer.getColorBufferTexture());
        shadowMap.flip(false, true);

        // Set up the shadow camera
        shadowCamera = new OrthographicCamera(100, 100);
        shadowCamera.position.set(-direction.x * 100f, -direction.y * 100f, -direction.z * 100f);
        shadowCamera.lookAt(0, 0, 0);
        shadowCamera.near = 0.1f;
        shadowCamera.far = 300f;
        shadowCamera.update();

        // Set up the shadow shader
        shadowShader = new ShaderProgram(Gdx.files.internal("assets/shadow_vertex.glsl"), Gdx.files.internal("assets/shadow_fragment.glsl"));
        if (!shadowShader.isCompiled()) {
            throw new GdxRuntimeException("Failed to compile shadow shader: " + shadowShader.getLog());
        }
    }

    public void beginShadowPass() {
        shadowFrameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void endShadowPass() {
        shadowFrameBuffer.end();
    }

    public void renderShadowPass(ModelBatch modelBatch, ModelInstance instance) {
        modelBatch.render(instance);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public TextureRegion getShadowMap() {
        return shadowMap;
    }

    public Camera getShadowCamera() {
        return shadowCamera;
    }

    public ShaderProgram getShadowShader() {
        return shadowShader;
    }

    public void dispose() {
        shadowFrameBuffer.dispose();
        shadowShader.dispose();
    }
}
