package PaooGame.States;

import PaooGame.Auxiliary.DataBaseItem;
import PaooGame.Auxiliary.DataBaseItemPlayer;
import PaooGame.DataBasePlayer;
import PaooGame.Game;
import PaooGame.Graphics.*;
import PaooGame.Map.Map;
import PaooGame.Map.RectangleId;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*! \class public class LoadGameState extends State
    \brief Implementeaza incarcarea unei salvari a jocului.
 */
public class LoadPlayerGameState extends State {
    private final BufferedImage loadGame;                           /*!< Referinta catre imaginea de fundal.*/
    private List<DataBaseItemPlayer> listItem;                      /*!< Referinta catre vectorul de inregistrari din baza de date.*/
    private int indexFirst = 0;                                     /*!< Referinta catre o variabila auxiliara.*/
    private RectangleId up, down, create;                            /*!< Referinte catre cele doua butoan.*/
    private boolean ok = false;                                     /*!< Verifica daca am selectat jucator*/

    /*! \fn public LoadGameState()
        \brief Constructorul clasei
     */
    public LoadPlayerGameState() {
        super();
        listItem = new LinkedList<>();
        loadGame = ImageLoader.LoadImage("/background/load.jpg");
        up = new RectangleId(Game.Width / 2 - 24, 100, 50, 50, Tile.upTile);
        down = new RectangleId(Game.Width / 2 - 24, 520, 50, 50, Tile.downTile);
        create = new RectangleId(Game.Width / 2 - 75, 580, 150, 45, Tile.createButtonTile);
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta .
     */
    @Override
    public void Update() {
        listItem = Game.dataBasePlayer.CREATE(4, "", 0);
        sortItems();
        ///o sa incep sa desenez de la x = 150 si o sa desenez 5 elemente daca am
        for (int i = indexFirst; i < indexFirst + 5; i++) {
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
                    Game.dataBasePlayer.CREATE(3, listItem.get(i).getName(), listItem.get(i).getPoint());
                    ok = true;
                }
                if (Variable.game.keyInput.delete) {
                    Game.dataBasePlayer.CREATE(5, listItem.get(i).getName(), 0);
                    listItem = Game.dataBasePlayer.CREATE(4, "", 0);
                    Variable.game.keyInput.delete = false;
                    ok = false;
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
                if (indexFirst < (listItem.size() - 5))
                    indexFirst++;
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            ///dala pentru inactivare
            down.tile = Tile.down1Tile;
        }
        if (create.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            ///dala pentru activare
            create.tile = Tile.createButtonTile;
            if (Variable.game.mouseInput.clickRight) {
                Game.dataBasePlayer.CREATE(1, "", 0);
                ok = true;
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            ///dala pentru inactivare
            create.tile = Tile.createButton1Tile;
        }
        if (Variable.game.keyInput.enter && ok) {
            State.SetState(Variable.game.menuState);                             //trec in meniu
        }

    }

    /*! \fn public void Draw()
        \brief Deseneaza starea curenta.
     */
    @Override
    public void Draw() {
        /// Se sterge ce era
        Variable.g.drawImage(loadGame, -3, 0, Variable.game.getWnd().GetWndWidth() + 3, Variable.game.getWnd().GetWndHeight(), null);
        // end operatie de desenare
        /// Se afiseaza pe ecran
        Variable.g.setColor(Color.orange);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 40));
        Variable.g.drawString("Select the player or create.", Game.Width / 2 - 300, 70);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        Variable.g.drawString("       Your name is : " + DataBasePlayer.currentPlayer, Game.Width / 2 - 200, 670);
        Variable.g.setColor(Color.BLACK);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        up.tile.Draw(Variable.g, up.r.x, up.r.y);
        create.tile.DrawButtonMic(Variable.g, create.r.x, create.r.y);
        down.tile.Draw(Variable.g, down.r.x, down.r.y);
        for (int i = indexFirst; i < indexFirst + 7; i++) {
            if (i < listItem.size()) {
                if (listItem.get(i).getRectangle().r.x != 0 && listItem.get(i).getRectangle().r.y != 0) {
                    listItem.get(i).getRectangle().tile.DrawItemDataBase(Variable.g, listItem.get(i).getRectangle().r.x, listItem.get(i).getRectangle().r.y);
                    Variable.g.drawString("Name: " + listItem.get(i).getName().replace(" ", ""), listItem.get(i).getRectangle().r.x + 40, listItem.get(i).getRectangle().r.y + 20);
                    Variable.g.drawString("Point: " + listItem.get(i).getPoint(), listItem.get(i).getRectangle().r.x + 40, listItem.get(i).getRectangle().r.y + 40);
                    int aux = i + 1;
                    Variable.g.drawString(" " + i, listItem.get(i).getRectangle().r.x, listItem.get(i).getRectangle().r.y + 30);
                }
            }
        }

        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        Variable.g.drawString("Press enter to continue", Variable.game.getWnd().GetWndWidth() / 2 - 120, Variable.game.getWnd().GetWndHeight() - 20);
        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }

    /*! \fn private void sortItems()
        \brief Sorteaza vecotrul de elemente.
     */
    private void sortItems() {
        int i, j;
        String str1, str2;
        int in1, in2;
        for (i = 0; i < listItem.size() - 1; i++)

            // Last i elements are already
            // in place
            for (j = 0; j < listItem.size() - i - 1; j++)
                if (listItem.get(j).getPoint() < listItem.get(j + 1).getPoint()) {
                    str1 = listItem.get(j).getName();
                    in1 = listItem.get(j).getPoint();
                    str2 = listItem.get(j + 1).getName();
                    in2 = listItem.get(j + 1).getPoint();
                    listItem.get(j).setPoint(in2);
                    listItem.get(j).setName(str2);
                    listItem.get(j + 1).setPoint(in1);
                    listItem.get(j + 1).setName(str1);
                }
    }

}
