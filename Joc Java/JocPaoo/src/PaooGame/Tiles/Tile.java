package PaooGame.Tiles;

import PaooGame.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class Tile
    \brief Retine toate dalele intr-un vector si ofera posibilitatea regasirii dupa un ID.
 */
public class Tile {
    private static final int NO_TILES = 64;                 /*!< Refetinta la numarul de dale.*/
    public static Tile[] tiles = new Tile[NO_TILES];        /*!< Vector de referinte la tipurile de dale.*/

    /// De remarcat ca urmatoarele dale sunt statice si publice. Acest lucru imi permite sa le am incarcate
    /// o singura data in memorie
    public static Tile sand1Tile = new Sand1Tile(20);                       //dala de nisip 1
    public static Tile sand2Tile = new Sand2Tile(1);                        //dala de nisip 2
    public static Tile sand3Tile = new Sand3Tile(2);                        //dala de nisip decupat dreapta
    public static Tile sand4Tile = new Sand4Tile(3);                        //dala de nisip decupat stanga
    public static Tile woodVerticalTile = new WoodVerticalTile(4);          //dala de lemn vertical
    public static Tile woodHorizontalTile = new WoodHorizontalTile(5);      //dala de lemn vertical
    public static Tile chest1Tile = new Chest1Tile(6);                      //dala pentru cutie lemn1
    public static Tile chest2Tile = new Chest2Tile(7);                      //dala pentru cutie lemn1
    public static Tile chest3Tile = new Chest3Tile(8);                      //dala pentru cutie lemn1
    public static Tile barrel1Tile = new Barrel1Tile(9);                    //butoi
    public static Tile barrel2Tile = new Barrel2Tile(10);                   //butoi
    public static Tile fenceTile = new FenceTile(11);                       //gard
    public static Tile coinsTile = new CoinsTile(12);                       //ban
    public static Tile sunTile = new SunTile(13);
    public static Tile diamond1Tile = new Diamond1Tile(14);
    public static Tile diamond2Tile = new Diamond2Tile(15);
    public static Tile diamond3Tile = new Diamond3Tile(16);
    public static Tile pauseTile = new PauseTile(17);
    public static Tile waterTile = new WaterTile(18);
    public static Tile start = new Start(23);
    public static Tile finish = new FinishTile(21);
    public static Tile blocaj = new Blocaj(22);

    public static Tile earth1 = new Earth1Tile(24);
    public static Tile earth2 = new Earth2Tile(25);
    public static Tile earth3 = new Earth3Tile(26);
    public static Tile earth4 = new Earth4Tile(27);
    public static Tile stone1 = new Stone1Tile(28);
    public static Tile stone2 = new Stone2Tile(29);
    public static Tile stone3 = new Stone3Tile(30);
    public static Tile stone4 = new Stone4Tile(32);
    public static Tile stone5 = new Stone5Tile(33);
    public static Tile treasure = new Treasure(34);

    public static Tile createButtonTile = new CreateButtonTile(31);
    public static Tile createButton1Tile = new CreateButton1Tile(31);
    public static Tile upTile = new UpTile(31);

    public static Tile on = new OnTile(31);
    public static Tile on1 = new On1Tile(31);
    public static Tile off = new OffTile(31);
    public static Tile off1 = new Off1Tile(31);

    public static Tile little = new LittleTile(31);
    public static Tile little1 = new Little1Tile(31);
    public static Tile big = new BigTile(31);
    public static Tile big1 = new Big1Tile(31);

