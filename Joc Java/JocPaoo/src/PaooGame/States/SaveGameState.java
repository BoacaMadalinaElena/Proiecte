package PaooGame.States;

import PaooGame.DataBase;
import PaooGame.Graphics.*;
import PaooGame.Variable;
import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class SaveGameState extends State
    \brief Implementeaza salvarea jocului.
 */
public class SaveGameState extends State {
    private final BufferedImage startGame;                                  /*!< Referinta spre imaginea de fundal a jocului.*/

    /*! \ fn public SaveGameState()
        \brief Constructorul clasei
     */
    public SaveGameState() {
        super();
        startGame = ImageLoader.LoadImage("/background/save.jpg");
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a salvarilor.
     */
    @Override
    public void Update() {
        if (Variable.game.keyInput.enter) {
            if(State.previousState == Variable.game.finishLVLState)
                State.SetState(Variable.game.finishLVLState);                             //trec in meniu
            else
                State.SetState(Variable.game.pauseState);                             //trec in meniu
        }
    }

    /*! \fn public void Draw()
        \brief Deseneaza fereastra de salvari.
     */
    @Override
    public void Draw() {
        /// Se sterge ce era
        Variable.g.drawImage(startGame, -3, 0, Variable.game.getWnd().GetWndWidth() + 3, Variable.game.getWnd().GetWndHeight(), null);
        // end operatie de desenare
        /// Se afiseaza pe ecran
        Variable.g.setColor(Color.orange);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 40));
        Variable.g.drawString("The current rescue has the name : " , 100, Variable.game.getWnd().GetWndHeight() /2);
        ///se va afisa numele salvari generat automat
        Variable.g.drawString(DataBase.currentSave , 300, Variable.game.getWnd().GetWndHeight() /2 + 40);
        Variable.g.setColor(Color.orange);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 20));
        Variable.g.drawString("Press enter to continue", Variable.game.getWnd().GetWndWidth() / 2 - 150, Variable.game.getWnd().GetWndHeight() - 50);
        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
