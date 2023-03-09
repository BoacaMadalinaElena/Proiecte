package PaooGame.Characters;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Map.Map;
import PaooGame.States.*;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;

import java.util.Vector;

import PaooGame.Map.RectangleId;

import java.awt.*;
import java.awt.image.BufferedImage;

    /*! \public class Hero implements Elements
    \brief Implementeaza notiunea de erou(pisica).

    Are ca scop simularea actiunilor personajului : miscare , coliziuni , lupta , moarte etc
    */
public class Hero extends Elements {

    private int life;                                               /*!< Referinta spre numarul de vieti al pisici.*/
    private BufferedImage actual;                                   /*!< Referinta spre imaginea actuala a pisici.*/
    static boolean right = true, left = false, leap = false, down = false, ataca = false; /*!< Referinte spre deplasarile pisici.*/
    boolean go = false;                                             /*!< Referinta ce indica daca pisica sa miscat.*/
    boolean direction = true;                                       /*!< Referinta spre directia pisici.*/
    private static Hero instance;                                   /*!< Referinta spre pisica unica din joc -> se foloseste SINGLETON*/
    private static int cadreRight = 0;
    private static int cadreLeft = 0;

    /*! \fn public Hero(int x, int y, int width, int height, int widthRectangle, int heightRectanle)
        \brief Constructorul clasei

        \param x,y,width,height,widthRectangle,heightRectangle  parametri pentru clasa de baza
     */
    private Hero(int x, int y, int width, int height, int widthRectangle, int heightRectanle) {
        super(x, y, width, height, widthRectangle, heightRectanle);
        actual = Assets.catRight[0];
        life = 6;
    }

    /*! \fn public static Hero getInstance()
        \brief Pentru a avea mereu aceiasi pisica intre nivele se va folosi SINGLETON
     */
    public static Hero getInstance(int x, int y, int width, int height, int widthRectangle, int heightRectanle) {
        if (instance == null)
            instance = new Hero(x, y, width, height, widthRectangle, heightRectanle);
        return instance;
    }

    /*! \fn public static void resetInstance()
        \brief Pentru a reseta pisica cand incep un joc nou la nivelul 1.
     */
    public static void resetInstance() {
        instance = null;
    }

    @Override
    /*! \fn public boolean Move()
        \brief Simuleaza mutarile personajului stanga , dreapta , salturi etc
     */
    public boolean Move() {
        //daca am facut ceva returneaz true
        if (Variable.game.keyInput.right && !Variable.game.keyInput.enter) {                       //il mut sprea dreapta cu 5 px
            ataca = false;
            return DisplacementRight();
        } else if (Variable.game.keyInput.left) {
            ataca = false;
            return DisplacementLeft();
        } else if (Variable.game.keyInput.space) {
            ataca = false;
            return Leap();
        } else if (Variable.game.keyInput.down) {
            ataca = false;
            return Fall();
        } else if (Variable.game.keyInput.v) {
            ataca = false;
            return Leap2();
        } else if (Variable.game.keyInput.up) {
            ataca = false;
            //se intoarce pe loc
            if (right) {
                right = false;
                left = true;
                Variable.game.keyInput.up = false;
            } else {
                right = true;
                left = false;
                Variable.game.keyInput.up = false;
            }
            return true;
        } else if (Variable.game.keyInput.enter) {
            //se ridica in piciore si trage
            ataca = true;
            return attack();
        } else {
            //nu am coliziune cu nimic si nu merge
            return false;
        }
    }

    /*! \fn public void Update()
        \brief Se actualizeaza pozitia prin mutare si viata
     */
    @Override
    public void Update() {
        //daca am mutari
        go = Move();
        if (getSnakeCollision(x, y) == 1) {
            life = life - 1;
        }
        if (getFireCollision(x, y) == 1) {
            life = 0;                   //a murit
        }
        if (getMoglyColision(x, y) == 1) {
            life -= 3;
        }
        if (getRinoColision(x, y) == 1) {
            life = life - 2;
        }
        if (life <= 0) {

            State.SetState(Variable.game.deadState);
        }

    }

    /*! \fn public void Draw()
        \brief Deseneaza eroul.
     */
    @Override
    public void Draw() {

        if (ataca) {
            DrawAtac();
        } else if (go && right && direction) {
            DrawDisplacementRight();
        } else if (go && left && direction) {
            DrawDisplacementLeft();
        } else if (go && leap && direction) {                           //sar in dreapta
            DrawSaltDreapta();
        } else if (go && down && direction) {
            DrawCadDreapta();
        } else if (go && down) {
            DrawCadStanga();
        } else {
            Variable.g.drawImage(actual, x, y, width, height, null);
        }

    }

