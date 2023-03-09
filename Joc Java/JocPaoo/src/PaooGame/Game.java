package PaooGame;

import PaooGame.Characters.*;
import PaooGame.GameWindow.Camera;
import PaooGame.GameWindow.GameWindow;
import PaooGame.Graphics.Assets;
import PaooGame.Input.Audio;
import PaooGame.Input.KeyInput;
import PaooGame.Input.MouseInput;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Vector;
import PaooGame.Map.Map;
import PaooGame.States.*;

/*! \class Game
    \brief Clasa principala a intregului proiect. Implementeaza Game - Loop (Update -> Draw)

                ------------
                |           |
                |     ------------
    60 times/s  |     |  Update  |  -->{ actualizeaza variabile, stari, pozitii ale elementelor grafice etc.
        =       |     ------------
     16.7 ms    |           |
                |     ------------
                |     |   Draw   |  -->{ deseneaza totul pe ecran
                |     ------------
                |           |
                -------------
    Implementeaza interfata Runnable:

        public interface Runnable {
            public void run();
        }

    Interfata este utilizata pentru a crea un nou fir de executie avand ca argument clasa Game.
    Clasa Game trebuie sa aiba definita metoda "public void run()", metoda ce va fi apelata
    in noul thread(fir de executie). Mai multe explicatii veti primi la curs.

    In mod obisnuit aceasta clasa trebuie sa contina urmatoarele:
        - public Game();            //constructor
        - private void init();      //metoda privata de initializare
        - private void update();    //metoda privata de actualizare a elementelor jocului
        - private void draw();      //metoda privata de desenare a tablei de joc
        - public run();             //metoda publica ce va fi apelata de noul fir de executie
        - public synchronized void start(); //metoda publica de pornire a jocului
        - public synchronized void stop()   //metoda publica de oprire a jocului
 */
public class Game implements Runnable {
    private GameWindow wnd;                         /*!< Fereastra in care se va desena tabla jocului*/
    private boolean runState;                       /*!< Flag ce starea firului de executie.*/
    private Thread gameThread;                      /*!< Referinta catre thread-ul de update si draw al ferestrei*/
    private BufferStrategy bs;                      /*!< Referinta catre un mecanism cu care se organizeaza memoria complexa pentru un canvas.*/
    public static int Width;                        /*!< Referinta spre latimea ferestrei.*/
    public static int Height;                       /*!< Referinta catre inaltimea ferestrei jocului.*/
    public static int COINS = 0;                    /*!< Referinta catre numarul de banuti colectati.*/
    public static int DIAMONDS = 0;                 /*!< Referinta catre numarul de diamnate colectate.*/
    public Camera camera;                           /*!< Referinta catre obiectul de tip camera.*/
    Graphics g;                                     /*!< Referinta catre un context grafic.*/
    public static Map map;                          /*!< Referinta catre harta/.*/
    ///variabile pentru starile jocului
    ///pentru implementarea jocului se va folosi Design Patterns - State Pattern
    public State menuState;                         /*!< Referinta catre starea de meniu.*/
    public State startGameState;                    /*!< Referinta catre starea inceput a jocului.*/
    public State loadGameState;                     /*!< Referinta catre starea load a jocului.*/
    public State saveGameState;                     /*!< Referinta catre starea de salvare a jocului.*/
    public State quitGameState;                     /*!< Referinta catre starea iesire din joc.*/
    public State deadState;                         /*!< Referinta catre starea de pierdere a jocului.*/
    public State castigatGame;                      /*!< Referinta catre starea de castigare  a jocului.*/
    public State level1GameState;                   /*!< Referinta catre starea : nivel 1 in joc.*/
    public State level2GameState;                   /*!< Referinta catre starea : nivel 2 in joc.*/
    public State level3GameState;                   /*!< Referinta catre starea : nivel 3 in joc.*/
    public State pauseState;                        /*!< Referinta catre starea de pauza.*/
    public  State finishLVLState;                   /*!< Referinta catre starea de castigare a unui nivel.*/

    ///referinte pentru mouse si tastatura
    public MouseInput mouseInput;                   /*!< Referinta catre un obiect de tip mouse.*/
    public KeyInput keyInput;                       /*!< Referinta catre un obiect de tip tastatura.*/
    public static DataBase dataBase;                /*!< Referinta catre clasa ce va gestiona baza de date(joc)*/
    public static DataBasePlayer dataBasePlayer;    /*!< Referinta catre clasa ce va gestiona baza de date(jucator)*/

