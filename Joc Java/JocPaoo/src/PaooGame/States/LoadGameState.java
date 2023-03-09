package PaooGame.States;

import PaooGame.Auxiliary.DataBaseItem;
import PaooGame.Game;
import PaooGame.Graphics.*;
import PaooGame.Map.Map;
import PaooGame.Map.RectangleId;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*! \class public class LoadGameState extends State
    \brief Implementeaza incarcarea unei salvari a jocului(incarcare a personajului).
 */
public class LoadGameState extends State {
    private final BufferedImage startGame;                          /*!< Referinta catre imaginea de fundal.*/
    private List<DataBaseItem> listItem;                            /*!< Referinta catre vectorul de inregistrari din baza de date.*/
    private int indexFirst = 0;                                     /*!< Referinta catre o variabila auxiliara.*/
    private RectangleId up, down;                                   /*!< Referinte catre cele doua butoan.*/

    /*! \fn public LoadGameState()
        \brief Constructorul clasei
     */
    public LoadGameState() {
        super();
        listItem = new LinkedList<>();
        startGame = ImageLoader.LoadImage("/background/load.jpg");
        up = new RectangleId(Game.Width / 2 - 24, 100, 50, 50, Tile.upTile);
        down = new RectangleId(Game.Width / 2 - 24, 660, 50, 50, Tile.downTile);
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta .
     */
    @Override
    public void Update() {
        listItem = Game.dataBase.CREATE(3, "");
        Collections.reverse(listItem);
        ///o sa incep sa desenez de la x = 150 si o sa desenez 7 elemente daca am
        for (int i = indexFirst; i < indexFirst + 7; i++) {
            if (i < listItem.size()) {
                listItem.get(i).setX(Game.Width / 2 - 200);
                listItem.get(i).setY(100 + (i - indexFirst + 1) * 70);
            }
        }
        for (int i = 0; i < listItem.size(); i++) {
            if (listItem.get(i).getRectangle().r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
                ///dala pentru activare
                listItem.get(i).getRectangle().tile = Tile.itemDataBaseTile;
                if (Variable.game.mouseInput.clickRight) {
                    Game.dataBase.CREATE(2, listItem.get(i).getName());
                    if (Map.TipMap == 1)
                        State.SetState(Variable.game.level1GameState);
                    if (Map.TipMap == 2)
                        State.SetState(Variable.game.level2GameState);
                    if (Map.TipMap == 3)
                        State.SetState(Variable.game.level3GameState);
                }
                if (Variable.game.keyInput.delete) {
                    Game.dataBase.CREATE(4,listItem.get(i).getName());
                    listItem = Game.dataBase.CREATE(3, "");
                    Collections.reverse(listItem);
                    Variable.game.keyInput.delete = false;
                }
            } else {
                ///dala pentru inactivare
                listItem.get(i).getRectangle().tile = Tile.itemDataBaseTile1;
            }
        }
        if (up.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            ///dala pentru activare
            up.tile = Tile.up1Tile;
            if (Variable.game.mouseInput.clickRight) {
                if (indexFirst > 0)
                    indexFirst--;
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            ///dala pentru inactivare
            up.tile = Tile.upTile;
        }
        if (down.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            ///dala pentru activare
            down.tile = Tile.downTile;
            if (Variable.game.mouseInput.clickRight) {
                if (indexFirst < (listItem.size() - 7))
                    indexFirst++;
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            ///dala pentru inactivare
            down.tile = Tile.down1Tile;
        }
        if (Variable.game.keyInput.enter) {
            State.SetState(Variable.game.menuState);                             //trec in meniu
        }

    }

    /*! \fn public void Draw()
        \brief Deseneaza starea curenta.
     */
    @Override
    public void Draw() {
        /// Se sterge ce era
        Variable.g.drawImage(startGame, -3, 0, Variable.game.getWnd().GetWndWidth() + 3, Variable.game.getWnd().GetWndHeight(), null);
        // end operatie de desenare
        /// Se afiseaza pe ecran
        Variable.g.setColor(Color.orange);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 40));
        Variable.g.drawString("Select the item to load", Game.Width / 2 - 270, 70);
        Variable.g.setColor(Color.BLACK);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        up.tile.Draw(Variable.g, up.r.x, up.r.y);
        down.tile.Draw(Variable.g, down.r.x, down.r.y);
        for (int i = indexFirst; i < indexFirst + 7; i++) {
            if (i < listItem.size()) {
                if (listItem.get(i).getRectangle().r.x != 0 && listItem.get(i).getRectangle().r.y != 0) {
                    listItem.get(i).getRectangle().tile.DrawItemDataBase(Variable.g, listItem.get(i).getRectangle().r.x, listItem.get(i).getRectangle().r.y);
                    Variable.g.drawString("Name: " + listItem.get(i).getName().replace(" ",""), listItem.get(i).getRectangle().r.x + 40, listItem.get(i).getRectangle().r.y + 20);
                    Variable.g.drawString("Date: " + listItem.get(i).getDate(), listItem.get(i).getRectangle().r.x + 40, listItem.get(i).getRectangle().r.y + 40);
                    int aux = i + 1;
                    Variable.g.drawString(" " + i, listItem.get(i).getRectangle().r.x, listItem.get(i).getRectangle().r.y + 30);
                }
            }
        }

        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        Variable.g.drawString("Press enter to exit", Variable.game.getWnd().GetWndWidth() / 2 - 120, Variable.game.getWnd().GetWndHeight() - 20);
        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
