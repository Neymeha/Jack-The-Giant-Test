package helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

public class GameManager { // Singleton класс для того что бы быть менеджером нашей игры, управлять паузами
    private static GameManager ourInstance = new GameManager(); // внутренняя приватная статичная переменная которая содержит уже готовый экземпляр класса

    public GameData gameData; // класс который предоставляет обьект который мы будем сохранять
    private Json json = new Json(); // класс с помощью которого мы будет сохранять, видимо конвертируя наш обьект в джейсон и сохраняя на диске
    private FileHandle fileHandle = Gdx.files.local("bin/GameData.json"); // репрезентация нашего файла, и пути к нему соответственно, нужен local(R/W) а не internal(read only)
    public boolean gameStartedFromMainMenu, isPaused = true;
    public int lifeScore, coinScore, score;
    private Music music;
    private GameManager(){ // должен быть приватный конструктор
    }
    public void initializeGameData() {
        if (!fileHandle.exists()) {
            gameData = new GameData();

            gameData.setHighscore(0);
            gameData.setCoinHighscore(0);

            gameData.setEasyDifficulty(false);
            gameData.setMediumDifficulty(true);
            gameData.setHardDifficulty(false);

            gameData.setMusicOn(false);

            saveData();
        } else {
            loadData();
        }
    }
    public void saveData() {
        if (gameData!=null) {
            fileHandle.writeString(Base64Coder.encodeString(json.prettyPrint(gameData)), false); // prittyPrint записывает все что будет в нашей гейм дата в виде строки, фолс - это значит мы хотим перезаписывать(override) инфу а не добавлять(append), так же добавляе минимальную защиту данных
        }
    }
    public void loadData() {
        gameData = json.fromJson(GameData.class, Base64Coder.decodeString(fileHandle.readString())); // указываем с каким классом работаем, и путь который уже содержит наш хендлер
    }
    public  void checkForNewHighscores() {
        // проверяем выросли ли наши счетчики посравнению с сохраненными в файле
        int oldHighscore = gameData.getHighscore();
        int oldCoinscore = gameData.getCoinHighscore();

        if (oldHighscore < score) {
            gameData.setHighscore(score);
        }

        if (oldCoinscore < coinScore) {
            gameData.setCoinHighscore(coinScore);
        }
        saveData();
    }

    public void playMusic() { // метод запуска и инициализации музыки
        if (music==null) {
            music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Background.mp3"));
        }
        if (!music.isPlaying()) {
            music.setLooping(true);
            music.play();
        }
    }

    public void stopMusic() { // метод остановки музыки и освобождения ее из памяти
        if (music.isPlaying()) {
            music.stop();
            music.dispose();
        }
    }

    public static GameManager getInstance() { // и геттер статичный который будет возвращать нам единственного представителя этого класса
        return ourInstance;
    }
}











