package PaooGame.Exception;

/*! \class public class ToManyItems extends Exception
    \brief Creaza o exceptie particulara pentru jocul meu(incercarea de inserare in BD a prea multor date)
 */
public class ToManyItems extends Exception{
    public ToManyItems(String str){
        super("Prea multe elemente in : " + str);
    }
}
