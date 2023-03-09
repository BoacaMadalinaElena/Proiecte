package PaooGame.States;

import PaooGame.DataBase;
import PaooGame.Game;
import PaooGame.Graphics.*;
import PaooGame.Variable;
import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class EfectiveSaveGameState extends State
    \brief Starea in care se sta ca urmare a salvari.
 */
public class EfectiveSaveGameState extends State {
    private final BufferedImage startGame;                                              /*!< Referinta catre imaginea de fundal.*/

    /*! \fn public EfectiveSaveGameState()
        \brief Constructorul clasei
     */
    public EfectiveSaveGameState() {
        super();
        startGame = ImageLoader.LoadImage("/background/save.jpg");
    }

    /*! \fn public void Update()
        \brief Se ofera posibilitatea reveniri la meniu.
     */
    @Override
    public void Update() {
        if (Variable.game.keyInput.enter) {
            State.SetState(Variable.game.pauseState);                             //trec in meniu
        }
        Game.dataBase.CREATE(1,"");
        State.SetState(Variable.game.saveGameState);
    }

    /*! \fn public void Draw()
        \brief Deseneaza starea curenta.
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
        //DataBase.currentSave
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
