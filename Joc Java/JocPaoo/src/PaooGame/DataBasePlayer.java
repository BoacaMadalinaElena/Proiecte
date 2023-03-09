package PaooGame;

import PaooGame.Auxiliary.DataBaseItemPlayer;
import PaooGame.Exception.ItemNotFond;
import PaooGame.Tiles.Tile;

import java.io.*;
import java.sql.*;
import java.util.*;

/*! \class public class DataBasePlayer
    \brief Va realiza gestionarea bazei de date pentru jucatori.

    Voi putea adauga un nou element in baza de date , afisa toate inregistrarile , modifica o inregistrare.
 */
public class DataBasePlayer {
    Connection c = null;                                                  /*!< Referinta pentru conectarea la baza de date.*/
    Statement stmt = null;                                                /*!< Referinta pentru a putea transmite comenzi bazei de date.*/
    public static String currentPlayer = "";                              /*!< Referinta spre identificatorul jucatorului.*/
    public static int point = 0;                                          /*!< Referinta spre numarul de puncte al jucatorului.*/

    ///prin convenite numarul de puncte este 2*DIAMOND + COINS
    /*! \fn public List<DataBaseItem> CREATE(int opt, String name)
        \brief Va gestiona baza de date(din ea voi apela celelalte metode).

        \param opt un intreg prin case voi specifica ce operatie realizez cu baza de date.
        \param name un String ce indica ce data extrag din baza de date sau sterg
        \param int point - numarul de puncte
     */
    public List<DataBaseItemPlayer> CREATE(int opt, String name, int point) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:res/DataBase/GameDataBasePlayer.db");
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS  GameDataBasePlayer " +
                    "(NAME CHAR(50) PRIMARY KEY NOT NULL ," +
                    " POINT INT NOT NULL)";
            stmt.execute(sql);
            if (opt == 1) {
                INSERT();
            } else if (opt == 2) {
                MODIFY(name, point);
            } else if (opt == 3) {
                DataBasePlayer.currentPlayer = name;
                DataBasePlayer.point = point;
            } else if (opt == 4) {
                return getDataBaseItems();
            }else if (opt == 5) {
                DELETE_ITEM(name);
            } else {
                System.out.println("Tabela pentru player a fost/este creata.");
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /*! \fn public void INSERT()
        \brief Va insera datele pentru un nou jucator(se va genera un nume nou - unic) si se vor pune numarul de puncte pe 0.
     */
    public void INSERT() {
        try {
            //voi salva numarul de jucatori intr-un fisier
            File myObj = new File("FileSaveNumberPlayer.txt");
            Scanner myReader = new Scanner(myObj);
            int cnt = myReader.nextInt();
            myReader.close();
            PreparedStatement prep = c.prepareStatement("INSERT INTO GameDataBasePlayer (NAME,POINT) VALUES(?,?)");
            String aux = "Player" + cnt;
            DataBasePlayer.currentPlayer = aux;
            DataBasePlayer.point=0;
            prep.setString(1, aux);
            prep.setInt(2, 0);
            prep.executeUpdate();
            BufferedWriter out = new BufferedWriter(new FileWriter("FileSaveNumberPlayer.txt", false));
            cnt++;
            String aux1 = "" +  cnt;
            out.write(aux1);
            out.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*! \fn private void MODIFY(String name) throws ItemNotFond
        \brief Va modifica scorul personajului daca e mai mare decat cel cu care a inceput.
     */
    private void MODIFY(String name, int currentPoint) throws ItemNotFond {
        try {
            PreparedStatement statement = c.prepareStatement("UPDATE  GameDataBasePlayer SET POINT = ? WHERE  NAME = ?");
            int val = Game.DIAMONDS * 2 + Game.COINS;
            if (currentPoint > val)
                val = currentPoint;
            statement.setInt(1, val);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /*! \fn public List<DataBaseItemPlayer> getDataBaseItems()
        \brief Va returna o lista cu toate inregistrarile din baza de date.
     */
    public List<DataBaseItemPlayer> getDataBaseItems() {
        List<DataBaseItemPlayer> listItem = new LinkedList<>();
        try {
            PreparedStatement statement = c.prepareStatement("SELECT NAME,POINT from GameDataBasePlayer ");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                int point = rs.getInt(2);
                listItem.add(new DataBaseItemPlayer(name, point, 0, 0, 400, 50, Tile.itemDataBaseTile));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return listItem;
    }

    private void DELETE_ITEM(String name) throws ItemNotFond {
        try {
            String sql = "DELETE FROM GameDataBasePlayer WHERE NAME = ?";

            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}


