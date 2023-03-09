package PaooGame.Auxiliary;

/*! \class public class MyVector
    \brief Utila pentru a converti din vector de string in vector de int.
 */
public class MyVector {
    public String[] vector ;

    /*! \fn public MyVector(String[] v)
        \brief Constructorul clasei
     */
    public MyVector(String[] v){
        vector = v;
    }

    /*! \fn public  int[] convertStringArrayToIntArray()
        \brief Returneaza vectorul de string ca vector de int.
     */
    public  int[] convertStringArrayToIntArray(){
        int[] arrayInt = new int[vector.length];
        for (int i=0;i<vector.length;i++){
            arrayInt[i] = Integer.parseInt(vector[i]);
        }
        return arrayInt;
    }
}
