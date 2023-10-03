package clouds;

import collectables.Collectable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import helpers.GameInfo;
import helpers.GameManager;
import player.Player;

import java.util.Random;

public class CloudsController {

    private World world;
    private Array <Cloud> clouds = new Array<>(); // массив для созданных облаков
    private Array <Collectable> collectables = new Array<>(); // массив наших итемов
    private final float DISTANCE_BETWEEN_CLOUDS = 250f; // константа для разделения облаков по расстоянию по Y
    private float minX, maxX;
    private float lastCloudPositionY; // нужно что бы избежать наложения созданных облаков
    private float cameraY;
    private Random random = new Random();
    public CloudsController(World world){ // подвязываем в мир и создаем облака в конструкторе
        this.world = world;
        minX = GameInfo.WIDTH/2f - 120;
        maxX = GameInfo.WIDTH/2f + 120;
        createClouds();
        positionClouds(true);
    }

    void createClouds(){ // метод для создания 8 облаков (2 - уничтожающих 6 обычных но с разными текстурами
        for (int i = 0; i<2; i++){
            clouds.add(new Cloud(world, "Dark Cloud"));
        }
        int index = 1; // вся эта шляпа только потому что у нас 3 картины облачка
        for (int i = 0; i<6; i++){
            clouds.add(new Cloud(world, "Cloud "+index));
            index++;
            if (index==4){
                index=1;
            }
        }
        clouds.shuffle(); // перемешиваем
    }

    public void positionClouds(boolean firstTimeArranging){ // задаем позицию нашим облакам, принимает булевое значение, первый раз мы задаем позицию или нет

        while (clouds.get(0).getCloudName().equals("Dark Cloud")) { // делаем так что бы первым не было черное блако
            clouds.shuffle();
        }

        float positionY = 0;
        if (firstTimeArranging) {
            positionY = GameInfo.HEIGHT/2f;
        } else {
            positionY = lastCloudPositionY;
        }

        int controlX = 0;

        for (Cloud c:clouds) { // фор луп для рандома по иксу и по игрику

            if (c.getX() == 0 && c.getY() == 0) { // у только созданных облаков нет позиции, для них и задаем, если есть уже облака с позициями - их не трогаем
                float tempX = 0;
                if (controlX==0) {
                    tempX = randimBetweenNumbers(maxX-40,maxX);
                    controlX = 1;
                    c.setDrawLeft(false);
                } else if (controlX==1) {
                    tempX = randimBetweenNumbers(minX+40, minX);
                    controlX = 0;
                    c.setDrawLeft(true);
                }
                c.setSpritePosition(tempX, positionY);
                positionY -= DISTANCE_BETWEEN_CLOUDS;
                lastCloudPositionY = positionY;

                // дальше создание итемов а не облаков
                if (!firstTimeArranging && !c.getCloudName().equals("Dark Cloud")) {
                    // для случайного создания итемов а не на каждом облаке
                    int rand = random.nextInt(10);

                    if (rand>5) {
                        // для случайного выбора жизнь или монетка
                        int randomCollectable = random.nextInt(2);

                        if (randomCollectable == 0) {
                            // создаем жизнь если их меньше 2
                            if (GameManager.getInstance().lifeScore<2) {
                                Collectable collectable = new Collectable(world,"Life");
                                collectable.setCollectablePosition(c.getX(), c.getY()+40);
                                collectables.add(collectable);
                            } else {
                                Collectable collectable = new Collectable(world,"Coin");
                                collectable.setCollectablePosition(c.getX(), c.getY()+40);
                                collectables.add(collectable);
                            }
                        } else {
                            // создаем монетку
                            Collectable collectable = new Collectable(world,"Coin");
                            collectable.setCollectablePosition(c.getX(), c.getY()+40);
                            collectables.add(collectable);
                        }
                    }
                }
            }
        }
    }

    public void drawClouds(SpriteBatch batch) { // отрисовываем облака
        for (Cloud c:clouds) {
            if (c.getDrawLeft()) { // если рисуем слева
                batch.draw(c, c.getX()-c.getWidth()/2f-20, c.getY()-c.getHeight()/2f); // поправка на положение наших картинок облака и физического облака
            } else { // если рисуем справа
                batch.draw(c, c.getX()-c.getWidth()/2f+10, c.getY()-c.getHeight()/2f); // тоже самое
            }
        }
    }

    public void drawCollectables(SpriteBatch batch) {
        for (Collectable c: collectables) {
            c.updateCollectable();
            batch.draw(c, c.getX(), c.getY());
        }
    }

    public void removeCollectables(){
        for (int i = 0; i < collectables.size; i++) { // если наш итем готов к удалению
            if (collectables.get(i).getFixture().getUserData().equals("Remove")) {
                collectables.get(i).changeFilter(); // меняем ему тип тела по фильтру
                collectables.get(i).getTexture().dispose(); // освобождаем текстуру
                collectables.removeIndex(i); // удаляем его из массива итемов
            }
        }
    }

    public void createAndArrangeNewClouds(){ // метод для удаления старых ненужных облаков и их освобождление из памяти и пересоздания новых
        for (int i = 0; i<clouds.size; i++) {
            if( (clouds.get(i).getY() - GameInfo.HEIGHT/2 - 20/* что бы уходили за экран */ ) > cameraY ) { // узнаем если какое то из облаков уже вывалилось за пределы камеры и больше нам не нужно
                clouds.get(i).getTexture().dispose(); // освобождаем его текстуру
                clouds.removeIndex(i); // удаляем из массива
            }
        }
        if (clouds.size == 4) { // если облака уменьшились то
            createClouds(); // создаем новые
            positionClouds(false); // даем им место, фолс потому что это уже не первый раз
        }
    }

    public void removeOffScreenCollectables(){
        for (int i = 0; i < collectables.size; i++) {
            // если итем вышел за рамки нашей камеры (видимого экрана0
            if (collectables.get(i).getY() - GameInfo.HEIGHT/2f - 15 > cameraY) {
                collectables.get(i).getTexture().dispose(); // освободили память
                collectables.removeIndex(i); // удалил итем из массива
            }
        }
    }

    public Player positionThePlayer (Player player) {
        player = new Player(world, clouds.get(0).getX() - 60, clouds.get(0).getY()+78);
        return player;
    }

    public void setCameraY(float cameraY) { // setter для передачи в класс местоположения центра камеры
        this.cameraY = cameraY;
    }

    private float randimBetweenNumbers(float min, float max) { // кастомный рандомайзер для разброса облаков по иксу
        return random.nextFloat() * (max - min) + min;
    }
}
