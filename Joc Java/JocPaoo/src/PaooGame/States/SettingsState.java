package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.ImageLoader;
import PaooGame.Input.Audio;
import PaooGame.Map.Map;
import PaooGame.Tiles.*;
import PaooGame.Variable;
import PaooGame.Map.RectangleId;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class SettingsState extends State
    \brief Implementeaza notiunea de setari pentru joc.
 */
public class SettingsState extends State {
    private final BufferedImage settings;                                           /*!< Referinta catre imaginea de fundal a jocului.*/
    private final RectangleId button, menu;                    /*!< Referinte catre cele 2 butoane din setari.*/
    private final RectangleId little, big;

    /*! \ fn public MenuState()
        \brief Constructorul clasei
     */
    public SettingsState() {
        super();
        settings = ImageLoader.LoadImage("/background/settings.png");
        //se vor folosi pentru coliziuni dreptunghiuri (impropriu spus coliziuni se va testa in ce dreptunghi sunt cu mouse-ul)
        //initializare butoane de meniu
        button = new RectangleId(Game.Width / 2 - 100, (int) (Variable.game.getWnd().GetWndHeight() * 0.25),
                Tile.TILE_WIDTH1, Tile.TILE_HEIGHT1, Tile.newTile);
        menu = new RectangleId(Game.Width / 2 - 100, 650, 200, 60, Tile.menu1Tile);
        little = new RectangleId(Game.Width / 2 - 310, (int) (Variable.game.getWnd().GetWndHeight() * 0.40),
                50, 50, Tile.little);
        big = new RectangleId(Game.Width / 2 - 250, (int) (Variable.game.getWnd().GetWndHeight() * 0.40),
                50, 50, Tile.big);
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a meniului.

        Se actualizeaza cand trec cu mouse-ul peste butone si la clik pe unul se efectueaza respectiva actiune.
     */
    @Override
    public void Update() {
        //inplicit  nu este nici una activa
        if (!Audio.status) {
            //afisez on
            button.tile = Tile.on1;
            if (button.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
                button.tile = Tile.on;
                if (Variable.game.mouseInput.clickRight) {
                    Audio.status = true;
                    Game.audio.play(Audio.volume);
                    Variable.game.mouseInput.clickRight = false;
                }
            } else {
                button.tile = Tile.on1;
            }
        } else {
            //afisez off
            button.tile = Tile.off1;
            if (button.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
                button.tile = Tile.off;
                if (Variable.game.mouseInput.clickRight) {
                    Audio.status = false;
                    Game.audio.stopAudio();
                    Variable.game.mouseInput.clickRight = false;
                }
            } else {
                button.tile = Tile.off1;
            }
        }
        if (menu.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
            menu.tile = Tile.menu1Tile;
            if (Variable.game.mouseInput.clickRight) {
                State.SetState(Variable.game.menuState);
                Variable.game.mouseInput.clickRight = false;
                Game.dataBaseSettings.CREATE(1);
            }
        } else {
            menu.tile = Tile.menuTile;
        }
            if (little.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
                little.tile = Tile.little1;
                if (Variable.game.mouseInput.clickRight) {
                    if (Audio.volume > 0.19) {
                        Audio.volume = Audio.volume - 0.1f;
                        if(Audio.status)
                            Game.audio.play(Audio.volume);
                        Variable.game.mouseInput.clickRight = false;
                    } else {
                        Audio.volume = 0;
                        if(Audio.status)
                            Game.audio.play(Audio.volume);
                        Variable.game.mouseInput.clickRight = false;
                    }
                }
            } else {
                little.tile = Tile.little;
            }
            if (big.r.contains(Variable.game.mouseInput.getX(), Variable.game.mouseInput.getY())) {
                big.tile = Tile.big1;
                if (Variable.game.mouseInput.clickRight) {
                    if (Audio.volume < 1) {
                        Audio.volume = Audio.volume + 0.1f;
                        Game.audio.play(Audio.volume);
                        Variable.game.mouseInput.clickRight = false;
                    } else
                        Audio.volume = 1;
                }
            } else {
                big.tile = Tile.big;
            }
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza meniul.
     */
    @Override
    public void Draw() {
        /// Se sterge ce era
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(settings, 0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);
        /// operatie de desenare
        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 80));
        Variable.g.drawString("Settings ", Variable.game.getWnd().GetWndWidth() / 2 - 150, 100);
        Variable.g.setColor(Color.ORANGE);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 40));
        Variable.g.drawString("Sound :  ", 200, (int) (Variable.game.getWnd().GetWndHeight() * 0.32));
        float aux = Audio.volume * 100;
        String auxStr = "" + aux;
        String[] listStr = auxStr.split("\\.");
        if (listStr.length >= 1) {
            int value = Integer.parseInt(listStr[0]);
            if (value % 10 != 0)
                value++;
            Variable.g.drawString("" + value, Variable.game.getWnd().GetWndHeight() / 2 + 200, (int) (Variable.game.getWnd().GetWndHeight() * 0.45));
        } else {
            int value = Integer.parseInt(auxStr);
            if (value % 10 != 0)
                value++;
            Variable.g.drawString("" + value, Variable.game.getWnd().GetWndHeight() / 2 , (int) (Variable.game.getWnd().GetWndHeight() * 0.45));
        }///desenare butoane
        menu.tile.DrawButton(Variable.g, menu.r.x, menu.r.y);
        button.tile.DrawButton(Variable.g, button.r.x, button.r.y);
        little.tile.Draw(Variable.g, little.r.x, little.r.y);
        big.tile.Draw(Variable.g, big.r.x, big.r.y);
        // end operatie de desenare
        /// Se afiseaza pe ecran
        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
