package PaooGame.States;

import PaooGame.Characters.*;
import PaooGame.Exception.TypeNotFound;
import PaooGame.Game;
import PaooGame.Map.*;
import PaooGame.Graphics.*;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.Vector;

/*! \class Level2GameState
    \brief Implementeaza nivelul 2 pentru joc.
 */
public class Level2GameState extends State {
    public final BufferedImage image;                                          /*!< Referinta spre imaginea de fundal a jocului*/
    public static Vector<Fire> fire = new Vector<>();                                    /*!< Referinta spre vectorul de focuri(obstacole)*/

    /*! \ fn public level2GameState()
        \brief Constructorul clasei
     */
    public Level2GameState() {
        super();
        //se refac parametri pentru joc
        Variable.gloante = new Vector<>();
        Game.snake = new Vector<>();
        Game.rino = new Vector<>();
        fire = new Vector<>();
        image = ImageLoader.LoadImage("/background/Level2Mare.jpg");

        //adaugare persoanaje -> voi face o fabrica abstracta
        Game.cat = Hero.getInstance(150, 650, 50, 50, 50, 50);
        Game.cat.setX(3 * 50);
        Game.cat.setY(650);
        fire.add(new Fire(8 * 50, 655, 50, 50, 50, 50));
        fire.add(new Fire(40 * 50, 655, 50, 50, 50, 50));
        fire.add(new Fire(41 * 50, 655, 50, 50, 50, 50));
        fire.add(new Fire(42 * 50, 655, 50, 50, 50, 50));
        fire.add(new Fire(63 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(64 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(65 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(66 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(67 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(68 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(69 * 50, 700, 50, 50, 50, 50));
        fire.add(new Fire(70 * 50, 700, 50, 50, 50, 50));
        try {
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 14 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 19 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 8 * 50, 530, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 13 * 50, 480, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 44 * 50, 630, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 53 * 50, 580, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 35 * 50, 430, 70, 70, 50, 50));
            Game.rino.add(Game.factoryMethod.createElements("rino", 0, 43 * 50, 300, 100, 100, 100, 100));
            Game.rino.add(Game.factoryMethod.createElements("rino", 0, 57 * 50, 500, 100, 100, 100, 100));
        } catch (TypeNotFound e) {
            System.out.println(e);
        }
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a nivelului 2.
     */
    @Override
    public void Update() {
        //trecere in starea de repaus
        if (Variable.game.keyInput.esc) {
            State.SetState(Variable.game.pauseState);
        }
        //centrare imagine pe pisica
        Variable.game.camera.update(Game.cat);
        //actualizare pisica , gloante , serpi,mamuti
        Game.cat.Update();
        for (int i = 0; i < Variable.gloante.size(); i++) {
            Variable.gloante.get(i).Update();
        }
        for (int i = 0; i < Game.snake.size(); i++) {
            Game.snake.get(i).Update();
        }
        for (int i = 0; i < Game.rino.size(); i++) {
            Game.rino.get(i).Update();
        }
    }

    /*! \fn public void Draw()
        \brief Deseneaza (randeaza) pe ecran starea curenta a nivelului.

     */
    @Override
    public void Draw() {
        Map.TipMap = 2;
        //se sterge ce era si redesenez
        Variable.g.translate(Variable.game.camera.getX(), Variable.game.camera.getY()); //begin of camera
        Game.cat.Draw();
        Variable.g.drawImage(image, -Variable.game.getWnd().GetWndWidth(), 0, 3 * Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);
        Variable.g.drawImage(image, 2 * Variable.game.getWnd().GetWndWidth(), 0, 3 * Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);

        //se deseneaza in stanga personajului locul liber
        DrawEarthAUX();

        Game.map.Draw();
        Game.cat.Draw();
        for (int i = 0; i < Variable.gloante.size(); i++) {
            Variable.gloante.get(i).Draw();
        }
        for (int i = 0; i < fire.size(); i++) {
            fire.get(i).Draw();
        }
        for (int i = 0; i < Game.snake.size(); i++) {
            Game.snake.get(i).Draw();
        }
        for (int i = 0; i < Game.rino.size(); i++) {
            Game.rino.get(i).Draw();
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

        Variable.g.drawImage(Assets.life1[Game.cat.getLife() - 1], Game.cat.getX() - 500, 20, null);

        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }

    /*! \fn public void DrawEarthAUX()
        \brief Deseneaza elemente ce nu sunt in matrice dar sunt in cadru.
     */
    private void DrawEarthAUX() {
        Tile.earth3.Draw(Variable.g, -50, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -100, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -150, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -200, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -250, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -300, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -350, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.earth3.Draw(Variable.g, -400, Variable.game.getWnd().GetWndHeight() - 50);
    }

}
