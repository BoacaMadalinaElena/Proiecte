package PaooGame.States;

import PaooGame.Graphics.ImageLoader;
import PaooGame.Variable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/*! \class public class ExitState extends State
    \brief Starea in care jocul sta cateva secunde inainte de a se inchide.
 */
public class ExitState extends State {
    private final BufferedImage finalGame;                              /*!< Referinta catre imaginea de fundal.*/

    /*! \fn public ExitState()
        \brief Constructorul clasei
     */
    public ExitState() {
        super();
        finalGame = ImageLoader.LoadImage("/background/startGame.png");
    }

    /*! \fn public void Update()
        \brief Se va parasi jocul dupa doua secunde.
     */
    @Override
    public void Update() {
        try {
            TimeUnit.SECONDS.sleep(2);
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

    /*! \fn public void Draw()
        \brief Nu este folosita.

        Se va evita ramanerea clasei abstracta , metoda nu face nimic.
     */
    @Override
    public void Draw() {

    }
}
