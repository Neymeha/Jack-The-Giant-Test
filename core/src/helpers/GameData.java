package helpers;

// класс для сохранения данных
public class GameData {
    private int highscore; // счетчик высшего прогресса
    private int coinHighscore; // счетчик высшего прогресса монет
    private boolean easyDifficulty; // какая сложность
    private boolean mediumDifficulty;
    private boolean hardDifficulty;
    private boolean musicOn; // включена ли музыка

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public int getCoinHighscore() {
        return coinHighscore;
    }

    public void setCoinHighscore(int coinHighscore) {
        this.coinHighscore = coinHighscore;
    }

    public boolean isEasyDifficulty() {
        return easyDifficulty;
    }

    public void setEasyDifficulty(boolean easyDifficulty) {
        this.easyDifficulty = easyDifficulty;
    }

    public boolean isMediumDifficulty() {
        return mediumDifficulty;
    }

    public void setMediumDifficulty(boolean mediumDifficulty) {
        this.mediumDifficulty = mediumDifficulty;
    }

    public boolean isHardDifficulty() {
        return hardDifficulty;
    }

    public void setHardDifficulty(boolean hardDifficulty) {
        this.hardDifficulty = hardDifficulty;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }


}