    /*! \fn private void DrawDisplacementRight()
        \brief Desenare mutari drepata
     */
    private void DrawDisplacementRight() {
        leap = down = false;
        Variable.g.drawImage(actual, (int) x, (int) y, width, height, null);
        if (cadreRight <= 3) {
            cadreRight = cadreRight + 1;
            actual = Assets.catRight[1];
        } else if (cadreRight > 3 && cadreRight <= 6) {
            cadreRight = cadreRight + 1;
            actual = Assets.catRight[2];
        } else if (cadreRight > 6 && cadreRight <= 9) {
            cadreRight = cadreRight + 1;
            actual = Assets.catRight[0];
        } else {
            cadreRight = 0;
        }

    }

    /*! \fn private void DrawDisplacementLeft()
        \brief Desenare mutari stanga
     */
    private void DrawDisplacementLeft() {
        leap = down = false;
        Variable.g.drawImage(actual, (int) x, (int) y, width, height, null);
        if (cadreLeft <= 3) {
            cadreLeft = cadreLeft + 1;
            actual = Assets.catLeft[1];
        } else if (cadreLeft > 3 && cadreLeft <= 6) {
            cadreLeft = cadreLeft + 1;
            actual = Assets.catLeft[2];
        } else if (cadreLeft > 6 && cadreLeft <= 9) {
            cadreLeft = cadreLeft + 1;
            actual = Assets.catLeft[0];
        } else {
            cadreLeft = 0;
        }

    }

    /*! \fn private void DrawAtac()
        \brief Desenare atac
     */
    private void DrawAtac() {
        if (right == true) {
            actual = Assets.catAtac[1];
            Variable.g.drawImage(actual, x, y, width, height, null);
            Variable.game.keyInput.enter = false;
        } else {
            actual = Assets.catAtac[0];
            Variable.g.drawImage(actual, x, y, width, height, null);
            Variable.game.keyInput.enter = false;
        }
    }

