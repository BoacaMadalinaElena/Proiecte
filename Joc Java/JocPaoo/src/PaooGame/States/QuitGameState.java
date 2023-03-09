package PaooGame.States;

import PaooGame.Graphics.ImageLoader;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class QuitGameState
    \brief Starea jocului la apasare butonului Quit
 */
public class QuitGameState extends State {
    private final BufferedImage finalGame;                              /*!< Referinta spre imaginea de fundal a jocului.*/

    /*! \ fn public quitGameState()
        \brief Constructorul clasei
     */
    public QuitGameState() {
        super();
        finalGame = ImageLoader.LoadImage("/background/startGame.png");
    }

    /*! \ fn public void Update()
        \brief Metoda de actualizare a stari

        Se va trece intr-o stare intermediara
     */
    @Override
    public void Update() {
        State.SetState(new ExitState());
    }

    /*! \ fn public void Draw()
        \brief Metoda va afisa starea curenta.
     */
    @Override
    public void Draw() {
        ///se sterge ce era mai intai
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(finalGame, 0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);                             //desenare fundal
        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 30));
        Variable.g.drawString("Goodbye", Variable.game.getWnd().GetWndWidth() / 2 - 70, Variable.game.getWnd().GetWndHeight() - 50);
        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
