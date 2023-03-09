package PaooGame.States;

import PaooGame.Game;
import PaooGame.Graphics.*;
import PaooGame.Variable;
import java.awt.image.BufferedImage;

/*! \class public class InformationState extends State
    \brief Implementeaza starea in care jucatorul poate citi informarii despre joc.
 */
public class InformationState extends State {
    private final BufferedImage startGame;                              /*!< Referinta catre imaginea de funal a jocului.*/

    /*! \fn public InformationState()
        \brief Constructorul clasei
     */
    public InformationState() {
        super();
        startGame = ImageLoader.LoadImage("/background/infoGame.jpg");
    }

    /*! \fn public void Update()
        \brief La enter se poate reveni in meniu.
     */
    @Override
    public void Update() {
        if (Variable.game.keyInput.enter) {
            State.SetState(Variable.game.menuState);                             //trec in meniu
        }
    }

    /*! \fn public void Draw()
        \brief Deseneaza starea actuala.
     */
    @Override
    public void Draw() {
        ///se sterge ce era mai intai
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(startGame, 0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight(), null);                             //desenare fundal

        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