    /*! \fn private void DrawCadDreapta()
        \brief Desenare cadere dreapta
     */
    private void DrawCadDreapta() {
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 5, y + Tile.TILE_HEIGHT / 5, width, height, null);
        actual = Assets.catLeap[1];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 4, y + Tile.TILE_HEIGHT / 4, width, height, null);
        actual = Assets.catLeap[2];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 3, y + Tile.TILE_HEIGHT / 3, width, height, null);
        actual = Assets.catLeap[3];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 2, y + Tile.TILE_HEIGHT / 2, width, height, null);
        actual = Assets.catLeap[3];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH, y + Tile.TILE_HEIGHT, width, height, null);
        actual = Assets.catRight[0];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH, y + Tile.TILE_HEIGHT, width, height, null);
        down = false;
        Variable.game.keyInput.down = false;
    }

    /*! \fn private void DrawCadStanga()
        \brief Desenare cad stanga.
     */
    private void DrawCadStanga() {
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 5, y + Tile.TILE_HEIGHT / 5, width, height, null);
        actual = Assets.catLeap1[3];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 4, y + Tile.TILE_HEIGHT / 4, width, height, null);
        actual = Assets.catLeap1[2];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 3, y + Tile.TILE_HEIGHT / 3, width, height, null);
        actual = Assets.catLeap1[1];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH / 2, y + Tile.TILE_HEIGHT / 2, width, height, null);
        actual = Assets.catLeap1[0];
        Variable.g.drawImage(actual, x - Tile.TILE_WIDTH, y + Tile.TILE_HEIGHT, width, height, null);
        actual = Assets.catLeft[0];
        down = false;
        Variable.game.keyInput.down = false;
    }

    /*! \fn private void DrawSaltDreapta()
        \brief Desenare salt dreapta
     */
    private void DrawSaltDreapta() {
        Variable.g.drawImage(actual, x + Tile.TILE_WIDTH, y - Tile.TILE_HEIGHT, width, height, null);
        Variable.game.keyInput.space = false;                       //ramane cu el true un timp ???????????? :(
        leap = false;
    }


    /*! \fn private boolean DisplacementRight()
        \brief Deplasare dreapta
     */
    private boolean DisplacementRight() {
        left = leap = down = false;
        right = true;
        Vector<RectangleId> c = null;
        c = getTileCollision(x + 5, y);
        if (c.size() != 0) {
            //am coliziune
            for (RectangleId rectangleId : c) {
                if (rectangleId.tile == Tile.coinsTile) {
                    Game.COINS = Game.COINS + 1;
                    Game.map.setVal(Map.TipMap, rectangleId.r.x / Tile.TILE_WIDTH, rectangleId.r.y / Tile.TILE_HEIGHT);
                }
                if (rectangleId.tile == Tile.diamond1Tile || rectangleId.tile == Tile.diamond2Tile || rectangleId.tile == Tile.diamond3Tile) {
                    Game.DIAMONDS = Game.DIAMONDS + 1;
                    Game.map.setVal(Map.TipMap, rectangleId.r.x / Tile.TILE_WIDTH, rectangleId.r.y / Tile.TILE_HEIGHT);
                }
                if (rectangleId.tile == Tile.finish) {
                    State.SetState(Variable.game.finishLVLState);
                }
                if (rectangleId.tile == Tile.treasure) {
                    Game.COINS += 10000;
                    Game.DIAMONDS += 500;
                    State.SetState(Variable.game.castigatGame);
                }
            }
            return false;
        } else {
            x = x + 5;
            if (Game.map.getLevelValMap(Map.TipMap, x + 5, y + height) == 0 ||
                    Game.map.getLevelValMap(Map.TipMap, x + 5, y + height) == 12) {             //ajung in bloc solid
                //daca blocul de sub unde ma duc e gol cad in jos acolo
                x = x - 5;
                Fall();                     //o sa cad in jos
                Variable.game.keyInput.right = false;
            }
            return true;
        }
    }

    /*! \fn private boolean DisplacementLeft()
        \brief Deplasare stanga
     */
    private boolean DisplacementLeft() {
        right = leap = down = false;
        left = true;
        boolean ok = true;
        Vector<RectangleId> c = null;
        c = getTileCollision(x - 5, y);
        if (c.size() != 0) {
            ///am coliziune daca e cu banut il colectez
            for (int i = 0; i < c.size(); i++) {
                if (c.get(i).tile == Tile.coinsTile) {
                    Variable.game.COINS = Variable.game.COINS + 1;
                    Game.map.setVal(Map.TipMap, (c.get(i).r.x) / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                }
                if (c.get(i).tile == Tile.diamond1Tile || c.get(i).tile == Tile.diamond2Tile || c.get(i).tile == Tile.diamond3Tile) {
                    Variable.game.DIAMONDS = Variable.game.DIAMONDS + 1;
                    Game.map.setVal(Map.TipMap, c.get(i).r.x / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                }
                if (c.get(i).tile == Tile.finish) {
                    State.SetState(Variable.game.finishLVLState);
                }
            }
            return false;
        } else {
            x = x - 5;
            if (Game.map.getLevelValMap(Map.TipMap, x - 5, y + height) == 0 ||
                    Game.map.getLevelValMap(Map.TipMap, x - 5, y + height) == 12) {             //ajung in bloc solid
                //daca blocul de sub unde ma duc e gol cad in jos acolo
                Fall();                     //o sa cad in jos
                Variable.game.keyInput.left = false;
            }
            return true;
        }
    }

    /*! \fn private boolean Leap()
        \brief Realizare salt simplu
     */
    private boolean Leap() {
        leap = true;
        down = false;
        //stanga si dreapta le las asa
        int xx = (int) (x / 50);                            //iau exact inceputul pisici pe 50x50
        xx = xx * 50;
        int yy = (int) (y / 50);
        yy = yy * 50;
        if (right)                                                           //sunt cu fata la dreapta sar la dreapta
        {
            Vector<RectangleId> c;
            c = getTileCollision(xx + Tile.TILE_WIDTH, yy - Tile.TILE_HEIGHT);
            if (c.size() == 0) {
                x = xx + Tile.TILE_WIDTH;
                y = yy - Tile.TILE_HEIGHT;
                Variable.game.keyInput.space = false;
                while (Game.map.getLevelValMap(Map.TipMap, x, y + height) == 0 ||
                        Game.map.getLevelValMap(Map.TipMap, x, y + height) == 12) {
                    //sunt in aer am vid sub
                    y = y + height;
                    if (Game.map.getLevelValMap(Map.TipMap, x, y) == 12) {
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, x / Tile.TILE_WIDTH, (y) / Tile.TILE_HEIGHT);
                    }
                }
                return true;
            } else {
                //am coliziune cu element dar e banut sau diamanat
                for (RectangleId rectangleId : c) {
                    if (rectangleId.tile == Tile.coinsTile) {
                        x = xx + Tile.TILE_WIDTH;
                        y = yy - Tile.TILE_HEIGHT;
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, rectangleId.r.x / Tile.TILE_WIDTH, rectangleId.r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.space = false;
                        return true;
                    }
                    if (rectangleId.tile == Tile.diamond1Tile || rectangleId.tile == Tile.diamond2Tile || rectangleId.tile == Tile.diamond3Tile) {
                        x = xx + Tile.TILE_WIDTH;
                        y = yy - Tile.TILE_HEIGHT;
                        Game.DIAMONDS = Game.DIAMONDS + 1;
                        Game.map.setVal(Map.TipMap, rectangleId.r.x / Tile.TILE_WIDTH, rectangleId.r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.space = false;
                        return true;
                    }
                    if (rectangleId.tile == Tile.finish) {
                        State.SetState(Variable.game.finishLVLState);
                    }
                }


                //daca pe pozitia actuala sunt in aer cad pana nu mai sunt
            }
        } else {                                                                           //sunt cu fata la stanga sar la stanga
            Vector<RectangleId> c;
            c = getTileCollision(x - Tile.TILE_WIDTH, y - Tile.TILE_HEIGHT);
            if (c.size() == 0) {
                x = x - Tile.TILE_WIDTH;
                y = y - Tile.TILE_HEIGHT;
                Variable.game.keyInput.space = false;
                while (Game.map.getLevelValMap(Map.TipMap, x, y + height) == 0 ||
                        Game.map.getLevelValMap(Map.TipMap, x, y + height) == 12) {
                    //sunt in aer am vid sub
                    y = y + height;
                    if (Game.map.getLevelValMap(Map.TipMap, x, y) == 12) {
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, x / Tile.TILE_WIDTH, y / Tile.TILE_HEIGHT);
                    }
                    if (Game.map.getLevelValMap(Map.TipMap, x, y) == 14 || Game.map.getLevelValMap(Map.TipMap, x, y) == 15 ||
                            Game.map.getLevelValMap(Map.TipMap, x, y) == 16) {
                        Game.DIAMONDS = Game.DIAMONDS + 1;
                        Game.map.setVal(Map.TipMap, (int) x / Tile.TILE_WIDTH, (int) (y) / Tile.TILE_HEIGHT);
                    }

                }
                return true;
            } else {
                for (int i = 0; i < c.size(); i++) {
                    if (c.get(i).tile.IsSolid() == false) {
                        x = xx - Tile.TILE_WIDTH;
                        y = yy - Tile.TILE_HEIGHT;
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, c.get(i).r.x / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.space = false;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*! \fn private boolean Fall()
        \brief Realizare caderea pisici
     */
    private boolean Fall() {
        int xx = (int) (x / 50);                            //iau exact inceputul pisici pe 50x50
        xx = xx * 50;
        int yy = (int) (y / 50);
        yy = yy * 50;
        if (right)                                                           //sunt cu fata la dreapta sar la dreapta
        {
            Vector<RectangleId> c;
            c = getTileCollision(xx + Tile.TILE_WIDTH, yy + Tile.TILE_HEIGHT);

            if (c.size() == 0) {

                x = xx + Tile.TILE_WIDTH;
                y = yy + Tile.TILE_HEIGHT;
                Variable.game.keyInput.down = false;
                while (Game.map.getLevelValMap(Map.TipMap, (int) (x), (int) (y + height)) == 0 ||
                        Game.map.getLevelValMap(Map.TipMap, (int) (x), (int) (y + height)) == 12) {
                    //sunt in aer am vid sub
                    y = y + height;
                    if (Game.map.getLevelValMap(Map.TipMap, (int) (x), (int) (y)) == 12) {
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, (int) x / Tile.TILE_WIDTH, (int) (y) / Tile.TILE_HEIGHT);
                    }

                }
                return true;
            } else {
                //am coliziune cu element dar e banut sau diamanat
                for (int i = 0; i < c.size(); i++) {
                    if (c.get(i).tile == Tile.coinsTile) {
                        x = xx + Tile.TILE_WIDTH;
                        y = yy + Tile.TILE_HEIGHT;
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, c.get(i).r.x / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.space = false;
                        Variable.game.keyInput.down = false;
                        return true;
                    }
                }
            }
        } else {                                                                           //sunt cu fata la stanga sar la stanga
            Vector<RectangleId> c;
            c = getTileCollision(xx - Tile.TILE_WIDTH, yy + Tile.TILE_HEIGHT);
            if (c.size() == 0) {

                x = xx - Tile.TILE_WIDTH;
                y = yy + Tile.TILE_HEIGHT;
                Variable.game.keyInput.down = false;
                while (Game.map.getLevelValMap(Map.TipMap, (x), (int) (y + height)) == 0 ||
                        Game.map.getLevelValMap(Map.TipMap, (x), (int) (y + height)) == 12) {
                    //sunt in aer am vid sub
                    y = y + height;
                    if (Game.map.getLevelValMap(Map.TipMap, (x), (int) (y)) == 12) {
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, (int) x / Tile.TILE_WIDTH, (int) (y) / Tile.TILE_HEIGHT);
                    }
                }
                return true;
            } else {
                //am coliziune cu element dar e banut sau diamanat
                for (int i = 0; i < c.size(); i++) {
                    if (c.get(i).tile == Tile.coinsTile) {
                        x = xx - Tile.TILE_WIDTH;
                        y = yy + Tile.TILE_HEIGHT;
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, c.get(i).r.x / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.space = false;
                        Variable.game.keyInput.down = false;
                        return true;
                    }
                }
            }
        }
        Variable.game.keyInput.down = false;
        return false;
    }

    /*! \fn private boolean Leap()
        \brief Realizare saltul dublu
     */
    private boolean Leap2() {
        int xx = x / 50;                            //iau exact inceputul pisici pe 50x50
        xx = xx * 50;
        int yy = y / 50;
        yy = yy * 50;
        boolean ok = false;
        if (right)                                                           //sunt cu fata la dreapta sar la dreapta
        {
            Vector<RectangleId> c,d;
            c = getTileCollision(xx + Tile.TILE_WIDTH, yy - 2 * Tile.TILE_HEIGHT);
            d = getTileCollision((int) x -25, y  -25);
            if (c.size() == 0 && d.size() ==0) {
                x = xx + Tile.TILE_WIDTH;
                y = yy - 2 * Tile.TILE_HEIGHT;
                Variable.game.keyInput.v = false;
                ok = true;
            } else {
                //am coliziune cu element dar e banut sau diamanat
                for (int i = 0; i < c.size(); i++) {
                    if (c.get(i).tile == Tile.coinsTile || c.get(i).tile == Tile.diamond1Tile || c.get(i).tile == Tile.diamond2Tile || c.get(i).tile == Tile.diamond3Tile) {
                        x = xx + Tile.TILE_WIDTH;
                        y = yy - 2 * Tile.TILE_HEIGHT;
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, c.get(i).r.x / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.v = false;
                        ok = true;
                    }
                }
            }
        } else {                                                                           //sunt cu fata la stanga sar la stanga
            Vector<RectangleId> c,d;
            c = getTileCollision((int) x - Tile.TILE_WIDTH, y - 2 * Tile.TILE_HEIGHT);
            d = getTileCollision((int) x + 25, y  -25);
            if (c.size() == 0 && d.size() == 0) {

                x = x - Tile.TILE_WIDTH;
                y = y - 2 * Tile.TILE_HEIGHT;
                Variable.game.keyInput.v = false;
                ok = true;
            } else {
                for (int i = 0; i < c.size(); i++) {
                    if (!c.get(i).tile.IsSolid()) {
                        x = xx - Tile.TILE_WIDTH;
                        y = yy - 2 * Tile.TILE_HEIGHT;
                        Game.COINS = Game.COINS + 1;
                        Game.map.setVal(Map.TipMap, c.get(i).r.x / Tile.TILE_WIDTH, c.get(i).r.y / Tile.TILE_HEIGHT);
                        Variable.game.keyInput.v = false;
                        ok = true;
                    }
                }
            }

        }
        while (Game.map.getLevelValMap(Map.TipMap, (x), y + height) == 0 ||
                Game.map.getLevelValMap(Map.TipMap, (x), (y + height)) == 12 ||
                Game.map.getLevelValMap(Map.TipMap, (x), (y + height)) == 14 ||
                Game.map.getLevelValMap(Map.TipMap, (x), (y + height)) == 15 ||
                Game.map.getLevelValMap(Map.TipMap, (x), (y + height)) == 16) {
            //sunt in aer am vid sub
            y = y + height;
            if (Game.map.getLevelValMap(Map.TipMap, (x), (y)) == 12) {
                Game.COINS = Game.COINS + 1;
                Game.map.setVal(Map.TipMap, x / Tile.TILE_WIDTH, (y) / Tile.TILE_HEIGHT);
            }
            if (Game.map.getLevelValMap(Map.TipMap, x, y) == 14||
                    Game.map.getLevelValMap(Map.TipMap, x, y) == 15||
                    Game.map.getLevelValMap(Map.TipMap, x, y) == 16) {
                Game.DIAMONDS = Game.DIAMONDS + 1;
                Game.map.setVal(Map.TipMap, x / Tile.TILE_WIDTH, (y) / Tile.TILE_HEIGHT);
            }
        }
        return ok;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /*! \fn public Vector<RectangleId> getTileCollision(int xx, int yy)
        \brief Se returneza coliziunile cu dalele
     */
    public Vector<RectangleId> getTileCollision(int xx, int yy) {
        int i;
        Vector<RectangleId> v = new Vector<>();
        if (Variable.game.level1GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL1.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL1.get(i).r)) {
                    //daca eroul se intersecreaza cu bucati din harta le returnez intrun vector
                    v.add(Map.RectangleTileL1.get(i));
                }
            }
        } else if (Variable.game.level2GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL2.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL2.get(i).r)) {
                    //daca eroul se intersecreaza cu bucati din harta le returnez intrun vector
                    v.add(Map.RectangleTileL2.get(i));
                }
            }
        } else if (Variable.game.level3GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL3.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL3.get(i).r)) {
                    //daca eroul se intersecreaza cu bucati din harta le returnez intrun vector
                    v.add(Map.RectangleTileL3.get(i));
                }
            }
        }
        return v;
    }


    public int getLife() {
        return life;
    }

    /*! \fn public int getSnakeCollision(int xx, int yy)
        \brief Se returneaza coliziunile cu sarpele
     */
    public int getSnakeCollision(int xx, int yy) {
        Rectangle drept = new Rectangle(xx, yy, width, height);
        for (int i = 0; i < Game.snake.size(); i++) {
            if (drept.intersects(Game.snake.get(i).space)) {
                //la coliziunea cu pisica sarpele moare
                Game.snake.remove(Game.snake.get(i));
                return 1;
            }
        }
        return 0;
    }

    /*! \fn public int getRinoColision(int xx, int yy)
        \brief Se returneaza coliziunile cu Rino
     */
    public int getRinoColision(int xx, int yy) {
        Rectangle drept = new Rectangle(xx, yy, width, height);
        for (int i = 0; i < Game.rino.size(); i++) {
            if (drept.intersects(Game.rino.get(i).space)) {
                //la coliziunea cu pisica sarpele moare
                Game.rino.remove(Game.rino.get(i));
                return 1;
            }
        }
        return 0;
    }

    /*! \fn public int getMoglyColision(int xx, int yy)
       \brief Se returneaza coliziunile cu Mogly
    */
    public int getMoglyColision(int xx, int yy) {
        Rectangle drept = new Rectangle(xx, yy, width, height);
        for (int i = 0; i < Game.mogly.size(); i++) {
            if (drept.intersects(Game.mogly.get(i).space)) {
                //la coliziunea cu pisica sarpele moare
                Game.mogly.remove(Game.mogly.get(i));
                return 1;
            }
        }
        return 0;
    }

    /*! \fn public int getFireCollision(int xx, int yy)
       \brief Se returneaza coliziunile cu focul
    */
    public int getFireCollision(int xx, int yy) {
        Rectangle drept = new Rectangle(xx, yy, width, height);
        if (State.GetState() == Variable.game.level2GameState) {
            for (int i = 0; i < Level2GameState.fire.size(); i++) {
                if (drept.intersects(Level2GameState.fire.get(i).space)) {
                    //am coliziune cu foc pisica moare
                    return 1;
                }
            }
        }
        return 0;
    }

    /*! \fn private boolean attack()
        \brief Simuleaza atacul eroului
     */
    private boolean attack() {

        if (right) {
            if (Variable.nrGloante > 0) {
                Variable.gloante.add(new Bullet(x + width, y + width / 2, 10, 10, 10, 10, 1));
                //generez un glont de la jumatatea pisici
                //se ve actualiza in clasa Lavel2GameState pozitia lui si se va desena
                Variable.nrGloante--;
                return true;
            }
        } else {
            if (Variable.nrGloante > 0) {
                Variable.gloante.add(new Bullet(x - width, y + width / 2, 10, 10, 10, 10, 2));
                Variable.nrGloante--;
            }
        }
        return true;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLife(int life) {
        this.life = life;
    }
}