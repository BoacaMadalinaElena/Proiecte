package PaooGame;

import PaooGame.Auxiliary.DataBaseItemPlayer;
import PaooGame.Exception.ItemNotFond;
import PaooGame.Input.Audio;
import PaooGame.Tiles.Tile;

import java.io.*;
import java.sql.*;
import java.util.*;

/*! \class public DataBaseSettings
    \brief Va realiza gestionarea bazei de date pentru  setari.

 */
public class DataBaseSettings {
    Connection c = null;                                                  /*!< Referinta pentru conectarea la baza de date.*/
    Statement stmt = null;                                                /*!< Referinta pentru a putea transmite comenzi bazei de date.*/

    /*! \fn public void CREATE(int opt)
        \brief Va gestiona baza de date(din ea voi apela celelalte metode - adaugare inregistrare,restaurare setari).

        \param opt un intreg prin case voi specifica ce operatie realizez cu baza de date.
     */
    public void CREATE(int opt) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:res/DataBase/GameDataBaseSettings.db");
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS  GameDataBaseSettings" +
                    "(OPTION INT NOT NULL, " +
                    "VOLUME FLOAT NOT NULL)";
            stmt.execute(sql);
            if (opt == 1) {
                INSERT();
            } else if (opt == 2) {
                SetSettings();
            } else {
                System.out.println("Tabela pentru player a fost/este creata.");
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /*! \fn public void INSERT()
        \brief Va insera setarile in baza de date.
     */
    public void INSERT() {
        try {
            PreparedStatement prep = c.prepareStatement("INSERT INTO GameDataBaseSettings (OPTION,VOLUME) VALUES(?,?)");
            if (Audio.status)
                prep.setInt(1, 1);
            else
                prep.setInt(1, 0);
            prep.setFloat(2,Audio.volume);
            prep.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /*! \fn public void  SetSettings()
        \brief Va restaura setarile din baza de date.
     */
    public void SetSettings() {
        try {
            PreparedStatement statement = c.prepareStatement("SELECT OPTION,VOLUME from GameDataBaseSettings ");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 1) {
                    Audio.status = true;
                    Audio.volume = rs.getFloat(2);
                    Game.audio.play(rs.getFloat(2));
                } else {
                    Audio.status = false;
                    Game.audio.stopAudio();
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}


