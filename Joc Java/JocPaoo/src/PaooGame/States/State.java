package PaooGame.States;

/*! \class State
    \brief Implementeaza sablonul de proiectare State : jocul este compus din mai multe stari.
 */
public abstract class State {
    private static State currentState = null;                           /*!< Referinta catre starea curenta*/
    public static State previousState = null;                           /*!< Referinta catre starea anterioara(salvare din Pause sau FinishLevel)*/

    /*! \fn public static void SetState(State s)
        \brief se va modifica valoarea lui currentState in timpul joclui.
     */
    public static void SetState(State s) {
        currentState = s;
    }

    /*! \fn public static State GetState()
        \brief Returneaza starea curenta.
     */
    public static State GetState() {
        return currentState;
    }

    ///se va redefini pentru fiecare stare in parte
    public abstract void Update();

    ///se va redefini pentru fiecare stare in parte
    public abstract void Draw();

}
