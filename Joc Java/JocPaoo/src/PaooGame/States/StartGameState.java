package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.*;
import PaooGame.Input.Audio;
import PaooGame.Variable;
import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class StartGameState extends State
    \brief Implementeaza ferestra de inceput a jocului.
 */
public class StartGameState extends State {
    private final BufferedImage startGame;                          /*!< Referinta sprea imaginea de fundal.*/

    /*! \ fn public startGameState(Game game)
        \brief Constructorul clasei
     */
    public StartGameState() {
        super();
        startGame = ImageLoader.LoadImage("/background/startGame.png");
    }

    /*! \fn public void Update()
        \brief La enter se va intra in meniu
     */
    @Override
    public void Update() {

        if (Variable.game.keyInput.enter) {
            State.SetState(new LoadPlayerGameState());                             //trec in meniu
            Variable.game.keyInput.enter = false;
        }
    }

    /*! \fn public void Draw(Game game)
        \brief Deseneaza fereastra de inceput a jocului.
     */
    @Override
    public void Draw() {
        //se sterge ce era mai intai
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(startGame, 0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);                             //desenare fundal

        //se scrie cum se poate iesi din aceasta stare
        Variable.g.setColor(Color.WHITE);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        Variable.g.drawString("Press enter to continue", Variable.game.getWnd().GetWndWidth() / 2 - 150, Variable.game.getWnd().GetWndHeight() - 50);
        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
