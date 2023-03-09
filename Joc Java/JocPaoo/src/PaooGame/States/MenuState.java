package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.ImageLoader;
import PaooGame.Map.Map;
import PaooGame.Tiles.*;
import PaooGame.Variable;
import PaooGame.Map.RectangleId;

import java.awt.image.BufferedImage;

/*! \class public class MenuState extends State
    \brief Implementeaza notiunea de meniu pentru joc.
 */
public class MenuState extends State {
    private final BufferedImage menu;                                           /*!< Referinta catre imaginea de fundal a jocului.*/
    private final RectangleId _new, load, information, quit,settings;                    /*!< Referinte catre cele 4 butoane din meniu.*/

    /*! \ fn public MenuState()
        \brief Constructorul clasei
     */
    public MenuState() {
        super();
        menu = ImageLoader.LoadImage("/background/menu1.png");
        //se vor folosi pentru coliziuni dreptunghiuri (impropriu spus coliziuni se va testa in ce dreptunghi sunt cu mouse-ul)
        //initializare butoane de meniu
        _new = new RectangleId((int) (Variable.game.getWnd().GetWndWidth() * 0.375), (int) (Variable.game.getWnd().GetWndHeight() * 0.25),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.newTile);
        load = new RectangleId((int) (Variable.game.getWnd().GetWndWidth() * 0.375), (int) (Variable.game.getWnd().GetWndHeight() * 0.40),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.loadTile);
        information = new RectangleId((int) (Variable.game.getWnd().GetWndWidth() * 0.375), (int) (Variable.game.getWnd().GetWndHeight() * 0.55),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.informationButtonTile);
        settings = new RectangleId((int) (Variable.game.getWnd().GetWndWidth() * 0.375), (int) (Variable.game.getWnd().GetWndHeight() * 0.70),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.settingsTile);
        quit = new RectangleId((int) (Variable.game.getWnd().GetWndWidth() * 0.375), (int) (Variable.game.getWnd().GetWndHeight() * 0.85),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.quitTile);
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a meniului.

        Se actualizeaza cand trec cu mouse-ul peste butone si la clik pe unul se efectueaza respectiva actiune.
     */
    @Override
    public void Update() {
        //inplicit  nu este nici una activa
        if (_new.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            _new.tile = Tile.new1Tile;
            if (Variable.game.mouseInput.clickRight) {
                Map.TipMap = 1;
                Variable.game.level1GameState = new Level1GameState();
                Game.map = new Map();
                State.SetState(Variable.game.level1GameState);
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            _new.tile = Tile.newTile;
        }
        if (load.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            load.tile = Tile.load1Tile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(Variable.game.loadGameState);
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            load.tile = Tile.loadTile;
        }
        if (information.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            information.tile = Tile.informationButtonTile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(new InformationState());
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            information.tile = Tile.informationButton1Tile;
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
        if (settings.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            settings.tile = Tile.settings1Tile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(new SettingsState());
                Variable.game.mouseInput.clickRight = false;
            }
        } else {
            settings.tile = Tile.settingsTile;
        }
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza meniul.
     */
    @Override
    public void Draw() {
        /// Se sterge ce era
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(menu, 0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);
        /// operatie de desenare
        _new.tile.DrawButton(Variable.g, _new.r.x, _new.r.y);
        load.tile.DrawButton(Variable.g, load.r.x, load.r.y);
        information.tile.DrawButton(Variable.g, information.r.x, information.r.y);
        quit.tile.DrawButton(Variable.g, quit.r.x, quit.r.y);
        settings.tile.DrawButton(Variable.g, settings.r.x, settings.r.y);
        // end operatie de desenare
        /// Se afiseaza pe ecran
        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
