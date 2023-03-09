package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.ImageLoader;
import PaooGame.Map.Map;
import PaooGame.Map.RectangleId;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class FinishState extends State
    \brief Implementeaza finalul unui nivel .
 */
public class FinishState extends State {
    private final BufferedImage finishLevelGame;                            /*!< Referinta spre imaginea de fundal*/
    private final RectangleId next_level, save, menu, quit;                 /*!< Referinte spre butoanele din aceasta stare*/

    /*! \ fn public FinishState()
        \brief Constructorul clasei
     */
    public FinishState() {
        super();
        finishLevelGame = ImageLoader.LoadImage("/background/FinishState.jpg");
        next_level = new RectangleId(50, (Variable.game.getWnd().GetWndHeight() - 100),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.newTile);
        save = new RectangleId(300, (Variable.game.getWnd().GetWndHeight() - 100),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.saveTile);
        menu = new RectangleId(550, (Variable.game.getWnd().GetWndHeight() - 100),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.menuTile);
        quit = new RectangleId(800, (Variable.game.getWnd().GetWndHeight() - 100),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.quitTile);
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea jocului si a butoanelor cand ma plimb cu mouse-ul.
     */
    @Override
    public void Update() {
        if (next_level.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            next_level.tile = Tile.nextLevel1Tile;
            if (Variable.game.mouseInput.clickRight) {
                //stabilesc unde ma duc
                if (Map.TipMap == 1) {
                    Variable.game.level2GameState = new Level2GameState();
                    Map.TipMap = 2;
                    Game.map = new Map();
                    State.SetState(Variable.game.level2GameState);
                } else {
                    Variable.game.level3GameState = new Level3GameState();
                    Map.TipMap = 3;
                    Game.map = new Map();
                    State.SetState(Variable.game.level3GameState);
                }
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            next_level.tile = Tile.nextLevelTile;
        }
        if (save.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            save.tile = Tile.save1Tile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(new EfectiveSaveGameState());
                Variable.game.mouseInput.clickRight = false;
                State.previousState = Variable.game.finishLVLState;
            }
        } else {
            save.tile = Tile.saveTile;
        }
        if (menu.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            menu.tile = Tile.menu1Tile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(Variable.game.menuState);
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            menu.tile = Tile.menuTile;
        }
        if (quit.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            quit.tile = Tile.quit1Tile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(Variable.game.quitGameState);
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            quit.tile = Tile.quitTile;
        }

    }

    /*! \fn public void Draw()
        \brief Deseneaza starea actuala.
     */
    @Override
    public void Draw() {
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(finishLevelGame, -3, 0, Variable.game.getWnd().GetWndWidth() + 3, Variable.game.getWnd().GetWndHeight(), null);
        ///desenare banuti colectati
        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 80));
        int val = Variable.game.COINS;
        Variable.g.drawString("Coins: " + val, Variable.game.getWnd().GetWndWidth() / 2 - 250, Variable.game.getWnd().GetWndHeight() / 2 - 80);
        ///desenare diamnate colectate
        Variable.g.setColor(Color.RED);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 80));
        val = Variable.game.DIAMONDS;
        Variable.g.drawString("Diamond: " + val, Variable.game.getWnd().GetWndWidth() / 2 - 250, Variable.game.getWnd().GetWndHeight() / 2);

        ///desenare butoane
        next_level.tile.DrawButtonLevel(Variable.g, next_level.r.x, next_level.r.y);
        save.tile.DrawButtonLevel(Variable.g, save.r.x, save.r.y);
        menu.tile.DrawButtonLevel(Variable.g, menu.r.x, menu.r.y);
        quit.tile.DrawButtonLevel(Variable.g, quit.r.x, quit.r.y);

        // end operatie de desenare
        // Se afiseaza pe ecran
        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}


