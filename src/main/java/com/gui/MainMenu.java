package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;

/**
 * MainMenu class represents the main menu screen of the game.
 */
public class MainMenu implements Screen {
    private final Stage stage;
    private final TextField textField;
    private final Skin skin;
    private final Music mainMenuMusic;

    /**
     * Constructor to initialize the main menu screen.
     *
     * @param game            The game launcher instance.
     * @param mainMenuMusic   The music to be played on the main menu screen.
     * @param settingsMusic   The music to be played on the settings menu screen.
     * @param gameMusic       The music to be played during the game.
     * @param parameters      The parameters for the game settings.
     */
    public MainMenu(GameLauncher game, Music mainMenuMusic, Music settingsMusic, Music gameMusic, HashMap<String, Double> parameters) {
        this.mainMenuMusic = mainMenuMusic;
        stage = new Stage(new ScreenViewport());

        // Create a new skin
        skin = new Skin();

        // Load the textures
        Texture textfieldTexture = new Texture(Gdx.files.internal("assets/skin/textfield.png"));
        Texture cursorTexture = new Texture(Gdx.files.internal("assets/skin/textcursor.png"));
        Texture selectionTexture = new Texture(Gdx.files.internal("assets/skin/selection.png"));
        Texture backgroundTexture = new Texture(Gdx.files.internal("assets/skin/background.png"));

        // Load the font
        BitmapFont font = new BitmapFont(Gdx.files.internal("assets/skin/default-font.fnt"));

        // Add textures and font to the skin
        skin.add("default-font", font);
        skin.add("textfield", textfieldTexture);
        skin.add("cursor", cursorTexture);
        skin.add("selection", selectionTexture);

        // Create and set styles
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default-font");
        textFieldStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        textFieldStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTexture));
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(textfieldTexture));
        textFieldStyle.selection = new TextureRegionDrawable(new TextureRegion(selectionTexture));
        skin.add("default", textFieldStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(textfieldTexture));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(textfieldTexture));
        textButtonStyle.checked = new TextureRegionDrawable(new TextureRegion(textfieldTexture));
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(textfieldTexture));
        textButtonStyle.font = skin.getFont("default-font");
        textButtonStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        skin.add("default", textButtonStyle);

        // Set up the background image
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Set up the UI components
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        textField = new TextField("", skin);
        textField.setAlignment(Align.center);
        table.add(textField).width(800).pad(10);
        table.row();

        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainMenuMusic.pause();
                if (!gameMusic.isPlaying()) {
                    gameMusic.setLooping(true);
                    gameMusic.play();
                }
                game.setScreen(new Golf3D(gameMusic, textField.getText(), parameters)); // Pass the game instance and music
            }
        });
        table.add(startButton).width(800).pad(10);
        table.row();

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainMenuMusic.pause();
                if (!settingsMusic.isPlaying()) {
                    settingsMusic.setLooping(true);
                    settingsMusic.play();
                }
                game.setScreen(new SettingsMenu(game, mainMenuMusic, settingsMusic, gameMusic, parameters)); // Pass the game instance and music
            }
        });
        table.add(settingsButton).width(800).pad(10);
        table.row();

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(quitButton).width(800).pad(10);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (!mainMenuMusic.isPlaying()) {
            mainMenuMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
