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

/*! \class Level3GameState
    \brief Implementeaza nivelul 3 pentru joc.
 */
public class Level3GameState extends State {
    private final BufferedImage image;                                  /*!< Referinta catre imaginea de fundal*/

    /*! \fn public Level3GameState()
        \brief Constructorul clasei
     */
    public Level3GameState() {
        super();

        Game.snake = new Vector<>();
        Game.rino = new Vector<>();
        Game.mogly = new Vector<>();
        Variable.gloante = new Vector<>();
        image = ImageLoader.LoadImage("/background/Level3Mare.png");
        ///pozitia initiala a pisici
        Game.cat = Hero.getInstance(150, 650, 50, 50, 50, 50);
        Game.cat.setX(3 * 50);
        Game.cat.setY(13 * 50);
        ///adaugare inamici
        try {
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 12 * 50, 13 * 50 - 20, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 22 * 50, 13 * 50 - 20, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 42 * 50, 13 * 50 - 20, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 13 * 50, 9 * 50 - 20, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 43 * 50, 7 * 50 - 20, 70, 70, 50, 50));
            Game.snake.add(Game.factoryMethod.createElements("snake", 0, 49 * 50, 7 * 50 - 20, 70, 70, 50, 50));
            Game.rino.add(Game.factoryMethod.createElements("rino", 0, 38 * 50, 13 * 50 - 50, 100, 100, 100, 100));
            Game.rino.add(Game.factoryMethod.createElements("rino", 0, 45 * 50, 13 * 50 - 50, 100, 100, 100, 100));
            Game.rino.add(Game.factoryMethod.createElements("rino", 0, 17 * 50, 7 * 50 - 50, 100, 100, 100, 100));
            Game.mogly.add(Game.factoryMethod.createElements("mogly", 0, 30 * 50, 13 * 50 - 20, 70, 70, 50, 50));
            Game.mogly.add(Game.factoryMethod.createElements("mogly", 0, 30 * 50, 9 * 50 - 20, 70, 70, 50, 50));
            Game.mogly.add(Game.factoryMethod.createElements("mogly", 0, 55 * 50, 13 * 50 - 20, 70, 70, 50, 50));
        } catch (TypeNotFound e) {
            System.out.println(e);
        }
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a nivelului.
     */
    @Override
    public void Update() {
        //trecerea in meniul de pauza
        if (Variable.game.keyInput.esc) {
            State.SetState(Variable.game.pauseState);
        }
        //se schimba camera dupa pisica
        Variable.game.camera.update(Game.cat);
        //se actualizeaza personajele jocului
        Game.cat.Update();
        for (int i = 0; i < Variable.gloante.size(); i++) {
            Variable.gloante.get(i).Update();
        }
        for (int i = 0; i < Game.snake.size(); i++) {
            Game.snake.get(i).Update();
        }
        for (int i = 0; i < Game.mogly.size(); i++) {
            Game.mogly.get(i).Update();
        }
        for (int i = 0; i < Game.rino.size(); i++) {
            Game.rino.get(i).Update();
        }
    }

    /*! \fn public void Draw()
        \brief Deseneaza (randeaza) pe ecran starea curenta a jocului.

     */
    @Override
    public void Draw() {

        Map.TipMap = 3;
        //se sterge ce era si redesenez
        ///se transleaza ecranul dupa noua pozitie a pisici
        Variable.g.translate(Variable.game.camera.getX(), Variable.game.camera.getY());
        Game.cat.Draw();
        //desenez defapt doua bucati la fel
        Variable.g.drawImage(image, -Variable.game.getWnd().GetWndWidth(), 0, 3 * Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);
        Variable.g.drawImage(image, 2 * Variable.game.getWnd().GetWndWidth(), 0, 3 * Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);
        //am in spate bucati care apar de la centrarea pisici(nu sunt in matrice)
        DrawStoneAUX();

        //desenare dale
        Game.map.Draw();
        //desenare persoanaje
        Game.cat.Draw();
        for (int i = 0; i < Variable.gloante.size(); i++) {
            Variable.gloante.get(i).Draw();
        }
        for (int i = 0; i < Game.snake.size(); i++) {
            Game.snake.get(i).Draw();
        }
        for (int i = 0; i < Game.mogly.size(); i++) {
            Game.mogly.get(i).Draw();
        }
        for (int i = 0; i < Game.rino.size(); i++) {
            Game.rino.get(i).Draw();
        }
        //desenare banuti colectati
        Variable.g.drawImage(Assets.coins, Game.cat.getX() + 350, 15, 50, 50, null);
        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", 1, 40));
        int val = Game.COINS;
        Variable.g.drawString("" + val, Game.cat.getX() + 400, 50);

        //desenare diamnate colectate
        Variable.g.drawImage(Assets.diamond2, Game.cat.getX() + 350, 60, 50, 50, null);
        Variable.g.setColor(Color.RED);
        Variable.g.setFont(new Font("Courier New", 1, 40));
        val = Game.DIAMONDS;
        Variable.g.drawString("" + val, Game.cat.getX() + 400, 100);

        //desenare(scriere) numar de gloante avute)
        Variable.g.drawImage(Assets.glont, Game.cat.getX() - 470, 80, 50, 50, null);
        Variable.g.setColor(Color.GRAY);
        Variable.g.setFont(new Font("Courier New", 1, 40));
        val = Variable.nrGloante;
        Variable.g.drawString("" + val, Game.cat.getX() - 420, 120);

        //desenare soare
        Variable.g.drawImage(Assets.life1[Game.cat.getLife() - 1], Game.cat.getX() - 500, 20, null);

        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }

    /*! \fn public void DrawStoneAUX()
        \brief Deseneaza elemente ce nu sunt in matrice dar sunt in cadru.
     */
    private void DrawStoneAUX() {
        Tile.stone5.Draw(Variable.g, -50, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -100, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -150, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -200, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -250, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -300, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -350, Variable.game.getWnd().GetWndHeight() - 50);
        Tile.stone5.Draw(Variable.g, -400, Variable.game.getWnd().GetWndHeight() - 50);
    }

}
