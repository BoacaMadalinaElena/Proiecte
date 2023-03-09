package PaooGame;

import PaooGame.Auxiliary.DataBaseItem;
import PaooGame.Auxiliary.MyVector;
import PaooGame.Characters.FactoryMethod;
import PaooGame.Characters.DBConcreteFactory;
import PaooGame.Characters.Elements;
import PaooGame.Exception.ItemNotFond;
import PaooGame.Exception.ToManyItems;
import PaooGame.Exception.TypeNotFound;
import PaooGame.Map.Map;
import PaooGame.States.Level1GameState;
import PaooGame.States.Level2GameState;
import PaooGame.States.Level3GameState;
import PaooGame.States.State;
import PaooGame.Tiles.Tile;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/*! \class public class DataBase
    \brief Va realiza gestionarea bazei de date.

    Voi putea adauga un nou element in baza de date , afisa toate inregistrarile , returna o anumita inregistrare , sterge o anumita inregistrare
 */
public class DataBase {
    Connection c = null;                                                  /*!< Referinta pentru conectarea la baza de date.*/
    Statement stmt = null;                                                /*!< Referinta pentru a putea transmite comenzi bazei de date.*/
    public static String currentSave = "";                                /*!< Referinta spre numele inregistrari curente(utila la salvare).*/
    FactoryMethod abstractFactory;                                       /*!< Referinta catre o fabrica abstracta de obiecte.*/

    /*! \fn public DataBase()
        \brief Constructorul clasei.
     */
    public DataBase() {
        abstractFactory = new DBConcreteFactory();
    }

