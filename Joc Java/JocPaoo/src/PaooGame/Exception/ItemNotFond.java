package PaooGame.Exception;

/*! \class public class ItemNotFond extends Exception
    \brief Creaza o exceptie particula pentru jocul meu : incercarea de extragere din baza de date a unui element inexistent
 */
public class ItemNotFond extends Exception{
    public ItemNotFond(String str){
        super("Inregistrarea : " +  str + "nu este in baza de date.");
    }
}
