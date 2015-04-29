package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;
import java.util.List;
/**
 *
 * @author Pedro G. Campos
 */
public class KeysAndIndexList<U,I,C extends ContextIF> implements Serializable {
    private static final long serialVersionUID = 1001L;
    private ArrayList<U> usersList;
    private ArrayList<I> itemsList;
    private int userMaxIndex;
    private int itemMaxIndex;
    private boolean closed;

    public int getUserIndex(U user){ return Collections.binarySearch((List)usersList, user); }
    public int getItemIndex(I item){ return Collections.binarySearch((List)itemsList, item); }
    public U getUserKey(int index){ return usersList.get(index); }
    public I getItemKey(int index){ return itemsList.get(index); }
    public int getUserMaxIndex(){ return userMaxIndex; }
    public int getItemMaxIndex(){ return itemMaxIndex; }
    public boolean existsUser    (U user)  { return   (Collections.binarySearch((List)usersList, user)>=0);   }
    public boolean existsItem    (I item)  { return   (Collections.binarySearch((List)itemsList, item)>=0);   }
    public boolean exists    (U user, I item)  { return  existsUser(user) && existsItem(item);  }


    public boolean addUser (U user) {
        if (closed) return false;
        int pos;
        pos=Collections.binarySearch((List)usersList, user);
        if (pos<0) {usersList.add(-pos-1,user);return true;}
        return false;

    }

    public boolean addItem (I item) {
        if (closed) return false;
        int pos;
        pos=Collections.binarySearch((List)itemsList, item);
        if (pos<0) {itemsList.add(-pos-1,item);return true;}
        return false;
    }

    public boolean addKeys (U user, I item){
        if (closed) return false;
        if (exists(user,item)) return false;
        else {
            addUser(user);
            addItem(item);
        }
        return true;
    }

    public KeysAndIndexList(){
        usersList=new ArrayList();
        itemsList=new ArrayList();
        userMaxIndex=0;
        itemMaxIndex=0;
        closed=false;
    }

    public KeysAndIndexList(ArrayList dim1List, ArrayList dim2List){
        this.usersList=dim1List;
        this.itemsList=dim2List;
        userMaxIndex=dim1List.size();
        itemMaxIndex=dim2List.size();
        closed=true;
    }

    public KeysAndIndexList(ModelIF<U,I,C> model){
        this.usersList=new ArrayList(model.getUsers());
        this.itemsList=new ArrayList(model.getItems());
        Collections.sort((List)usersList);
        Collections.sort((List)itemsList);
        this.userMaxIndex=usersList.size();
        this.itemMaxIndex=itemsList.size();
        closed=true;
    }

    public boolean generateKeys(ModelIF<U,I,C> model){
        if (closed) return false;
        this.usersList=new ArrayList(model.getUsers());
        this.itemsList=new ArrayList(model.getItems());
        Collections.sort((List)usersList);
        Collections.sort((List)itemsList);
        this.userMaxIndex=usersList.size();
        this.itemMaxIndex=itemsList.size();
        closed=true;
        return true;
    }

}