    /*! \fn public List<DataBaseItem> CREATE(int opt, String name)
        \brief Va gestiona baza de date(din ea voi apela celelalte metode).

        \param opt un intreg prin case voi specifica ce operatie realizez cu baza de date.
        \param name un String ce indica ce data extrag din baza de date sau sterg
     */
    public List<DataBaseItem> CREATE(int opt, String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:res/DataBase/GameDataBase.db");
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS  GameDataBase " +
                    ///int
                    "(NAME CHAR(50) NOT NULL ," +
                    "TIME CHAR(19) NOT NULL," +
                    "NUMBER_LEVEL INT  NOT NULL," +
                    " CAT_X INT NOT NULL, " +
                    " CAT_Y INT NOT NULL, " +
                    " COINS INT NOT NULL, " +
                    " DIAMOND INT NOT NULL," +
                    "LIFE INT NOT NULL, " +
                    "BULLET CHAR(200) NOT NULL, " +
                    "SNAKE CHAR(200) NOT NULL, " +
                    "MOGLY CHAR(200) NOT NULL," +
                    "RINO CHAR(200) NOT NULL, " +
                    "MAP CHAR(3000) NOT NULL, " +
                    "NUMBER_BULLET NOT NULL )";
            stmt.execute(sql);
            if (opt == 1) {
                INSERT();
            } else if (opt == 2) {
                SELECT_ITEM(name);
            } else if (opt == 3) {
                return getDataBaseItems();
            } else if (opt == 4) {
                DELETE_ITEM(name);
            } else {
                System.out.println("Table created successfully");
            }

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /*! \fn private String convertArrayCharactersToString(Vector<Elements> characters)
        \brief Va converti vectorul de personaje la String.

        \param characters vectorul de convertit .

        Baza de date nu ofera posibilitati pentru salvarea vectorilor.
     */
    private String convertArrayCharactersToString(Vector<Elements> characters) throws ToManyItems {
        StringBuilder aux = new StringBuilder();
        try {
            if (characters != null && characters.size() > 0) {
                ///conversia la string
                aux.append(characters.get(0).getSens());
                aux.append("-");
                aux.append(characters.size());
                aux.append("-");
                aux.append(characters.get(0).getWidth());
                aux.append("-");
                aux.append(characters.get(0).getHeight());
                aux.append("-");
                aux.append(characters.get(0).getSpace().width);
                aux.append("-");
                aux.append(characters.get(0).getSpace().height);
                aux.append("-");
                for (Elements character : characters) {
                    aux.append(character.getX());
                    aux.append("-");
                    aux.append(character.getY());
                    aux.append("-");
                }
                if (aux.length() > 200) {
                    throw new ToManyItems("Vectorul este prea mare.");
                }
                aux.append(" ".repeat(200 - aux.length()));
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            System.out.println("Exception : " + e);
        }
        return aux.toString();
    }

    /*! \fn private String convertMatrixToString(int[][] mat)
        \brief Va converti harta(matricea) intr-un string.

        \param mat este matricea de convertit.

        Baza de date nu ofera posibilitati pentru salvarea directa a matricelor.
     */
    private String convertMatrixToString(int[][] mat) throws ToManyItems {
        StringBuilder aux = new StringBuilder();
        // '|' separa doua linii
        // '-' separa doua coloane
        if (mat == null)
            return "";

        for (int[] ints : mat) {
            for (int anInt : ints) {
                aux.append(anInt).append("-");
            }
            aux.append("|");
        }
        String sir = aux.toString();
        sir = sir.replace("-|", "|");
        if (sir.length() > 3000)
            throw new ToManyItems("Creare string din matrice");
        return sir;
    }

    /*! \fn private static String getActualTime()
        \brief Va returna momentul actual de timp ca un string.
     */
    private static String getActualTime() {
        String time = LocalDate.now() + "  " + LocalTime.now().toString();
        time = time.replace(":", "-");
        String[] list = time.split("\\.");
        list[1] = "";
        time = list[0];
        return time;
    }

    /*! \fn public void INSERT()
        \brief Va insera datele actuale(viata,banuti,diamante etc) in baza de date.
     */
    public void INSERT() {
        try {
            PreparedStatement prep = c.prepareStatement("INSERT INTO GameDataBase (NAME,TIME,NUMBER_LEVEL,CAT_X,CAT_Y,COINS,DIAMOND,LIFE,BULLET,SNAKE,MOGLY,RINO,MAP,NUMBER_BULLET) VALUES(?,?, ?, ?,?, ?, ?,?,?,?,?,?,?,?)");
            StringBuilder aux = new StringBuilder(getActualTime());
            prep.setString(2, aux.toString());

            File myObj = new File("FileSaveNumberSave.txt");
            Scanner myReader = new Scanner(myObj);
            int cnt = myReader.nextInt();
            String name = "Salvage" + cnt;
            DataBase.currentSave = name;
            prep.setString(1, name);
            prep.setInt(3, Map.TipMap);
            prep.setInt(4, Game.cat.getX());
            prep.setInt(5, Game.cat.getY());
            prep.setInt(6, Game.COINS);
            prep.setInt(7, Game.DIAMONDS);
            prep.setInt(8, Game.cat.getLife());

            ///conversie vector string
            try {
                prep.setString(9, convertArrayCharactersToString(Variable.gloante));
                prep.setString(10, convertArrayCharactersToString(Game.snake));
                prep.setString(11, convertArrayCharactersToString(Game.mogly));
                prep.setString(12, convertArrayCharactersToString(Game.rino));
                prep.setString(13, convertMatrixToString(Game.map.getMap()));
                prep.setInt(14, Variable.nrGloante);
            } catch (ToManyItems e) {
                System.out.println("Exception : " + e.getMessage());
            }
            BufferedWriter out = new BufferedWriter(new FileWriter("FileSaveNumberSave.txt", false));
            cnt++;
            String aux1 = "" + cnt;
            out.write(aux1);
            out.close();
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*! \fn private void SELECT_ITEM(String name)
        \brief Va extrage o anumita inregistrare din baza de date.

        \param name este numele salvari de extras din baza de date

        Se va face automat si schimbarea valorilor din jocul curent(restaurarea stari).
     */
    private void SELECT_ITEM(String name) throws ItemNotFond {
        try {
            PreparedStatement statement = c.prepareStatement("SELECT * from GameDataBase WHERE  NAME = ?");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            boolean visit = false;
            while (rs.next()) {
                visit = true;
                ///parsare elemente din baza de date ...
                int numberLevel = rs.getInt(3);
                int catX = rs.getInt(4);
                int catY = rs.getInt(5);
                int coins = rs.getInt(6);
                int diamond = rs.getInt(7);
                int life = rs.getInt(8);
                int nrBullet = rs.getInt(14);
                String bullet = rs.getString(9);
                String snake = rs.getString(10);
                String mogly = rs.getString(11);
                String rino = rs.getString(12);
                ///valori vectori
                Map.TipMap = numberLevel;
                String map = rs.getString(13);
                if (numberLevel == 1) {
                    Variable.game.level1GameState = new Level1GameState();
                    State.SetState(Variable.game.level1GameState);
                } else if (numberLevel == 2) {
                    Variable.game.level2GameState = new Level2GameState();
                    State.SetState(Variable.game.level2GameState);
                } else if (numberLevel == 3) {
                    Variable.game.level3GameState = new Level3GameState();
                    State.SetState(Variable.game.level3GameState);
                }
                Game.COINS = coins;
                Game.DIAMONDS = diamond;
                Game.cat.setX(catX);
                Game.cat.setY(catY);
                Game.cat.setLife(life);
                Variable.nrGloante = nrBullet;
                ///procesare string pentru Elements
                Vector<Elements> elements = ParseStringElements(bullet, "bullet");
                Game.gloante = elements;
                Variable.gloante = elements;
                elements = ParseStringElements(snake, "snake");
                Game.snake = elements;
                elements = ParseStringElements(mogly, "mogly");
                Game.mogly = elements;
                elements = ParseStringElements(rino, "rino");
                Game.rino = elements;
                int[][] _map = ParseStringMatrix(map);
            }
            if (!visit)
                throw new ItemNotFond(name);                            //voi arunca exceptie
            rs.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /*! \fn public List<DataBaseItem> getDataBaseItems()
        \brief Va returna o lista cu toate inregistrarile din baza de date.
     */
    public List<DataBaseItem> getDataBaseItems() {
        List<DataBaseItem> listItem = new LinkedList<>();
        try {
            PreparedStatement statement = c.prepareStatement("SELECT NAME,TIME from GameDataBase ");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                String date = rs.getString(2);
                listItem.add(new DataBaseItem(name, date, 0, 0, 400, 50, Tile.itemDataBaseTile));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return listItem;
    }

    /*! \fn private Vector<Elements> ParseStringElements(String str, String tip)
        \brief Va converti string-ul dat ca parametru in elementul de tip : tip.

        \param str - sursa din care se extrag datele
        \param tip - indica tipul in care se converteste
    */
    private Vector<Elements> ParseStringElements(String str, String tip) {
        Vector<Elements> elements = new Vector<>();
        if (!Objects.equals(str, "")) {
            String[] list = str.split("-");
            int sens = Integer.parseInt(list[0]);
            int size = Integer.parseInt(list[1]);
            int width = Integer.parseInt(list[2]);
            int height = Integer.parseInt(list[3]);
            int widthSpace = Integer.parseInt(list[4]);
            int heightSpace = Integer.parseInt(list[5]);
            int x, y;
            ///de la 5 pana la lenght-2 am x y pentru gloante
            for (int i = 0; i < size * 2; i = i + 2) {
                x = Integer.parseInt(list[i + 6]);
                y = Integer.parseInt(list[i + 7]);
                try {
                    elements.add(abstractFactory.createElements(tip, sens, width, height, widthSpace, heightSpace, x, y));
                }catch (TypeNotFound e){
                    System.out.println(e);
                }
            }
        }
        return elements;
    }

    /*! \fn private int[][] ParseStringMatrix(String str)
        \brief Va folosi string-ul dat ca parametru pentru a reconstitui matricea.

        \param str - sursa din care se extrag datele
    */
    private int[][] ParseStringMatrix(String str) {
        Vector<MyVector> mat = new Vector<>();
        String[] linii = str.split("\\|");
        for (int i = 0; i < linii.length; i++) {
            String[] linie = linii[i].split("-");
            mat.add(new MyVector(linie));
        }
        //iau din MyVector linia ca int[] transformata si creez din nou matricea
        int[] lineInt;
        for (int i = 0; i < mat.size(); i++) {
            lineInt = mat.get(i).convertStringArrayToIntArray();
            for (int j = 0; j < lineInt.length; j++) {
                Game.map.setMap(i, j, lineInt[j]);
            }
        }
        return null;
    }

    private void DELETE_ITEM(String name) throws ItemNotFond {
        try {
            String sql = "DELETE FROM GameDataBase WHERE NAME = ?";

            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}


