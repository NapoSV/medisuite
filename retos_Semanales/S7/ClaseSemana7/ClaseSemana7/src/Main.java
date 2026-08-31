import com.sv.uues.semana7.clase.dao.DaoClient;
import com.sv.uues.semana7.clase.dao.DaoProduct;
import com.sv.uues.semana7.clase.entities.Client;
import com.sv.uues.semana7.clase.entities.Product;
import com.sv.uues.semana7.clase.interfaces.IParameter;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main(String[] args) {

    Client client = new Client();
    client.setId("2");
    client.setName("TEST2");
    client.setLastName("LAST NAME");
    client.setAddress("ADDRESS");
    client.setCellphone("7777777");
    client.setPhone("22222222");
    client.setEmail("email@mail.com");
    DaoClient dao = new DaoClient();
    dao.insert(client);
    ArrayList<IParameter> data =dao.read();

    for (IParameter parameter:data){
        System.out.println(parameter.toString());
    }

    Product product = new Product(1,"CHOCOLATE","CANDY");
    DaoProduct daoProduct = new DaoProduct();
    daoProduct.insert(product);

    //puedo declarar otra variable de tipo lista de la interface IParameter o puedo reutilizar la existente
    //ArrayList<IParameter> dataProductos =daoProduct.read();
    data =daoProduct.read();
    System.out.println("Products.....");
    for (IParameter parameter:data){
        System.out.println(parameter.toString());
    }



}