    ///referinte spre persoaneje si gloante
    public static Vector<Elements> snake = null;    /*!< Referinta spre vectorul de serpi*/
    public static Vector<Elements> rino = null;     /*!< Referinta spre vectorul de mamuti*/
    public static  Vector<Elements> gloante = null; /*!< Referinta spre vectorul de gloante*/
    public static  int nrGloante = 20;              /*!< Referinta spre numarul de gloante disponibile*/
    public static Vector<Elements> mogly = null;    /*!< Referinta spre inamic(omul)*/
    public static Hero cat;                         /*!< Referinta spre pisica*/
    public static FactoryMethod factoryMethod ;     /*!< Referinta catre fabrica de obiecte.*/
    public static Audio audio ;
    public static DataBaseSettings dataBaseSettings;

    /// Sunt cateva tipuri de "complex buffer strategies", scopul fiind acela de a elimina fenomenul de
    /// flickering (palpaire) a ferestrei.
    /// Modul in care va fi implementata aceasta strategie in cadrul proiectului curent va fi triplu buffer-at

    ///                         |------------------------------------------------>|
    ///                         |                                                 |
    ///                 ****************          *****************        ***************
    ///                 *              *   Show   *               *        *             *
    /// [ Ecran ] <---- * Front Buffer *  <------ * Middle Buffer * <----- * Back Buffer * <---- Draw()
    ///                 *              *          *               *        *             *
    ///                 ****************          *****************        ***************

    /*! \fn public Game(String title, int width, int height)
        \brief Constructor de initializare al clasei Game.

        Acest constructor primeste ca parametri titlul ferestrei, latimea si inaltimea
        acesteia avand in vedere ca fereastra va fi construita/creata in cadrul clasei Game.

        \param title Titlul ferestrei.
        \param width Latimea ferestrei in pixeli.
        \param height Inaltimea ferestrei in pixeli.
     */
    public Game(String title, int width, int height) {
        /// Obiectul GameWindow este creat insa fereastra nu este construita
        /// Acest lucru va fi realizat in metoda init() prin apelul
        /// functiei BuildGameWindow();
        wnd = new GameWindow(title, width, height);
        /// Resetarea flagului runState ce indica starea firului de executie (started/stoped)
        Width = width;
        Height = height;
        runState = false;
        Variable.game = this;
        factoryMethod = new NewConcreteFactory();
    }

    /*! \fn private void init()
        \brief  Metoda construieste fereastra jocului, initializeaza aseturile, listenerul de tastatura etc.

        Fereastra jocului va fi construita prin apelul functiei BuildGameWindow();
        Sunt construite elementele grafice (assets): dale, player, elemente active si pasive.

     */
    private void InitGame() {
        wnd = new GameWindow("Tom’s adventure", Width, Height);
        ///initializare variabile pentru Input(mouse si tastatura)
        mouseInput = new MouseInput();
        ///Este construcit obiectul de tip mouse
        keyInput = new KeyInput();
        /// Este construita fereastra grafica.
        wnd.BuildGameWindow();
        /// Se incarca toate elementele grafice (dale)
        Assets.Init();
        ///Se ataseaza ferestrei variabilele de intrare pentru mause si tastatura
        wnd.GetWndFrame().addKeyListener(keyInput);
        wnd.GetCanvas().addKeyListener(keyInput);
        wnd.GetWndFrame().addMouseListener(mouseInput);
        wnd.GetWndFrame().addMouseMotionListener(mouseInput);
        wnd.GetCanvas().addMouseListener(mouseInput);
        wnd.GetCanvas().addMouseMotionListener(mouseInput);

        ///se initializeaza starile jocului
        startGameState = new StartGameState();
        menuState = new MenuState();
        loadGameState = new LoadGameState();
        map = new Map();
        saveGameState = new SaveGameState();
        deadState = new DeadState();
        castigatGame = new FinishGameState();
        quitGameState = new QuitGameState();
        pauseState = new PauseState();
        finishLVLState = new FinishState();
        ///Este construit obiectul de tip camera(pentru deplasarea personajului )
        camera = new Camera(0, 0);
        dataBase = new DataBase();
        dataBasePlayer = new DataBasePlayer();
        dataBasePlayer.CREATE(0,"",0);
        audio = new Audio();
        audio.play(0.5f);
        dataBaseSettings = new DataBaseSettings();
        dataBaseSettings.CREATE(2);
        ///se seteaza starea curenta a programului
        State.SetState(startGameState);
        ///se seteaza valorile curente pentru Map
    }