    public static Tile up1Tile = new Up1Tile(31);
    public static Tile downTile = new DownTile(31);
    public static Tile down1Tile = new Down1Tile(31);
    public static Tile itemDataBaseTile = new ItemDataBaseTile(31);
    public static Tile itemDataBaseTile1 = new ItemDataBaseTile1(31);
    public static Tile newTile = new NewTile(31);                          //nu conteaza indexul pentru butoane => sunt putine
    public static Tile continuePauseTile = new ContinuePauseTile(31);
    public static Tile continuePause1Tile = new ContinuePause1Tile(31);
    public static Tile exitPauseTile = new ExitPauseTile(31);
    public static Tile exitPause1Tile = new ExitPause1Tile(31);
    public static Tile savePauseTile = new SavePauseTile(31);
    public static Tile savePause1Tile = new SavePause1Tile(31);
    public static Tile new1Tile = new New1Tile(31);
    public static Tile loadTile = new LoadTile(31);
    public static Tile load1Tile = new Load1Tile(31);
    public static Tile saveTile = new SaveTile(31);
    public static Tile save1Tile = new Save1Tile(31);
    public static Tile settingsTile = new SettingsTile(31);
    public static Tile settings1Tile = new Settings1Tile(31);
    public static Tile informationButtonTile = new InformationButtonTile(31);
    public static Tile informationButton1Tile = new InformationButton1Tile(31);
    public static Tile quitTile = new QuitTile(31);
    public static Tile menuTile = new MenuTile(31);
    public static Tile menu1Tile = new Menu1Tile(31);
    public static Tile quit1Tile = new Quit1Tile(31);
    public static Tile nextLevelTile = new NextLevelTile(31);
    public static Tile nextLevel1Tile = new NextLevel1Tile(31);
    public static final int TILE_WIDTH = Game.Width / 21;                        /*!< Latimea unei dale.*/
    public static final int TILE_HEIGHT = Game.Height / 15;                      /*!< Inaltimea unei dale.*/
    public static final int TILE_WIDTH1 = (int) (Game.Width * 0.25);                /*!< Latimea unui buton.*/
    public static final int TILE_HEIGHT1 = (int) (Game.Height * 0.10);              /*!< Inaltimea unui buton.*/
    public static Tile invalid = new VidTile(0);
    protected BufferedImage img;                                                 /*!< Imaginea aferenta tipului de dala.*/
    protected final int id;                                                      /*!< Id-ul unic aferent tipului de dala.*/

    /*! \fn public Tile(BufferedImage texture, int id)
        \brief Constructorul aferent clasei.

        \param image Imaginea corespunzatoare dalei.
        \param id : Id-ul dalei.
     */
    public Tile(BufferedImage image, int idd) {
        img = image;
        id = idd;
        tiles[id] = this;
    }

    /*! \fn public void Draw(Graphics g, int x, int y)
        \brief Deseneaza in fereastra majoritatea dalelor.

        Pentru dalele speciale exista clasele de mai jos.

        \param g Contextul grafic in care sa se realizeze desenarea
        \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
        \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
     */
    public void Draw(Graphics g, int x, int y) {
        /// Desenare dala
        g.drawImage(img, x, y, TILE_WIDTH, TILE_HEIGHT, null);
    }

    public void DrawButtonMic(Graphics g, int x, int y) {
        /// Desenare dala
        g.drawImage(img, x, y, 150, 45, null);
    }

    public void DrawSun(Graphics g, int x, int y) {
        /// Desenare dala
        g.drawImage(img, x, y, 100, 100, null);
    }

    /*! \fn public void DrawButton(Graphics g, int x, int y)
        \brief Desenare butoane(tratate ca dale)
     */
    public void DrawButton(Graphics g, int x, int y) {
        /// Desenare tuton(tot o data)
        g.drawImage(img, x, y, TILE_WIDTH1, TILE_HEIGHT1, null);
    }

    public void DrawButtonLevel(Graphics g, int x, int y) {
        /// Desenare tuton(tot o data)
        g.drawImage(img, x, y, 200, 80, null);
    }

    public void DrawLife(Graphics g, int x, int y) {
        /// Desenare tuton(tot o data)
        g.drawImage(img, x, y, 200, 80, null);
    }

    public void DrawIO(Graphics g, int x, int y) {
        /// Desenare dala
        g.drawImage(img, x, y, 75, 75, null);
    }
    public void DrawItemDataBase(Graphics g, int x, int y) {
        /// Desenare dala
        g.drawImage(img, x, y, 400, 50, null);
    }

    /*! \fn public boolean IsSolid()
        \brief Returneaza proprietatea de dala solida (supusa coliziunilor) sau nu.Se suprascrie in dalele derivate.
     */
    public boolean IsSolid() {
        return false;
    }

    public static Tile getTile(int id) {
        return tiles[id];
    }

    public int getId() {
        return id;
    }
}
