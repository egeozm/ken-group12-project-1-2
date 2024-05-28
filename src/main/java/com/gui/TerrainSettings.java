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
 * The TerrainSettings class provides a screen for configuring the terrain settings in the game.
 */
public class TerrainSettings implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Music settingsMusic;
    private HashMap<String, Double> parameters;
    private final TextField mapHeightTextField;
    private final TextField mapWidthTextField;

    /**
     * Constructs a TerrainSettings screen.
     *
     * @param game         the GameLauncher instance
     * @param gameMusic    the music to play during the game
     * @param mainMenuMusic the music to play in the main menu
     * @param settingsMusic the music to play in the settings menu
     * @param parameters   the terrain parameters
     */
    public TerrainSettings(GameLauncher game, Music gameMusic, Music mainMenuMusic, Music settingsMusic, HashMap<String, Double> parameters) {
        this.settingsMusic = settingsMusic;
        stage = new Stage(new ScreenViewport());
        initHashMap(this.parameters);

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
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Set up the UI components
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Table sizeField = new Table();
        sizeField.add(new Label("Map size", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE))).pad(10);
        mapHeightTextField = new TextField("25", skin);
        mapHeightTextField.setAlignment(Align.center);
        sizeField.add(mapHeightTextField).width(400).center().pad(5);
        mapWidthTextField = new TextField("25", skin);
        mapWidthTextField.setAlignment(Align.center);
        sizeField.add(mapWidthTextField).width(400).center().pad(5);
        table.add(sizeField).colspan(2).center().pad(5);
        table.row();

        Table ballField = new Table();
        ballField.add(new Label("Ball position", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE))).pad(10);
        TextField ballX = new TextField("Random", skin);
        ballX.setAlignment(Align.center);
        ballField.add(ballX).width(400).center().pad(5);
        TextField ballZ = new TextField("Random", skin);
        ballZ.setAlignment(Align.center);
        ballField.add(ballZ).width(400).center().pad(5);
        table.add(ballField).colspan(2).center().pad(5);
        table.row();

        Table golfHoleField = new Table();
        golfHoleField.add(new Label("Golf hole position", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE))).pad(10);
        TextField holeX = new TextField("Random", skin);
        holeX.setAlignment(Align.center);
        golfHoleField.add(holeX).width(400).center().pad(5);
        TextField holeZ = new TextField("Random", skin);
        holeZ.setAlignment(Align.center);
        golfHoleField.add(holeZ).width(400).center().pad(5);
        table.add(golfHoleField).colspan(2).center().pad(5);
        table.row();

        Table spawnField = new Table();
        spawnField.add(new Label("Spawn Rate", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE))).pad(10);
        TextField treeRate = new TextField("0.003", skin);
        treeRate.setAlignment(Align.center);
        spawnField.add(treeRate).width(400).center().pad(5);
        TextField houseRate = new TextField("0.001", skin);
        houseRate.setAlignment(Align.center);
        spawnField.add(houseRate).width(400).center().pad(5);
        table.add(spawnField).colspan(2).center().pad(5);
        table.row();

        Table functionProperties = new Table();
        functionProperties.add(new Label("Terrain Settings", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE))).pad(10);
        TextField heightCoefficient = new TextField("1", skin);
        heightCoefficient.setAlignment(Align.center);
        functionProperties.add(heightCoefficient).width(400).center().pad(5);
        TextField yBias = new TextField("0", skin);
        yBias.setAlignment(Align.center);
        functionProperties.add(yBias).width(400).center().pad(5);
        TextField functionStep = new TextField("0.1", skin);
        functionStep.setAlignment(Align.center);
        functionProperties.add(functionStep).width(400).center().pad(5);
        table.add(functionProperties).colspan(2).center().pad(5);
        table.row();

        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parameters.put("width", parseString(mapWidthTextField.getText()));
                parameters.put("height", parseString(mapHeightTextField.getText()));
                parameters.put("xBall", parseString(ballX.getText()));
                parameters.put("zBall", parseString(ballZ.getText()));
                parameters.put("xHole", parseString(holeX.getText()));
                parameters.put("zHole", parseString(holeZ.getText()));
                parameters.put("treeSpawnRate", parseString(treeRate.getText()));
                parameters.put("housesSpawnRate", parseString(houseRate.getText()));
                parameters.put("heightCoefficient", parseString(heightCoefficient.getText()));
                parameters.put("yBias", parseString(yBias.getText()));
                parameters.put("functionStep", parseString(functionStep.getText()));
                game.setScreen(new SettingsMenu(game, mainMenuMusic, settingsMusic, gameMusic, parameters));
            }
        });
        table.add(backButton).colspan(2).center().pad(5);
    }

    /**
     * Parses a string to a Double value.
     *
     * @param s the string to parse
     * @return the parsed Double value, or Double.NaN if the string is "Random"
     */
    public Double parseString(String s) {
        if (s.equals("Random"))
            return Double.NaN;
        return Double.parseDouble(s);
    }

    /**
     * Initializes the parameters HashMap with default values if it is null or empty.
     *
     * @param a the HashMap to initialize
     */
    public void initHashMap(HashMap<String, Double> a) {
        if (a == null || a.isEmpty()) {
            a = new HashMap<>();
            a.put("width", Double.NaN);
            a.put("height", Double.NaN);
            a.put("xBall", Double.NaN);
            a.put("zBall", Double.NaN);
            a.put("xHole", Double.NaN);
            a.put("zHole", Double.NaN);
            a.put("treeSpawnRate", Double.NaN);
            a.put("housesSpawnRate", Double.NaN);
            a.put("heightCoefficient", Double.NaN);
            a.put("yBias", Double.NaN);
            a.put("functionStep", Double.NaN);
        }
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