    /*! \fn public void run()
        \brief Functia ce va rula in thread-ul creat.

        Aceasta functie va actualiza starea jocului si va redesena tabla de joc (va actualiza fereastra grafica)
     */
    public synchronized void run() {
        /// Initializeaza obiectul game
        InitGame();
        long oldTime = System.nanoTime();   /*!< Retine timpul in nanosecunde aferent frame-ului anterior.*/
        long curentTime;                    /*!< Retine timpul curent de executie.*/

        /// Apelul functiilor Update() & Draw() trebuie realizat la fiecare 16.7 ms
        /// sau mai bine spus de 60 ori pe secunda.

        final int framesPerSecond = 60; /*!< Constanta intreaga initializata cu numarul de frame-uri pe secunda.*/
        final double timeFrame = 1000000000 / framesPerSecond; /*!< Durata unui frame in nanosecunde.*/
        dataBase.CREATE(0,"");
        /// Atat timp timp cat threadul este pornit Update() & Draw()
        while (runState) {
            /// Se obtine timpul curent
            curentTime = System.nanoTime();
            /// Daca diferenta de timp dintre curentTime si oldTime mai mare decat 16.6 ms
            if ((curentTime - oldTime) > timeFrame) {
                /// Actualizeaza pozitiile elementelor
                Update();
                /// Deseneaza elementele grafica in fereastra.
                Draw();
                oldTime = curentTime;
            }
        }
    }

    public GameWindow getWnd() {
        return wnd;
    }

    public BufferStrategy getBs() {
        return bs;
    }

    public void setBs(BufferStrategy x) {
        bs = x;
    }

    /*! \fn public synchronized void start()
        \brief Creaza si starteaza firul separat de executie (thread).

        Metoda trebuie sa fie declarata synchronized pentru ca apelul acesteia sa fie semaforizat.
     */
    public synchronized void StartGame() {
        if (!runState) {
            /// Se actualizeaza flagul de stare a threadului
            runState = true;
            /// Se construieste threadul avand ca parametru obiectul Game. De retinut faptul ca Game class
            /// implementeaza interfata Runnable. Threadul creat va executa functia run() suprascrisa in clasa Game.
            gameThread = new Thread(this);
            /// Threadul creat este lansat in executie (va executa metoda run())
            gameThread.start();
        } else {
            /// Thread-ul este creat si pornit deja
            return;
        }
    }

    /*! \fn public synchronized void stop()
        \brief Opreste executie thread-ului.

        Metoda trebuie sa fie declarata synchronized pentru ca apelul acesteia sa fie semaforizat.
     */
    public synchronized void StopGame() {
        if (runState) {
            /// Actualizare stare thread
            runState = false;
            /// Metoda join() arunca exceptii motiv pentru care trebuie incadrata intr-un block try - catch.
            try {
                /// Metoda join() pune un thread in asteptare panca cand un altul isi termina executie.
                /// Totusi, in situatia de fata efectul apelului este de oprire a threadului.
                gameThread.join();
            } catch (InterruptedException ex) {
                /// In situatia in care apare o exceptie pe ecran vor fi afisate informatii utile pentru depanare.
                ex.printStackTrace();
                System.exit(0);
            }
        } else {
            /// Thread-ul este oprit deja.
            return;
        }
    }

    /*! \fn private void Update()
        \brief Actualizeaza starea elementelor din joc.

        Metoda este declarata privat deoarece trebuie apelata doar in metoda run()
        In fond este apelata metoda Update pentru starea curenta(in fiecare stare am altceva de facut)
     */
    private void Update() {
        ///se va actualiza starea curenta
        if(Game.COINS + Game.DIAMONDS*2 > DataBasePlayer.point){
            Game.dataBasePlayer.CREATE(2,DataBasePlayer.currentPlayer,Game.COINS + Game.DIAMONDS*2);
        }
        State.GetState().Update();
    }

    /*! \fn private void Draw()
        \brief Deseneaza elementele grafice in fereastra coresponzator starilor actualizate ale elementelor.

        Metoda este declarata privat deoarece trebuie apelata doar in metoda run()
     */
    private void Draw() {

        /// Se sterge ce era
        /// Returnez bufferStrategy pentru canvasul existent
        BufferStrategy x = getWnd().GetCanvas().getBufferStrategy();
        setBs(x);
        /// Verific daca buffer strategy a fost construit sau nu
        if (getBs() == null) {
            /// Se executa doar la primul apel al metodei Draw()
            try {
                /// Se construieste tripul buffer
                getWnd().GetCanvas().createBufferStrategy(3);
                return;
            } catch (Exception e) {
                /// Afisez informatii despre problema aparuta pentru depanare.
                e.printStackTrace();
            }
        }
        /// Se obtine contextul grafic curent in care se poate desena.
        Graphics g;
        g = getBs().getDrawGraphics();
        ///am nevoie des de contextul grafic deci il atribui intr-o clasa ce il retine
        Variable.g = g;
        ///se apeleaza desenarea pentru starea actuala
        State.GetState().Draw();
        Variable.game.getBs().show();
        /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
        /// elementele grafice ce au fost desenate pe canvas).
        g.dispose();
    }

    public Graphics getG() {
        return g;
    }


}


