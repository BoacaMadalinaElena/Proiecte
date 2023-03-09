package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.ImageLoader;
import PaooGame.Map.Map;
import PaooGame.Map.RectangleId;
import PaooGame.Tiles.Tile;
import PaooGame.Variable;
import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class PauseState extends State
    \brief Implementeaza starea de pauze.
 */
public class PauseState extends State {
    private final BufferedImage finishLevelGame;                          /*!< Referinta spre imaginea de fundal*/
    private final RectangleId _continue, save, exit,menu;                 /*!< Referinte spre butoanele din aceasta stare*/

    /*! \fn public PauseState()
        \brief Constructorul clasei
     */
    public PauseState() {
        super();
        _continue = new RectangleId(Game.Width / 2 - 100, 275, 200, 60, Tile.continuePauseTile);
        save = new RectangleId(Game.Width / 2 - 100, 375, 200, 60, Tile.savePauseTile);
        exit = new RectangleId(Game.Width / 2 - 100, 575, 200, 60, Tile.exitPauseTile);
        menu = new RectangleId(Game.Width/2-100,475,200,60,Tile.menu1Tile);
        finishLevelGame = ImageLoader.LoadImage("/background/menu1.png");
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea jocului si a butoanelor cand ma plimb cu mouse-ul.
     */
    @Override
    public void Update() {
        if (_continue.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            _continue.tile = Tile.continuePauseTile;
            if (Variable.game.mouseInput.clickRight) {
                if(Map.TipMap == 1)
                    State.SetState(Variable.game.level1GameState);
                if(Map.TipMap == 2)
                    State.SetState(Variable.game.level2GameState);
                if(Map.TipMap == 3)
                    State.SetState(Variable.game.level3GameState);
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            _continue.tile = Tile.continuePause1Tile;
        }
        if (save.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            save.tile = Tile.savePauseTile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(new EfectiveSaveGameState());
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            save.tile = Tile.savePause1Tile;
        }
        if (exit.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            exit.tile = Tile.exitPauseTile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(Variable.game.quitGameState);
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            exit.tile = Tile.exitPause1Tile;
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
        Variable.g.drawString("PAUSE ", Variable.game.getWnd().GetWndWidth() / 2 -120, 220);
        ///desenare butoane
        _continue.tile.DrawButtonLevel(Variable.g, _continue.r.x, _continue.r.y);
        save.tile.DrawButtonLevel(Variable.g, save.r.x, save.r.y );
        exit.tile.DrawButtonLevel(Variable.g, exit.r.x, exit.r.y );
        menu.tile.DrawButtonLevel(Variable.g, menu.r.x, menu.r.y );
        // end operatie de desenare
        // Se afiseaza pe ecran
        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}


