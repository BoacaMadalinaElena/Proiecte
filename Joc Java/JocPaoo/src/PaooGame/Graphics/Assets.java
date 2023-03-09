package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.
 */
public class Assets {
    /// Referinte catre elementele grafice (dale) utilizate in joc.
    public static BufferedImage sand1;                  //nisip interior
    public static BufferedImage sand2;                  //nisip exterior
    public static BufferedImage sand3;                  //nisip decupat dreapta
    public static BufferedImage sand4;                  //nisip decupat stanga
    public static BufferedImage woodVertical;           //lemn vertical
    public static BufferedImage woodHorizontal;         //lemn orizontal
    public static BufferedImage chest1;                 //lada/cutie ? lemn
    public static BufferedImage chest2;                 //lada/cutie ? lemn
    public static BufferedImage chest3;                 //lada/cutie ? lemn
    public static BufferedImage barrel1;                //butoi 1
    public static BufferedImage barrel2;                //butoi 2
    public static BufferedImage fence;                  //gard
    public static BufferedImage coins;                  //bani
    public static BufferedImage sun;                    //soare
    public static BufferedImage diamond1;
    public static BufferedImage diamond2;
    public static BufferedImage diamond3;
    public static BufferedImage pause;
    public static BufferedImage _new;                   //butoane pentru meniu
    public static BufferedImage _new1;
    public static BufferedImage load;
    public static BufferedImage load1;
    public static BufferedImage save;
    public static BufferedImage save1;
    public static BufferedImage settings;
    public static BufferedImage settings1;
    public static BufferedImage quit;
    public static BufferedImage quit1;
    public static BufferedImage water;
    public static BufferedImage vid;
    public static BufferedImage[] life1 = new BufferedImage[6];
    public static BufferedImage[] catRight = new BufferedImage[3];
    public static BufferedImage[] catLeft = new BufferedImage[3];
    public static BufferedImage[] catLeap = new BufferedImage[4];
    public static BufferedImage[] catLeap1 = new BufferedImage[4];
    public static BufferedImage[] snakeRight = new BufferedImage[7];
    public static BufferedImage[] snakeLeft = new BufferedImage[7];
    public static BufferedImage[] catAtac = new BufferedImage[3];
    public static BufferedImage[] fire = new BufferedImage[4];
    public static BufferedImage start;
    public static BufferedImage finish;
    public static BufferedImage cufar;
    public static BufferedImage menu;
    public static BufferedImage menu1;
    public static BufferedImage nextLevel;
    public static BufferedImage nextLevel1;
    public static BufferedImage glont;
    public static BufferedImage glont1;
    public static BufferedImage earth1;                             //iarba decupata pe stanga
    public static BufferedImage earth2;                             //iarba decupata pe dreapta
    public static BufferedImage earth3;                             //pamant exterior
    public static BufferedImage earth4;                             //pamant interior
    public static BufferedImage[] rinoLeft = new BufferedImage[4];
    public static BufferedImage[] rinoRight = new BufferedImage[4];
    public static BufferedImage[] moglyLeft = new BufferedImage[4];
    public static BufferedImage[] moglyRight = new BufferedImage[4];
    public static BufferedImage stone1;                             //pamant exterior
    public static BufferedImage stone2;                             //pamant interior
    public static BufferedImage stone3;                             //pamant interior
    public static BufferedImage stone4;                             //pamant interior
    public static BufferedImage stone5;                             //pamant interior
    public static BufferedImage _continue;                           //pamant buton
    public static BufferedImage _continue1;                           //pamant buton
    public static BufferedImage exit;                           //pamant buton
    public static BufferedImage exit1;                           //pamant buton
    public static BufferedImage Save;                           //pamant buton
    public static BufferedImage Save1;                           //pamant buton
    public static BufferedImage information;                           //pamant buton
    public static BufferedImage information1;                           //pamant buton
    public static BufferedImage itemDataBaseImg;
    public static BufferedImage itemDataBaseImg1;
    public static BufferedImage up,up1;
    public static BufferedImage down,down1;
    public static BufferedImage create,create1,off,on,off1,on1;
    public static BufferedImage little1,big1,little,big;
    /*! \fn public static void Init()
        \brief Functia initializaza referintele catre elementele grafice utilizate.

        Aceasta functie poate fi rescrisa astfel incat elementele grafice incarcate/utilizate
        sa fie parametrizate. Din acest motiv referintele nu sunt finale.
     */
    public static void Init() {
        /// Se creaza temporar un obiect SpriteSheet initializat prin intermediul clasei ImageLoader
        SpriteSheet sheet = new SpriteSheet(ImageLoader.LoadImage("/textures/spritesheet.png"));
        SpriteSheet button = new SpriteSheet(ImageLoader.LoadImage("/textures/buttons.png"));
        SpriteSheet life = new SpriteSheet(ImageLoader.LoadImage("/textures/life.png"));
        SpriteSheet catImg = new SpriteSheet(ImageLoader.LoadImage("/textures/Cat.png"));
        SpriteSheet Vid = new SpriteSheet(ImageLoader.LoadImage("/textures/Vid.png"));
        SpriteSheet imgsnakeRight = new SpriteSheet(ImageLoader.LoadImage("/textures/SarpeDreapta.png"));
        SpriteSheet imgsnakeLeft = new SpriteSheet(ImageLoader.LoadImage("/textures/SarpeStanga.png"));
        SpriteSheet startImg = new SpriteSheet(ImageLoader.LoadImage("/textures/Start.png"));
        SpriteSheet cufarImg = new SpriteSheet(ImageLoader.LoadImage("/textures/Cufar.png"));
        SpriteSheet finismImg = new SpriteSheet(ImageLoader.LoadImage("/textures/Finish.png"));
        SpriteSheet glontimg = new SpriteSheet(ImageLoader.LoadImage("/textures/glont.png"));
        SpriteSheet rinoimg = new SpriteSheet(ImageLoader.LoadImage("/textures/Rino.png"));
        SpriteSheet moglyimg = new SpriteSheet(ImageLoader.LoadImage("/textures/Mogly.png"));
        SpriteSheet buttonPauseImg = new SpriteSheet(ImageLoader.LoadImage("/textures/buttons2.png"));
        SpriteSheet soundButton = new SpriteSheet(ImageLoader.LoadImage("/textures/sound.png"));
        SpriteSheet soundVolumeButton = new SpriteSheet(ImageLoader.LoadImage("/textures/sunet.png"));
        /// Se obtin subimaginile corespunzatoare elementelor necesare.
        sand1 = sheet.crop(11, 0);
        sand2 = sheet.crop(12, 0);
        sand3 = sheet.crop(13, 0);
        sand4 = sheet.crop(14, 0);
        woodVertical = sheet.crop(16, 0);
        woodHorizontal = sheet.crop(17, 0);
        chest1 = sheet.crop(4, 0);
        chest2 = sheet.crop(5, 0);
        chest3 = sheet.crop(6, 0);
        earth1 = sheet.crop(22,0);
        earth2 = sheet.crop(23,0);
        earth3 = sheet.crop(24,0);
        earth4 = sheet.crop(25,0);
        stone1 = sheet.crop(30,0);
        stone3 =sheet.crop(31,0);
        stone2 = sheet.crop(32,0);
        stone4 =sheet.crop(33,0);
        stone5 =sheet.crop(34,0);

        fire[0] = sheet.crop(26,0);
        fire[1] = sheet.crop(27,0);
        fire[2] = sheet.crop(28,0);
        fire[3] = sheet.crop(29,0);
        itemDataBaseImg = sheet.crop(35,0);
        itemDataBaseImg1 = sheet.crop(36,0);
        up = sheet.crop(37,0);
        up1 = sheet.crop(38,0);
        down = sheet.crop(39,0);
        down1 = sheet.crop(40,0);
        barrel1 = sheet.crop(2, 0);
        barrel2 = sheet.crop(3, 0);
        fence = sheet.crop(10, 0);
        coins = sheet.crop(1, 0);
        sun = sheet.crop(15, 0);
        diamond1 = sheet.crop(7, 0);
        diamond2 = sheet.crop(8, 0);
        diamond3 = sheet.crop(9, 0);
        pause = sheet.crop(16, 0);
        water = sheet.crop(0, 0);
        _new = button.cropButton(0,3);
        _new1 = button.cropButton(0,2);
        load = button.cropButton(0,0);
        load1 = button.cropButton(0,1);
        quit = button.cropButton(0,4);
        quit1 = button.cropButton(0,5);
        settings = button.cropButton(0,9);
        settings1 = button.cropButton(0,8);
        save = button.cropButton(0,7);
        save1 = button.cropButton(0,6);

        life1[5] = life.cropLife(0,0);
        life1[4] = life.cropLife(0,1);
        life1[3] = life.cropLife(0,2);
        life1[2] = life.cropLife(0,3);
        life1[1] = life.cropLife(0,4);
        life1[0] = life.cropLife(0,5);
        catRight[0] = catImg.cropCat(0,0);                                                      //stari pentru mers
        catRight[1] = catImg.cropCat(1,0);
        catRight[2] = catImg.cropCat(2,0);
        vid = Vid.cropCat(0,0);
        catLeft[0] = catImg.cropCat(1,2);                                                      //stari pentru mers
        catLeft[1] = catImg.cropCat(2,2);
        catLeft[2] = catImg.cropCat(3,2);
        catLeap[0] = catImg.cropCat(3,0);
        catLeap[1] = catImg.cropCat(4,0);
        catLeap[2] = catImg.cropCat(0,0);
        snakeRight[6] = imgsnakeRight.cropSnake(6,0);
        snakeLeft[0] = imgsnakeLeft.cropSnake(0,0);
        snakeLeft[1] = imgsnakeLeft.cropSnake(1,0);
        snakeLeft[2] = imgsnakeLeft.cropSnake(2,0);
        snakeLeft[3] = imgsnakeLeft.cropSnake(3,0);
        snakeLeft[4] = imgsnakeLeft.cropSnake(4,0);
        snakeRight[0] = imgsnakeRight.cropSnake(0,0);
        snakeRight[1] = imgsnakeRight.cropSnake(1,0);
        snakeRight[2] = imgsnakeRight.cropSnake(2,0);
        snakeRight[3] = imgsnakeRight.cropSnake(3,0);
        snakeRight[4] = imgsnakeRight.cropSnake(4,0);
        snakeRight[5] = imgsnakeRight.cropSnake(5,0);
        snakeLeft[5] = imgsnakeLeft.cropSnake(5,0);
        snakeLeft[6] = imgsnakeLeft.cropSnake(6,0);
        start = startImg.cropStartFinish(0,0);
        finish = finismImg.cropStartFinish(0,0);
        cufar = cufarImg.cropStartFinish(0,0);
        menu = button.cropButton(0,10);
        menu1 = button.cropButton(0,11);
        nextLevel = button.cropButton(0,13);
        nextLevel1 = button.cropButton(0,12);
        information = button.cropButton(0,14);
        information1 = button.cropButton(0,15);
        glont = glontimg.cropBullet(0,0);
        glont1 = glontimg.cropBullet(1,0);
        catAtac[0] = catImg.cropCat(0,4);
        catAtac[1] = catImg.cropCat(4,1);
        catAtac[2] = catImg.cropCat(0,2);
        rinoLeft[0] = rinoimg.cropRino(0,0);
        rinoLeft[1] = rinoimg.cropRino(1,0);
        rinoLeft[2] = rinoimg.cropRino(2,0);
        rinoLeft[3] = rinoimg.cropRino(3,0);
        rinoRight[0] = rinoimg.cropRino(0,1);
        rinoRight[1] = rinoimg.cropRino(1,1);
        rinoRight[2] = rinoimg.cropRino(2,1);
        rinoRight[3] = rinoimg.cropRino(3,1);

        //-----------------
        moglyLeft[0] = moglyimg.cropRino(0,1);
        moglyLeft[1] = moglyimg.cropRino(1,1);
        moglyLeft[2] = moglyimg.cropRino(2,1);
        moglyLeft[3] = moglyimg.cropRino(3,1);
        moglyRight[0] = moglyimg.cropRino(0,0);
        moglyRight[1] = moglyimg.cropRino(1,0);
        moglyRight[2] = moglyimg.cropRino(2,0);
        moglyRight[3] = moglyimg.cropRino(3,0);
        _continue = buttonPauseImg.cropButton(0,0);
        _continue1 = buttonPauseImg.cropButton(0,1);
        exit = buttonPauseImg.cropButton(0,2);
        exit1 = buttonPauseImg.cropButton(0,3);
        Save = buttonPauseImg.cropButton(0,4);
        Save1 = buttonPauseImg.cropButton(0,5);
        create = buttonPauseImg.cropButton(0,6);
        create1 = buttonPauseImg.cropButton(0,7);
        off = soundButton.cropSound(0,0);
        on = soundButton.cropSound(0,3);
        off1 = soundButton.cropSound(0,1);
        on1 = soundButton.cropSound(0,2);
        big = soundVolumeButton.cropRino(0,0);
        little = soundVolumeButton.cropRino(1,0);
        big1 = soundVolumeButton.cropRino(2,0);
        little1 = soundVolumeButton.cropRino(3,0);
    }
}
