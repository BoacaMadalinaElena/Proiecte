package PaooGame.Exception;

/*! \class public class ToManyItems extends Exception
    \brief Creaza o exceptie particulara pentru jocul meu(incercarea de inserare in BD a prea multor date)
 */
public class TypeNotFound extends Exception{
    public TypeNotFound(String str){
        super("Nu exista nici un obiect activ cu acest nume : " + str);
    }
}
