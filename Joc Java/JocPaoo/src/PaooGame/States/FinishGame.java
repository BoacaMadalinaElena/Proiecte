package PaooGame.States;

import PaooGame.Graphics.*;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class FinishGame
    \brief Implementeaza fereastra pentru final .
 */
public final class FinishGame extends State {
    private BufferedImage finish,menuActual;

    /*! \ fn public FinishGame()
        \brief Constructorul clasei
     */
    public FinishGame() {
        super();
        finish = ImageLoader.LoadImage("/menu/FinishState.jpg");
        menuActual = Assets.menu;
    }

    /*! \fn public void Update()
        \brief Actualizeaza setarile si meniul din ele .
     */
    @Override
    public void Update() {
        if (Variable.game.mouseInput.getX() >= Variable.game.getWnd().GetWndWidth() / 2 - 100 && Variable.game.mouseInput.getX() <=
                Variable.game.getWnd().GetWndWidth() / 2 + 100) {
            if (Variable.game.mouseInput.getY() >= Variable.game.getWnd().GetWndHeight() - 100 && Variable.game.mouseInput.getY() <= Variable.game.getWnd().GetWndHeight() - 20) {
                if (Variable.game.mouseInput.clickRight) {
                    State.SetState(Variable.game.menuState);
                    Variable.game.mouseInput.clickRight = false;
                }
                menuActual = Assets.menu1;
            } else {
                menuActual = Assets.menu;
            }
        } else {
            menuActual = Assets.menu;
        }
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza pe ecran fereastra pentru castigare.
     */
    @Override
    public void Draw() {
        Variable.g.clearRect(0, 0, Variable.game.getWnd().GetWndWidth(), Variable.game.getWnd().GetWndHeight());
        Variable.g.drawImage(finish, -3, 0, Variable.game.getWnd().GetWndWidth() + 3, Variable.game.getWnd().GetWndHeight(), null);                             //desenare fundal
        Variable.g.drawImage(menuActual, Variable.game.getWnd().GetWndWidth() / 2 - 100, Variable.game.getWnd().GetWndHeight() - 100, 200, 80, null);
        Variable.g.setColor(Color.YELLOW);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 80));
        int val = Variable.game.COINS;
        Variable.g.drawString("Coins: " + val, Variable.game.getWnd().GetWndWidth() / 2 - 250, Variable.game.getWnd().GetWndHeight() / 2 - 80);
        ///desenare diamnate colectate
        Variable.g.setColor(Color.RED);
        Variable.g.setFont(new Font("Courier New", Font.BOLD, 80));
        val = Variable.game.DIAMONDS;
        Variable.g.drawString("Diamond: " + val, Variable.game.getWnd().GetWndWidth() / 2 - 250, Variable.game.getWnd().GetWndHeight() / 2);

        // end operatie de desenare
        // Se afiseaza pe ecran
        Variable.game.getBs().show();
        // Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        // elementele grafice ce au fost desenate pe canvas).
        Variable.g.dispose();
    }
}
