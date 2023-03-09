package PaooGame.States;

import PaooGame.Graphics.*;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class DeadState
    \brief Pisica pierde jocul prin deces.
 */
public class DeadState extends State {
    private final BufferedImage deadCat;                                /*!< Referinta catre imaginea de fundal.*/

    /*! \fn public DeadState()
        \brief Constructorul clasei
     */
    public DeadState() {
        super();
        deadCat = ImageLoader.LoadImage("/background/GameOver.png");
    }

    /*! \fn public void Update()
        \brief Asteapta ca jucatorul sa apese enter pentru a reveni in meniu.
     */
    @Override
    public void Update() {
        if (Variable.game.keyInput.enter) {
            State.SetState(Variable.game.menuState);                             //trec in meniu
        }
    }

    /*! \fn public void Draw(Game game)
        \brief Deseneaza starea actuala.
     */
    @Override
    public void Draw() {
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(deadCat, -3, 0, Variable.game.getWnd().GetWndWidth() + 3, Variable.game.getWnd().GetWndHeight(), null);                             //desenare fundal
        Variable.g.setColor(Color.WHITE);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        Variable.g.drawString("  Press enter to continue", Variable.game.getWnd().GetWndWidth() / 2 - 150, Variable.game.getWnd().GetWndHeight() - 50);
        // end operatie de desenare
        // Se afiseaza pe ecran
        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
