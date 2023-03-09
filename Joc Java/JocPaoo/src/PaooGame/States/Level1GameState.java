package PaooGame.States;

import PaooGame.Characters.*;
import PaooGame.Exception.TypeNotFound;
import PaooGame.Game;
import PaooGame.Graphics.*;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.Vector;

/*! \class Level1GameState
    \brief Implementeaza nivelul 1 pentru joc
 */
public class Level1GameState extends State {
    public BufferedImage image;                                                        /*!< Referinta spre imaginea de fundal a nivelului*/

    /*! \ fn public level1GameState()
        \brief Constructorul clasei
     */
    public Level1GameState() {
        super();
        //valori initiale scor
        Hero.resetInstance();
        Game.DIAMONDS = 0;
        Game.COINS = 0;
        Game.mogly = new Vector<>();
        Game.rino = new Vector<>();
        image = ImageLoader.LoadImage("/background/Level1Mare.png");
        Game.snake = new Vector<>();
        //adaugare elemente
        Game.cat = Hero.getInstance(150, 650, 50, 50, 50, 50);
        try {
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 17 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 50 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 45 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 55 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 35 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 30 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 37 * 50, 380, 70, 70, 50, 50));
        }catch (TypeNotFound e){System.out.println(e);}
        Game.gloante = new Vector<>();
        Game.nrGloante = 45;
        Variable.gloante = Game.gloante;
        Variable.nrGloante = Game.nrGloante;
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a meniului.
     */
    @Override
    public void Update() {
        if (Variable.game.keyInput.esc) {
            State.SetState(Variable.game.pauseState);
        }
        Variable.game.camera.update(Game.cat);
        Game.cat.Update();
        for (int i = 0; i < Game.snake.size(); i++) {
            Game.snake.get(i).Update();
        }
        for (int i = 0; i < Game.gloante.size(); i++) {
            Game.gloante.get(i).Update();
        }
    }

    /*! \fn public void Draw()
        \brief Deseneaza nivelul curent.
     */
    @Override
    public void Draw() {
        //se sterge ce era si redesenez
        Variable.g.translate(Variable.game.camera.getX(), Variable.game.camera.getY()); //begin of camera
        Game.cat.Draw();
        Variable.g.drawImage(image, -Variable.game.getWnd().GetWndWidth(), 0, 3 * Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);
        Variable.g.drawImage(image, 2 * Variable.game.getWnd().GetWndWidth(), 0, 3 * Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);

        DrawWaterAUX();

        Game.map.Draw();
        Game.cat.Draw();
        for (int i = 0; i < Game.snake.size(); i++) {
            Game.snake.get(i).Draw();
        }
        for (int i = 0; i < Game.gloante.size(); i++) {
            Game.gloante.get(i).Draw();
        }
        //banuti colectati
        Variable.g.drawImage(Assets.coins, Game.cat.getX() + 350, 15, 50, 50, null);
        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", 1, 40));
        int val = Game.COINS;
        Variable.g.drawString("" + val, Game.cat.getX() + 400, 50);

        ///diamnate colectate
        Variable.g.drawImage(Assets.diamond2, Game.cat.getX() + 350, 60, 50, 50, null);
        Variable.g.setColor(Color.RED);
        Variable.g.setFont(new Font("Courier New", 1, 40));
        val = Game.DIAMONDS;
        Variable.g.drawString("" + val, Game.cat.getX() + 400, 100);

        //nr de gloante avute
        Variable.g.drawImage(Assets.glont, Game.cat.getX() - 470, 80, 50, 50, null);
        Variable.g.setColor(Color.GRAY);
        Variable.g.setFont(new Font("Courier New", 1, 40));
        val = Variable.nrGloante;
        Variable.g.drawString("" + val, Game.cat.getX() - 420, 120);


        //desenare vieti actuale
        Variable.g.drawImage(Assets.life1[Game.cat.getLife() - 1], Game.cat.getX() - 500, 20, null);
    }

    /*! \fn public void DrawWaterAUX()
        \brief Deseneaza elemente ce nu sunt in matrice dar sunt in cadru.
     */
    private void DrawWaterAUX() {
        //desenate pentru partea de acran inaccesibila
        Tile.waterTile.Draw(Variable.g, -50, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -100, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -150, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -200, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -250, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -300, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -350, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.waterTile.Draw(Variable.g, -400, Variable.game.getWnd().GetWndHeight() - 50);
    }

    public BufferedImage getImage() {
        return image;
    }
}
