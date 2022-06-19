import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Servidor {
    private static int replicas = 3;
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            //He tenido que usar el puerto 1100 porque el 1099 no me funcionaba (me decia que ya estaba en uso y no estaba en uso)
            Registry reg = LocateRegistry.createRegistry(1100);
            ArrayList<String> array = new ArrayList<>();
            array.add("server2");
            array.add("server3");
            for (int i = 0; i < replicas; i++){
                int numServer = i+1;
                ServidorDonacion server = new ServidorDonacion("localhost","server" + numServer,array);
                Naming.rebind("server" + numServer, server);
                System.out.println("-> Server" + numServer + " listo");
            }

        } catch (RemoteException | MalformedURLException e) {
            System.out.println(e.toString());
        }

    }
}
