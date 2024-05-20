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

public class SettingsMenu implements Screen {
    private Stage stage;
    private Slider volumeSlider;
    private TextButton backButton;
    private TextButton terrainSettings;
    private TextButton gameLogicsSettings;
    private GameLauncher game;
    private Skin skin;
    private Image backgroundImage;
    private TextField textField1;
    private TextField textField2;
    private Music mainMenuMusic;
    private Music settingsMusic;
    private Music gameMusic;
    private HashMap<String, Double> parameters;

    public SettingsMenu(GameLauncher game, Music mainMenuMusic, Music settingsMusic, Music gameMusic, HashMap<String, Double> parameters) {
        this.game = game;
        this.mainMenuMusic = mainMenuMusic;
        this.settingsMusic = settingsMusic;
        this.gameMusic = gameMusic;
        stage = new Stage(new ScreenViewport());
        this.parameters = parameters;
        // Create a new skin
        skin = new Skin();

        // Load the textures
        Texture textfieldTexture = new Texture(Gdx.files.internal("assets/skin/textfield.png"));
        Texture cursorTexture = new Texture(Gdx.files.internal("assets/skin/textcursor.png"));
        Texture selectionTexture = new Texture(Gdx.files.internal("assets/skin/selection.png"));
        Texture backgroundTexture = new Texture(Gdx.files.internal("assets/skin/background.png"));
        Texture sliderBackgroundTexture = new Texture(Gdx.files.internal("assets/skin/slider_background.png"));

        // Load the font
        BitmapFont font = new BitmapFont(Gdx.files.internal("assets/skin/default-font.fnt"));

        // Add textures and font to the skin
        skin.add("default-font", font);
        skin.add("textfield", textfieldTexture);
        skin.add("cursor", cursorTexture);
        skin.add("selection", selectionTexture);
        skin.add("slider_background", sliderBackgroundTexture);

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

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBackgroundTexture));
        skin.add("default-horizontal", sliderStyle);

        // Set up the background image
        backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Set up the UI components
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        volumeSlider = new Slider(0, 100, 1, false, skin, "default-horizontal");
        volumeSlider.setValue(settingsMusic.getVolume() * 100); // Initialize the slider with the current volume
        volumeSlider.addListener(event -> {
            float volume = volumeSlider.getValue() / 100f;
            settingsMusic.setVolume(volume);
            mainMenuMusic.setVolume(volume);
            gameMusic.setVolume(volume);
            return false;
        });
        table.add(new Label("Volume", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE))).pad(10);
        table.add(volumeSlider).width(1400).center().pad(10);
        table.row();


        // Add a label for the button

        // Create a table for the buttons and add the buttons to it
        /*Table buttonTable = new Table();
        TextButton button1 = new TextButton("Morning", skin);
        TextButton button2 = new TextButton("Day", skin);
        TextButton button3 = new TextButton("Night", skin);
        buttonTable.add(button1).pad(10);
        buttonTable.add(button2).pad(10);
        buttonTable.add(button3).pad(10);

        // Add the button table to the main table
        table.add(buttonTable).colspan(2).center().pad(10);
        table.row();*/
        terrainSettings = new TextButton("Terrain Settings", skin);
        terrainSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TerrainSettings(game, gameMusic, mainMenuMusic, settingsMusic, parameters)); // Pass the game instance and music
            }
        });
        table.add(terrainSettings).colspan(2).center().pad(10);
        table.row();
        gameLogicsSettings = new TextButton("Game Logics", skin);
        gameLogicsSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TerrainSettings(game, gameMusic, mainMenuMusic, settingsMusic, parameters)); // Pass the game instance and music
            }
        });
        table.add(gameLogicsSettings).colspan(2).center().pad(10);
        table.row();
        backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsMusic.pause();
                if (!mainMenuMusic.isPlaying()) {
                    mainMenuMusic.setLooping(true);
                    mainMenuMusic.play();
                }
                game.setScreen(new MainMenu(game, mainMenuMusic, settingsMusic, gameMusic, parameters)); // Pass the game instance and music
            }
        });
        table.add(backButton).colspan(2).center().pad(10);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (!settingsMusic.isPlaying()) {
            settingsMusic.play();
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
    public void pause() {}

    @Override
    public void resume() {}

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
